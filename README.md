# BeautyBuddy

⚠️ For Viewing Only: This project is public for portfolio/review purposes. No reuse allowed.

BeautyBuddy is a full-stack beauty community platform built for product discovery, social proof, and personalized routines. Users can browse products, write reviews, ask product questions, join discussions, and track routines, wishlist items, and breakout triggers.

## Employer Snapshot

- Project Type: Full-stack web application
- Focus: Product discovery plus social community features
- Role: End-to-end design and implementation across frontend, backend, API contracts, and data model
- Primary Value: Demonstrates practical, production-style engineering across auth, data integrity, and multi-feature UX

## What I Built

- Product discovery with search, sort, filtering, and detailed product pages
- Shade-aware reviews with pagination, sorting, search, upvotes, and reporting
- Product Q&A with threaded answers, upvotes, and moderation/report flows
- Community discussions with comments, upvotes, and reporting
- Personalized user features: wishlist, routines (makeup/skincare/haircare), and breakout list tracking
- Authentication with JWT in HTTP-only cookies and password hashing
- Relational schema management and migrations with Flyway

## Engineering Decisions

- Modular backend domains: features split by bounded areas (product, review, qa, discussion, routine, wishlist, etc.)
- Clear DTO boundaries: controllers return feature-specific DTOs instead of exposing entities
- Shared interaction model: upvotes and reports implemented as reusable services across multiple content types
- Security-first defaults: stateless auth flow with cookie-based JWT handling and Argon2 password encoding
- Migration-driven database setup: schema and seed evolution managed through Flyway scripts

## Tech Stack

- Frontend: React, Vite, React Router, React Icons
- Backend: Java 21, Spring Boot 3, Spring Web, Spring Data JPA, Spring Security
- Database: PostgreSQL
- Migrations: Flyway
- Auth and Security: JWT (jjwt), Argon2 password hashing
- Build Tools: Maven (backend), npm and Vite (frontend)

## Repository Layout

- BeautyBuddy-backend: Spring Boot API, security, business logic, persistence, Flyway migrations
- BeautyBuddy-frontend: React SPA with feature-based folders and reusable UI components

## Project Structure

```
BeautyBuddy-backend/
	src/main/java/com/beautybuddy/   # Domain modules (products, reviews, qa, discussions, routines, etc.)
	src/main/resources/
		application.properties
		db/migration/                  # Flyway SQL migrations
	pom.xml

BeautyBuddy-frontend/
	src/
		app/                           # Routing and app shell
		features/                      # Feature modules (products, reviews, questions, auth, etc.)
		components/                    # Shared UI components
	package.json
```

## Getting Started

1. Clone the repository:

```bash
git clone https://github.com/your-username/BeautyBuddy.git
cd BeautyBuddy
```

2. Start PostgreSQL and create a database named `beautybuddy`.

3. Start the backend:

```powershell
cd BeautyBuddy-backend
$env:JWT_SECRET_KEY="YOUR_BASE64_SECRET_AT_LEAST_32_BYTES"
mvn spring-boot:run
```

4. Start the frontend:

```powershell
cd ../BeautyBuddy-frontend
npm install
npm run dev
```

5. Open the app at `http://localhost:5173`.

## Portfolio Notes

- This project is designed to showcase end-to-end software engineering across frontend, backend, security, and data layers, rather than focusing on a single feature.
- The codebase emphasizes maintainability through feature modules, service boundaries, and migration-backed persistence.
- The API includes authenticated and unauthenticated paths to reflect realistic user journeys.

## API Endpoints

Base URL (local): `http://localhost:8080/api`

