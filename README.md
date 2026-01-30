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

 # Core:
### Основная группа сервисов
## Бизнес-сервисы:
- `category-service` - управление категориями событий
- `compilation-service` - управление подборками событий
- `event-service` - управление событиями
- `review-service` - управление отзывами
- `request-service` - управление заявками на участие
- `user-service` - управление пользователями
- `stats-server` - сервис статистики
## ⚙️ Core Services Configuration

**Общая конфигурация каждого core сервиса**. StatsClient + Load Balancing + таймауты.

### ReviewConfig (аналогично во всех сервисах)

```java
@Configuration
public class ReviewConfig {

    @Bean @LoadBalanced
    public RestClient.Builder loadBalancedRestClientBuilder() {
        return RestClient.builder();
    }

    @Bean
    public StatsClient statsClient(@LoadBalanced RestClient.Builder restClientBuilder) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000);  // 5s
        factory.setReadTimeout(5000);     // 5s

        RestClient restClient = restClientBuilder
                .baseUrl("http://stats-server")  // Service Discovery
                .requestFactory(factory)
                .build();

        return new StatsClient(restClient);
    }
}
```

### Ключевые компоненты конфигурации

| Компонент            | Назначение                   | Значение     |
|---------------------|------------------------------|--------------|
| `@LoadBalanced`     | **Spring Cloud LoadBalancer**| Автоматический выбор инстанса `stats-server` |
| `baseUrl("http://stats-server")` | **Service Discovery** | Eureka/Consul резолвинг имени сервиса |
| `ConnectTimeout=5s` | **TCP подключение**          | Защита от "зависших" Stats Server |
| `ReadTimeout=5s`    | **Чтение ответа**            | Защита от долгих запросов статистики |
| `RestClient`        | **HTTP 2.0 клиент**          | Современная замена RestTemplate |


Для всех сервисов используются Dockerfile и bootstrap схожего вида

```dockerfile
FROM eclipse-temurin:21-jdk-alpine
COPY target/xxx-service-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
```

```yaml
spring:
  application:
    name: xxx-service
  config:
    import: "configserver:"

  cloud:
    config:
      discovery:
        enabled: true
        serviceId: config-server

eureka:
  client:
    serviceUrl:
      defaultZone: http://localhost:8761/eureka/
```
где вместо 'xxx' используется название сервиса

Eureka запускается на порту 8761 и предоставляет адреса другим сервисам, включая cloud config и gateway-server

## 📂 User Service 

**Управление пользователями системы**. Создание, поиск, валидация и удаление пользователей.

### Основной функционал
- **CRUD операций** с пользователями (admin)
- **Валидация существования** для других сервисов (Feign клиенты)
- **Уникальность email** при регистрации
- **Пагинация** при получении списка пользователей

### Endpoints (через Gateway: `/admin/users`)

| Метод | Путь | Описание | Параметры |
|-------|------|----------|-----------|
| `GET` | `/admin/users` | Список пользователей | `?ids=1,2,3&from=0&size=10` |
| `GET` | `/admin/users/client/exist/{userId}` | Проверить существование | `-` |
| `GET` | `/admin/users/client/{userId}` | Пользователь (short) | `-` |
| `POST` | `/admin/users` | Создать пользователя | `NewUserRequest` |
| `DELETE` | `/admin/users/{userId}` | Удалить пользователя | `-` |

### Модели данных

**DTO:**
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



### Клиент сервиса:
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

 **Справочник категорий событий**  
 Микросервис для управления категориями событий в системе Explore With Me.

### Основной функционал
- **Полный CRUD** категорий (admin)
- **Публичный доступ** к справочнику с пагинацией
- **Валидация уникальности имени** категории
- **Блокировка удаления** при наличии связанных событий (Feign → Event Service)
- **Транзакционная целостность**

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

**Entity:**
```java
@Entity
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;
    @Column(nullable = false, unique = true)
    private String name;
}
```

## 📂 Request Service

**Управление заявками на участие в событиях**  
Создание, отмена, массовое подтверждение/отклонение.

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

**DTO:**
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

public enum RequestStatus {
    PENDING, CONFIRMED, REJECTED, CANCELED;

