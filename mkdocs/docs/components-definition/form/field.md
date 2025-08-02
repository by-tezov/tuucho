# Field Component

A `Field` component represents an input form field that supports validation rules and user guidance messages.

## Example

```json
{
  "id": /* id object */,
  "subset": "form-field",
  "option": {
    "validator": /* validator object or array of validator objects */
  },
  "content": {
    "title": /* text object */,
    "placeholder": /* text object */,
    "message-error": /* text object or array of text objects */
  },
  "state": {
    "initial-value": /* text object */
  }
}
```

## 🔑 Keys

### `id`
- [Object ID](../../object-definition/id.md): A unique identifier used to reference this component locally or externally.
- Optional if the object is inline and not meant for reuse.

---

### **`subset`**
- **MANDATORY**: Must be exactly `"field"` to define the component type.
- **MANDATORY if not resolved by another pointing reference** (e.g., when the component is not coming from a resolved reference).

---

### `option`

Validation options defining rules applied to the field input.

```json
"option": {
  "validator": /* validator object or array of validator objects */
}
```

- `validator`: A [Validator object](../../object-definition/validator.md) or an array of such objects defining validation rules.

**Note:** The `id-message-error` key inside each validator object links to the corresponding error message defined in the component's `content/message-error` array or object by matching the same ID.

---

### 📝 `content`

The content provides user-facing text such as titles, placeholders, and validation error messages.

```json
"content": {
  "title": /* text object */,
  "placeholder": /* text object */,
  "message-error": /* text object or array of text objects */
}
```

- `title`: A text object representing the field label shown to the user.
- `placeholder`: A text object displayed when the field is empty.
- `message-error`: Either a single or an array of [Text objects](../../object-definition/text.md) representing validation error messages linked to the validators.

---

### `state`

The state provides initial value.

```json
"state": {
  "initial-value": /* text object */
}
```

- `initial-value`: A text object representing the initial value shown to the user.
 
