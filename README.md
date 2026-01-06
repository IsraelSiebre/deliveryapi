# Delivery API - Documentação Técnica

## 📋 Visão Geral

A **Delivery API** é uma aplicação backend robusta desenvolvida em **Java 21+** utilizando o **Spring Boot 3.5.3**. Ela fornece uma solução completa para gerenciamento de entregas, incluindo autenticação de usuários, gerenciamento de pedidos, produtos, endereços e administração do sistema.

## 🏗️ Arquitetura

### Padrão de Projeto
- **MVC (Model-View-Controller)**
- **Repository Pattern** para acesso a dados
- **DTO Pattern** para transferência de dados
- **JWT** para autenticação stateless

### Tecnologias Principais
- **Java 21+**
- **Spring Boot 3.5.3**
- **Spring Security** com JWT
- **Spring Data JPA**
- **Hibernate**
- **PostgreSQL**
- **Maven**
- **Lombok** para redução de boilerplate

## 📁 Estrutura do Projeto

```
com.br.deliveryapi/
├── config/           # Configurações da aplicação
├── controller/       # Controladores REST
├── dto/             # Data Transfer Objects
│   ├── address/
│   ├── admin/
│   ├── client/
│   ├── order/
│   ├── product/
│   └── user/
├── entity/          # Entidades JPA
├── enums/           # Enumeradores
├── exception/       # Exceções customizadas
├── handler/         # Manipuladores de exceção
├── repository/      # Interfaces JPA Repository
├── security/        # Configurações de segurança
└── service/         # Lógica de negócio
```

## 🔐 Sistema de Autenticação

### Fluxo de Autenticação
1. **Registro**: `/auth/register/client` ou `/auth/register/admin`
2. **Login**: `/auth/login` (retorna JWT token)
3. **Autorização**: Token Bearer no header `Authorization`
4. **Logout**: `/auth/logout` (blacklist do token)

### Segurança Implementada
- **BCrypt** para hash de senhas
- **JWT** com expiração de 1 hora
- **Token Blacklist** para logout
- **Autorização baseada em roles** (ADMIN, CLIENT)
- **CSRF protection disabled** (API stateless)
- **Sessões stateless**

## 👥 Modelos de Dados

### 1. **User** (Classe Abstrata)
- `id`, `name`, `email`, `password`, `phone`, `role`

### 2. **Admin** (Herda de User)
- Gerencia produtos e pedidos

### 3. **Client** (Herda de User)
- `address` (relacionamento com Address)

### 4. **Address**
- Dados completos de endereço com validação de CEP

### 5. **Product**
- `name`, `description`, `price`, `available`

### 6. **Order**
- `client`, `status`, `paymentMethod`, `price`, `deliveryOption`, `items`

### 7. **OrderItem**
- `order`, `product`, `quantity`, `totalPrice`

## 🚀 Endpoints da API

### 📍 Autenticação (`/auth`)
| Método | Endpoint | Permissão | Descrição |
|--------|----------|-----------|-----------|
| POST | `/login` | Público | Autenticação de usuário |
| POST | `/register/client` | Público | Registro de cliente |
| POST | `/register/admin` | Público | Registro de administrador |
| POST | `/logout` | Autenticado | Logout e blacklist de token |

### 👤 Clientes (`/client`)
| Método | Endpoint | Permissão | Descrição |
|--------|----------|-----------|-----------|
| GET | `/{id}` | ROLE_CLIENT | Buscar cliente por ID |
| PUT | `/{id}` | ROLE_CLIENT | Atualizar cliente |
| DELETE | `/{id}` | ROLE_CLIENT | Excluir cliente |

### 👨‍💼 Administradores (`/admin`)
| Método | Endpoint | Permissão | Descrição |
|--------|----------|-----------|-----------|
| GET | `/` | ROLE_ADMIN | Listar todos os administradores |
| GET | `/{id}` | ROLE_ADMIN | Buscar administrador por ID |
| PUT | `/{id}` | ROLE_ADMIN | Atualizar administrador |
| DELETE | `/{id}` | ROLE_ADMIN | Excluir administrador |

### 🏠 Endereços (`/address`)
| Método | Endpoint | Permissão | Descrição |
|--------|----------|-----------|-----------|
| GET | `/{id}` | ROLE_CLIENT | Buscar endereço por ID |
| PUT | `/{id}` | ROLE_CLIENT | Atualizar endereço |
| DELETE | `/{id}` | ROLE_CLIENT | Excluir endereço |

