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
1. **Discovery Server** (порт: 8761) 
2. **Config Server** (порт случайный)
3. **Gateway Server** (порт: 8080)
4. **Бизнес-сервисы** (случайный порт)

Для всех сервисов используются Dockerfile схожего вида

```dockerfile
FROM eclipse-temurin:21-jdk-alpine
COPY target/xxx-service-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]

```
где вместо 'xxx' используется название сервиса

## 🛠️ Технологический стек

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

**Управление пользователями системы**. Создание, поиск, валидация и удаление пользователей.

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
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewUserRequest {
    @NotBlank
    @Size(min = 2, max = 250)
    private String name;
    @NotBlank
    @Email
    @Size(min = 6, max = 254)
    private String email;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String name;
    private String email;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserShortDto {
    private Long id;
    private String name;
}
```
**Entity:**
```java
@Entity
@Table(name = "users")
@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder(toBuilder = true)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    Long id;
    @Column(unique = true)
    String email;
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

## 📂 Request Service

**Управление заявками на участие в событиях**. Создание, отмена, массовое подтверждение/отклонение.

### Endpoints (Gateway: `/users/{userId}/**`)

| Метод | Путь | Описание | Параметры |
|-------|------|----------|-----------|
| `GET` | `/{userId}/events/{eventId}/requests` | Заявки на событие | - |
| `GET` | `/{userId}/requests` | Заявки пользователя | - |
| `POST` | `/{userId}/requests?eventId=1` | Создать заявку | `eventId` |
| `PATCH` | `/{userId}/events/{eventId}/requests` | Массовое обновление статуса | `EventRequestStatusUpdateRequest` |
| `PATCH` | `/{userId}/requests/{requestId}/cancel` | Отменить заявку | - |
| `GET` | `/client/count?eventIds=1,2&requestStatus=CONFIRMED` | Подсчет подтвержденных | `eventIds`, `status` |
| `GET` | `/{userId}/client/event/{eventId}` | Заявка по user+event | - |

### Модели данных

**Entity:**
```java
@Entity @Table("participation_requests")
@Builder public class ParticipationRequest {
    @Id 
    Long id;
    LocalDateTime created;
    Long eventId;
    Long requesterId;
    RequestStatus status; // PENDING, CONFIRMED, REJECTED, CANCELED
}
```

**DTO**
```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParticipationRequestDto {
    private Long id;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created;
    private Long event;
    private Long requester;
    private String status;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventRequestStatusUpdateRequest {
    private Set<Long> requestIds;
    private RequestStatus status;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventRequestStatusUpdateResult {
    private List<ParticipationRequestDto> confirmedRequests;
    private List<ParticipationRequestDto> rejectedRequests;
}
```


### Клиент сервиса
```java
@FeignClient(name = "request-service", path = "/users")
public interface RequestServiceClient {
    @GetMapping("/client/count")
    Map<Long, List<ParticipationRequestDto>> getConfirmedRequestsCount(
            List<Long> eventIds, RequestStatus status);

    @GetMapping("/{userId}/client/event/{eventId}")
    ParticipationRequestDto getByUserAndEvent(Long userId, Long eventId);
}
```

## 📂 Review Service

### Endpoints (Gateway: /admin/reviews, /users/{userId}/reviews, /reviews)

| Метод | Путь | Описание | Доступ | Параметры |
|-------|------|----------|--------|-----------|
| `GET` | `/admin/reviews` | Поиск отзывов | Admin | `text, users, events, from=0, size=10` |
| `DELETE` | `/admin/reviews/{reviewId}` | Удалить отзыв | Admin | - |
| `POST` | `/users/{userId}/reviews/events/{eventId}` | Создать отзыв | Private | `NewReviewDto` |
| `PATCH` | `/users/{userId}/reviews/{reviewId}` | Обновить отзыв | Private | `UpdateReviewDto` |
| `DELETE` | `/users/{userId}/reviews/{reviewId}/events/{eventId}` | Удалить свой отзыв | Private | - |
| `GET` | `/users/{userId}/reviews/{reviewId}` | Мой отзыв по ID | Private | - |
| `GET` | `/users/{userId}/reviews` | Мои отзывы | Private | - |
| `GET` | `/reviews/{eventId}` | Отзывы события | Public | `?from=0&size=10` |

### Модели данных

**Entity:**
```java
@Entity
@Table(name = "reviews")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long id;
    @Column(name = "text", nullable = false)
    private String text;
    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Long eventId;
    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private Long authorId;
    @Column(name = "created_on", nullable = false)
    @CreationTimestamp
    private LocalDateTime createdOn;
    @UpdateTimestamp
    @Column(name = "last_updated_on")
    private LocalDateTime lastUpdatedOn;
}
```

**DTO**
```java
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NewReviewDto {
    @NotBlank
    @Size(min = 2, max = 2000)
    private String text;
}

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewDto {
    private Long id;
    private String text;
    private Long eventId;
    private Long authorId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdOn;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastUpdatedOn;
}

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateReviewDto {
    @NotBlank
    @Size(min = 2, max = 2000)
    private String text;
}
```


