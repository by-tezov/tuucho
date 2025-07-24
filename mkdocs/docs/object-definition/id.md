# ID Definition

The `id` field plays a **dual role** in defining components within the UI rendering system. It can serve as a **unique identifier** or as a **pointer (reference)** to another element, enabling both targeted interactions and structural reusability.

---

## 1. Full Object Format

- `value`: A **unique ID** to target the component (for animations or logic)
- `source`: A **reference** to reuse a shared or predefined component

Example:

```json
"id": {
  "value": "button-form-confirm",
  "source": "*shared-button-primary"
}
```

In this example:

- `"button-form-confirm"` is the unique ID you can target in animations or logic.
- `"*shared-button-primary"` is a pointer referencing a shared component defined elsewhere.

This pattern combines reusability with precise control, useful when you want to interact with a reused component as a distinct element.

---

## 2. Unique Identifier (Short Syntax)

When the `id` is a simple string **not starting with `*`**, it acts as a unique identifier for the component:

- Used for animations
- Used for targeted actions or dynamic behaviors

Example:

```json
"id": "login-form"
```

This is equivalent to:

```json
"id": {
  "value": "login-form"
}
```

Each `id` should be unique within its scope (e.g., within a page or a subs collection).

---

## 3. Pointer Reference (Short Syntax)

If the `id` is a string starting with `*`, it is treated as a pointer (reference) to another component:

- The engine first looks for the referenced element in the local page scope, then in the shared subs.
- This enables reusing shared components without redefining them.

Example:

```json
"id": "*shared-header"
```

```json
"id": {
  "source": "*shared-header"
}
```

In this case, the element's `id` points to the shared `"shared-header"` component.

---

## 4. Auto-Generated IDs

If a component is only defined as a pointer (e.g., `"id": "*some-shared-id"`) without an explicit unique ID, TUUCHO automatically assigns an internal ID.

- These auto-generated IDs **cannot** be referenced in animations or logic because their exact value is unknown.

---

## Summary Table

| Format                          | Purpose                                   | Example                                                  |
|--------------------------------|-------------------------------------------|----------------------------------------------------------|
| Object with `value` + `source` | Unique + reusable (combined reference)   | `<json>"id": { "value": "btn-ok", "source": "*shared-button" }</json>` |
| String without `*`              | Unique identifier                         | `<json>"id": "login-form"</json>`                        |
| String starting with `*`        | Pointer to a shared/local component       | `<json>"id": "*shared-header"</json>`                    |

Use the **object format** if you need both a pointer and a unique, trackable ID for animation or other features.

---

For more details, see the [Components Definition](../components-definition/index.md).
