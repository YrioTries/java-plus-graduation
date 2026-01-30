# Explore With Me - Инфраструктурные сервисы

[![Java](https://img.shields.io/badge/Java-21-blue)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.4-brightgreen)](https://spring.io/projects/spring-boot)
[![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2023.0.3-green)](https://spring.io/projects/spring-cloud)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-42.7.3-brightblue)](https://www.postgresql.org/)

## 📋 О проекте

Микросервисная платформа для организации мероприятий с системой управления событиями, категориями, пользователями и отзывами. Проект построен на **Spring Cloud** с использованием сервис-ориентированной архитектуры.

## 🏗️ Архитектура

### Инфраструктурные сервисы:
1. **Config Server** - централизованный сервис конфигурации
2. **Discovery Server (Eureka)** - сервис регистрации и обнаружения
3. **Gateway Server** - API Gateway для маршрутизации запросов

### Бизнес-сервисы:
- `category-service` - управление категориями событий
- `compilation-service` - управление подборками событий
- `event-service` - управление событиями
- `review-service` - управление отзывами
- `request-service` - управление заявками на участие
- `user-service` - управление пользователями
- `stats-server` - сервис статистики

### Предварительные требования
- Java 21 или выше
- Maven 3.8+
- PostgreSQL 15+
- Docker (опционально)

### Порядок запуска сервисов:
1. **Config Server** (порт случайный)
2. **Discovery Server** (порт: 8761)
3. **Gateway Server** (порт: 8080)
4. Бизнес-сервисы (случайный порт)

Для всех сервисов используются Dockerfile схожего вида

```dockerfile
FROM eclipse-temurin:21-jdk-alpine
COPY target/xxx-service-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]

```
где вместо 'xxx' используется название сервиса

## 🛠️ Технологический стек (из root pom.xml)

### ☕ Java & Build
- Java 21
- Maven 3.8+
- Spring Boot 3.3.4 (parent)

### 🌐 Spring Ecosystem
- Spring Boot 3.3.0
- Spring Cloud 2023.0.3
- Spring Data JPA
- Spring Web / Validation
- Spring Actuator

### 🗄️ Базы данных
- PostgreSQL 42.7.3
- H2 (tests)

### 🔌 Интеграции
- 📡 MapStruct 1.5.5.Final (DTO)
- 🔒 Lombok 1.18.32
- ✅ Jakarta Validation 3.0.2
- 📊 SpringDoc OpenAPI 2.6.0 (Swagger)

### 📡 Микросервисы & Messaging
- Spring Cloud Circuit Breaker (Resilience4j)
- Kafka Clients 3.6.1
- Avro 1.11.3
- gRPC 1.63.0 + Protobuf 3.23.4

### 📖 API Docs
- SpringDoc OpenAPI 2.6.0 (Swagger UI)

### 🧪 Тестирование & Quality
- JUnit 5 (Spring Boot Test)
- Hamcrest 2.2
- JaCoCo 0.8.12 (80%+ coverage)
- SpotBugs 4.8.5.0
- Checkstyle 10.3

### 🔨 Maven Plugins
- Compiler 3.11.0
- Surefire 3.1.2
- Avro Maven Plugin
- Protobuf Maven Plugin


## 📂 User Service 

**Управление пользователями системы**. Регистрация, поиск, валидация и удаление пользователей.

### Endpoints (через Gateway: `/admin/users`)

| Метод | Путь | Описание | Параметры |
|-------|------|----------|-----------|
| `GET` | `/admin/users` | Список пользователей | `?ids=1,2,3&from=0&size=10` |
| `GET` | `/admin/users/client/exist/{userId}` | Проверить существование | `-` |
| `GET` | `/admin/users/client/{userId}` | Пользователь (short) | `-` |
| `POST` | `/admin/users` | Создать пользователя | `NewUserRequest` |
| `DELETE` | `/admin/users/{userId}` | Удалить пользователя | `-` |

### Модели данных

**DTO**
```java
@Data public class UserDto {
Long id; String name; String email;
}

// Для создания
@Data public class NewUserRequest {
@NotBlank @Size(min=2, max=250) String name;
@Email @Size(min=6, max=254) String email;
}

// Короткая информация (для клиентов)
@Data public class UserShortDto {
Long id;
String name;
}
```
**Entity:**
```java
@Entity @Table(name = "users")
public class User {
    @Id @GeneratedValue Long id;
    @Column(unique = true) String email;
    String name;
}
```



### Клиент сервиса
```java
@FeignClient(name = "user-service", path = "/admin/users")
public interface UserServiceClient {
@GetMapping("/client/{userId}")
UserShortDto getUserShortDtoClientById(Long userId);

    @GetMapping("/client/exist/{userId}")
    void validateUserExistingById(Long userId);
}
```

# Category Service

Микросервис для управления категориями событий в системе Explore With Me. Предоставляет полный CRUD функционал для категорий через REST API с разделением на административные и публичные endpoints.

## 📋 Обзор

### Endpoints (через Gateway: /admin/categories, /categories)

| Метод | Путь | Описание | Параметры |
|-------|------|----------|-----------|
| POST | /admin/categories | Создать категорию | NewCategoryDto |
| PATCH | /admin/categories/{catId} | Обновить категорию | CategoryDto |
| DELETE | /admin/categories/{catId} | Удалить категорию | - |
| GET | /admin/categories | Список категорий | ?from=0&size=10 |
| GET | /categories | Список категорий (public) | ?from=0&size=10 |
| GET | /categories/{catId} | Категория по ID | - |


**Category Service** - сервис управления категориями событий, позволяющий создавать, обновлять, удалять и получать категории. Категории используются для классификации событий в системе (концерты, выставки, спортивные мероприятия и т.д.).

**Ключевые возможности:**
- Создание, обновление и удаление категорий (администратор/публичный)
- Просмотр всех категорий с пагинацией
- Получение категории по ID
- Интеграция с другими сервисами через Feign Client

### Модель данных:

```java
@Entity
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String name; // Название категории (уникальное)
}
```


