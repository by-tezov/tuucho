# Contributing Guide

First, thank you for your interest in contributing!  
All contributions — small or large — are welcome.

---

## 🪄 Getting Started

1. **Fork the repository**  
   Create your own copy to work on.

2. **Create your working branch**
    - Base your branch on the **latest release branch** (e.g. `release/x.y.z`).
    - If no release branch exists, **ask for one** before starting.
    - Name your branch using one of these prefixes:
        - `feat/` — for a new feature
        - `fix/` — for a bug fix
        - `chore/` — for non-functional updates (refactor, CI, etc.)

    Example:
    ```bash
        git checkout -b feat/my-awesome-feature origin/release/1.2.3
    ```

3. **Make your changes**  
   Focus first on the code you want to contribute.  
   Help can be provided later for tests and documentation updates if needed.

## Step by Step to Build and Run

**Tuucho** project
1. Open the **Tuucho** project on your branch.
2. Check the `versionName` in `libs.versions.toml`. 
3. Build and publish the library locally: `./gradlew rootPublishProdToMavenLocal`

**Tuucho-Backend** project
This is required since Tuucho renders JSON from a backend server.
You’ll need Node.js v22 or later. Check `How to Install Node.js` section just below if needed
1. clone the backend dev `git clone https://github.com/by-tezov/tuucho-backend`
2. Checkout the corresponding release branch.
3. install the node module with `npm install`
4. start the local server with `npm run start:dev`

**Tuucho-Sample** project
1. Open the **`sample`** folder.
2. At the root of the sample project, open `libs.versions.toml` and ensure its `versionName` matches the one from the **Tuucho** project.  
   Both must be identical for testing your code.
3. Copy config.properties.sample and name it config.properties (update the value if needed)
4. Run the **Android** or **iOS** application to test your development changes.

That’s it — you’re ready to build, run, and test your contribution. Don't hesitate to request help.

Note: 
- inside `settings.gradle.kts` file from **Tuucho** project, you can uncomment the line `includeBuild("sample")`. This allows you to develop and test using only one Android Studio instance.
- If you see the warning `You are using JDK 21 but 17 is declared in the project`
Follow these steps to fix it:
1. Open Android Studio Settings
2. Go to Build, Execution, Deployment
3. Then select Build Tools
4. Under Gradle JDK, choose a JDK 17 version.

## How to Install Node.js
If Node.js v22+ is not installed:

macOS (with Homebrew)
```bash
    brew install node@22
```

Linux (via NodeSource)
```bash
    curl -fsSL https://deb.nodesource.com/setup_22.x | sudo -E bash -
    sudo apt-get install -y nodejs
```

Windows
Download and install from https://nodejs.org/

After installation, confirm your version:
```bash
  node -v
```

It should print something like v22.x.x.

---

## 🧾 Pull Request Requirements

When opening your Pull Request:

1. **Describe clearly what your contribution does**
2. **Add screenshots or a short demo video** if your change affects the UI or behavior and if relevant.
3. **Ensure your branch is up to date** with the latest release branch.

---

## 🧪 Requirements

Your contribution **must not break existing tests**:

- ✅ **Unit tests**
  - All unit tests must pass.
  - Add new ones if relevant for your change.

- ✅ **End-to-End (E2E) tests**
  - Must not break.

- ✅ **Documentation**
    - Must be updated.

**Help can be provided for theses tasks**

Run all unit tests before opening your pull request:  ./gradlew rootMockUnitTest

---

## 🧭 Need Ideas or Help?

- For questions or guidance, you can contact:  
  📩 **tezov.app@gmail.com**

---

## 💙 Final Notes

Contribute at your own pace.  
Focus on what you want to build or fix — support will be provided for the rest.  
Every contribution counts and is greatly appreciated.