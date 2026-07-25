# 🎙️ Spring AI Budgeting API

Projeto desenvolvido como parte do **Bootcamp NTT DATA Java & IA** da **Digital Innovation One (DIO)**.

O objetivo deste projeto foi aplicar, na prática, os conceitos apresentados durante a trilha, utilizando **Spring Boot** e **Spring AI** para construir uma API capaz de interpretar comandos financeiros enviados por texto ou voz.

Além da implementação proposta no desafio, utilizei este projeto como uma oportunidade para aprofundar meus conhecimentos em **Java**, **Spring Boot**, **Spring AI**, organização em camadas, tratamento de exceções, testes automatizados e documentação técnica.

---

# 🎓 Informações de Entrega (Desafio DIO)

## 📌 O que o projeto faz?

A aplicação evolui uma API de orçamento financeiro para permitir o gerenciamento de receitas e despesas por meio de linguagem natural.

O usuário pode enviar comandos por:

- Texto;
- Áudio (voz).

No fluxo de voz, a aplicação:

1. Transcreve o áudio utilizando **OpenAI Whisper (Speech-to-Text)**;
2. Interpreta a intenção do usuário através do **Spring AI ChatClient**;
3. Executa automaticamente a regra de negócio por meio do **Tool Calling**;
4. Gera uma resposta em áudio utilizando **Text-to-Speech (TTS)**.

---

# 🚀 Tecnologias Utilizadas

- Java 21
- Spring Boot 3.3.5
- Spring AI 1.0.0-M5
- Gradle
- Lombok
- Jakarta Validation

### Modelos OpenAI

- GPT-4o-mini (Chat + Tool Calling)
- Whisper-1 (Speech-to-Text)
- TTS-1 (Text-to-Speech)

---

# ✅ Melhorias Implementadas

Além do projeto base proposto pela trilha, foram adicionadas melhorias como:

- Resumo financeiro filtrado por categoria;
- Tratamento global de exceções utilizando `ProblemDetail` (RFC 7807);
- Melhor organização da arquitetura em camadas;
- Tratamento adequado do cabeçalho HTTP contendo a transcrição de voz;
- Testes unitários e de integração;
- Documentação técnica completa do projeto.

---

# ▶️ Como Executar

```bash
git clone https://github.com/ericknunes93/spring-ai-task-assistant.git

cd spring-ai-task-assistant
```

Configure sua chave da OpenAI:

Linux/macOS

```bash
export OPENAI_API_KEY="sua-chave"
```

Windows CMD

```cmd
set OPENAI_API_KEY=sua-chave
```

PowerShell

```powershell
$env:OPENAI_API_KEY="sua-chave"
```

Execute:

```bash
./gradlew bootRun
```

A aplicação ficará disponível em:

```
http://localhost:8080
```

---

# 🧪 Como Testar

## Texto

```bash
curl -X POST http://localhost:8080/api/budget/text-command \
-H "Content-Type: application/json" \
-d '{"message":"Recebi meu salário de R$5000"}'
```

---

## Voz

```bash
curl -X POST http://localhost:8080/api/budget/voice-command \
-F "file=@audio.mp3"
```

---

## Resumo Financeiro

```bash
GET /api/transactions/summary?category=SALARIO
```

---

# 📚 O que aprendi

Durante o desenvolvimento deste projeto pude praticar e compreender melhor:

- desenvolvimento de APIs REST utilizando Spring Boot;
- organização do código em camadas;
- integração do Spring AI com modelos da OpenAI;
- utilização do Tool Calling para conectar modelos de IA às regras de negócio;
- transcrição de áudio utilizando Whisper;
- geração de respostas em áudio (Text-to-Speech);
- tratamento global de exceções;
- criação de testes unitários e de integração;
- documentação técnica utilizando README.

Este projeto representa uma etapa importante da minha evolução como desenvolvedor Java e servirá de base para aprofundar meus estudos em Spring Boot, arquitetura de software e Inteligência Artificial.

---

# 🏗️ Arquitetura

O projeto foi organizado em camadas para separar responsabilidades e facilitar futuras evoluções.

- **Domain** → entidades e regras de negócio;
- **Application** → casos de uso e serviços;
- **Infrastructure** → controllers, repositórios e integrações externas.

