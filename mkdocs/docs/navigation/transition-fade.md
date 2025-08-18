# Transition: Fade

> The **fade transition** controls how screens appear and disappear using alpha blending.  
> TUUCHO provides a flexible JSON structure where defaults can be inherited from higher levels and overridden locally.

---

## Simplest

```json
"transition": "fade"
```

Nothing more to do, you will have fade transition in all directions ith default parameters

## Full Definition

A complete fade transition object looks like this:

```json
"transition": {
  "forward": {
    "enter": { "type": "fade", "alpha-initial":"0.2" },
    "exit": { "type": "fade", "alpha-initial":"0.3" }
  },
  "backward": {
    "enter": { "type": "fade", "alpha-initial":"0.0" },
    "exit": { "type": "fade", "alpha-initial":"0.5" }
  }
}
```

- **`forward`**: Transition applied when navigating **to** a page.
- **`backward`**: Transition applied when navigating **back** from a page.
- **`enter` / `exit`**: Define how the current and next screens appear or disappear.

They are available for all transitions type.

- **`alpha-initial`**: Starting opacity (0.0 = fully transparent, 1.0 = fully opaque).

---

## Simplified with Shared Type

The `type` can be placed at the higher level, and will be automatically applied to all `enter` and `exit` if missing:

```json
"transition": {
  "forward": {
    "type": "fade",
    "enter": { "alpha-initial":"0.0" },
    "exit": { "alpha-initial":"0.5" }
  },
  "backward": {
    "type": "fade",
    "enter": { "alpha-initial":"0.0" },
    "exit": { "alpha-initial":"0.5" }
  }
}
```

Here, `type: "fade"` is automatically injected into each `enter` and `exit`. This logic works will all transition parameters

---

## Global Type

You can also define `type` at the root of the transition object.  
It applies to all sub-objects unless overridden.

```json
"transition": {
  "type": "fade",
  "forward": {
    "enter": { "alpha-initial":"0.0" },
    "exit": { "alpha-initial":"0.5" }
  },
  "backward": {
    "enter": { "alpha-initial":"0.0" },
    "exit": { "type": "slide-vertical" }  --> Look here, the type for this one will be different
  }
}
```

- The `backward/exit` explicitly overrides the type with a **slide-vertical** transition.

---

## Key Points

- Fade transition uses **opacity changes** to animate screens in and out.
- **`alpha-initial`** controls starting opacity and can differ for enter/exit.

