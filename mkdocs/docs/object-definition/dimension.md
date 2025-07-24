# Dimension Definition

The `dimension` object is used to define unitless numeric values that represent sizes, lengths, or other measurements. It supports both direct definitions and references, allowing for reuse and consistency across screens or subs.

---

## 1. Full Object Format

- `id` follows the same rules outlined in [ID Definition](id.md), meaning it can be either a unique definition, a pointer, or a combination.
- `default`: defines the fallback or main numeric value (**required**)
- Additional keys (e.g., different contexts or variants) can define alternative numeric values

Example:

```json
"key-dimension": {
  "id": "button-height",
  "default": 48,
  "small": 36,
  "large": 64
}
```

This format ensures the application can render dimensions based on the active context or variant. If no value is found for the current context, the `default` value is used.

- `key-dimension` depends on the components. Check the component definition.

---

## 2. Primitive Format (Short Syntax)

You can simplify the declaration by using a single numeric value:

```json
"key-dimension": 48
```

This is equivalent to:

```json
"key-dimension": {
  "default": 48
}
```

This format is useful when you’re not using multiple variants or want to quickly define a static dimension.

---

## 3. Pointer Reference (Short Syntax)

Instead of defining dimension directly, you can reference a dimension defined elsewhere (in the same page or in Subs) by prefixing the value with `*`.

Example:

```json
"key-dimension": "*shared-button-height"
```

Equivalent to:

```json
"key-dimension": {
  "id": "*shared-button-height"
}
```

The engine will resolve the pointer by searching first in the local page, then in the shared Subs.

This approach helps maintain consistent dimensions across pages and reduces duplication.

---

## Important Note on Units

Dimension values are **unitless** numbers. The interpretation of units (pixels, dp, em, etc.) is handled by the component that consumes these values. This allows flexibility across different platforms or rendering contexts.

---

## Summary Table

| Format                            | Purpose                                | Example                                     |
|-----------------------------------|--------------------------------------|---------------------------------------------|
| Full object with variants          | Multi-variant or contextual values    | See section 1                               |
| Numeric value                     | Quick/default dimension                | `"dimension": 24`                           |
| String starting with `*`           | Pointer to shared/local dimension object | `"dimension": "*shared-padding"`             |

---

For more advanced examples and usage patterns, see the [Components Definition](../components-definition/index.md).
