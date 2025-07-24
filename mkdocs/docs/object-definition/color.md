# Color Definition

The `color` object is used to define multi-context color values. It supports both direct definitions and references, allowing for consistent theming and reusability across screens or subs.

---

## 1. Full Object Format

- `id` follows the same rules outlined in [ID Definition](id.md), meaning it can be either a unique definition, a pointer, or a combination.
- `default`: defines the fallback or main color value (**required**)
- Additional keys (e.g., `dark`, `light`, or any custom theme/context code) define alternative color values for different contexts or themes

Example:

```json
"key-color": {
  "id": "primary-color",
  "default": "#007bff",
  "dark": "#0056b3",
  "light": "#66b2ff"
}
```

This format ensures the application can render colors based on the active theme or context. If no value is found for the current context, the `default` value is used.

- `key-color` depends on the components. Check the component definition.

---

## 2. String Format (Short Syntax)

You can simplify the declaration by using a single string for the default color:

```json
"key-color": "#007bff"
```

This is equivalent to:

```json
"key-color": {
  "default": "#007bff"
}
```

This format is useful when you’re not using multiple themes or want to quickly define a static color.

---

## 3. Pointer Reference (Short Syntax)

Instead of defining color directly, you can reference a color defined elsewhere (in the same page or in Subs) by prefixing the value with `*`.

Example:

```json
"key-color": "*shared-primary-color"
```

Equivalent to:

```json
"key-color": {
  "id": "*shared-primary-color"
}
```

The engine will resolve the pointer by searching first in the local page, then in the shared Subs.

This approach helps maintain consistent colors across pages and reduces duplication.

---

## Summary Table

| Format                            | Purpose                               | Example                                   |
|-----------------------------------|-------------------------------------|-------------------------------------------|
| Full object with contexts         | Multicontext or themed colors        | See section 1                             |
| String value                     | Quick/default color                   | `"color": "#ff0000"`                      |
| String starting with `*`          | Pointer to shared/local color object | `"color": "*shared-primary-color"`       |

---

For more advanced examples and usage patterns, see the [Components Definition](../components-definition/index.md).
