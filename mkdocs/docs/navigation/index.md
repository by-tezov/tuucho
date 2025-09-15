---
comments: true
---

# TUUCHO Navigation System

> TUUCHO provides a flexible navigation system for each page using JSON definitions.  
> Navigation is defined under `root/setting/navigation`.

---

## Navigation Structure

Navigation is always defined inside each page's `root` under:

```json
"root": {
  "setting": {
    "navigation": {
      "navigation": {
        "extra": {
          "is-background-solid": true/false
        },
        "definition": [
          {
            "selector": { /* selector object */ },
            "option": { /* option object */ },
            "transition": { /* transition object */ }
          }
        ]
      }
    }
  }
}
```

- **`extra`**: Helps the stack preparator for transitions.  
  Example: if a screen is an overlay, `is-background-solid` helps determine whether the previous screen should be included in the transition. (by default to true)

- **`definition`**: Defines navigation rules, options, and transitions.
    - Can be a **single object** if no selector is needed.
    - Multiple definitions can be used with a **selector** to pick the correct navigation logic based on the current stack.

---

## Selector Object

Selectors determine when a navigation definition should apply:

```json
"selector": {
  "type": "page-bread-crumb",
  "values": ["page-c", "page-a"]
}
```

- **`page-bread-crumb`**: Applies the definition only if the last pages in the stack are exactly `page-c` followed by `page-a`.

---

## Option Object

The **option** object defines stack behavior for navigation:

```json
"option": { 
  "clear-stack": true/false,
  "single": true/false,
  "reuse": "last/first/true",
  "popUpTo": {
    "route": "",
    "inclusive": true/false,
    "greedy": true/false
  }
}
```

- **`clear-stack`**: Clears the navigation stack before navigating.
- **`single`**: Ensures only one instance of this page exists in the stack.
- **`reuse`**: Can be `"last"`, `"first"`, or `true` to reuse an existing instance of the page, instead of creating a new one.
- **`popUpTo`**: Pops the stack to a specific route with options:
  - `route`: Route to find.
  - `inclusive`: Remove the target route itself.
  - `greedy`: pop until the last routes (if multiple instance) or untile the first one found

---

## Transition Object

The **transition** object defines screen animations:

```json
"transition": {
  "forward": {
    "enter": { "type": "" },
    "exit": { "type": "" }
  },
  "backward": {
    "enter": { "type": "" },
    "exit": { "type": "" }
  }
}
```

- `type`: Can be `"fade"`, `"slide-horizontal"` or `"slide-vertical"`.
- You can also provide a **string** as a shortcut with all default parameters for all directions

```json
"navigation": "fade"
```

- The `transition` keys can be customized depending on animation type. For details, see [Transition Fade](transition-fade.md) and [Transition Slide](transition-slide.md).

---

## Key Points

- Navigation must be defined under `root.setting.navigation` in each page.
- Each page can have multiple navigation definitions with selectors.
- TUUCHO handles reuse, single-top, clear-stack, and popUpTo rules similar to Compose Navigation.