### 🛒 Pedidos (`/orders`)
| Método | Endpoint | Permissão | Descrição |
|--------|----------|-----------|-----------|
| POST | `/` | ROLE_CLIENT | Criar pedido |
| GET | `/` | ROLE_CLIENT, ROLE_ADMIN | Listar todos os pedidos |
| GET | `/{id}` | ROLE_CLIENT, ROLE_ADMIN | Buscar pedido por ID |
| PUT | `/{id}` | ROLE_CLIENT | Atualizar pedido |
| PATCH | `/{id}/{status}` | ROLE_ADMIN | Atualizar status do pedido |
| DELETE | `/{id}` | ROLE_CLIENT | Excluir pedido |
| GET | `/client/{id}` | ROLE_CLIENT, ROLE_ADMIN | Pedidos por cliente |
| GET | `/status/{status}` | ROLE_CLIENT, ROLE_ADMIN | Pedidos por status |
| POST | `/period` | ROLE_CLIENT, ROLE_ADMIN | Pedidos por período |
| POST | `/{orderId}/items` | ROLE_CLIENT | Adicionar item ao pedido |
| DELETE | `/{orderId}/items/{itemId}` | ROLE_CLIENT | Remover item do pedido |
| PATCH | `/{orderId}/items/{itemId}/quantity` | ROLE_CLIENT | Atualizar quantidade do item |
| POST | `/{id}/checkout` | ROLE_CLIENT | Finalizar pedido |

### 📦 Produtos (`/product`)
| Método | Endpoint | Permissão | Descrição |
|--------|----------|-----------|-----------|
| POST | `/` | ROLE_ADMIN | Criar produto |
| GET | `/` | ROLE_ADMIN, ROLE_CLIENT | Listar todos os produtos |
| GET | `/{id}` | ROLE_ADMIN, ROLE_CLIENT | Buscar produto por ID |
| PUT | `/{id}` | ROLE_ADMIN | Atualizar produto |
| DELETE | `/{id}` | ROLE_ADMIN | Excluir produto |

## 🛡️ Sistema de Segurança

### Configuração em `SecurityConfig.java`
- **Stateless sessions**: `SessionCreationPolicy.STATELESS`
- **CSRF**: Desabilitado para API REST
- **Filtro JWT**: `JwtAuthenticationFilter`
- **Provider**: `DaoAuthenticationProvider`

### Roles e Permissões
- **ROLE_ADMIN**: Acesso completo ao sistema
- **ROLE_CLIENT**: Acesso a funcionalidades de cliente

## 🎯 Funcionalidades Avançadas

### 1. **Validação de Dados**
- Anotações `@Valid` em controllers
- Constraints como `@NotBlank`, `@Email`, `@NotNull`
- Validação customizada no CEP

### 2. **Tratamento de Exceções**
- `GlobalExceptionHandler` com `@ControllerAdvice`
- Respostas HTTP apropriadas
- Mensagens de erro claras

### 3. **Internacionalização**
- `LocaleConfig` com `SessionLocaleResolver`
- Locale padrão: `Locale.US`

### 4. **Documentação da API**
- **Swagger/OpenAPI** integrado
- Acesso em `/swagger-ui.html`

### 5. **Gerenciamento de Pedidos**
- Status do pedido (PENDING, PREPARING, DELIVERED, etc.)
- Cálculo automático de preços
- Gestão de itens do pedido
- Checkout com validação

## ⚙️ Configuração e Deploy

### Pré-requisitos
- Java 21 ou superior
- Maven 3.9+
- PostgreSQL 17+
- IntelliJ IDEA

### Configuração do Banco de Dados
```properties
# application.properties
spring.datasource.url=jdbc:postgresql://localhost:5432/SEU_BANCO_DE_DADOS
spring.datasource.username=SEU_USUARIO
spring.datasource.password=SUA_SENHA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

### Build e Execução
```bash
# Compilar
mvn clean compile

# Testar
mvn test

# Empacotar
mvn package

# Executar
java -jar target/delivery-api-0.0.1-SNAPSHOT.jar
```

## 🧪 Testes e Qualidade

### Validações Implementadas
- ✅ Validação de entrada de dados
- ✅ Tratamento de exceções
- ✅ Segurança JWT
- ✅ Autorização baseada em roles
- ✅ Uniqueness constraints no banco

### Boas Práticas
- **DTO Pattern** para separação de camadas
- **Services** para lógica de negócio
- **Repositories** para acesso a dados
- **Controllers** apenas para HTTP
- **Imutabilidade** com records (Java 14+)

## 🔧 Considerações Técnicas

### Performance
- FetchType.LAZY em relacionamentos
- Cache de tokens (blacklist em memória)
- Queries otimizadas com Spring Data JPA

### Segurança
- Senhas hashadas com BCrypt
- Tokens JWT com expiração
- Proteção contra replay attacks (blacklist)
- Validação de entrada em todos os endpoints

### Manutenibilidade
- Código limpo com Lombok
- Separação clara de responsabilidades
- Documentação via Swagger
- Tratamento consistente de erros
