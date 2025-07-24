# Style Definition

The `style` object is used to define visual styling information for components. It supports both direct definitions and references, enabling style reuse and modularity across screens or subs.

---

## 1. Full Object Format

- `id`: follows the same rules outlined in [ID Definition](id.md). It can be a unique ID, a pointer, or a combination.
- `subset`: (optional) a string indicating the component type this style belongs to (e.g., `"button"`, `"label"`). This helps the parser optimize resolution. If omitted, the parser will infer the component type during resolution.
- Other keys depend on the specific component and define style properties relevant to that component. For detailed style properties, refer to the [Components Definition](../components-definition/index.md).

Example:

```json
"style": {
  "id": "primary-button-style",
  "subset": "button",
  "backgroundColor": "#007BFF",
  "textColor": "#FFFFFF",
  "borderRadius": 4
}
```

In this example:

- `backgroundColor` and `textColor` follow the [color](color.md) object rules.
- `borderRadius` follows the [dimension](dimension.md) object rules.

For simplicity, the example uses the short syntax (primitive values).

---

## 2. Pointer Reference (Short Syntax)

A `style` can be defined as a primitive string starting with `*`, which acts as a reference to another style defined elsewhere (in the same page or in Subs).

- **Note:** Plain string values (not starting with `*`) are **not allowed** as styles.

Example:

```json
"style": "*shared-primary-button-style"
```

Equivalent to:

```json
"style": {
  "id": "*shared-primary-button-style"
}
```

The engine resolves this pointer by searching first in the local page, then in shared Subs, allowing for consistent style reuse.

---

## Important Notes

- The `subset` key assists the parser in identifying which component the style applies to. Although optional, specifying it can improve parsing and validation, especially for shared or split elements. If the style is defined for the entire component, including `subset` is not necessary.
- Other keys in the style object depend on the target component and should be defined accordingly. For valid style properties, refer to the specific [Components Definition](../components-definition/index.md).

---

## Summary Table

| Format                          | Purpose                               | Example                                   |
|---------------------------------|-------------------------------------|-------------------------------------------|
| Full object with `id` and `subset` | Complete style definition             | See above example                         |
| String starting with `*`          | Pointer to shared/local style object  | `"style": "*shared-primary-button-style"` |

---

For more advanced examples and usage patterns, see the [Components Definition](../components-definition/index.md).
