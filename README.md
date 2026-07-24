# 🎙️ DIO Spring Boot - API de Orçamento com Voz e Texto (Spring AI)

Projeto desenvolvido como solução do **Desafio de Projeto com Spring AI** da Digital Innovation One (DIO).

A **Spring AI Budgeting API** é uma aplicação Java/Spring Boot que utiliza uma arquitetura em camadas inspirada em **Domain-Driven Design (DDD)**, separando claramente Domínio, Aplicação e Infraestrutura, além de aplicar o **Single Responsibility Principle (SRP)**. 

A aplicação processa comandos em linguagem natural por **texto** e **voz (upload de áudio)** para gestão de transações financeiras pessoais utilizando o mecanismo de **Tool Calling** do Spring AI.

---

## 🎓 Informações de Entrega (Desafio DIO)

### 1. O que o projeto faz?
A aplicação evolui uma API de orçamento financeiro para permitir a gestão de receitas e despesas por linguagem natural. O usuário pode enviar comandos em **texto** ou **arquivos de áudio (voz)**. A API transcreve a voz com **OpenAI Whisper (STT)**, entende a intenção e executa automaticamente funções de negócio via **Tool Calling (Spring AI ChatClient)**, e sintetiza uma resposta vocal em **MP3 (TTS)**.

### 2. Tecnologias Utilizadas
- **Linguagem**: Java 21
- **Framework**: Spring Boot 3.3.5
- **Inteligência Artificial**: Spring AI 1.0.0-M5 (Spring AI BOM)
- **Modelos OpenAI**:
  - Chat & Tool Calling: `gpt-4o-mini`
  - Transcrição de Áudio (STT): `whisper-1`
  - Síntese de Voz (TTS): `tts-1`
- **Build & Dependências**: Gradle, Lombok, Jakarta Validation (`spring-boot-starter-validation`)

### 3. Melhorias Implementadas
- **Resumo Financeiro com Filtro por Categoria**: Cálculo de `Saldo = Receitas - Despesas` com agregação por categoria (ex: `ALIMENTACAO`, `MORADIA`, `SALARIO`).
- **Arquitetura Dual de Erros**: Respostas programáticas estritas via RFC 7807 (`ProblemDetail`) nos endpoints REST tradicionais e **Conversational Resilience** nos fluxos por IA (o modelo sintetiza falhas amigavelmente em português com HTTP 200 OK sem derrubar a requisição).
- **Tratamento de Cabeçalho HTTP (RFC 3986)**: Transcrição em cabeçalho `X-Transcribed-Text` sanitizada, truncada em 200 caracteres e codificada via Spring `UriUtils.encode` (`%20`), prevenindo erros de codificação em caracteres acentuados.

### 4. Como Executar o Projeto

```bash
# 1. Clonar o repositório
git clone https://github.com/ericknunes93/spring-ai-task-assistant.git
cd spring-ai-task-assistant

# 2. Configurar a chave de API da OpenAI (Variável de Ambiente)
export OPENAI_API_KEY="sua-chave-openai-aqui" # Linux/macOS
set OPENAI_API_KEY=sua-chave-openai-aqui      # Windows CMD
$env:OPENAI_API_KEY="sua-chave-openai-aqui"    # Windows PowerShell

# 3. Compilar e executar a aplicação
./gradlew bootRun
```

A API estará acessível em `http://localhost:8080`.

### 5. Como Testar os Fluxos Principais

#### A. Testar Comando em Linguagem Natural por Texto
```bash
curl -X POST http://localhost:8080/api/budget/text-command \
  -H "Content-Type: application/json" \
  -d '{"message": "Recebi meu salário de R$ 5000 no dia de hoje."}'
```
**Resposta Esperada**:
```json
{
  "response": "Receita no valor de R$ 5000.00 registrada com sucesso na categoria SALARIO."
}
```

#### B. Testar Comando por Voz (Upload de Áudio MP3/WAV)
```bash
curl -X POST http://localhost:8080/api/budget/voice-command \
  -F "file=@comando_gastei_almoco.mp3" \
  --output resposta_voz.mp3 \
  -i
```
**Resposta Esperada**:
- **Status**: `HTTP/1.1 200 OK`
- **Header**: `X-Transcribed-Text: Gastei%2045%20reais%20no%20almo%C3%A7o`
- **Body**: Arquivo de áudio `resposta_voz.mp3` contendo a resposta sintetizada.

#### C. Testar Resumo Financeiro Tradicional
```bash
curl -X GET "http://localhost:8080/api/transactions/summary?category=SALARIO"
```

### 6. O que foi aprendido?
- **Spring AI & ChatClient**: Como orquestrar interações com modelos de linguagem e registrar funções Java como ferramentas (`Tool Calling`) usando anotações `@Bean` e `@Description`.
- **Arquitetura Multimodal (STT + LLM + TTS)**: Como encadear transcrição de voz (Whisper), processamento cognitivo e síntese vocal mantendo o princípio da responsabilidade única (SRP).
- **Resiliência e Erros em IA**: Como diferenciar contratos rígidos REST (RFC 7807) de fluxos de resiliência conversacional em interfaces de voz/texto.

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
                         └──────────┬──────────┘
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

## 🧪 Testes Unitários e de Integração

A aplicação conta com uma suíte completa de testes unitários e de integração em `src/test/java/dio/budgeting/`:
- `CreateTransactionUseCaseTest`: Valida criação e invariantes de transações.
- `GetFinancialSummaryUseCaseTest`: Valida agregação de saldo por categoria com tratamento de listas vazias e proteção contra NullPointerException.
- `ListTransactionsUseCaseTest`: Valida filtros combinados por tipo e categoria.
- `TransactionControllerIT`: Valida criação, listagem e falhas de validação DTO (retornando `urn:problem:validation-error`).
- `BudgetCommandControllerIT`: Valida orquestração textual.
- `VoiceBudgetControllerIT`: Valida fluxo de upload multipart de áudio, truncamento de 200 caracteres e percent-encoding RFC 3986 (%20) no cabeçalho `X-Transcribed-Text`.

Para executar toda a suíte de testes:
```bash
./gradlew test
```
