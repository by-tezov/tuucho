

```mermaid
flowchart TD
  Start --> Step1[Process Step 1]
  Step1 --> Step2[Process Step 2]
  Step2 --> End[Finish]
```

```mermaid
sequenceDiagram
  participant User
  participant App
  participant Server

  User->>App: Click "Submit"
  App->>Server: POST /data
  Server-->>App: 200 OK
  App-->>User: Success message
```

```mermaid
classDiagram
  class User {
    +String name
    +login()
    +logout()
  }

  class Admin {
    +banUser()
  }

  User <|-- Admin
```

```mermaid
erDiagram
  USERS ||--o{ POSTS : has
  USERS {
    string id
    string name
  }
  POSTS {
    string id
    string title
    string user_id
  }
```

```mermaid
stateDiagram-v2
  [*] --> Idle
  Idle --> Loading : start()
  Loading --> Success : success()
  Loading --> Error : fail()
  Error --> Idle : retry()
```

```mermaid
gantt
  title Project Plan
  dateFormat  YYYY-MM-DD
  section Development
  Setup       :done, 2025-07-01, 2d
  Backend     :active, 2025-07-03, 5d
  Frontend    : 2025-07-05, 4d
```

```mermaid
pie
  title Market Share
  "Chrome" : 60
  "Firefox" : 20
  "Safari" : 10
  "Others" : 10
```

```mermaid
mindmap
  root((Project))
    Planning
      Goals
      Timeline
    Development
      Backend
      Frontend
    Testing
      Unit
      Integration
```

```mermaid
erDiagram
  CUSTOMER ||--o{ ORDER : places
  CUSTOMER {
    id PK
    name
  }
  ORDER {
    id PK
    customer_id FK
    total
  }
```

```mermaid
journey
  title User onboarding
  section Landing
    Visit Homepage: 5: Anonymous
    Click “Sign up”: 4: Anonymous
  section Signup
    Fill form: 3: Anonymous
    Submit: 2: Registered
  section Complete
    Welcome email: 1: Registered
```

```mermaid
gitGraph
  commit
  branch feature
  checkout feature
  commit
  checkout main
  merge feature
  commit
```

```mermaid
quadrantChart
  title Reach vs Engagement
  x-axis Low Reach --> High Reach
  y-axis Low Engagement --> High Engagement
  quadrant-1 Expand Strategy
  quadrant-2 Promote More
  "Campaign A": [0.3, 0.5]
  "Campaign B": [0.7, 0.2]
```

```mermaid
xychart-beta
  title Sales vs Time
  x-axis [Jan, Feb, Mar]
  y-axis "Sales ($)" 0 --> 10000
  bar [5000, 7000, 8500]
  line [4500, 6500, 8000]
```

```mermaid
timeline
  2025-01-01 : Project Kickoff
  2025-02-10 : Alpha Release
  2025-05-20 : Beta Release
  2025-07-01 : Public Launch
```

```mermaid
graph LR
  A([Database])
  B([API Server])
  C([Web Client])
  C --> B --> A
```

```mermaid
C4Context
  Person(user, "User")
  System(system, "Web App")
  user --> system : "Uses"
```


```mermaid
flowchart TD
  %% === Start Phase ===
  A([Start Application]) --> B[Request <b>config.json</b>]
  B --> C[For each <b>url</b> in config → Check cache]
  
  %% === Cache Check ===
  C --> C1{In Database?}
  C1 -- No --> D[Request <b>url</b> JSON]
  C1 -- Yes --> H[App wants to show Screen A]

  %% === Process & Store JSON ===
  D --> E[Rectifier:<br/>Make JSON engine-compatible]
  E --> F[Breaker:<br/>Split into parts]
  F --> G[Store parts in DB]
  G --> H

  %% === Render Phase ===
  H --> H1{Is Screen A in DB?}
  H1 -- Yes --> I[Retrieve parts from DB]
  H1 -- No --> D
  I --> J[Assembler:<br/>Rebuild Page]
  J --> K[Renderer:<br/>Draw UI of Screen A]

  %% === Grouping for Clarity ===
  subgraph Network ["💻 Network Requests"]
    D --> E --> F
  end

  subgraph Storage ["🗄️ Local Database"]
    G
    I
  end

  subgraph UI ["🖥️ UI Rendering"]
    J --> K
  end
```

```mermaid
flowchart TD
  A[Start Application] --> B[Request **config.json**]
  B --> C[For each **url** in config: Check cache]
  C --> C1{In Database?}
  C1 -- No --> D[Request **url** JSON]
  D --> E[Rectifier: make JSON engine-compatible]
  E --> F[Breaker: split into parts]
  F --> G[Store parts in DB]
  C1 -- Yes --> H
  G --> H[Application want show 'Screen A' URL]
  H --> H1{Is in DB?}
  H1 -- Yes --> I[Retrieve parts from DB]
  I --> J[Assembler: Rebuild Page]
  J --> K[Renderer: Draw UI of Screen A]
  H1 -- No --> D
```
