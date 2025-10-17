# 🧩 Contributing Guide

Thank you for your interest in contributing to **TUUCHO**!  
All contributions — small or large — are welcome and appreciated.

This page will guide you through setting up your local environment, contributing code, and submitting your changes.

## 💡 What TUUCHO Needs

TUUCHO is an evolving project built with passion, chaos, and curiosity — and **everyone is welcome** to join the adventure.  
We’re not a startup, not a company — just a bunch of people building things that are so **useless they somehow become useful**.

Here’s who we’re looking for (and yes, that includes you):

- 🧑‍💻 **Developers (Android / iOS / Explorer)** — Kotlin, KMM, Compose, Swift, and everything in between, above, or below.  
  _Explorer?_ That’s the developer who got lost, doesn’t really know why they’re here, but still ends up helping anyway. You belong here.

- 🎨 **Designers** — to make the UI look like something you’d actually want to touch.  
  We have pixels. You have taste. Let’s collaborate.

- 🧭 **Product Thinkers** — with a sense of UX, logic, and flow.  
  Basically, people who can ask “Why?”... and still want to know the answer.

- 🧠 **Architects** — the brave ones who can see the big picture, plan ahead, and stay calm when the codebase suddenly starts doing interpretive dance.

- 🧱 **DevOps Heroes** — Docker tamers, Jenkins whisperers, CI/CD guardians.  
  You’ll keep the whole circus running between builds.

- ⚙️ **Backend Wizards** — for demo servers, APIs, and mysterious data flows that make JSON feel alive.

- 🧾 **Documentation Writers / Updaters** — the real MVPs who make sense of what the rest of us meant to do.

---

### 🤡 Why You Might Want to Help

Maybe you were just **fired**, or you’re **about to be**, thanks to “economic reasons.”  
Or maybe **l’inspection du travail** refused your layoff, and now you’re stuck at your desk with your motivation buried six feet under while all your colleagues are gone.

Come take a breath.  
Join **TUUCHO** under a cool, anonymous pseudonym — something like _Anonymopus_ (the anonymous octopus 🐙).  
Find fun again. Build something weird, unexpected, and maybe even meaningful.

---

Whatever your **level**, **experience**, or **motivation**, you’re welcome.  
If you’re here to **learn**, **experiment**, or just **have fun**, this is your place — I’m still learning too.

Come for the code. Stay for the chaos.  
Because sometimes, making something completely useless… ends up being the most useful thing of all.

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
4. Start the development server:
   ```
   npm run start:dev
   ```

If Node.js is not installed, see the section [How to Install Node.js](#how-to-install-nodejs).

---

### **3. TUUCHO Sample Project**

1. Open the **`sample`** folder.
2. Open `libs.versions.toml` and make sure `versionName` matches the one from the **TUUCHO** project.  
   Both must be identical to test your changes.
3. Copy `config.properties.sample` and rename it to `config.properties` (update values if needed).
4. Run the **Android** or **iOS** application to test your modifications.

**Note**:
To test your development, you must first publish the library to your local Maven repository from the TUUCHO project, and then build the sample project.
It’s not the most convenient workflow — I’m working on a simpler and more efficient solution, but for now, this is the process to follow.

---

### ⚙️ JDK Version Warning

If you see: `You are using JDK 21 but 17 is declared in the project`

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
Download from [https://nodejs.org/](https://nodejs.org/) and install.

**Verify installation:**  
```
node -v
```

It should print something like `v22.x.x`.

---

## 🧾 Pull Request Requirements

When opening a Pull Request:

1. **Clearly describe** what your contribution does.
2. **Add screenshots or short demo videos** if your change affects the UI or behavior.
3. **Ensure your branch is up to date** with the latest release branch.

---

## 🧪 Contribution Requirements

Your contribution **must not break existing tests**.

### ✅ Unit Tests
- All unit tests must pass.
- Add new ones if relevant to your change.

```
./gradlew rootMockUnitTest
```

### ✅ Ensure You Don’t Break the Public API
- Adding **new APIs** is allowed.
- Changing or removing existing **production APIs** requires prior validation and a clear justification.
- If a breaking change is necessary, a **migration guide** must be provided and properly tested.

```
./gradlew rootValidateProdApi
```

### ✅ End-to-End (E2E) Tests
- Must remain functional.

> Done by the CI when you open a Pull request

### ✅ Documentation
- Must be updated if your change impacts it.

💡 Help can be provided for these tasks if needed.

---

## 🧭 Need Help or Ideas?

If you have questions or need guidance:  
📩 **tezov.app@gmail.com**

---

## 💙 Final Notes

Contribute at your own pace.  
Focus on what you want to build or fix — support will be provided for the rest.  
Every contribution counts and helps shape **TUUCHO’s** future.  
Thank you for being part of it!
