# Spacer Component

A `Spacer` is a layout component used to create flexible empty space between other components.

## Example

```json
{
  "id": /* id object */,
  "subset": "spacer",
  "style": {
    "weight": /* dimension object */,
    "width": /* dimension object */,
    "height": /* dimension object */
  }
}
```

## 🔑 Keys

### `id`
- [Object ID](../object-definition/id.md): A unique identifier used to reference this component locally or externally.
- Optional if the object is inline and not meant for reuse.

---

### **`subset`**
- **MANDATORY**: Must be exactly `"spacer"` to define the component type.
- **MANDATORY if not resolved by another pointing reference** (e.g., when the component is not coming from a resolved reference).

---

## 🎨 `style`

The style controls how the spacer allocates space.

```json
"style": {
  "weight": /* dimension object */,
  "width": /* dimension object */,
  "height": /* dimension object */
}
```

- `weight`: A dimension defining flexible space allocation inside a `LayoutLinear` component. **Cannot be used simultaneously with `width` or `height`.**
- `width`, `height`: A dimension defining fixed horizontal space. Cannot be used with `weight`.

Check dimension object [Dimension object](../object-definition/dimension.md)

---

## Usage Notes

- `weight` only applies when the spacer is inside a `LayoutLinear` component.
- Use `width` or `height` for fixed-sized spacers outside linear layouts.

