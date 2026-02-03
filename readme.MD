# VeloCity Backend Services

A microservices-based ride-sharing platform built with Spring Boot.

## 🏗️ Architecture

VeloCity uses a microservices architecture with the following services:

- **velocity-core**: Shared utilities, DTOs, and exception handling
- **velocity-user-service**: User authentication and profile management
- **velocity-driver-service**: Driver management and location tracking
- **velocity-ride-service**: Ride booking and management
- **velocity-payment-service**: Wallet and payment processing
- **velocity-notification-service**: Email and push notifications
- **velocity-gateway**: API Gateway for routing and load balancing

## 🚀 Quick Start

### Prerequisites

- Java 17 or higher
- Maven 3.8+
- Docker & Docker Compose
- PostgreSQL 15 (via Docker)
- Redis 7 (via Docker)

### 1. Start Infrastructure Services

```bash
cd backend
./start-services.sh docker
```

This starts:
- PostgreSQL (port 5432)
- Redis (port 6379)
- pgAdmin (port 5050)
- Redis Commander (port 8086)
- Prometheus (port 9090)
- Grafana (port 3001)
- MailHog (ports 1025, 8025)

### 2. Start Application Services

**Option A: Using the startup script (Recommended)**

```bash
# In separate terminal windows:
./start-services.sh user        # Port 8081
./start-services.sh driver      # Port 8082
./start-services.sh ride        # Port 8083
./start-services.sh payment     # Port 8084
./start-services.sh notification # Port 8085
```

**Option B: Using Maven directly**

```bash
# User Service
mvn spring-boot:run -pl velocity-user-service

# Driver Service
mvn spring-boot:run -pl velocity-driver-service

# Ride Service
mvn spring-boot:run -pl velocity-ride-service

# Payment Service
mvn spring-boot:run -pl velocity-payment-service

# Notification Service
mvn spring-boot:run -pl velocity-notification-service
```

### 3. Verify Services

Check health endpoints:
```bash
curl http://localhost:8081/actuator/health  # User Service
curl http://localhost:8082/actuator/health  # Driver Service
curl http://localhost:8083/actuator/health  # Ride Service
curl http://localhost:8084/actuator/health  # Payment Service
curl http://localhost:8085/actuator/health  # Notification Service
```

## 📋 Port Allocation

| Service | Port | URL |
|---------|------|-----|
| User Service | 8081 | http://localhost:8081 |
| Driver Service | 8082 | http://localhost:8082 |
| Ride Service | 8083 | http://localhost:8083 |
| Payment Service | 8084 | http://localhost:8084 |
| Notification Service | 8085 | http://localhost:8085 |
| API Gateway | 5000 | http://localhost:5000 |
| PostgreSQL | 5432 | jdbc:postgresql://localhost:5432/velocity_db |
| Redis | 6379 | redis://localhost:6379 |
| pgAdmin | 5050 | http://localhost:5050 |
| Redis Commander | 8086 | http://localhost:8086 |
| Prometheus | 9090 | http://localhost:9090 |
| Grafana | 3001 | http://localhost:3001 |
| MailHog Web | 8025 | http://localhost:8025 |

**📖 For detailed port information, see:** [`PORTS_AND_SERVICES.md`](PORTS_AND_SERVICES.md) or [`../docs/PORT_ALLOCATION.md`](../docs/PORT_ALLOCATION.md)

## 🔧 Configuration

### Environment Variables

Copy `.env.example` to `.env` and customize:

```bash
cp .env.example .env
```

Key configurations:
- Database credentials
- Redis connection
- JWT secret keys
- Service ports
- Email settings

### Port Customization

All services support custom ports via environment variables:

```bash
USER_SERVICE_PORT=9001 mvn spring-boot:run -pl velocity-user-service
```

## 🗄️ Database

### Migrations

Database migrations are managed by Flyway and run automatically on startup.

Migration files location: `src/main/resources/db/migration/`

### Access Database

**Via pgAdmin:**
1. Open http://localhost:5050
2. Login: admin@velocity.com / admin
3. Add server: localhost:5432
4. Database: velocity_db
5. Username: velocity_user
6. Password: velocity_pass

**Via CLI:**
```bash
docker exec -it velocity-postgres psql -U velocity_user -d velocity_db
```

## 📊 Monitoring

### Prometheus
- URL: http://localhost:9090
- Metrics endpoint: `/actuator/prometheus` on each service

### Grafana
- URL: http://localhost:3001
- Login: admin / admin
- Pre-configured dashboards for service metrics

## 📧 Email Testing

MailHog captures all emails sent by the application:
- Web UI: http://localhost:8025
- SMTP: localhost:1025

## 🧪 Testing

### Run All Tests
```bash
mvn clean test
```

### Run Tests for Specific Service
```bash
mvn test -pl velocity-user-service
```

### Integration Tests
```bash
mvn verify
```

## 🛠️ Development Tools

### Redis Commander
- URL: http://localhost:8086
- View and manage Redis data

### Hot Reload
Spring Boot DevTools is enabled for hot reload during development.

## 📦 Build

### Build All Services
```bash
mvn clean package
```

### Build Specific Service
```bash
mvn clean package -pl velocity-user-service
```

### Skip Tests
```bash
mvn clean package -DskipTests
```

## 🐳 Docker

### Start Infrastructure
```bash
docker-compose up -d
```

### Stop Infrastructure
```bash
docker-compose down
```

### View Logs
```bash
docker-compose logs -f [service-name]
```

### Rebuild Containers
```bash
docker-compose up -d --build
```

## 🔍 Troubleshooting

### Port Already in Use

Check what's using a port:
```bash
lsof -i :8081
```

Kill process on port:
```bash
kill -9 $(lsof -t -i:8081)
```

### Database Connection Issues

1. Ensure PostgreSQL is running: `docker-compose ps`
2. Check logs: `docker-compose logs postgres`
3. Verify credentials in `.env` or `application.yml`

### Redis Connection Issues

1. Ensure Redis is running: `docker-compose ps`
2. Check logs: `docker-compose logs redis`
3. Test connection: `redis-cli -h localhost -p 6379 ping`

## 📚 API Documentation

Each service exposes Swagger/OpenAPI documentation:
- User Service: http://localhost:8081/swagger-ui.html
- Driver Service: http://localhost:8082/swagger-ui.html
- Ride Service: http://localhost:8083/swagger-ui.html
- Payment Service: http://localhost:8084/swagger-ui.html
- Notification Service: http://localhost:8085/swagger-ui.html

## 🏗️ Project Structure

```
backend/
├── velocity-core/              # Shared utilities and DTOs
├── velocity-user-service/      # User authentication
├── velocity-driver-service/    # Driver management
├── velocity-ride-service/      # Ride booking
├── velocity-payment-service/   # Payment processing
├── velocity-notification-service/ # Notifications
├── velocity-gateway/           # API Gateway
├── docker-compose.yml          # Infrastructure services
├── pom.xml                     # Parent POM
├── .env.example                # Environment template
├── .env.ports                  # Port configuration
└── start-services.sh           # Service startup script
```

## 🤝 Contributing

1. Create a feature branch
2. Make your changes
3. Run tests: `mvn test`
4. Submit a pull request

## 📄 License

Copyright © 2026 VeloCity Team

---

**Need Help?** Check [`PORTS_AND_SERVICES.md`](PORTS_AND_SERVICES.md) for quick reference or [`../docs/PORT_ALLOCATION.md`](../docs/PORT_ALLOCATION.md) for detailed documentation.

## By Md Rizabul