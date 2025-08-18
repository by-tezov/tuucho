# Transition: Slide

> The **slide transition** controls how screens slide in and out of view.  
> TUUCHO provides a flexible JSON structure where defaults can be inherited from higher levels and overridden locally.

---

## Simplest

```json
"transition": "slide-horizontal" or "slide-vertical"
```

Nothing more to do, you will have a slide transition in all directions with default parameters.

---

## Full Definition

A complete slide transition object looks like this:

```json
"transition": {
  "forward": {
    "enter": {
      "type": "slide-horizontal",
      "duration": 350,
      "entrance": "from-start",
      "effect": "push"
    },
    "exit": {
      "type": "slide-horizontal",
      "duration": 350,
      "exit-dark-alpha-factor": 0.6,
      "effect": "cover"
    }
  },
  "backward": {
    "enter": {
      "type": "slide-horizontal",
      "duration": 350,
      "entrance": "from-end",
      "effect": "cover-push"
    },
    "exit": {
      "type": "slide-horizontal",
      "duration": 350,
      "exit-dark-alpha-factor": 0.6,
      "effect": "push"
    }
  }
}
```

- **`forward`**: Transition applied when navigating **to** a page.
- **`backward`**: Transition applied when navigating **back** from a page.
- **`enter` / `exit`**: Define how the current and next screens appear or disappear.

They are available for all transitions type.

- **`duration`**: Transition duration in milliseconds.
- **`exit-dark-alpha-factor`**: Optional dark overlay factor applied when leaving a screen in `cover-push` or `cover` effect
- **`entrance`**: Direction of the entering page. Options:
    - `from-start`
    - `from-end`
    - `from-top`
    - `from-bottom`
- **`effect`**: Defines the variant transition to use:
    - `push`
    - `cover-push`
    - `cover`

---

## Simplified with Shared Type

You can define the `type` (and other parameters) at the higher level. They will be injected into all `enter` and `exit` unless overridden.

```json
"transition": {
  "type": "slide-vertical",
  "duration": 350,
  "exit-dark-alpha-factor": 0.5,
  "entrance": "from-bottom"
  "forward": {
    "effect": "cover-push"
  },
  "backward": {
    "effect": "cover"
  }
}
```

Here:
- `type`, `duration`, `exit-dark-alpha-factor` and `entrance` are automatically applied to all transitions.
- For `forward` and `backward` we use a different variant.

---

## Key Points

- Slide transition moves screens in and out of view.
- **Horizontal**: `from-start`, `from-end`.
- **Vertical**: `from-top`, `from-bottom`.
- **Effects**: `push`, `cover-push`, `cover`.
