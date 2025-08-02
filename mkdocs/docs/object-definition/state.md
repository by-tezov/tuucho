# State Definition

The `state` object is used to define the internal state configuration of a component.

---

## 1. Full Object Format

- `id`: follows the same rules outlined in [ID Definition](id.md). It can be a unique ID, a pointer, or a combination.
- `subset`: (optional) a string indicating the component type this state belongs to (e.g., `"field"`). This helps the parser optimize resolution. If omitted, the parser will infer the component type during resolution.
- Other keys are context-specific and defined by the component consuming the state. For valid option keys, refer to the relevant [Components Definition](../components-definition/index.md).

Example:

```json
"state": {
  "id": /* id object */
  "initial-value": /* text object */
}
```

In this example:

- `initial-value` is a [Text Object](text.md) and sets the field’s initial content.

---

## Important Notes

- The `subset` key helps the parser determine which component the state is applied to. While optional, using it is recommended for shared or dynamic states that span multiple components.
- Additional keys in the `state` object are defined by the target component. For valid properties, see the [Components Definition](../components-definition/index.md).

---

For more advanced patterns and component-specific keys, see the [Components Definition](../components-definition/index.md).
