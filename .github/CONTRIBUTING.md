# 🧩 Contributing Guide

Thank you for your interest in contributing to **TUUCHO**!  
All contributions — small or large — are welcome and appreciated. ❤️

For the full contributor documentation, visit 👉 [**doc.tuucho.com**](https://doc.tuucho.com)

---

## 🪄 Getting Started

### 1. Fork the Repository
Create your own copy of the repository to work on.

### 2. Create Your Working Branch
- Base your branch on the **latest release branch** (e.g. `release/x.y.z`).
- If no release branch exists, **ask for one** before starting.
- Name your branch using one of these prefixes:
    - `feat/` — for a new feature
    - `fix/` — for a bug fix
    - `chore/` — for non-functional updates (refactor, CI, etc.)

**Example:**  
```
git checkout -b feat/my-awesome-feature origin/release/1.2.3
```

### 3. Make Your Changes
Focus first on the code you want to contribute.  
Help can be provided later for tests and documentation updates if needed.

---

## 🧱 Step-by-Step: Build and Run

### **1. TUUCHO Project**

1. Open the **TUUCHO** project on your branch.
2. Check the `versionName` in `libs.versions.toml`.
3. Build and publish the library locally:  
   ```
   ./gradlew rootPublishProdToMavenLocal
   ```

---

### **2. TUUCHO Backend**

TUUCHO renders JSON from a backend server.  
You’ll need **Node.js v22 or later**.

If Node.js is not installed, see [How to Install Node.js](#how-to-install-nodejs).

#### Steps:
1. Clone the backend:  
   ```
   git clone https://github.com/by-tezov/tuucho-backend
   ```
2. Checkout the corresponding release branch.
3. Install dependencies:  
   ```
   npm install
   ```
4. Start the local server:  
   ```
   npm run start:dev
   ```

---

### **3. TUUCHO Sample Project**

1. Open the **`sample`** folder.
2. At the root of the sample project, open `libs.versions.toml` and ensure its `versionName` matches the one from the **TUUCHO** project.  
   Both must be identical for testing your code.
3. Copy `config.properties.sample` and rename it to `config.properties` (update values if needed).
4. Run the **Android** or **iOS** application to test your development changes.

**Note:**  
To test your development, you must first publish the library to your local Maven repository from the **TUUCHO** project, and then build the sample project.  
It’s not the most convenient workflow — a simpler and more efficient solution is being worked on, but for now, this is the process to follow.

---

### ⚙️ JDK Version Warning

If you see:  
`You are using JDK 21 but 17 is declared in the project`

**Fix:**
1. Open Android Studio Settings
2. Go to **Build, Execution, Deployment → Build Tools**
3. Under **Gradle JDK**, select **JDK 17**

---

## 📦 How to Install Node.js

If Node.js v22+ is not installed:

### macOS (Homebrew)
```
brew install node@22
```

### Linux (NodeSource)
```
curl -fsSL https://deb.nodesource.com/setup_22.x | sudo -E bash -
sudo apt-get install -y nodejs
```

### Windows
Download and install from [https://nodejs.org/](https://nodejs.org/)

**Verify installation:**  
```
node -v
```

It should print something like `v22.x.x`.

---

## 🧾 Pull Request Requirements

When opening your Pull Request:

1. **Clearly describe** what your contribution does.
2. **Add screenshots or a short demo video** if your change affects the UI or behavior.
3. **Ensure your branch is up to date** with the latest release branch.

---

## 🧪 Contribution Requirements

Your contribution **must not break existing tests**.

### ✅ Unit Tests
- All unit tests must pass.
- Add new ones if relevant for your change.

Run all unit tests before opening your PR:  
```
./gradlew rootMockUnitTest
```

### ✅ Ensure You Don’t Break the Public API
- Adding **new APIs** is allowed.
- Changing or removing existing **production APIs** requires prior validation and a clear justification.
- If a breaking change is necessary, a **migration guide** must be provided and properly tested.

Validate API compatibility:  
```
./gradlew rootValidateProdApi
```

### ✅ End-to-End (E2E) Tests
- Must remain functional.
> These are executed automatically by the CI when you open a Pull Request.

### ✅ Documentation
- Must be updated if your change impacts it.  
  💡 Help can be provided for these tasks if needed.

---

## 🧭 Need Ideas or Help?

If you have questions, suggestions, or just want to talk:  
📩 **tezov.app@gmail.com**  
💬 More docs and examples available at [**doc.tuucho.com**](https://doc.tuucho.com)

---

## 💙 Final Notes

Contribute at your own pace.  
Focus on what you want to build or fix — support will be provided for the rest.  
Every contribution counts and helps shape **TUUCHO’s** future.  
Thank you for being part of it!
