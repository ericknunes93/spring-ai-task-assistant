# 🎙️ DIO Spring Boot - API de Orçamento com Voz e Texto (Spring AI)

Projeto desenvolvido como solução do **Desafio de Projeto com Spring AI** da Digital Innovation One (DIO).

A **Spring AI Budgeting API** é uma aplicação Java/Spring Boot construída sobre os princípios de **Domain-Driven Design (DDD)** e **Single Responsibility Principle (SRP)**, capaz de processar comandos em linguagem natural por **texto** e **voz (upload de áudio)** para gestão de transações financeiras pessoais utilizando **Tool Calling** (Function Calling).

---

## 🎯 Visão Geral & Funcionalidades

- **Processamento por Linguagem Natural (Texto & Voz)**:
  - Permite criar receitas, despesas e consultar saldos através de frases naturais como *"Recebi meu salário de R$ 4.500,00"* ou *"Gastei R$ 35,00 no almoço"*.
- **Speech-To-Text (Whisper STT)**:
  - Transcrição de arquivos de áudio enviados via `MultipartFile` utilizando o modelo `whisper-1` da OpenAI.
- **Text-To-Speech (TTS)**:
  - Síntese da resposta textual gerada pela IA em um arquivo de áudio sintetizado MP3 (`tts-1`).
- **Tool Calling (Function Calling)**:
  - O `ChatClient` do Spring AI analisa a intenção do usuário e invoca automaticamente ferramentas Java (`@Bean` + `@Description`) para persistir ou buscar dados.
- **Melhoria de Valor (Resumo Financeiro & Categorias)**:
  - Cálculo consolidado de saldo disponível (`Saldo = Receitas - Despesas`) e suporte ao agrupamento por categorias (`ALIMENTACAO`, `TRANSPORTE`, `MORADIA`, `LAZER`, `SALARIO`, `OUTROS`).

---

## 🏛️ Arquitetura DDD & SRP (Single Responsibility Principle)

```text
src/main/java/dio/budgeting/
├── domain/                               # Camada de Domínio (Regras de Negócio)
│   ├── Transaction.java                  # Entidade de Transação Financeira
│   ├── TransactionType.java              # Enum (RECEITA, DESPESA)
│   ├── TransactionCategory.java          # Enum (ALIMENTACAO, TRANSPORTE, MORADIA, LAZER, SALARIO, OUTROS)
│   └── TransactionRepository.java        # Contrato da Camada de Dados
│
├── application/                           # Camada de Aplicação (Casos de Uso e IA)
│   ├── CreateTransactionUseCase.java     # Caso de uso de criação
│   ├── ListTransactionsUseCase.java       # Caso de uso de listagem
│   ├── GetFinancialSummaryUseCase.java   # Caso de uso de saldo (Melhoria)
│   ├── dto/                              # DTOs (CreateTransactionCommand, TransactionResponse, FinancialSummary)
│   ├── ai/                               # Serviços de IA com Responsabilidade Única (SRP)
│   │   ├── SpeechToTextService.java      # Transcrição via OpenAiAudioTranscriptionModel
│   │   ├── BudgetChatService.java        # Centraliza o ChatClient e Tool Calling
│   │   └── TextToSpeechService.java      # Síntese vocal via OpenAiAudioSpeechModel
│   └── tools/
│       └── FinancialToolsConfig.java     # Definição das ferramentas do Spring AI (@Bean + Function)
│
└── infrastructure/                        # Camada de Infraestrutura (Adapters & HTTP)
    ├── repository/
    │   └── InMemoryTransactionRepository.java # Repositório em memória thread-safe
    └── http/
        ├── TransactionController.java    # REST API tradicional (/api/transactions)
        ├── BudgetCommandController.java  # Endpoint de comandos em linguagem natural por texto (/api/budget/text-command)
        └── VoiceBudgetController.java    # Endpoint de comando por voz (/api/budget/voice-command)
```

---

## 🔄 Fluxo de Processamento

### 1. Fluxo por Texto (`POST /api/budget/text-command`)
1. **Entrada**: Payload JSON contendo a mensagem em texto.
2. **Orquestração**: O `BudgetChatService` repassa o prompt ao `ChatClient`.
3. **Tool Calling**: A IA escolhe e executa a função financeira correspondente (`criarTransacao`, `listarTransacoes` ou `obterResumoFinanceiro`).
4. **Resposta**: Retorno textual em JSON com o resultado da ação.

### 2. Fluxo por Voz (`POST /api/budget/voice-command`)
> 💡 **Reutilização de Código**: O fluxo por voz reutiliza **integralmente** o `BudgetChatService` (usado pelo endpoint textual), adicionando apenas a etapa prévia de **Speech-to-Text (Whisper)** e a etapa posterior de **Text-to-Speech (TTS)**.

```text
[Cliente: Áudio] ──> SpeechToTextService (Whisper STT) ──> [Texto Transcrito]
                                                                  │
                                                                  ▼
[Cliente: Áudio Response] <── TextToSpeechService (TTS) <── BudgetChatService (ChatClient + Tool Calling)
```

---

## 💾 Justificativa da Persistência em Memória

O [InMemoryTransactionRepository](file:///C:/Users/erick/Documents/Projects/REPOSITÓRIOS/taskmanager/src/main/java/dio/budgeting/infrastructure/repository/InMemoryTransactionRepository.java) foi mantido em memória para **isolar e focar o desafio na utilização do Spring AI, Whisper, TTS e Tool Calling**, sem introduzir complexidade adicional desnecessária de banco de dados relacional ou migração de schemas.

---

## ⚡ Como Executar

### Pré-requisitos
- **Java 21**
- **Gradle Wrapper** (incluso `./gradlew`)

### 1. Configurar Chave de API (OpenAI)
Defina a variável de ambiente ou edite o [application.yml](file:///C:/Users/erick/Documents/Projects/REPOSITÓRIOS/taskmanager/src/main/resources/application.yml):

```bash
export OPENAI_API_KEY="sk-proj-sua-chave-aqui"
```

### 2. Compilar e Iniciar

```bash
./gradlew bootRun
```

A aplicação estará disponível em `http://localhost:8080`.

---

## 🧪 Endpoints para Teste

### 1. Comando de Linguagem Natural por Texto (Spring AI Tool Calling)
- **POST** `/api/budget/text-command`
- **Body**:
```json
{
  "message": "Recebi meu salário de R$ 5.000,00 no dia de hoje."
}
```
- **Resposta**:
> *"Transação de R$ 5.000,00 registrada com sucesso na categoria SALARIO."*

- **Consultando o Saldo**:
```json
{
  "message": "Qual é o meu saldo atual?"
}
```
- **Resposta**:
> *"Seu total de receitas é de R$ 5.000,00, total de despesas R$ 0,00 e seu saldo atual é de R$ 5.000,00."*

---

### 2. Comando por Voz (Multipart Audio STT + Tool Calling + TTS)
- **POST** `/api/budget/voice-command`
- **Header**: `Content-Type: multipart/form-data`
- **Body**: `file`: (Seu arquivo de áudio `comando.mp3` ou `comando.wav`)
- **Retorno**: Arquivo de áudio MP3 sintetizado contendo a resposta falada da IA.

---

### 3. Endpoints REST Tradicionais
- **POST** `/api/transactions`: Criar transação manualmente via JSON.
- **GET** `/api/transactions`: Listar transações ativas.
- **GET** `/api/transactions/summary`: Retorna o DTO de resumo de saldo consolidado.
