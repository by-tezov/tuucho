---
comments: true
---

# Roadmap

> This roadmap outlines the planned evolution of the TUUCHO rendering engine.  
> It highlights upcoming features and improvements aimed at making the library robust, scalable, and production-ready.

---

## 🚧 Current Focus

Work in progress:

- **Public API + Error management**
  - Allow user to add custom action
  - All need middleware and interceptor
  - Correct and comprehensible error feedback
  - First attempt to define architecture to allow custom view features
  - Add Dokka doc + ktlint + Danger for CI
  - Improve Quick Start documentation for user and contributor
  - CI allows the use of different branches for dev/backend Docker container

- **Images / Assets store**  
  Add a way to cache images and use them (or remote assets)

- **Real time Data Support**  
  Introduce bi-directional communication to enable real-time content updates.

---

## 🎯 Medium-Term Goals

- **Implement the use of selectors (language, style, etc...)**

- **Advanced UI Components**  
  Add comprehensive, fully tested UI components to cover a wider range of application needs.

- **Validators, Navigation, Settings, other useful solvable by pointer**
  - Minimise and keep only what is useful and start to think about the public dev API

- **Data store**  
  Allow the use of recorded data from the JSON (still need to think about the use case and how)

- **Modular UI Components**  
  Support UI components as independent modules for easier customization and extensibility.

- **Unit Tests + E2E Tests**  
  Add as many meaningful tests as possible before production validation to protect from any regression.

---

## 🚀 Long-Term Vision

- **Add useful device features (GPS, permissions, ...)**

- **Behavior & Animation System**  
  Implement an extensible behavior module to handle animations and interactive UI elements.

- **Accessibility Enhancements**  
  Improve accessibility features to meet modern standards and ensure inclusivity.

- **Improved JSON Parsing Feedback**  
  Provide detailed, user-friendly warnings and errors to simplify troubleshooting of JSON input.

- **Parser Efficiency Optimizations**  
  Explore cache indexing, metadata, or other innovative techniques to accelerate JSON parsing and rendering.

- **Documentation**  
  Add developer documentation for creating user modules and custom components.

---

## ✅ Realized

- **Data store** (2025-10)  
  Add a way to record data key/value on device and use them.

- **Migration to library architecture in order to be published on Maven Central** (2025-09)  
  Refactor Jenkins pipelines to support the new architecture.

- **Configuration File Support** (2025-09)  
  Complete support for configuration files to allow application-wide settings.

- **Cache Management Enhancements** (2025-08)
  - Add TTL (time-to-live) options to avoid indefinite cache persistence.
  - Implement versioning and automatic cache purging for consistency and efficiency.

- **Add CI Jenkins pipeline** (2025-08)
  - Unit testing
  - End to end testing
  - Visual regression testing
  - All commands accessible through GitHub description + statuses feedback on pull requests

- **Navigation Stack Implementation** (2025-08)
  - Enhance navigation capabilities with a robust stack system to manage screen transitions.

- **Contextual Data Support** (2025-07)
  - Introduce dynamic data binding to enable context content resolution.

- **KMM Integration & iOS Support** (2025-06)
  - Full Kotlin Multiplatform Mobile support implemented.

---

We continuously update this roadmap as TUUCHO evolves. For the latest updates and detailed planning, stay tuned to this documentation.

---

*Contributions, feedback, and investor interest are highly welcomed to help shape the future of TUUCHO.*
