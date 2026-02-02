---
comments: true
---

# Tuucho Backend — Quick Start with NestJS

Tuucho requires a backend capable of returning JSON structures. This guide explains how to set up a **development server** using **NestJS**.

This is **not** intended for production but for quick test and experiment with Tuucho.

!!! info
    The sample application includes an embedded mock server, which you can use to quickly prototype without needing a real backend.

---

**Mobile Integration**: For the mobile side, follow this guide: [mobile-integration/quick-start.md](mobile-integration/quick-start.md)

---

# Minimal NestJS Server

```
import { NestFactory } from '@nestjs/core';
import { AppModule } from './app.module';

async function bootstrap() {
const app = await NestFactory.create(AppModule);
await app.listen(3000, '0.0.0.0');
}
bootstrap();
```

This starts a NestJS server on port **3000**, listening on all interfaces.

---

# AppModule Structure

All required modules for a Tuucho dev backend:

```
@Module({
  imports: [],
  controllers: [
    HealthController,
    ResourceAuthController,
    ResourceLobbyController,
    ImageAuthController,
    ImageLobbyController,
    SendFormLobbyController,
    SendAuthController,
  ],
  providers: [
    AuthGuard,
    AuthGuardOptional,
    LoginTokenStore,
    ResourceRepositoryService,
    ImageRepositoryService,
  ],
})
export class AppModule {}
```

### Controllers overview

| Controller                 | Endpoint area              | Purpose                                |
|----------------------------|----------------------------|----------------------------------------|
| `HealthController`         | `/health`                  | Health probe used by Tuucho            |
| `ResourceAuthController`   | `/resource/auth`           | Protected resource zone                |
| `ResourceLobbyController`  | `/resource/lobby` (public) | Unauthenticated resource zone          |
| `ImageAuthController`   | `/image/auth`              | Protected image zone                   |
| `ImageLobbyController`  | `/image/lobby` (public)    | Unauthenticated image zone             |
| `SendFormLobbyController`  | `/send/lobby` (public)     | Receive public form submissions        |
| `SendAuthController`       | `/send/auth`               | Receive authenticated form submissions |

!!! info
    The endpoint `/health`, `/resource`, `/image` and `/send` must matches with those defined in the application. Check [Mobile Config](mobile-integration/config.md)

### Providers

| Provider              | Role                                       |
|-----------------------|--------------------------------------------|
| `AuthGuard`           | Protects authenticated resource/send zones |
| `LoginTokenStore`     | Stores login tokens for the session        |
| `ResourceRepositoryService` | Provides resource JSON for all endpoints   |
| `ImageRepositoryService` | Provides image for all endpoints      |

Each area uses its own config file and JSON resource definition.

---

# Reference Implementation

A complete example backend is available publicly: [https://github.com/by-tezov/tuucho-backend](https://github.com/by-tezov/tuucho-backend)

This repository shows:

- health resource
- public & authenticated resources
- public & authenticated images
- send endpoints
- guards
- token storage
- sample forms & actions
- folder structure recommended for Tuucho

---

**Requirements** You will need **Node.js v22 or later**.

---

# TUUCHO Backend dev repository

### 1. Clone the backend

```
git clone https://github.com/by-tezov/tuucho-backend
```

Checkout on the desired Tag or used the lastest version on master.

### 2. Checkout the corresponding release branch

(Ensure your backend version matches your Tuucho mobile version.)

### 3. Install dependencies

```
npm install
```

### 4. Start the development server

```
npm run start:dev
```

This launches the Tuucho backend at: http://localhost:3000


You can now point your mobile app to this server and start iterating on your UI & actions.
