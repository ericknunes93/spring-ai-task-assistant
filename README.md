# 🎙️ DIO Spring Boot - API de Orçamento com Voz e Texto (Spring AI)

Projeto desenvolvido como solução do **Desafio de Projeto com Spring AI** da Digital Innovation One (DIO).

A **Spring AI Budgeting API** é uma aplicação Java/Spring Boot que utiliza uma arquitetura em camadas inspirada em **Domain-Driven Design (DDD)**, separando claramente Domínio, Aplicação e Infraestrutura, além de aplicar o **Single Responsibility Principle (SRP)**. 

A aplicação processa comandos em linguagem natural por **texto** e **voz (upload de áudio)** para gestão de transações financeiras pessoais utilizando o mecanismo de **Tool Calling** do Spring AI.

---

## 🏛️ Diagrama Arquitetural Geral

```text
                               [ Cliente HTTP ]
                                  │       │
             (Texto /text-command)│       │(Áudio /voice-command)
                                  ▼       ▼
                     ┌───────────────────────────────┐
                     │          Controllers          │
                     │  (BudgetCommand / Voice)      │
                     └──────────────┬────────────────┘
                                    │
                         ┌──────────┴──────────┐
                         │ SpeechToTextService │ (Processa Whisper STT se for voz)
                         └──────────┬──────────┘
                                    │
                                    ▼
                         ┌─────────────────────┐
                         │  BudgetChatService  │ (Centraliza comunicação LLM)
                         └──────────┬──────────┘
                                    │
                                    ▼
                         ┌─────────────────────┐
                         │  Spring ChatClient  │ (Tool Calling Engine)
                         └──────────┬──────────┘
                                    │
                        ┌───────────┴───────────┐
                        │  FinancialToolsConfig │ (Adaptadores anotados com @Description)
                        └───────────┬───────────┘
                                    │
                        ┌───────────┴───────────┐
                        │       UseCases        │ (Regras de Negócio Puras)
                        └───────────┬───────────┘
                                    │
                        ┌───────────┴───────────┐
                        │ TransactionRepository │ (Persistência Thread-Safe em Memória)
                        └───────────┬───────────┘
                                    │
                         ┌──────────┴──────────┐
                         │ TextToSpeechService │ (Gera síntese MP3 se for voz)
                         └──────────┴──────────┘
                                    │
                                    ▼
                             [ Cliente HTTP ]
```

---

## 🧠 Comportamento Arquitetural de Erros: REST vs. Tool Calling

A aplicação adota duas estratégias de tratamento de erro intencionais e alinhadas com as melhores práticas de IA:

1. **Contrato REST Tradicional (`/api/transactions`)**:
   - **Formato**: RFC 7807 (`ProblemDetail`) com URNs estáveis (`urn:problem:bad-request`, `urn:problem:validation-error`, `urn:problem:payload-too-large`).
   - **Objetivo**: Garantir respostas determinísticas e estritas para clientes de API programáticos.

2. **Contrato Conversacional & Tool Calling (`/api/budget/text-command` e `/api/budget/voice-command`)**:
   - **Formato**: Resposta conversacional em linguagem natural com HTTP `200 OK`.
   - **Objetivo**: **Conversational Resilience** — Quando uma ferramenta exposta ao Spring AI sofre uma exceção durante a execução do Tool Calling (ex: categoria não suportada), o framework captura a falha e a devolve ao contexto do LLM. O modelo sintetiza uma resposta amigável em linguagem natural (ex: *"Não foi possível registrar a despesa pois a categoria informada é inválida. Categorias suportadas: ALIMENTACAO, TRANSPORTE, etc."*), permitindo que a interação por voz/texto continue sem derrubar a requisição com um erro de protocolo HTTP.

---

## 🎯 Fluxo de Funcionamento do Tool Calling (Spring AI)

```text
[Prompt em Linguagem Natural] 
           │
           ▼
[OpenAI GPT-4o-mini]
           │ Analisa as definições anotadas com @Description em FinancialToolsConfig
           ▼
[Seleção Automática da Tool] ──> Ex: "criarTransacao(amount=45.0, type='DESPESA', category='ALIMENTACAO')"
           │
           ▼
[Execução do Use Case] ───────> Ex: CreateTransactionUseCase.execute(...)
           │
           ▼
[Retorno para o LLM] ──────────> OpenAI produz a resposta final sintetizada em texto
```

> 💡 **Adaptadores Desacoplados**: As ferramentas expostas em `FinancialToolsConfig` atuam **exclusivamente como adaptadores** entre o framework Spring AI e os casos de uso da aplicação, sem conter regras de negócio.

---

## 🎙️ Fluxos por Texto e Voz (Reutilização de Código & Header RFC 3986)

- **Fluxo por Texto (`POST /api/budget/text-command`)**:
  - Recebe comandos textuais e os repassa diretamente ao `BudgetChatService`.
