# ReservAr — Restaurant Reservation API

A production-ready REST API for managing restaurant reservations, built with Spring Boot. Supports real-time notifications via WebSocket/STOMP, payment processing through MercadoPago, and rate limiting with Bucket4j.

---

## Quick Start

```bash
git clone https://github.com/JPLopezOrozco/reservAR.git
cd reservAR
cp .env.example .env   # fill in your values
docker compose up --build
```

API available at `http://localhost:8080`

> PostgreSQL starts first with a healthcheck. The app waits until the database is ready — no race conditions on startup.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Framework | Spring Boot 3, Spring Security |
| Auth | JWT (custom) + BCrypt |
| Database | PostgreSQL 16 + Flyway |
| Real-time | WebSocket / STOMP |
| Payments | MercadoPago SDK |
| Rate limiting | Bucket4j |
| Containerization | Docker + Docker Compose |

---

## Environment Variables

Create a `.env` file in the root directory based on this template:

```env
# PostgreSQL
POSTGRES_DB=reservar_db
POSTGRES_USER=reservar_user
POSTGRES_PASSWORD=your_password_here

# Datasource (used by the Spring app inside Docker)
RESERVAR_DATASOURCE_URL=jdbc:postgresql://reservar-db:5432/reservar_db
RESERVAR_DATASOURCE_USERNAME=reservar_user
RESERVAR_DATASOURCE_PASSWORD=your_password_here

# JWT
JWT_SECRET=your_jwt_secret_min_32_characters

# MercadoPago
MERCADO-PAGO_TOKEN=your_mercadopago_access_token
MERCADO-PAGO_WEBHOOK_URL=https://your-domain.com/payment/mercado-pago/webhook
```

> Never commit your `.env` file. It is already listed in `.gitignore`.

---

## API Endpoints

### Auth — `/auth`

| Method | Path | Description | Auth |
|---|---|---|---|
| POST | `/auth/user` | Register customer | No |
| POST | `/auth/staff` | Register staff | No |
| POST | `/auth/user/login` | Login | No |
| GET | `/auth/id/{id}` | Get user by ID | Yes |

### Restaurants — `/restaurants`

| Method | Path | Description |
|---|---|---|
| GET | `/restaurants` | List all restaurants |
| GET | `/restaurants/id/{id}` | Get by ID |
| POST | `/restaurants` | Create restaurant |
| PUT | `/restaurants/update/{id}` | Update restaurant |
| PATCH | `/restaurants/price/{id}` | Update price |

### Tables — `/table`

| Method | Path | Description |
|---|---|---|
| GET | `/table/id/{id}` | Get by ID |
| GET | `/table/restaurant/{id}` | Get tables by restaurant |
| POST | `/table` | Create table |
| DELETE | `/table/delete/{id}` | Delete table |

### Availability — `/availability`

| Method | Path | Description |
|---|---|---|
| GET | `/availability/id/{id}` | Get by ID |
| GET | `/availability/restaurant/{id}` | Get by restaurant |
| POST | `/availability` | Create slot |
| DELETE | `/availability/delete/{id}` | Delete slot |

`dayOfWeek` accepts: `MONDAY` `TUESDAY` `WEDNESDAY` `THURSDAY` `FRIDAY` `SATURDAY` `SUNDAY`

### Inventory Rules — `/rule`

| Method | Path | Description |
|---|---|---|
| GET | `/rule/id/{id}` | Get by ID |
| GET | `/rule` | List all |
| POST | `/rule` | Create rule |
| DELETE | `/rule/delete/{id}` | Delete rule |

### Reservations — `/reservation`

| Method | Path | Description |
|---|---|---|
| GET | `/reservation/id/{id}` | Get by ID |
| GET | `/reservation/user` | Get for authenticated user |
| POST | `/reservation` | Create reservation |
| PUT | `/reservation/cancel/{id}` | Cancel reservation |
| PUT | `/reservation/completedReservation/{id}` | Mark as completed |

### Payments — `/payment`

| Method | Path | Description |
|---|---|---|
| POST | `/payment/mercado-pago/{id}` | Initiate payment |
| POST | `/payment/mercado-pago/webhook` | Webhook handler |

---

## Security

All endpoints require a Bearer token except:

```
/auth/**
/payment/mercado-pago/webhook
```

Authenticated requests:

```http
Authorization: Bearer <token>
```

Passwords are hashed with BCrypt. Rate limiting is applied per IP with Bucket4j.

---

## Real-time Notifications

Connect via SockJS + STOMP to receive live reservation events:

```javascript
const socket = new SockJS('http://localhost:8080/ws');
const client = Stomp.over(socket);

client.connect({}, () => {
  client.subscribe('/topic/reservations', (message) => {
    const event = JSON.parse(message.body);
    console.log(event);
  });
});
```

---

## Database Migrations

Flyway runs migrations automatically on startup.

```
src/main/resources/db/migration/
├── V1__create_user_table.sql
├── V2__create_tables.sql
└── ...
```

---

## Project Structure

```
src/main/java/com/reservAR/backreservar/
├── config/       # Security, WebSocket, Jackson, rate limiter
├── controller/   # REST controllers
├── dto/          # Request / Response DTOs
├── exception/    # Custom exceptions + GlobalExceptionHandler
├── jwt/          # JwtFilter + JwtService
├── model/        # JPA entities
├── repository/   # Spring Data JPA repositories
└── service/      # Interfaces + implementations
```

---

## Tests

```bash
./mvnw test
```

Coverage includes: Controllers · Services · JWT · Rate Limiter

Tests use `@WebMvcTest` with mocked dependencies — no database required.

