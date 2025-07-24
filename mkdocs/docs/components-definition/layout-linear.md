# Layout-Linear Component

A `layout-linear` is a container component that arranges its child components in a linear sequence, either vertically or horizontally.
## Example

```json
{
  "id": /* id object */,
  "subset": "layout-linear",
  "style": {
    "orientation": /* "vertical" or "horizontal" */,
    "background-color": /* color object */,
    "fill-max-size": /* boolean */,
    "fill-max-width": /* boolean */
  },
  "content": {
    "items": [
      /* array of any components */
    ]
  }
}
```

## 🔑 Keys

### `id`
- [Object ID](../object-definition/id.md): A unique identifier used to reference this component locally or externally.
- Optional if the object is inline and not meant for reuse.

---

### **`subset`**
- **MANDATORY**: Must be exactly `"layout-linear"` to define the component type.
- **MANDATORY if not resolved by another pointing reference** (e.g., when the component is not coming from a resolved reference).

---

## 🎨 `style`

Defines layout styling properties.

```json
"style": {
  "orientation": /* string: "vertical" or "horizontal" */,
  "background-color": /* color object */,
  "fill-max-size": /* boolean */,
  "fill-max-width": /* boolean */
}
```

- `orientation`: Specifies the direction in which child components are arranged; either `"vertical"` or `"horizontal"`.
- `background-color`: Reference to a [Color object](../object-definition/color.md) defining the background color.
- `fill-max-size`: Boolean flag to fill the maximum available size in both width and height.
- `fill-max-width`: Boolean flag to fill the maximum available width only.

---

## 📝 `content`

The `items` array holds child components inside this linear layout.

```json
"content": {
  "items": [
    /* components */
  ]
}
```

- `items`: An array of any components. See the full list in the [Component Index](../components-definition/index.md).

