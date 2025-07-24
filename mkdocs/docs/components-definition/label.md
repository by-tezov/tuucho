# Label Component

A `Label` is a basic component used to display text content. It supports some styling.

## Example

```json
{
  "id": /* id object */,
  "subset": "label",
  "style": {
    "id": /* id object */,
    "font-size": /* dimension object */,
    "font-color": /* color object */
  },
  "content": {
    "value": /* text object */
  }
}
```

## 🔑 Keys

### `id`
- [Object ID](../object-definition/id.md): a unique identifier used to reference this component locally or externally.
- Optional if the object is inline and not meant for reuse.

---

### **`subset`**
- **MANDATORY**: Must be exactly `"label"` to define the component type.
- **MANDATORY if not resolved by another pointing reference** (e.g., when the component is not coming from a resolved reference).

---

## 🎨 `style`

Style defines visual aspects of the label like font size or color. You can reuse existing styles via the `id` key or define them inline.

```json
"style": {
    "id": /* id object */,
    "font-size": /* dimension object */,
    "font-color": /* color object */
}
```

- `id`: Reference to a shared [Style object](../object-definition/id.md).
- `font-size`: Reference to a [Dimension object](../object-definition/dimension.md).
- `font-color`: Reference to a [Color object](../object-definition/color.md).

---

## 📝 `content`

The content key holds the text to display. You can define the value inline or reference an external or shared text object.

```json
"content": {
  "value": /* text object */
}
```

- `value`: Reference to a [Text object](../object-definition/text.md) or inline text.
