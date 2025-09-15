# Form Components Definition

List of supported Form UI components:

- [Form-Field](field.md) (`subset`: `form-field`)

---

# Form Send Action

The `form-send` action is used to submit form data to a server endpoint. It is typically triggered by a `Button` component and only executes if all local field validations pass successfully.

---

## 🔧 Action Format

- `value`: `form-send://url/{the_server_endpoint}`
- `action-validated`: A follow-up action (commonly a navigation) to trigger after a successful server response.
- `action-denied`: A follow-up action to trigger after a successful server response, by default if you don't defined denied action, the error user feedback will be shown. This field is useful only if you want define a custom behavior. ...Documentation do not explain how yet...

### Example

Used in a `Button` component:

```json
"action": {
  "value": "form-send://url/form-from-page-home",
  "action-validated": "navigate://url/page-confirmation"
}
```

In this example:

- The form data is submitted to the server endpoint `form-from-page-home`.
- If the local validators succeed and the server confirms, the user is redirected to `page-confirmation`.

---

## 📨 Data Sent to Server

When triggered, and only if **all validators succeed locally**, the following data is sent as a POST payload to the server:

```json
queries: "version" and "url",

body:
{
    "fields": {
      "age": "55",
      "email": "my-email@gmail.com",
      "comment": "mmm, no",
      "hobies": "none"
    }
}

```

- The `fields` object includes all user-filled values from validated fields. Other components like toggle, checkbox, list,... will arrived.

---

## ✅ Server Response

The server must return one of the following:

### Success Response

```json
{
  "type": "all-succeed"
}
```

- Indicates that the form submission was accepted.
- Triggers the optional `action-validated` from the original action (e.g., navigating to a confirmation page).

### Error Response

```json
{
  "type": "failure-result",
  "content": [
    {
      "id": "id of invalid field",
      "reason": /* Text object */
    },...
  ]
}
```

- Indicates that the server rejected the submission, despite successful local validation.
- `content`: a list of rejected inputs, each with an optional `reason` [Text object](../../object-definition/text.md) that can be displayed in the UI to guide the user.
- If no `reason` is provided for an input that is locally valid, the application will ignore the server feedback for that input.  
  **Always provide a reason** to ensure the user receives feedback about the error.


For more on actions, see the [Action object](../../object-definition/action.md) and [Button component](../../components-definition/button.md)






