# 📋 TaskManager - Assistente Inteligente de Tarefas com Spring AI

Projeto desenvolvido como solução do **Desafio de Projeto Spring Boot / Spring AI** da Digital Innovation One (DIO).

O **TaskManager** é uma API em Java/Spring Boot estruturada com a arquitetura **Domain-Driven Design (DDD)** que integra recursos de **Inteligência Artificial (Spring AI Tool Calling)** para gerenciamento inteligente de tarefas por comandos de linguagem natural.

---

## 🎯 Objetivo do Projeto

Aplicar o ecossistema **Spring AI** mantendo o isolamento de camadas arquiteturais (DDD). 

A aplicação permite criar, listar, atualizar e resumir tarefas através de requisições REST tradicionais ou interagindo diretamente com um modelo de linguagem (LLM - OpenAI/GPT), que decide e invoca as ferramentas da aplicação (`@Tool`) automaticamente.

---

## 🌟 Melhorias de Valor Implementadas

1. **Arquitetura DDD Refatorada**:
   - Organização estrita de responsabilidades em `domain`, `application` e `infrastructure`.
2. **Resumo & Estatísticas Inteligentes de Tarefas (`GetTaskSummaryUseCase`)**:
   - Funcionalidade que consolida total de tarefas, pendentes, em progresso e concluídas.
3. **Integração de Ferramentas IA (`TaskTools`)**:
   - Método anotado com `@Tool` (`obterResumoTarefas`) permitindo perguntar à IA: *"Como está o resumo das minhas tarefas hoje?"*.

---

## 🏛️ Estrutura do Projeto (DDD)

```text
src/main/java/bootcamp/taskmanager/
├── domain/                         # Camada de Domínio (Regras de Negócio)
│   ├── Task.java                   # Entidade Principal
│   ├── TaskId.java                 # Objeto de Valor (UUID)
│   ├── TaskStatus.java             # Enum de Estados (PENDING, IN_PROGRESS, COMPLETED)
│   └── TaskRepository.java         # Contrato da Camada de Dados
│
├── application/                    # Camada de Aplicação (Casos de Uso)
│   ├── CreateTaskUseCase.java
│   ├── ListTasksUseCase.java
│   ├── UpdateTaskStatusUseCase.java
│   ├── GetTaskSummaryUseCase.java
│   ├── dto/                        # DTOs (CreateTaskCommand, TaskResponse, TaskSummary)
│   └── tools/
│       └── TaskToolsConfig.java    # Ferramentas expostas ao Spring AI (@Bean + Function)
│
└── infrastructure/                 # Camada de Infraestrutura (Adapters & HTTP)
    ├── repository/
    │   └── InMemoryTaskRepository.java # Repositório JPA/Em-memória
    └── http/
        ├── TaskController.java     # Endpoints REST Tradicionais
        └── TaskAiController.java   # Endpoint de Interação por Linguagem Natural (/api/ai/chat)
```

---

## ⚡ Como Executar o Projeto

### Pré-requisitos
- **Java 21** instalado
- **Gradle** (incluso via Gradle Wrapper `./gradlew`)

### 1. Configurar Chave de API (Spring AI / OpenAI)
No arquivo `src/main/resources/application.yml` ou definindo a variável de ambiente:

```bash
export OPENAI_API_KEY="sua-chave-openai-aqui"
```

### 2. Compilar e Iniciar a Aplicação

```bash
./gradlew bootRun
```

A API estará rodando em `http://localhost:8080`.

---

## 🧪 Endpoints para Teste

### 1. Criar Tarefa (REST)
- **POST** `/api/tasks`
- **Body**:
```json
{
  "title": "Estudar Spring AI",
  "description": "Praticar integração de Tool Calling no desafio DIO"
}
```

### 2. Obter Resumo de Estatísticas (REST - Melhoria de Valor)
- **GET** `/api/tasks/summary`
- **Resposta**:
```json
{
  "totalTasks": 3,
  "pendingTasks": 2,
  "inProgressTasks": 0,
  "completedTasks": 1
}
```

### 3. Interagir com a IA (Spring AI - Tool Calling)
- **POST** `/api/ai/chat`
- **Body**:
```json
{
  "message": "Crie uma tarefa para preparar a apresentação do bootcamp da DIO"
}
```
- **Resposta da IA**:
> *"Tarefa 'Preparar a apresentação do bootcamp da DIO' criada com sucesso!"*

- **Pergunta sobre o Resumo**:
```json
{
  "message": "Qual é o resumo das minhas tarefas atuais?"
}
```
- **Resposta da IA**:
> *"Você possui um total de 4 tarefas: 3 pendentes e 1 concluída."*
