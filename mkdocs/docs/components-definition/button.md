# Button Component

A `Button` is an interactive component that typically triggers an action when tapped. It displays a `Label` and is linked to an `Action`.

## Example

```json
{
  "id": /* id object */,
  "subset": "button",
  "content": {
    "label": /* label component */,
    "action": /* action object */
  }
}
```

## 🔑 Keys

### `id`
- [Object ID](../object-definition/id.md): A unique identifier used to reference this component locally or externally.
- Optional if the object is inline and not meant for reuse.

---

### **`subset`**
- **MANDATORY**: Must be exactly `"button"` to define the component type.
- **MANDATORY if not resolved by another pointing reference** (e.g., when the component is not coming from a resolved reference).

---

## 📝 `content`

Defines the button’s content including the displayed label and the triggered action.

```json
"content": {
    "label": /* label component */,
    "action": /* action object */
}
```

- `label`: A [Label component](../components-definition/label.md) used to render the button’s text and style.
- `action`: An [Action object](../object-definition/action.md) triggered on press.

---

## 🚦 Supported Action Commands

The button supports the following action command for navigation:

- `navigate://url/{the_page_to_reach}` — Performs navigation to the specified page URL.
- `form-send://url/{the_server_endpoint}` — Sends the form data to the server if the form is valid, waits for the server confirmation, then navigates to the page URL specified in the action's `params`.
  Check [Action object](../object-definition/action.md) and [Form Send action](../components-definition/form/index.md)

