---
comments: true
---

# Roadmap

> This roadmap outlines the planned evolution of the TUUCHO rendering engine.  
> It highlights upcoming features and improvements aimed at making the library robust, scalable, and production-ready.

---

## 🚧 Current Focus

Work in progress:

- **Data store**  
  Add a way for the server to record data key/value on device and use them.

- **Images / Assets store**  
  Add a way for the server to add cache images and use them (or remote assets)

---

## 🎯 Medium-Term Goals

- **Configuration File Support**  
  Complete support for configuration files to allow application-wide settings.

- **Real time Data Support**  
  Introduce bi-directional communication to enable real-time content updates within the UI.

- **Advanced UI Components**  
  Add comprehensive, fully tested UI components to cover a wider range of application needs.

- **Actions, Validators, Navigation, Settings, other useful solvable by pointer**  

- **Exception handlers / Loader Screen**

- **Add useful device feature (gps, permissions, ...)**

---

## 🚀 Long-Term Vision

- **Behavior & Animation System**  
  Implement an extensible behavior module to handle animations and interactive UI elements.

- **Modular UI Components**  
  Support UI components as independent modules for easier customization and extensibility.

- **Accessibility Enhancements**  
  Improve accessibility features to meet modern standards and ensure inclusivity.

- **Improved JSON Parsing Feedback**  
  Provide detailed, user-friendly warnings and errors to simplify troubleshooting of JSON input.

- **Parser Efficiency Optimizations**  
  Explore cache indexing, metadata, or other innovative techniques to accelerate JSON parsing and rendering.

- **Unit Tests + E2E Tests**  
  Add as many and meaningful tests before production validation to protect from any regression.

- **Documentation**  
  Add developer documentation for creating user modules and custom components.

---

## ✅ Realized

- **Cache Management Enhancements** (2025-08)
    - Add TTL (time-to-live) options to avoid indefinite cache persistence.
    - Implement versioning and automatic cache purging for consistency and efficiency.

- **Add CI Jenkins pipeline** (2025-08)
    - unit testing
    - end to end testing
    - visual regression testing
    - all command accessible through github description + statuses feedback on pull request

- **Navigation Stack Implementation** (2025-08)
    - Enhance navigation capabilities with a robust stack system to manage screen transitions.

- **Contextual Data Support** (2025-07)
    - Introduce dynamic data binding to enable context content resolution within the UI.

- **KMM Integration & iOS Support** (2025-06)
    - Full Kotlin Multiplatform Mobile support implemented.

---

We continuously update this roadmap as TUUCHO evolves. For the latest updates and detailed planning, stay tuned to this documentation.

---

*Contributions, feedback, and investor interest are highly welcomed to help shape the future of TUUCHO.*