Essa organização foi inspirada nos conceitos apresentados durante o Bootcamp e utilizada como exercício de arquitetura para aplicações Spring Boot.

---

# 🏛️ Diagrama Arquitetural

```
Cliente HTTP
     │
     ▼
Controllers
     │
     ▼
SpeechToTextService (Whisper)
     │
     ▼
BudgetChatService
     │
     ▼
Spring AI ChatClient
     │
     ▼
FinancialToolsConfig
     │
     ▼
Use Cases
     │
     ▼
TransactionRepository
     │
     ▼
TextToSpeechService
     │
     ▼
Cliente
```

---

# 🤖 Funcionamento do Tool Calling

```
Prompt do usuário
        │
        ▼
OpenAI GPT-4o-mini
        │
Seleciona automaticamente a Tool
        │
        ▼
Use Case
        │
        ▼
Retorno para o modelo
        │
        ▼
Resposta final ao usuário
```

As ferramentas expostas ao Spring AI atuam apenas como adaptadores entre o modelo de IA e os casos de uso da aplicação, mantendo as regras de negócio desacopladas da camada de integração.

---

# 🛡️ Tratamento de Exceções

A aplicação utiliza um `GlobalExceptionHandler` responsável por tratar exceções comuns da API REST, como:

- JSON inválido;
- parâmetros inválidos;
- falhas de validação;
- upload de arquivos acima do limite;
- exceções de negócio.

Para os endpoints tradicionais são utilizadas respostas baseadas em `ProblemDetail (RFC 7807)`.

Nos fluxos de IA, o próprio modelo gera respostas amigáveis ao usuário quando ocorre alguma falha durante a execução de uma ferramenta.

---

# 💾 Persistência

Para manter o foco do desafio na integração com Spring AI, foi utilizada uma implementação em memória (`InMemoryTransactionRepository`).

Essa escolha reduz a complexidade da infraestrutura e permite concentrar o estudo na arquitetura da aplicação e na integração com Inteligência Artificial.

---

# 🧪 Testes

O projeto possui testes unitários e de integração cobrindo os principais fluxos da aplicação, incluindo:

- criação de transações;
- resumo financeiro;
- listagem de dados;
- validação de entradas;
- integração dos controllers;
- fluxo de comandos por texto;
- fluxo de comandos por voz.

Execução:

```bash
./gradlew test
```

Nota sobre caminhos do sistema:

Evite usar caminhos com caracteres não-ASCII (ex.: pastas com acentos) para armazenar o repositório e executar builds/testes. Ferramentas Java, Gradle e runners de teste podem falhar ao carregar classes quando o caminho contém caracteres especiais.

Se o repositório já estiver em um caminho com acentos, crie um junction (atalho de pasta) ou clone o repositório em um caminho ASCII. Exemplo (PowerShell):

```powershell
# cria C:\projects\taskmanager apontando para a pasta atual com acentos
New-Item -ItemType Junction -Path C:\projects\taskmanager -Target "C:\Users\erick\Documents\Projects\REPOSITÓRIOS\taskmanager"
```

Após isso, abra o projeto a partir do caminho ASCII (por exemplo C:\projects\taskmanager) ou execute os comandos Gradle a partir desse caminho.

---

---

# 🌱 Evoluções Futuras

Este projeto continuará evoluindo conforme avanço nos estudos de Java, Spring Boot e arquitetura de software.

Entre as melhorias planejadas estão:

- Mitigar riscos de Indirect Prompt Injection;
- Configurar CI/CD com GitHub Actions;
- Migrar a persistência para PostgreSQL utilizando Spring Data JPA;
- Adicionar autenticação com Spring Security e JWT;
- Documentar a API utilizando OpenAPI/Swagger;
- Containerizar a aplicação com Docker e Docker Compose;
- Ampliar a cobertura de testes automatizados;
- Publicar a aplicação em ambiente de nuvem.

---

# 👨💻 Autor

**Erick Oliveira**

Projeto desenvolvido durante o Bootcamp **NTT DATA Java & IA** da **Digital Innovation One**, como parte da minha jornada de aprendizado em Java, Spring Boot e Inteligência Artificial aplicada ao desenvolvimento de software.