    @Override
    public String toString() {
        return name();
    }
}
```

### Клиент сервиса:
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

**Управление отзывами на события**  
Сложная бизнес-логика с проверками участия, статуса события и прав доступа.

### Основной функционал
- **Мощный поиск** для админов (Spring Data JPA Specifications)
- **Полный CRUD** своих отзывов (private) с жесткими проверками
- **Публичный доступ** к отзывам событий с пагинацией
- **Множественные Feign интеграции** (User/Event/Request сервисы)
- **Сложная валидация** перед созданием отзыва

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

**DTO:**
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

## 📂 Compilation-Service

**Управление подборками событий**  
Создание тематических коллекций событий для главной страницы и рекомендаций.

### Основной функционал
- **CRUD подборок** событий (только admin)
- **Публичный просмотр** с фильтрацией по закреплению (`pinned`)
- **Пагинация** списков подборок
- **Интеграция** с Event Service для получения событий в подборке

### Endpoints (Gateway: /admin/compilations, /compilations)

| Метод | Путь | Описание | Доступ | Параметры |
|-------|------|----------|--------|-----------|
| `POST` | `/admin/compilations` | Создать подборку | Admin | `NewCompilationDto` |
| `DELETE` | `/admin/compilations/{compId}` | Удалить подборку | Admin | - |
| `PATCH` | `/admin/compilations/{compId}` | Обновить подборку | Admin | `UpdateCompilationRequest` |
| `GET` | `/compilations` | Список подборок | Public | `?pinned=true&from=0&size=10` |
| `GET` | `/compilations/{compId}` | Подборка по ID | Public | - |

**Entity:**
```java
@Entity
@Table(name = "compilations")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
public class Compilation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "compilation_id")
    Long id;
    Boolean pinned;
    @Size(max = 50)
    @Column(name = "title", nullable = false)
    String title;
    @ElementCollection(targetClass = Long.class)
    @CollectionTable(
            name = "compilation_events",
            joinColumns = @JoinColumn(name = "compilation_id")
    )
    @Builder.Default
    @Column(name = "event_id")
    private Set<Long> eventsId = new HashSet<>();
}
```

**DTO:**
```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompilationDto {
    private Set<EventShortDto> events;
    private Long id;
    private Boolean pinned;
    private String title;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewCompilationDto {
    private Set<Long> events;
    private Boolean pinned = false;
    @NotBlank
    @Size(min = 1, max = 50)
    private String title;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCompilationRequest {
    private Set<Long> events;
    private Boolean pinned;
    @Size(max = 50)
    private String title;
}
```

## 📂 Event-Service

**Сердце платформы**  
Полный жизненный цикл событий + мощный публичный поиск + Stats интеграция.

### Основной функционал
- **Мощнейший поиск** с 8+ фильтрами (Specifications)
- **Полный CRUD** (Admin + Private) с публикацией/отменой
- **StatsClient** — подсчет просмотров для каждого события
- **4 Feign клиента** (User/Category/Request/Stats)
- **Клиентские методы** для других сервисов

### Endpoints (Gateway: /admin/events, /users/{userId}/events, /events)

| Метод | Путь | Описание | Доступ | Параметры |
|-------|------|----------|--------|-----------|
| `GET` | `/admin/events` | Список событий | Admin | `users, states, categories, rangeStart, rangeEnd, from, size` |
| `PATCH` | `/admin/events/{eventId}` | Обновить | Admin | `UpdateEventAdminRequest` |
| `GET` | `/users/{userId}/events` | Мои события | Private | `from=0, size=10` |
| `POST` | `/users/{userId}/events` | Создать событие | Private | `NewEventDto` |
| `GET` | `/users/{userId}/events/{eventId}` | Мое событие | Private | - |
| `PATCH` | `/users/{userId}/events/{eventId}` | Обновить свое | Private | `UpdateEventUserRequest` |
| `GET` | `/events` | Поиск событий | Public | `text, categories, paid, rangeStart/End, onlyAvailable, sort, from, size` |
| `GET` | `/events/{id}` | Событие по ID | Public | - |

### Модели данных

**Entity:**
```java
@Entity
@Table(name = "events")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    Long id;
    @Column(name = "annotation", length = 2000)
    String annotation;
    @Column(name = "category_id", nullable = false)
    Long categoryId;
    @Transient
    private Long views;
    @Column(name = "confirmed_requests")
    Integer confirmedRequests;
    @Column(name = "created_on", nullable = false)
    LocalDateTime createdOn;
    @Column(name = "published_on")
    LocalDateTime publishedOn;
    @Column(name = "description", length = 7000)
    String description;
    @Column(name = "event_date")
    LocalDateTime eventDate;
    @Column(name = "initiator_id", nullable = false)
    Long initiatorId;
    @Embedded
    Location location;
    Boolean paid;
    @Column(name = "participant_limit")
    Integer participantLimit;
    @Enumerated(value = EnumType.STRING)
    EventState state;
    @Column(name = "request_moderation")
    Boolean requestModeration;
    String title;
}

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Location {
    Float lat;
    Float lon;
}
```

**DTO:**
```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventFullDto {
    private Long id;
    private String annotation;
    private CategoryDto category;
    private Long views;
    private Integer confirmedRequests;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdOn;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishedOn;
    private String description;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    private UserShortDto initiator;
    private LocationDto location;
    private Boolean paid;
    private Integer participantLimit;
    private String state;
    private Boolean requestModeration;
    private String title;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventShortDto {
    private Long id;
    private String annotation;
    private CategoryDto category;
    private Long views;
    private Integer confirmedRequests;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    private UserShortDto initiator;
    private Boolean paid;
    private String title;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocationDto {
    @Min(-90)
    @Max(90)
    @NotNull
    private Float lat;
    @Min(-180)
    @Max(180)
    @NotNull
    private Float lon;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewEventDto {
    @NotBlank
    @Length(max = 2000, min = 20)
    private String annotation;
    @NotNull
    @Positive
    private Long category;
    @NotBlank
    @Length(max = 7000, min = 20)
    private String description;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    @NotNull
    @Valid
    private LocationDto location;
    private Boolean paid = false;
    @PositiveOrZero
    private Integer participantLimit = 0;
    private Boolean requestModeration = true;
    @NotNull
    @Length(min = 3, max = 120)
    private String title;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEventAdminRequest {
    @Size(min = 20, max = 2000)
    private String annotation;
    private Long category;
    @Size(min = 20, max = 7000)
    private String description;
    @Future
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    @Valid
    private LocationDto location;
    private Boolean paid;
    @PositiveOrZero
    private Integer participantLimit;
    private Boolean requestModeration;
    private String stateAction;
    @Size(min = 3, max = 120)
    private String title;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEventUserRequest {
    @Length(min = 20, max = 2000)
    private String annotation;
    private Long category;
    @Length(min = 20, max = 7000)
    private String description;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    @Valid
    private LocationDto location;
    private Boolean paid;
    @PositiveOrZero
    private Integer participantLimit;
    private Boolean requestModeration;
    private String stateAction;
    @Size(min = 3, max = 120)
    private String title;
}

public enum EventState {
    PENDING, PUBLISHED, CANCELED;

    @Override
    public String toString() {
        return name();
    }
}
```

### Клиент сервиса:
```java
@FeignClient(
        name = "event-service",
        path = "/events"
)
public interface EventServiceClient {
    @RequestMapping(
            method = RequestMethod.HEAD,
            value = "/categories/{catId}/exists"
    )
    Boolean categoryHasEvents(@PathVariable Long catId);
    @GetMapping("/client/short/{id}")
    EventShortDto getEventShortDtoByIdClient(@PathVariable @Positive Long id);
    @GetMapping("/client/full/{id}")
    EventFullDto getEventFullDtoByIdClient(@PathVariable @Positive Long id);
    @GetMapping("/client/validate/{eventId}")
    void validateEventExistingById(@PathVariable @Positive Long eventId);
    @GetMapping("/client/validate/category/{categoryId}")
    void validateCategoryHasNoEvents(
            @PathVariable @Positive Long categoryId);
    @GetMapping("/client/find/all")
    Set<EventShortDto> getEventShortDtoSetByIds(@RequestParam Set<Long> eventIds);
}
```

## 📦 Interaction-API (библиотека)

Содержит в себе две главные директории, в первой (exception) - ошибки валидации,
во второй (model) - dto конкретных сущностей и их клиентов.

### Назначение:
- **Единые контракты** между всеми сервисами core
- **DTO для телеметрии** — передача информации и работа с сервисами
- **Переиспользуемые модели** для всех микросервисов

#Stats:
## 📊 Сервисы статистики:
- `stats-client` - **HTTP-клиент для Stats Server** (hit/getStats)
- `stats-server` - **Сервер статистики** (сбор/хранение/анализ посещений)
- `stats-dto` - **DTO модели** (EndpointHitDto, StatResponseDto)

## 📈 Stats-Client:
**Клиент для взаимодействия со Stats Server**. Отслеживание хитов и получение статистики посещений.

### Методы

| Метод | Описание | Параметры |
|-------|----------|-----------|
| `hit(EndpointHitDto)` | Отправить хит (посещение) | `app, uri, ip, timestamp` |
| `getStats(start, end, uris, unique)` | Получить статистику | `даты, список URI, уникальные IP` |

### Использование

**Отправка хита (в каждом контроллере Public):**
```java
@Autowired StatsClient statsClient;

@GetMapping("/events")
public List<EventShortDto> getEvents(...) {
    // логика
    EndpointHitDto hit = EndpointHitDto.builder()
        .app("ewm-main-service")
        .uri("/events")
        .ip(httpRequest.getRemoteAddr())
        .timestamp(LocalDateTime.now())
        .build();
    statsClient.hit(hit); // асинхронно
    return events;
}
```

## 📦 Stats DTO Module (библиотека)

**Общие модели данных для Stats Client ↔ Stats Server**.

### Назначение:
- **Единые контракты** между Stats Client (во всех сервисах) и Stats Server
- **DTO для телеметрии** — передача информации о посещениях
- **Переиспользуемые модели** для всех микросервисов

## 📈 Stats Server

**Центральный сервер телеметрии**. Собирает и агрегирует статистику посещений всех микросервисов.

### Endpoints

| Метод | Путь | Описание | Параметры |
|-------|------|----------|-----------|
| `POST` | `/hit` | **Логировать посещение** | `EndpointHitDto` (app, uri, ip, timestamp) |
| `GET` | `/stats` | **Получить статистику** | `start, end, uris?, unique=false` |

### Функциональность

**1. Сбор хитов (каждый публичный запрос):**  
**2. Аналитика посещений:**  

### Модель данных (Stat)
```java
@Entity
public class Stat {
    Long id;
    String app;   
    String uri;   
    String ip;   
    LocalDateTime timestamp;
}
```
