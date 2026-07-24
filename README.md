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
                        │ TransactionRepository │ (Persistência Thread-Safe)
                        └───────────┬───────────┘
                                    │
                         ┌──────────┴──────────┐
                         │ TextToSpeechService │ (Gera síntese MP3 se for voz)
                         └──────────┬──────────┘
                                    │
                                    ▼
                             [ Cliente HTTP ]
```

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

## 🎙️ Fluxos por Texto e Voz (Reutilização de Código)

- **Fluxo por Texto (`POST /api/budget/text-command`)**:
  - Recebe comandos textuais e os repassa diretamente ao `BudgetChatService`.
- **Fluxo por Voz (`POST /api/budget/voice-command`)**:
  - **Reutilização de Código**: Reutiliza **integralmente** a lógica do `BudgetChatService`, adicionando apenas a etapa prévia de **Speech-to-Text (Whisper)** e a etapa posterior de **Text-to-Speech (TTS)**.

---

## 🛡️ Tratamento Global de Exceções & Retornos Fortes

- A aplicação utiliza `@RestControllerAdvice` na classe `GlobalExceptionHandler`, interceptando exceções como `IllegalArgumentException` e `IOException` e padronizando as respostas de erro através do formato RFC 7807 (`ProblemDetail`).
- Os controllers utilizam tipos de retorno estritos (`ResponseEntity<byte[]>` e `ResponseEntity<Map<String, String>>`), garantindo consistência na API.

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
  - **Header Especial**: `X-Transcribed-Text: Gastei 45 reais no almoço`
  - **Body**: Array de bytes do áudio sintetizado em MP3.

### 3. REST Tradicional - Obter Resumo (Melhoria de Valor)
- **GET** `/api/transactions/summary`
- **Resposta**:
```json
{
  "totalReceitas": 5000.00,
  "totalDespesas": 150.00,
  "saldoAtual": 4850.00
}
```

---

## 🧪 Testes Unitários

A aplicação conta com uma suíte de testes unitários em `src/test/java/dio/budgeting/application/`:
- `CreateTransactionUseCaseTest`: Valida criação e invariantes de transações.
- `GetFinancialSummaryUseCaseTest`: Valida agregação de saldo (`Saldo = Receitas - Despesas`).
- `ListTransactionsUseCaseTest`: Valida filtros combinados por tipo e categoria.
