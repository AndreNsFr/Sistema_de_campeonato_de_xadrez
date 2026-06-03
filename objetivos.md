# Desafio — Sistema de campeonato de xadrez (Java)

> Versão original: sistema de gerenciamento de torneio, sem engine de jogo.  
> Stack: Java 17+, terminal puro, sem interface gráfica.

---

## Objetivo

Construir um sistema orientado a objetos com regras reais de domínio para gerenciar:

- Jogadores e perfis
- Partidas e resultados
- Ranking por ELO
- Torneios
- Histórico de partidas
- Estatísticas avançadas

---

## Perguntas de design — responda antes de abrir o IDE

1. Quem calcula e aplica a variação de ELO — a `Partida`, o `Torneio`, ou um serviço externo?
2. A `Partida` deve conhecer regras de pontuação, ou apenas transportar dados?
3. O histórico de partidas pertence ao `Jogador` ou ao `Torneio`? Os dois? Como evitar duplicação?
4. Como garantir que o ELO nunca fique inconsistente se `finalizarPartida()` for chamado duas vezes?
5. O `Torneio` está fazendo coisas demais? O que dá para extrair num serviço separado?

> Responda isso no papel antes de escrever código. Modelagem ruim destrói projeto pequeno rapidamente.

---

## Estrutura de pacotes

```
src/
├── models/          ← Jogador, Partida, Torneio
├── service/        ← EloService, TorneioService, RankingService
├── enums/          ← ResultadoPartida, StatusTorneio
├── util/           ← Formatador, helpers de exibição
├── exception/      ← PartidaJaFinalizadaException, etc.
└── Main.java       ← Apenas orquestração e menu
```

---

## Modelo de domínio

### `Jogador` — `models/`

Entidade central. Carrega identidade, estatísticas e histórico próprio.

**Atributos:**

| Atributo | Tipo | Descrição |
| --- | --- | --- |
| `id` | `UUID` | Gerado na criação, nunca reutilizado |
| `nome` | `String` | Nome exibido no ranking |
| `vitorias` | `int` | Contador de vitórias |
| `derrotas` | `int` | Contador de derrotas |
| `empates` | `int` | Contador de empates |
| `pontuacaoElo` | `int` | ELO atual. Mínimo: 100 |
| `historico` | `List<Partida>` | Partidas jogadas |

**Métodos:**

| Assinatura | Objetivo |
| --- | --- |
| `registrarVitoria()` | Incrementa `vitorias`. Não altera ELO — isso é responsabilidade do `EloService`. |
| `registrarDerrota()` | Incrementa `derrotas`. |
| `registrarEmpate()` | Incrementa `empates`. |
| `adicionarPartida(Partida)` | Adiciona ao histórico. Valida que a partida está finalizada antes de aceitar. |
| `getTaxaVitoria()` | Retorna `vitorias / total`. Retorna `0.0` se não jogou nenhuma partida. |
| `getWinStreak()` | Percorre o histórico de trás para frente e conta a sequência atual de vitórias. |

---

### `Partida` — `models/`

Representa um confronto entre dois jogadores. Não contém lógica de pontuação — apenas transporta dados.

**Atributos:**

| Atributo | Tipo | Descrição |
| --- | --- | --- |
| `jogadorBrancas` | `Jogador` | Jogador com as peças brancas |
| `jogadorPretas` | `Jogador` | Jogador com as peças pretas |
| `resultado` | `ResultadoPartida` | Resultado final |
| `data` | `LocalDateTime` | Data e hora da partida |
| `finalizada` | `boolean` | Controle de idempotência |

**Métodos:**

| Assinatura | Objetivo |
| --- | --- |
| `finalizarPartida(ResultadoPartida)` | Define o resultado. Lança `PartidaJaFinalizadaException` se já foi finalizada — garante que o ELO não seja alterado duas vezes. |
| `exibirResumo()` | Imprime no terminal: jogadores, resultado, data. |
| `getVencedor()` | Retorna `Optional<Jogador>`. Vazio se empate. Nunca retorna `null`. |

---

### `Torneio` — `models/`

Orquestrador de jogadores e partidas. Deve delegar cálculos para serviços — não acumular lógica internamente.

**Atributos:**

| Atributo | Tipo | Descrição |
| --- | --- | --- |
| `nome` | `String` | Nome do torneio |
| `jogadores` | `List<Jogador>` | Participantes |
| `partidas` | `List<Partida>` | Partidas registradas |
| `status` | `StatusTorneio` | Estado atual do torneio |

**Métodos:**

| Assinatura | Objetivo |
| --- | --- |
| `adicionarJogador(Jogador)` | Adiciona jogador se o torneio ainda não começou. Lança exceção se `status != AGUARDANDO`. |
| `registrarPartida(Partida)` | Registra partida finalizada. Valida que ambos os jogadores pertencem ao torneio. |
| `getRanking()` | Retorna `List<Jogador>` ordenada por ELO desc via `stream().sorted(Comparator...)`. |
| `getCampeao()` | Retorna `Optional<Jogador>` com o primeiro do ranking. Vazio se nenhuma partida foi jogada. |
| `encerrar()` | Muda `status` para `ENCERRADO`. Impede novos jogadores e partidas. |

