# Component Definition

The `component` key defines a UI element, either directly or by referencing a shared definition. It supports reuse via references, and provides configuration using `id`, `subset`, `style`, `content`, and `option`.

---

## 1. Full Object Format

- `id`: follows the same rules outlined in [ID Definition](id.md). It can be a unique ID, a pointer, or a combination.
- `subset`: **Mandatory** unless inherited through a reference. Indicates the component type (e.g., `button`, `input`, `label`).
- `option`: A [Option Object](./option.md) applied to this component.
- `style`: A [Style Object](./style.md) applied to this component.
- `content`: A [Content Object](./content.md) applied to this component.

Example:

```json
"component": {
  "id": "submit-button",
  "subset": "button",
  "style": "*style-primary-button",
  "content": {
    "label": "Submit",
    "action": {
      "primaries": "form-send://url/form-from-newletter-subscription",
      "validated": "navigate://url/auth/page-confirmation"
    }
  },
  "option": {
    "analytics": ["on-appear", "on-click"]
  }
}
```

---

## 2. Pointer Reference (Short Syntax)

A component can be defined by referencing a shared or previously defined component using a string starting with `*`.

```json
"component": "*shared-submit-button"
```

Equivalent to:

```json
"component": {
  "id": "*shared-submit-button"
}
```

If the referenced component **does not define `subset`**, the current reference **must include it**.

---

## Subset Resolution Rules

- `subset` is **mandatory** if:
    - The component is defined inline and not referencing anything.
    - The referenced component does **not** define a `subset`.

- `subset` is **optional** if:
    - The referenced component already defines it.

Failure to define `subset` where required will result in a parsing error.

---

## Summary Table

| Field     | Required | Description                                                 |
|-----------|----------|-------------------------------------------------------------|
| `id`      | Optional | Identifier or pointer (see [ID](./id.md))                   |
| `subset`  | Conditional | Required unless inherited through a pointer                 |
| `option`  | Optional | A [Option Object](./option.md) to apply to this component   |
| `style`   | Optional | A [Style Object](./style.md) to apply to this component     |
| `content` | Optional | A [Content Object](./content.md) to apply to this component |

---

For supported `subset` values and component-specific options, see the [Components Definition](../components-definition/index.md).
