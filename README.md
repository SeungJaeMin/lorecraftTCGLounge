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

### Technology Stack
- **Backend**: Spring Boot 3.x (Java 17)
- **Frontend**: React 18 with TypeScript
- **Database**: MySQL 8.0 with Redis caching
- **API**: RESTful API + WebSocket for real-time features
- **Documentation**: Swagger/OpenAPI 3.0
- **Containerization**: Docker & Docker Compose
- **Cloud**: AWS/GCP ready with auto-scaling support

### Project Structure
```
Workspace_lorecraft_TCG/
├── backend/              # Spring Boot backend application
│   ├── src/
│   ├── pom.xml
│   └── Dockerfile
├── frontend/             # React frontend application
│   ├── src/
│   ├── package.json
│   └── Dockerfile
├── docker-compose.yml    # Full stack orchestration
├── docker-compose.dev.yml # Development environment
└── README.md
```

## 📦 Domain Structure

- **Card Domain**: Card search, deck management
- **Competition Domain**: Tournament organization, match tracking
- **User Domain**: Authentication, profiles, rankings
- **Store Domain**: Store management, ordering
- **Content Domain**: CMS, events, documentation

## 🚀 Getting Started

### Prerequisites
- Docker & Docker Compose
- Node.js 18+ (for local development)
- Java 17+ (for local backend development)
- MySQL 8.0+ (optional, if not using Docker)
- Redis 6.0+ (optional, if not using Docker)

### Quick Start with Docker

1. Clone the repository
```bash
git clone https://github.com/SeungJaeMin/lorecraftTCGLounge.git
cd lorecraftTCGLounge
```

2. Copy environment variables
```bash
cp .env.example .env
# Edit .env with your configurations
```

3. Start the full stack
```bash
# Development environment with database UIs
docker-compose -f docker-compose.dev.yml up -d

# Or production-like environment
docker-compose up -d
```

4. Access the applications
- Frontend: http://localhost:3000
- Backend API: http://localhost:8080/api
- Swagger UI: http://localhost:8080/api/swagger-ui.html
- Adminer (DB UI): http://localhost:8081
- Redis Commander: http://localhost:8082

### Local Development

#### Backend
```bash
cd backend
./mvnw spring-boot:run
```

#### Frontend
```bash
cd frontend
npm install
npm start
```

## 🧪 Testing

### Backend Tests
```bash
cd backend
./mvnw test
./mvnw test jacoco:report
```

### Frontend Tests
```bash
cd frontend
npm test
npm run test:coverage
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

### Backend Structure
```
backend/src/main/java/com/lorecraft/tcglounge/
├── domain/          # Domain logic (card, competition, user, store, content)
├── application/     # Application services and orchestration
├── api/            # REST controllers and WebSocket handlers
├── infrastructure/ # Configuration, security, caching
└── common/         # Shared utilities and exceptions
```

### Frontend Structure
```
frontend/src/
├── components/     # Reusable UI components
├── pages/         # Page components
├── services/      # API services
├── hooks/         # Custom React hooks
├── utils/         # Utility functions
├── types/         # TypeScript type definitions
└── styles/        # Global styles and themes
```

### Naming Conventions
- Backend Services: `[Verb][Object]Service` (e.g., `CreateDeckService`)
- Backend Repositories: `[Entity]Repository`
- Backend Controllers: `[Actor]Controller`
- React Components: PascalCase (e.g., `CardList.tsx`)
- React Hooks: camelCase with 'use' prefix (e.g., `useAuth.ts`)

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

