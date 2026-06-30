# CandidatesPack-Labs

## What is this?

**candidatePark** is a production-ready **authentication & user management backend** built with Spring Boot 3 and Java 17. It's the drop-in auth layer for any application that needs secure sign-up, email verification, and login — with rate limiting and JWT out of the box.

## How can this help your application?

Every app that has users needs authentication. Getting it right is hard — password hashing, email verification, token management, brute-force protection, session security. This project gives you all of that **pre-built, tested, and ready to deploy**. Drop it into your stack and your user auth is solved.

## Core Use Cases

| Use Case | What it does |
|---|---|
| **User Registration** | Sign up with email + password (BCrypt-hashed, strength 12) |
| **Email Verification** | Token-based email verification before granting access |
| **Secure Login** | JWT-based authentication with Spring Security |
| **Brute-Force Protection** | In-memory rate limiter — throttles repeated login attempts |
| **Talent/Candidate Profiles** | Extensible profile system (stub included) ready for resumes, transcripts, health data |
| **Production Email** | SMTP integration (Gmail) with dev-mode fallback that logs instead of sending |

## Advantages

- **Zero boilerplate auth** — No need to write security filters, JWT utilities, or password encoders. It's all wired up.
- **Defense in depth** — BCrypt + rate limiting + email verification + stateless JWT. Multiple layers of protection.
- **Tested** — 17+ unit tests covering happy paths, edge cases, rate limit windowing, and failure modes.
- **Extensible** — Clean package structure (controllers, services, repos, DTOs) so you can add features without fighting the framework.
- **Modern stack** — Java 17, Spring Boot 3.5, Spring Security 6, JPA, H2/any SQL DB, Maven.
- **Developer-friendly** — H2 in-memory DB for dev, Lombok to cut boilerplate, JaCoCo for coverage insights.

## Architecture at a Glance

```
Client  ──>  AuthController  ──>  UserServiceImpl  ──>  UserRepository (JPA)
                          │                              └── H2 / PostgreSQL
                          ├── JWTService (token gen/validation)
                          ├── LoginRateLimiter (token-bucket algorithm)
                          └── EmailService (SMTP or dev log)
```

### API Endpoints

| Method | Path | Auth Required |
|---|---|---|
| `GET` | `/V1/auth/home` | ❌ |
| `POST` | `/V1/auth/signup` | ❌ |
| `GET` | `/V1/auth/verify-email?token=...` | ❌ |
| `POST` | `/V1/auth/login` | ❌ |
| All others | any | ✅ JWT Bearer |

## Who is this for?

- **Startups** building an MVP — ship auth fast and move on to your core product.
- **Talent/Candidate platforms** — the candidate profile model (`TalentProfile`) is already scaffolded.
- **Hackathon projects** — deploy with H2 in-memory, zero setup.
- **Anyone who doesn't want to roll their own auth** and values a well-tested, secure foundation.

## Tech Stack

| Component | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.5.6 |
| Security | Spring Security + JWT (jjwt 0.12.6) |
| Database | H2 (dev) / any SQL via JPA |
| Build | Maven + Wrapper |
| Testing | JUnit 5 + Mockito |

## Getting Started

```bash
cd candidatePark
./mvnw spring-boot:run
```

The server starts on `http://localhost:8080` with an in-memory H2 database. Sign up, verify your email (check the logs for the link in dev mode), and log in to receive your JWT.

## Quick Test

```bash
# Sign up
curl -X POST http://localhost:8080/V1/auth/signup \
  -H "Content-Type: application/json" \
  -d '{"email": "user@example.com", "password": "securePass123"}'

# Log in (after verifying email via the link in server logs)
curl -X POST http://localhost:8080/V1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "user@example.com", "password": "securePass123"}'
# Returns a JWT token — use it as: Authorization: Bearer <token>
```

## License

Unlicensed — all rights reserved.