- **Fluxo por Voz (`POST /api/budget/voice-command`)**:
  - **Reutilização de Código**: Reutiliza **integralmente** a lógica do `BudgetChatService`, adicionando apenas a etapa prévia de **Speech-to-Text (Whisper)** e a etapa posterior de **Text-to-Speech (TTS)**.
  - **Conformidade de Cabeçalho HTTP (RFC 3986)**: O texto transcrito no cabeçalho `X-Transcribed-Text` é sanitizado (remoção de quebras de linha), truncado em no máximo **200 caracteres** antes da codificação (para evitar estouro de cabeçalho no container HTTP) e codificado via `UriUtils.encode(..., StandardCharsets.UTF_8)` do Spring (substituindo espaços por `%20` e acentos por percent-encoding), garantindo total compatibilidade com `decodeURIComponent` em aplicações frontend.

---

## 🛡️ Tratamento Global de Exceções REST (`GlobalExceptionHandler.java`)

- A classe `GlobalExceptionHandler` intercepta exceções como `IllegalArgumentException`, `HttpMessageNotReadableException` (JSON malformado), `MethodArgumentTypeMismatchException`, `MethodArgumentNotValidException` (validação DTO), `MaxUploadSizeExceededException` (áudio > 10MB) e `IOException`, retornando URNs estáveis como `urn:problem:validation-error` e `urn:problem:payload-too-large`.
- Erros internos do servidor são registrados no log do servidor com stack trace completa (`log.error(...)`), prevenindo o vazamento de detalhes internos da API externa da OpenAI.

---

## 🔒 Segurança & Evoluções Futuras (Mitigação de Prompt Injection)

- **Superfície de Ataque via Transcrição**:
  - A descrição de transações geradas via transcrição de voz (`Whisper`) é armazenada e posteriormente pode retornar ao contexto do `ChatClient` durante consultas de histórico.
  - **Evolução Futura**: Sanitização e delimitadores explícitos (ex: XML/Markdown blocks) no prompt do sistema para evitar que entradas maliciosas no áudio alterem o comportamento do modelo LLM (*Indirect Prompt Injection*).

---

## 💾 Justificativa do Repositório em Memória

O [InMemoryTransactionRepository](file:///C:/Users/erick/Documents/Projects/REPOSITÓRIOS/taskmanager/src/main/java/dio/budgeting/infrastructure/repository/InMemoryTransactionRepository.java) foi mantido em memória para **isolar e focar o desafio na utilização do Spring AI, Whisper, TTS e Tool Calling**, sem introduzir complexidade adicional desnecessária de banco de dados relacional ou migração de schemas.

---

## 🧪 Exemplos de Requisição e Resposta

### 1. Comando de Linguagem Natural por Texto
- **POST** `/api/budget/text-command`
- **Headers**: `Content-Type: application/json`
- **Body**:
```json
{
  "message": "Recebi meu salário de R$ 5.000,00."
}
```
- **Resposta**:
```json
{
  "response": "Receita no valor de R$ 5.000,00 registrada com sucesso na categoria SALARIO."
}
```

### 2. Comando por Voz
- **POST** `/api/budget/voice-command`
- **Header**: `Content-Type: multipart/form-data`
- **Body**: `file`: (Arquivo de áudio `comando.mp3` ou `comando.wav`)
- **Resposta**:
  - **Status**: `200 OK`
  - **Content-Type**: `audio/mpeg`
  - **Header Especial**: `X-Transcribed-Text: Gastei%2050%20reais%20no%20caf%C3%A9%20da%20manh%C3%A3%20e%20almo%C3%A7o` (Percent-encoding RFC 3986 `%20` via Spring `UriUtils`)
  - **Body**: Array de bytes do áudio sintetizado em MP3.

### 3. REST Tradicional - Obter Resumo por Categoria
- **GET** `/api/transactions/summary?category=ALIMENTACAO`
- **Resposta**:
```json
{
  "totalReceitas": 0.00,
  "totalDespesas": 45.00,
  "saldoAtual": -45.00
}
```

---

## 🧪 Testes Unitários e de Integração

A aplicação conta com uma suíte de testes unitários e de integração em `src/test/java/dio/budgeting/`:
- `CreateTransactionUseCaseTest`: Valida criação e invariantes de transações.
- `GetFinancialSummaryUseCaseTest`: Valida agregação de saldo por categoria com tratamento de listas vazias.
- `ListTransactionsUseCaseTest`: Valida filtros combinados por tipo e categoria.
- `TransactionControllerIT`: Valida criação, listagem e falhas de validação DTO (retornando `urn:problem:validation-error`).
- `BudgetCommandControllerIT`: Valida orquestração textual.
- `VoiceBudgetControllerIT`: Valida fluxo de upload multipart de áudio, truncamento de 200 caracteres e percent-encoding RFC 3986 (%20) no cabeçalho `X-Transcribed-Text`.