---

## Enums

### `ResultadoPartida` — `enums/`

```java
enum ResultadoPartida {
    BRANCAS_VENCERAM,
    PRETAS_VENCERAM,
    EMPATE
}
```

### `StatusTorneio` — `enums/`

```java
enum StatusTorneio {
    AGUARDANDO,
    EM_ANDAMENTO,
    ENCERRADO
}
```

---

## Serviços

### `EloService` — `service/`

Toda lógica de cálculo e aplicação de ELO. Stateless — recebe dados, retorna resultado, sem mutar nada por conta própria.

| Método | Objetivo |
| --- | --- |
| `calcular(int eloA, int eloB, ResultadoPartida)` | Retorna `EloChange` com os deltas para cada jogador. Regras: vitória `+15/-10`, empate `+2/+2`. ELO mínimo: 100. |
| `aplicar(Jogador, Jogador, ResultadoPartida)` | Único método que altera ELO de `Jogador`. Chama `calcular()` e aplica. Garante consistência — nenhuma outra classe faz isso. |

### `RankingService` — `service/`

Consultas de ranking e estatísticas. Usa streams — não guarda estado.

| Método | Objetivo |
| --- | --- |
| `getRanking(Torneio)` | Jogadores ordenados por ELO desc. |
| `getTopN(Torneio, int n)` | Top N jogadores via `stream().limit(n)`. |
| `buscarPorNome(Torneio, String)` | Busca case-insensitive. Retorna `Optional<Jogador>`. |
| `getStats(Torneio)` | Retorna `TournamentStats`: média de ELO, jogador mais ativo, maior streak, total por resultado. |
| `getMaisAtivo(Torneio)` | Jogador com mais partidas via `stream().max(Comparator...)`. |

### `TorneioService` — `service/`

Orquestra criação e ciclo de vida de torneios.

| Método | Objetivo |
| --- | --- |
| `criarTorneio(String nome)` | Cria novo `Torneio` com `status = AGUARDANDO`. |
| `iniciarTorneio(Torneio)` | Valida mínimo de 2 jogadores e muda status para `EM_ANDAMENTO`. |
| `registrarResultado(Torneio, Partida)` | Finaliza a partida, atualiza ELO via `EloService`, registra no torneio e no histórico de cada jogador. Ponto central de consistência. |

---

## Regras de ELO

| Resultado | Vencedor | Perdedor |
| --- | --- | --- |
| Vitória | +15 | −10 |
| Empate | +2  | +2  |

- ELO mínimo: **100** (nunca vai abaixo)
- O cálculo de ELO não pertence à `Partida` nem ao `Torneio` — pertence ao `EloService`
- `finalizarPartida()` com campo `finalizada` garante idempotência

---

## Requisitos técnicos obrigatórios

- `ArrayList` e `HashMap` para coleções internas
- `Enum` para `ResultadoPartida` e `StatusTorneio`
- `UUID` para identidade de `Jogador`
- `LocalDateTime` para data das partidas
- `Optional<T>` — nunca retornar `null` onde há ausência possível
- `Stream` com `sorted()`, `filter()`, `max()`, `limit()`
- `Comparator` para ordenação de ranking
- Encapsulamento: nenhum atributo público
- Separação em pacotes: `models`, `service`, `enums`, `util`, `exception`
- Exceções de domínio próprias (ex.: `PartidaJaFinalizadaException`)

---

## Proibido

- Lógica de negócio dentro da `Main`
- Atributos públicos
- Métodos com mais de ~25 linhas
- Código duplicado — extraia num método ou serviço
- `null` como retorno onde `Optional` resolve
- Camadas misturadas: `models` não chama `service`

---

## Extras opcionais

### 1. Persistência em JSON

Salvar e carregar torneio de um arquivo `.json` usando Gson ou Jackson.

```
TorneioRepository
  salvar(Torneio, String caminho)
  carregar(String caminho) → Torneio
```

### 2. Busca com Optional

```java
Optional<Jogador> buscarPorNome(String nome)
```

Implementar no `RankingService` com `stream().filter().findFirst()`. Nunca retornar `null`.

### 3. Estatísticas avançadas via streams

- Maior win streak atual
- Jogador mais ativo (mais partidas)
- Média de ELO do torneio
- Total de partidas por resultado

### 4. Sistema suíço (desafio pesado)

Emparelhar jogadores automaticamente por ELO sem repetir confrontos já realizados. Você vai precisar de um mapa de confrontos já jogados. Aqui começa o algoritmo de verdade.

---

## O que esse projeto força você a aprender

- Modelagem de domínio: separar o que é dado, regra e orquestração
- Estado mutável controlado: evitar inconsistência quando objetos se referenciam
- Responsabilidade única: classes que fazem uma coisa e fazem bem
- Streams e coleções: manipulação expressiva sem loops manuais
- Exceções de domínio: comunicar erros de negócio com clareza
- Arquitetura em camadas: models não conhece service, Main não conhece domínio
