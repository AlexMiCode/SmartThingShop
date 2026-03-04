# SmartThingShop E-Commerce Microservices Platform

Проект реализован по ТЗ из `ТЕХНИЧЕСКОЕ ЗАДАНИЕ.pdf` с вашими изменениями:
- формат выполнения: **одиночный проект**
- домен магазина: **товары для умного дома**
- каталог товаров заполнен **интернет-заглушками** (URL изображений + seed товары)

## Архитектура

Микросервисы:
1. `service-registry` (Eureka)
2. `config-server` (Spring Cloud Config)
3. `api-gateway` (Spring Cloud Gateway + JWT Resource Server)
4. `user-service`
5. `product-service`
6. `order-service`
7. `notification-service`

Технологии:
- Java 17
- Spring Boot 3.5.0
- Spring Cloud 2025.0.0
- PostgreSQL (database-per-service)
- Redis (кэш `GET /api/products`)
- Resilience4j (Circuit Breaker + fallback)
- Docker / Docker Compose
- Kubernetes (Deployment/Service/ConfigMap/Secret/HPA)
- GitHub Actions CI/CD
- OpenAPI/Swagger (`springdoc`)

## Функциональность

### User Service
- Создание, получение по id, список, обновление, удаление
- Валидация входных данных
- Логирование операций
- Глобальный обработчик исключений

### Product Service
- CRUD товаров
- Фильтрация по цене (`GET /api/products?minPrice=&maxPrice=`)
- Изменение остатка (`PATCH /api/products/{id}/stock`)
- Redis-кэш списка товаров
- Seed smart-home товаров (`product-service/src/main/resources/data.sql`)

### Order Service
- Создание заказа
- Получение заказов пользователя
- Изменение статуса (`NEW`, `PAID`, `CANCELLED`)
- Проверка товара через вызов `product-service`
- Circuit Breaker + fallback при недоступности зависимого сервиса

### Notification Service
- Логирование уведомлений о создании заказа
- Получение списка уведомлений

## Безопасность

- API Gateway защищен JWT Resource Server
- Секрет задается через `JWT_SECRET`
- Открыты только технические endpoint'ы (`/actuator/**`, Swagger)

## Локальный запуск

### 1. Запуск тестов
```bash
./mvnw test
```
или в Windows:
```powershell
.\mvnw.cmd test
```

### 2. Запуск через Docker Compose
```bash
docker compose up --build
```

### 3. Smoke-тест после старта
```powershell
./scripts/smoke-test.ps1
```

## Swagger

После запуска сервисов:
- User: `http://localhost:8081/swagger-ui/index.html`
- Product: `http://localhost:8082/swagger-ui/index.html`
- Order: `http://localhost:8083/swagger-ui/index.html`
- Notification: `http://localhost:8084/swagger-ui/index.html`

## CI/CD

Файл: `.github/workflows/ci-cd.yml`

Pipeline:
1. `mvn clean verify`
2. сборка Docker-образов
3. push в GHCR (`ghcr.io/<owner>/smartthingshop-<service>:latest`)

## Kubernetes

Папка `k8s/` содержит:
- `Deployment` и `Service` для каждого сервиса
- общий `ConfigMap` и `Secret`
- `HPA` для `order-service`

## Соответствие тестированию из ТЗ

Реализовано:
- Unit-тесты (Mockito, бизнес-слой)
- Интеграционные тесты (Controller -> DB через H2)
- API-тесты (статусы/JSON)
- Тест отказоустойчивости (`fallback` в `order-service`)
- Smoke-тест для запуска в контейнерах

## Структура репозитория

- `pom.xml` — root multi-module
- `docker-compose.yml` — локальная оркестрация
- `k8s/` — Kubernetes manifests
- `.github/workflows/ci-cd.yml` — CI/CD
- `scripts/smoke-test.ps1` — smoke проверка
