# Component Setting

```json
"setting": {
  "shadower": { ... },
  "navigation": { ... }
}
```

`shadower` and `navigation` is only meaningful on `root/setting`. There are ignored if you apply them on sub-component.

---

## Shadower

The `shadower` object enables **contextual data loading**.  
It controls whether extra contextual resources (components, content, texts) are requested associated to context (user specific data).

### Navigation Forward / Backward

- To activate contextual loading, you must explicitly **enable the shadower** on navigation forward and/or backward.
- Two flags are available:
    - `enable`: `true` or `false` — turn the shadower on or off for that direction.
    - `wait-done-to-render`:
        - `true` → synchronous, the page waits until contextual resources are fully loaded before rendering.
        - `false` → asynchronous (default), the page renders immediately and contextual resources are added when ready.

Example:

```json
"setting": {
    "shadower": {
        "navigate-forward": {
            "wait-done-to-render": false/true,
            "enable": true/true
        },
        "contextual": {
            "url": {
              "component": "${url-origin}-contextual-components"
            }
        }
    }
}
```

---

### Contextual

The `contextual` section defines **URLs for contextual resources** that should be loaded dynamically based on the current page. You can use ${url-origin} as replacement token.

You can specify contextual URLs for:
- `component`
- `content`
- `text`

URLs can also be declared individually inside the **ID object** of each resource. See [ID object](id.md).

---

## Navigation

For `setting/navigation` Refer to the [Navigation](../navigation/index.md) page for details.

---
