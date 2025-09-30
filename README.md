<table>
  <tr>
    <td style="vertical-align: middle; padding-right: 10px;">
      <img src="https://doc.tuucho.com/latest/assets/tuucho-logo.svg" alt="TUUCHO Logo" width="100" />
    </td>
    <td><h1>TUUCHO - ตู้โชว์ - Rendering Engine</h1></td>
  </tr>
</table>

TUUCHO is a Kotlin Multiplatform Mobile (KMM) project with most of its code shared across platforms, enabling native iOS and Android support from a single Kotlin codebase. It follows Clean Architecture principles wherever they best fit the project structure, ensuring modularity, testability, and maintainability.

TUUCHO is a powerful and flexible application rendering engine that dynamically generates user interfaces by parsing JSON descriptions. It enables developers to declaratively define app layouts and UI components in a structured JSON format, which TUUCHO then renders seamlessly across platforms.

---

## ⚠️ Project Status

**TUUCHO is currently in active development and is not yet ready for production use.**  
Expect ongoing changes, new features, and improvements as the project evolves.

Check out the [Roadmap](https://doc.tuucho.com/latest/roadmap/) for full details.

## ⚠️ Quick Start

[Quick Start Documentation](https://doc.tuucho.com/latest/quick-start/)

---

### Supported Features

- **Json Content**
    - 100% of the application is driven by the server
    - Definable components with unique IDs allowing shared references to reduce JSON payload size. This applies to content, styles, text, and more.
    - Intelligent caching: JSON objects are cached locally to minimize repeated network requests. Content is fetched over the network only when necessary.
    - Dynamic context data fetch asynchronously with TTL capabilities

- **Navigation Stack**  
  TUUCHO provides its own navigation stack and supports all capabilities offered by Compose Navigation, including:
    - Clear Stack
    - Single Top
    - Reuse (do not create a new one, bring back an existing one)
    - PopUpTo (inclusive or not)

- **Transition Animations**  
  TUUCHO supports transition animations between screens:
    - Fade
    - Slide Vertically
    - Slide Horizontally

- **Cache control**
    - Time To Live components, contents and texts group and individual

- **Form submission**
    - Local validator and remote controls with user feedback
    - Custom command on success or failure

### Supported Components

- **Linear Layout** (vertical or horizontal orientation)
- **Button** with built-in actions:
    - Navigation
    - Form submission
- **Label**
- **Input Field** (form element)
- **Spacer**

---

## Why Choose TUUCHO?

- **Cross-Platform Target** — Built with Kotlin Multiplatform Mobile (KMM), TUUCHO runs natively on both iOS and Android from a shared codebase.
- **Clean Architecture** — Adheres to modular, clean design principles that improve scalability and ease of testing.
- **Dynamic UI Updates** — Modify your app UI dynamically by changing JSON descriptions without recompiling and without mobile publication.
- **Extensible and Modular** — Designed to grow with your needs, TUUCHO is actively evolving with support for new components, styles, actions, and validation logic.
- **Open Source & Community Driven** — Join the TUUCHO community and contribute to making the platform more robust and feature-rich.

---

## Documentation & Resources

For comprehensive documentation, examples, and developer guides, visit:

[Full Documentation](https://doc.tuucho.com/0.0.1-alpha13/)

---

## Backend Repository

The TUUCHO backend repository for quick dev and test:

[tuucho-backend GitHub Repository](https://github.com/by-tezov/tuucho-backend)

---

Feel free to explore, contribute, and provide feedback. TUUCHO is building towards a powerful, scalable rendering solution for modern mobile applications!

