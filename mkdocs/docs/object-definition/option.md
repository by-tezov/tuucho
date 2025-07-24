# Option Definition

The `option` object is used to define customizable configuration or behavioral values for components. It supports both full object definitions and pointer references, promoting reuse and modular organization across screens or subs.

---

## 1. Full Object Format

- `id`: follows the same rules outlined in [ID Definition](id.md). It can be a unique ID, a pointer, or a combination.
- `subset`: (optional) a string indicating the component type this style belongs to (e.g., `"button"`, `"label"`). This helps the parser optimize resolution. If omitted, the parser will infer the component type during resolution.
- Other keys are context-specific and depend on the component that consumes the option. For valid option keys, refer to the relevant [Components Definition](../components-definition/index.md).

Example:

```json
"option": {
  "id": "email-field-options",
  "validator": /* validator */
}
```

In this example:

- `validator` follow the [validator](validator.md) object rules.

---

## 2. Pointer Reference (Short Syntax)

An `option` can be defined as a primitive string starting with `*`, referencing another option defined in the same page or in Subs.

- **Note:** Plain string values (not starting with `*`) are **not allowed** as option definitions.

Example:

```json
"option": "*shared-email-field-options"
```

Equivalent to:

```json
"option": {
  "id": "*shared-email-field-options"
}
```

This allows referencing shared configuration logic across components for consistency and reduced duplication.

---

## Important Notes

- The `subset` key assists the parser in identifying which component the option applies to. Although optional, specifying it can improve parsing and validation, especially for shared or split elements. If the option is defined for the entire component, including `subset` is not necessary.
- Additional keys in the `option` object are defined by the target component. For valid properties, see the [Components Definition](../components-definition/index.md).

---

## Summary Table

| Format                          | Purpose                                 | Example                                   |
|---------------------------------|-----------------------------------------|-------------------------------------------|
| Full object with `id` and keys  | Complete option configuration           | See above example                         |
| String starting with `*`        | Pointer to shared/local option object   | `"option": "*shared-email-field-options"` |

---

For more advanced examples and usage patterns, see the [Components Definition](../components-definition/index.md).