| Domain | Method | Endpoint | Description | Auth |
|---|---|---|---|---|
| Auth | POST | `/auth/register` | Register a new user | No |
| Auth | POST | `/auth/login` | Login and set JWT cookie | No |
| Auth | GET | `/auth/me` | Get current user | Optional |
| Auth | POST | `/auth/logout` | Logout and clear cookie | Yes |
| Products | GET | `/products` | Get all products | No |
| Products | GET | `/products/search?q=&sort=&category=` | Search, filter, sort products | No |
| Products | GET | `/products/{id}` | Get product details | No |
| Products | GET | `/products/{id}/ingredients` | Get product ingredients | No |
| Products | GET | `/products/{id}/maycontain` | Get may-contain ingredients | No |
| Products | POST | `/products/{id}/report` | Report a product | Yes |
| Reviews | POST | `/reviews/add` | Submit review | Yes |
| Reviews | POST | `/reviews/{reviewId}/edit` | Edit review | Yes |
| Reviews | DELETE | `/reviews/{reviewId}` | Delete review (soft delete) | Yes |
| Reviews | GET | `/reviews/{productId}` | Get product reviews (paged) | Optional |
| Reviews | GET | `/reviews/{productId}/search?query=` | Search reviews | Optional |
| Reviews | GET | `/reviews/{productId}/average-rating` | Product average rating | No |
| Reviews | POST | `/reviews/{reviewId}/upvote` | Upvote review | Yes |
| Reviews | DELETE | `/reviews/{reviewId}/upvote` | Remove review upvote | Yes |
| Reviews | POST | `/reviews/{reviewId}/report` | Report review | Yes |
| Q&A | POST | `/questions/ask` | Ask product question | Yes |
| Q&A | GET | `/questions/{productId}` | Get product questions and answers (paged) | Optional |
| Q&A | GET | `/questions/{productId}/search?query=` | Search questions | Optional |
| Q&A | POST | `/answers/submit` | Submit answer | Yes |
| Discussions | GET | `/discussions?page=&size=&sort=` | Get discussions | Optional |
| Discussions | GET | `/discussions/search?query=` | Search discussions | Optional |
| Discussions | POST | `/discussions` | Create discussion | Yes |
| Discussions | POST | `/discussions/{discussionId}/comment` | Add discussion comment | Yes |
| Discussions | POST | `/discussions/{discussionId}/upvote` | Upvote discussion | Yes |
| Wishlist | GET | `/wishlist?sort=&category=&priceRange=&query=` | Get wishlist with filters | Yes |
| Wishlist | POST | `/wishlist/add` | Add item to wishlist | Yes |
| Wishlist | POST | `/wishlist/remove` | Remove item from wishlist | Yes |
| Routines | GET | `/routines/makeup` | Get makeup routines | Yes |
| Routines | GET | `/routines/skincare` | Get skincare routines | Yes |
| Routines | GET | `/routines/haircare` | Get haircare routine | Yes |
| Routines | POST | `/routines/makeup` | Create makeup routine | Yes |
| Routines | PUT | `/routines/{id}` | Update routine | Yes |
| Routines | POST | `/routines/{id}/add-product` | Add product to routine | Yes |
| Routines | DELETE | `/routines/{routineId}/{productId}` | Remove product from routine | Yes |
| Breakout List | POST | `/breakout-list/add` | Add ingredient or product to breakout list | Yes |
| Breakout List | DELETE | `/breakout-list/remove` | Remove item from breakout list | Yes |
| Breakout List | GET | `/breakout-list/products` | Get breakout products | Yes |
| Breakout List | GET | `/breakout-list/ingredients` | Get breakout ingredients | Yes |

## Environment Variables

| Variable | Required | Example | Used By | Notes |
|---|---|---|---|---|
| `JWT_SECRET_KEY` | Yes | Base64-encoded 32+ byte secret | Backend | Required for token signing and validation. |
| `SPRING_DATASOURCE_URL` | Recommended | `jdbc:postgresql://localhost:5432/beautybuddy` | Backend | Overrides datasource URL. |
| `SPRING_DATASOURCE_USERNAME` | Recommended | `postgres` | Backend | Overrides DB username. |
| `SPRING_DATASOURCE_PASSWORD` | Recommended | `your_password` | Backend | Overrides DB password. |
| `SPRING_JPA_HIBERNATE_DDL_AUTO` | Optional | `validate` | Backend | Default in this project is validate. |
| `SPRING_JPA_SHOW_SQL` | Optional | `true` | Backend | Useful for local debugging. |
| `VITE_API_BASE_URL` | Optional | `http://localhost:8080` | Frontend | Future-friendly option if API URLs are externalized. |

PowerShell example:

```powershell
$env:JWT_SECRET_KEY="YOUR_BASE64_SECRET_AT_LEAST_32_BYTES"
$env:SPRING_DATASOURCE_URL="jdbc:postgresql://localhost:5432/beautybuddy"
$env:SPRING_DATASOURCE_USERNAME="postgres"
$env:SPRING_DATASOURCE_PASSWORD="your_password"
```

## Skills Demonstrated

- Designing and implementing a modular full-stack architecture
- Building secure authentication and session handling with JWT and cookies
- Creating scalable REST APIs with Spring Boot and JPA
- Managing relational schema evolution with Flyway migrations
- Implementing rich client-side UX for search, sort, filtering, and pagination
- Developing social interaction systems (upvotes, comments, reports, Q&A)
- Coordinating frontend-backend integration across multiple feature domains

## In Development

This project is still actively in development and is not feature-complete yet.

- Some features are still being refined or expanded
- UI and interaction details are being iterated based on testing and feedback
- Additional hardening work is planned for production readiness (testing, deployment, and monitoring)

The repository is updated as new improvements are implemented.
