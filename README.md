# Lorecraft TCG Lounge

Trading Card Game management platform providing comprehensive features for gamers, store owners, and administrators.

## 🎯 Project Overview

Lorecraft TCG Lounge is a web/mobile integrated application that supports:
- Card search and deck management
- Competition organization and participation
- Real-time match tracking
- Player rankings and statistics
- Store management and ordering

## 🏗️ Architecture

- **Backend**: Spring Boot 3.x (Java 17)
- **Database**: MySQL with Redis caching
- **API**: RESTful API + WebSocket for real-time features
- **Documentation**: Swagger/OpenAPI 3.0
- **Cloud**: AWS/GCP ready with auto-scaling support

## 📦 Domain Structure

- **Card Domain**: Card search, deck management
- **Competition Domain**: Tournament organization, match tracking
- **User Domain**: Authentication, profiles, rankings
- **Store Domain**: Store management, ordering
- **Content Domain**: CMS, events, documentation

## 🚀 Getting Started

### Prerequisites
- Java 17+
- Maven 3.6+
- MySQL 8.0+
- Redis 6.0+

### Setup
1. Clone the repository
```bash
git clone https://github.com/SeungJaeMin/lorecraftTCGLounge.git
cd lorecraftTCGLounge
```

2. Configure database
```bash
# Create MySQL database
mysql -u root -p
CREATE DATABASE tcg_lounge_dev;
CREATE USER 'tcg_user'@'localhost' IDENTIFIED BY 'tcg_password';
GRANT ALL PRIVILEGES ON tcg_lounge_dev.* TO 'tcg_user'@'localhost';
```

3. Start Redis server
```bash
redis-server
```

4. Run the application
```bash
./mvnw spring-boot:run
```

### API Documentation
After starting the application, visit:
- Swagger UI: http://localhost:8080/api/swagger-ui.html
- API Docs: http://localhost:8080/api/v3/api-docs

## 🧪 Testing

```bash
# Run all tests
./mvnw test

# Run with coverage
./mvnw test jacoco:report
```

## 📚 Documentation

- [Architecture v1.0](./Lorecraft_TCGlounge_Architecture_v1.md)
- [ERD Diagram](./Lorecraft_TCG_Lounge.drawio)
- [Use Case Diagram](./Lorecraft_Web_USECASE_0802.drawio)

## 🎮 User Types

### Non-Members
- View game information and announcements
- Search cards
- Browse public content

### Gamers
- Create and manage deck recipes
- Join competitions and events
- View personal match history and rankings
- Check-in to tournaments

### Store Owners
- Organize tournaments
- Manage participant check-ins
- Input match results
- Order card packs

### Administrators
- Create competitions and assign store owners
- Manage content and announcements
- Monitor system-wide statistics
- Handle user permissions

## 🔧 Development

### Project Structure
```
src/main/java/com/lorecraft/tcglounge/
├── domain/          # Domain logic (card, competition, user, store, content)
├── application/     # Application services and orchestration
├── api/            # REST controllers and WebSocket handlers
├── infrastructure/ # Configuration, security, caching
└── common/         # Shared utilities and exceptions
```

### Naming Conventions
- Services: `[Verb][Object]Service` (e.g., `CreateDeckService`)
- Repositories: `[Entity]Repository`
- Controllers: `[Actor]Controller`

## 📈 Roadmap

### Phase 1: MVP (0-6 months)
- Basic competition management
- Card search and deck building
- User authentication and profiles

### Phase 2: Growth (6-12 months)
- Real-time tournament features
- Mobile application
- Advanced statistics

### Phase 3: Scale (12+ months)
- Global deployment
- AI-powered matching
- E-sports integration

## 🤝 Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 📞 Contact

- Project Team: Lorecraft Development Team
- Email: contact@lorecraft.com
- Repository: https://github.com/SeungJaeMin/lorecraftTCGLounge

