# ReservAr - Backend

## Descripción

**ReservAr** es una API REST desarrollada con **Spring Boot** para
gestionar reservas en restaurantes.

El sistema permite: - Registrar usuarios y autenticarlos mediante
**JWT** - Gestionar restaurantes - Definir disponibilidad de
restaurantes - Administrar mesas - Crear y gestionar reservas - Procesar
pagos mediante **Mercado Pago** - Notificar cambios de reservas mediante
**WebSocket**

La aplicación utiliza **PostgreSQL** como base de datos y **Flyway**
para las migraciones.

------------------------------------------------------------------------

# Quick Start

Levantar todo el proyecto con un solo comando:

docker compose up --build

La API quedará disponible en:

http://localhost:8080

------------------------------------------------------------------------

# Tecnologías utilizadas

-   Java
-   Spring Boot
-   Spring Security
-   JWT Authentication
-   PostgreSQL
-   Flyway
-   Docker
-   Docker Compose
-   Mercado Pago SDK
-   WebSocket (STOMP)
-   Bucket4j (Rate Limiting)

------------------------------------------------------------------------

# Arquitectura

El proyecto sigue una arquitectura en capas:

Controller → Endpoints REST\
Service → Lógica de negocio\
Repository → Acceso a base de datos\
DTO → Transferencia de datos\
Model → Entidades JPA

------------------------------------------------------------------------

# Infraestructura con Docker

El proyecto está completamente dockerizado utilizando **Docker** y
**Docker Compose**.

La infraestructura levanta automáticamente:

-   Backend Spring Boot
-   Base de datos PostgreSQL
-   Red Docker dedicada
-   Volumen persistente para datos

------------------------------------------------------------------------

# Servicios Docker

## Backend (reservar-app)

API REST que gestiona:

-   usuarios
-   restaurantes
-   mesas
-   reservas
-   pagos
-   notificaciones en tiempo real

Puerto expuesto:

8080

------------------------------------------------------------------------

## Base de datos (reservar-db)

Contenedor PostgreSQL 16.

Configuración:

DB: reservar_db\
USER: reservar_user\
PASSWORD: 123456789\
PORT: 5433

Características:

-   Healthcheck usando pg_isready
-   Volumen persistente para los datos
-   Red Docker dedicada

------------------------------------------------------------------------

# Ejecutar el proyecto

1.  Clonar repositorio

git clone `https://github.com/JPLopezOrozco/reservAR.git`

2.  Levantar infraestructura

docker compose up --build

Esto iniciará:

-   PostgreSQL
-   Backend Spring Boot

3.  Detener servicios

docker compose down

------------------------------------------------------------------------

# Base de datos

Las migraciones se gestionan con **Flyway**.

Ubicación:

src/main/resources/db/migration

Las migraciones se ejecutan automáticamente al iniciar la aplicación.

------------------------------------------------------------------------

# Seguridad

El sistema utiliza:

-   Spring Security
-   JWT Authentication
-   BCrypt para contraseñas
-   Rate limiting con Bucket4j

Endpoints públicos:

/auth/\*\* /payment/mercado-pago/webhook

El resto requiere autenticación:

Authorization: Bearer TOKEN

------------------------------------------------------------------------

# WebSockets

Endpoint:

/ws

Broker:

/topic

Se utiliza para enviar notificaciones de cambios en reservas.

------------------------------------------------------------------------

# Tests

El proyecto incluye tests para:

-   Controllers
-   Services
-   JWT
-   Rate Limiter

Ubicación:

src/test/java


