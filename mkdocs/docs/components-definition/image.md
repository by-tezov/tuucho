# Label Component

A `Image` is a basic component used to display image. It supports some styling.

## Example

```json
{
  "id": /* id object */,
  "subset": "image",
  "style": {
    "id": /* id object */,
    "shape": /* "rounded" or "rounded-square" */,
    "height": /* dimension object */,
    "width": /* dimension object */,
    "padding": /* dimension object */,
    "tintColor": /* color object */,
    "alpha": /* dimension object */,
    "backgroundColor": /* color object */
  },
  "content": {
    "values": /* array of image object */,
    "description": /* text object */,
  }
}
```

## 🔑 Keys

### `id`
- [Object ID](../object-definition/id.md): a unique identifier used to reference this component locally or externally.
- Optional if the object is inline and not meant for reuse.

---

### **`subset`**
- **MANDATORY**: Must be exactly `"image"` to define the component type.
- **MANDATORY if not resolved by another pointing reference** (e.g., when the component is not coming from a resolved reference).

---

## 🎨 `style`

Style defines visual aspects of the label like font size or color. You can reuse existing styles via the `id` key or define them inline.

```json
"style": {
    "id": /* id object */,
    "shape": /* "rounded" or "rounded-square" */,
    "height": /* dimension object */,
    "width": /* dimension object */,
    "padding": /* dimension object */,
    "tintColor": /* color object */,
    "alpha": /* dimension object */,
    "backgroundColor": /* color object */
}
```

- `id`: Reference to a shared [Style object](../object-definition/id.md).
- `shape`: Specifies the shape frame of the image; either `"rounded"` or `"rounded-square"`.
- `height`, `width`, `padding`, `alpha`: Reference to a [Dimension object](../object-definition/dimension.md).
- `tintColor`, `backgroundColor`: Reference to a [Color object](../object-definition/color.md).

---

## 📝 `content`

The `values` array holds image object. And `description` is an accessibility text object.

```json
"content": {
    "values": /* array of image object */,
    "description": /* text object */,
}
```

- `values`: Reference to a [Image object](../object-definition/image.md) or inline text.
- `description`: Reference to a [Text object](../object-definition/text.md) or inline text.
