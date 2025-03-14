# OTP with Nikita 🔐

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-latest-blue)](https://www.postgresql.org/)
[![Redis](https://img.shields.io/badge/Redis-latest-red)](https://redis.io/)
[![Docker](https://img.shields.io/badge/Docker-supported-blue)](https://www.docker.com/)

Современная система для отправки и верификации одноразовых паролей (OTP) через SMS с использованием Spring Boot, PostgreSQL, Redis и интеграцией с SMS-провайдером Nikita.

## 📋 Функциональность

- Отправка OTP на указанный номер телефона
- Верификация OTP
- Настраиваемое время жизни OTP
- REST API с документацией Swagger
- Docker-поддержка для легкого развертывания

## ⚙️ Технический стек

- **Backend**: Java 17, Spring Boot
- **База данных**: PostgreSQL
- **Кэширование**: Redis
- **Документация API**: Swagger / OpenAPI 3
- **Контейнеризация**: Docker & Docker Compose

## 🚀 Быстрый старт

### Предварительные требования

- Java 17+
- Maven
- PostgreSQL
- Redis
- Docker и Docker Compose (опционально)

### Запуск через Docker (рекомендуется)

```bash
# Клонировать репозиторий
git clone 
cd otp-with-nikita

# Запустить с Docker Compose
docker-compose up --build

# Запустить в фоновом режиме
docker-compose up -d

# Остановить контейнеры
docker-compose down

# Просмотр логов
docker-compose logs -f

# Перезапуск отдельного сервиса
docker-compose restart app

# Запуск с пересборкой только определенного сервиса
docker-compose up --build app

# Проверка статуса сервисов
docker-compose ps
```

Приложение будет доступно по адресу: http://localhost:9898

### Ручная установка

1. **Клонирование репозитория**
   ```bash
   git clone 
   cd otp-with-nikita
   ```

2. **Настройка базы данных PostgreSQL**
   ```sql
   CREATE DATABASE otp_db;
   ```

3. **Настройка конфигурации**

   Отредактируйте `application.properties`:
   ```properties
   # Database
   spring.datasource.url=jdbc:postgresql://localhost:5432/otp_db
   spring.datasource.username=postgres
   spring.datasource.password=postgres

   # Redis
   spring.data.redis.host=localhost
   spring.data.redis.port=6379
   spring.data.redis.password=myredis

   # SMS Service
   smspro.api.url=https://smspro.nikita.kg/api
   smspro.login=ваш-логин
   smspro.password=ваш-пароль
   smspro.sender.id=ваш-sender-id
   ```

4. **Сборка и запуск**
   ```bash
   mvn clean install
   java -jar target/otp-with-nikita.jar
   ```

## 🐳 Docker Compose

Файл `docker-compose.yaml` содержит конфигурацию всех необходимых сервисов:

```yaml
version: '3.8'

services:
  app:
    build: .
    container_name: otp-app
    ports:
      - "9898:9898"
    environment:
      - SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/otp_db
      - SPRING_DATASOURCE_USERNAME=postgres
      - SPRING_DATASOURCE_PASSWORD=postgres
      - SPRING_DATA_REDIS_HOST=redis
      - SPRING_DATA_REDIS_PORT=6379
      - SPRING_DATA_REDIS_PASSWORD=myredis
    depends_on:
      - db
      - redis
    restart: always

  db:
    image: postgres:14-alpine
    container_name: otp-postgres
    ports:
      - "5432:5432"
    environment:
      - POSTGRES_DB=otp_db
      - POSTGRES_USER=postgres
      - POSTGRES_PASSWORD=postgres
    volumes:
      - postgres_data:/var/lib/postgresql/data
    restart: always

  redis:
    image: redis:7-alpine
    container_name: otp-redis
    ports:
      - "6379:6379"
    command: redis-server --requirepass myredis
    volumes:
      - redis_data:/data
    restart: always

volumes:
  postgres_data:
  redis_data:
```

## 📡 API Endpoints

### Отправка OTP

```http
POST /api/v1/auth/send-otp?phoneNumber={phoneNumber}
```

**Параметры**:
- `phoneNumber` (обязательный): Номер телефона в формате 996XXXXXXXXX

**Пример запроса**:
```bash
curl -X POST "http://localhost:9898/api/v1/auth/send-otp?phoneNumber=996xxxxxxxxx"
```

**Успешный ответ** (200 OK):
```json
{
  "success": true,
  "message": "OTP успешно отправлен"
}
```

### Верификация OTP

```http
POST /api/v1/auth/verify-otp?phoneNumber={phoneNumber}&otpCode={otpCode}
```

**Параметры**:
- `phoneNumber` (обязательный): Номер телефона, на который был отправлен OTP
- `otpCode` (обязательный): OTP-код для верификации

**Пример запроса**:
```bash
curl -X POST "http://localhost:9898/api/v1/auth/verify-otp?phoneNumber=996xxxxxxxxx&otpCode=123456"
```

**Успешный ответ** (200 OK):
```json
{
  "success": true,
  "message": "OTP успешно подтвержден"
}
```

## 📚 Документация API

- **Swagger UI**: http://localhost:9898/swagger-ui.html
- **OpenAPI JSON**: http://localhost:9898/v3/api-docs

## 📂 Структура проекта

```
otp-with-nikita/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── tashiev/
│   │   │           └── otpwithnikita/
│   │   │               ├── config/          # Конфигурационные классы
│   │   │               ├── controller/      # Контроллеры
│   │   │               ├── dto/             # Data Transfer Objects
│   │   │               ├── service/         # Сервисы
│   │   │               └── OtPwithNikitaApplication.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/                               # Тесты
├── docker-compose.yaml
├── pom.xml
└── README.md
```

## 📦 Зависимости

- Spring Boot Starter Web
- Spring Data JPA
- Spring Data Redis
- PostgreSQL Driver
- Jedis (Redis клиент)
- Lombok
- Springdoc OpenAPI (Swagger)

## 🔧 Конфигурация

### Настройка времени жизни OTP

По умолчанию OTP действителен в течение 5 минут. Для изменения этого значения отредактируйте:

```properties
otp.expiration.minutes=3
```

### Настройка длины OTP

По умолчанию длина OTP составляет 6 цифр. Для изменения:

```properties
otp.length=6
```

## 🛠️ Разработка

### Сборка проекта

```bash
mvn clean install
```

### Запуск тестов

```bash
mvn test
```

## 🔐 Безопасность

- OTP хранится в Redis с ограниченным временем жизни
- Защита от перебора с помощью ограничения количества попыток верификации
- Шифрование данных при передаче


## 📞 Контакты

Если у вас возникли вопросы или проблемы, пожалуйста, создайте issue в репозитории или свяжитесь с автором по [zholdoshbektashiev@gmail.com](mailto:email@example.com).