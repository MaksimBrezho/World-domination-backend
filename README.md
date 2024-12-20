# World Domination Backend

Добро пожаловать в репозиторий бэкенда для игры **World Domination**! Этот проект обеспечивает серверную часть браузерной игры, где игроки соревнуются за стратегическое доминирование в мире. Бэкенд построен с использованием **Spring Boot**, **PostgreSQL** и **JWT-аутентификации** для обеспечения безопасности.

## Структура проекта

Краткое описание структуры репозитория:

### Основные директории и файлы

- **`.git`**: Файлы системы контроля версий Git.
- **`.idea`**: Настройки для IntelliJ IDEA.
- **`.mvn`**: Maven Wrapper для упрощённого управления сборкой.
- **`README.md`**: Этот файл с описанием проекта.
- **`pom.xml`**: Конфигурация Maven-проекта.
- **`src/main/java/com/brezho/world/domination`**:
    - **`controllers`**: REST-контроллеры для обработки запросов (аутентификация, игровая логика, тестирование).
    - **`exceptions`**: Пользовательские исключения для обработки ошибок.
    - **`game`**: Основная игровая логика (игроки, команды, роли, юниты, санкции и параметры игры).
    - **`models`**: Сущности для базы данных (пользователи, роли и т.д.).
    - **`payload`**:
        - **`request`**: DTO для входящих запросов (например, LoginRequest, SignupRequest).
        - **`response`**: DTO для ответов API (например, JwtResponse, MessageResponse).
    - **`repository`**: Репозитории для взаимодействия с PostgreSQL.
    - **`security`**: Модуль безопасности (конфигурация JWT, фильтры, сервисы аутентификации).
- **`src/main/resources`**: Конфигурационные файлы (например, `application.properties`).
- **`src/test`**: Тесты для проверки функциональности приложения.

## Основные возможности

1. **JWT-аутентификация**: Защита API с использованием токенов.
2. **Управление ролями**: Определение и назначение ролей (игрок, администратор и др.).
3. **Игровая логика**:
    - Управление игроками, командами, юнитами и другими игровыми сущностями.
    - Настраиваемые параметры игры.
4. **Интеграция с PostgreSQL**: Хранение данных в реляционной базе.
5. **Модульный REST API**: Простота расширения и тестирования.

## Требования

- **Java 17+**
- **Maven 3.8+**
- **PostgreSQL** (конфигурация в `application.properties`)

## Как запустить

1. Клонируйте репозиторий:
   ```bash
   git clone https://github.com/MaksimBrezho/World-domination-backend.git
   ```
2. Перейдите в папку проекта:
   ```bash
   cd World-domination-backend
   ```
3. Соберите проект:
   ```bash
   ./mvnw clean install
   ```
4. Запустите сервер:
   ```bash
   ./mvnw spring-boot:run
   ```
5. API будет доступно по адресу `http://localhost:8080`.
