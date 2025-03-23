# API для работы с CDR данными и отчетами

Данный репозиторий содержит REST API для управления CDR (Call Detail Record) записями и формирования отчетов на их основе.

## Содержание

- [Общее описание](#общее-описание)
- [Запуск приложения](#запуск-приложения)
- [Контроллеры](#контроллеры)
    - [CDR Контроллер](#cdr-контроллер)
    - [Контроллер отчетов](#контроллер-отчетов)
- [Использование API](#использование-api)
- [Примеры запросов](#примеры-запросов)

## Общее описание

API предоставляет функциональность для:
- Генерации CDR записей за год
- Получения CDR записей с возможностью фильтрации по абоненту
- Формирования отчетов на основе CDR данных
- Отслеживания статуса генерации отчетов
- Скачивания готовых отчетов

## Запуск приложения

1. **Клонирование репозитория:**

   ```bash
   git clone https://github.com/r1ngoch1/CallService.git
   ```

2. **Сборка и запуск приложения:**

   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

3. **Документация API:**

   После запуска приложения, документация API доступна по адресу:

   [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

4. **Работа с базой данных H2:**

   Для просмотра и управления базой данных H2, перейдите по адресу:

   [http://localhost:8080/h2-console](http://localhost:8080/h2-console)

    - **URL JDBC:** `jdbc:h2:mem:cdrdb`
    - **Username:** `user`
    - **Password:** `password`

   Чтобы посмотреть всех существующих абонентов, выполните SQL-запрос в консоли H2:
    ```sql
    SELECT * FROM SUBSCRIBERS;
    ```

## Контроллеры

### CDR Контроллер

Базовый путь: `/api/cdr`

| Метод | Путь | Описание |
|-------|------|----------|
| POST | `/generate` | Генерирует CDR записи за год |
| GET | `/` | Получает список всех CDR записей |
| GET | `/subscriber/{msisdn}` | Получает CDR записи конкретного абонента по MSISDN |
| GET | `/report/{msisdn}` | Формирует отчет по CDR записям для указанного абонента |

#### Детали методов CDR контроллера

- **POST /api/cdr/generate**
    - Генерирует CDR записи за год
    - Ответ: 200 OK с сообщением о успешной генерации

- **GET /api/cdr**
    - Возвращает все CDR записи в системе
    - Ответ: 200 OK со списком объектов CdrRecord

- **GET /api/cdr/subscriber/{msisdn}**
    - Возвращает все CDR записи для указанного абонента
    - Параметры пути: `msisdn` - номер абонента
    - Ответ: 200 OK со списком объектов CdrRecord

- **GET /api/cdr/report/{msisdn}**
    - Формирует текстовый отчет для указанного абонента
    - Параметры пути: `msisdn` - номер абонента
    - Ответ: 200 OK с текстовым отчетом или 404 Not Found, если записи не найдены

### Контроллер отчетов

Базовый путь: `/api/reports`

| Метод | Путь | Описание |
|-------|------|----------|
| POST | `/generate` | Генерирует отчет по заданным параметрам |
| POST | `/generate/periodic` | Генерирует периодический отчет |
| GET | `/status/{requestId}` | Проверяет статус отчета по его ID |
| GET | `/download/{requestId}` | Скачивает сгенерированный отчет |

#### Детали методов контроллера отчетов

- **POST /api/reports/generate**
    - Генерирует отчет по заданным параметрам
    - Тело запроса: объект `ReportGenerationRequest`
    - Ответ: 200 OK с объектом `ReportGenerationResponse`, содержащим `requestId` и статус

- **POST /api/reports/generate/periodic**
    - Генерирует периодический отчет
    - Тело запроса: объект `PeriodicReportRequest`
    - Ответ: 200 OK с объектом `ReportGenerationResponse`

- **GET /api/reports/status/{requestId}**
    - Проверяет статус отчета
    - Параметры пути: `requestId` - UUID запроса
    - Ответ: 200 OK с объектом `ReportGenerationResponse` или 404 Not Found

- **GET /api/reports/download/{requestId}**
    - Скачивает сгенерированный отчет
    - Параметры пути: `requestId` - UUID запроса
    - Ответ: 200 OK с файлом отчета или 404 Not Found, если отчет не найден/не завершен

## Использование API

### Общий процесс работы с отчетами:

1. Отправить запрос на генерацию отчета через `POST /api/reports/generate` или `POST /api/reports/generate/periodic`
2. Получить `requestId` из ответа
3. Проверять статус отчета через `GET /api/reports/status/{requestId}`
4. Когда статус будет "completed", скачать отчет через `GET /api/reports/download/{requestId}`

## Примеры запросов

### Генерация CDR записей

```
POST /api/cdr/generate
```

### Получение CDR записей для абонента

```
GET /api/cdr/subscriber/79001234567
```
### Генерация отчета по периодам

```
POST /api/reports/generate/periodic
Content-Type: application/json

{
  "msisdn": "79161234567",
  "period": "6months"
}
```

### Генерация отчета

```
POST /api/reports/generate
Content-Type: application/json

{
  "msisdn": "79161234567",
  "startDate": "2025-03-01T10:00:00",
  "endDate": "2025-03-23T18:00:00"
}
```

### Проверка статуса отчета

```
GET /api/reports/status/123e4567-e89b-12d3-a456-426614174000
```

### Скачивание отчета

```
GET /api/reports/download/123e4567-e89b-12d3-a456-426614174000
```