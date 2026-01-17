# Form Components Definition

List of supported Form UI components:

- [Form-Field](field.md) (`subset`: `form-field`)

---

# Form Send Action

The `form-send` action is used to submit form data to a server endpoint. It is typically triggered by a `Button` component and only executes if all local field validations pass successfully.

---

## 🔧 Action Format

- `primary`: `form-send://url/{the_server_endpoint}`
- `validated`: A follow-up action (commonly a navigation) to trigger after a successful server response.
- `denied`: A follow-up action to trigger after a successful server response, by default if you don't defined denied action, the error user feedback will be shown. This field is useful only if you want define a custom behavior. ...Documentation do not explain how yet...

### Example

Used in a `Button` component:

```json
"action": {
  "primaries": "form-send://url/form-from-page-home",
  "validated": "navigate://url/page-confirmation"
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
  "subset": "form",
  "all-succeed": true,
  "action": {
    "before": ["store://key-value/save?login-authorization=${token}"],
    "after": ["store://key-value/save?foo=bar"]
  }
}
```

- Indicates that the form submission was accepted.
- Triggers the optional `validated` from sending action (e.g., navigating to a confirmation page).
- `before` action will be executed before the `validated` from the sending action.
- `after` action will be executed after the `validated` from the sending action.

### Error Response

```json
{
  "subset": "form",
  "all-succeed": false,
  "failure-results": [
    {
      "id": "id of invalid field",
      "reason": /* Text object */
    },...
  ],
  "action": {
    "before": ["store://key-value/save?fail-login=3"],
    "after": ["store://key-value/save?foo=bar"]
  }
}
```

- Indicates that the server rejected the submission, despite successful local validation.
- `failure-result`: a list of rejected inputs, each with an optional `reason` [Text object](../../object-definition/text.md) that can be displayed in the UI to guide the user.
- If no `reason` is provided for an input that is locally valid, the application will ignore the server feedback for that input.  
  **Always provide a reason** to ensure the user receives feedback about the error.
- Triggers the optional `denied` from sending action (e.g., navigating to a confirmation page).
- `before` action will be executed before the `denied` from the sending action.
- `after` action will be executed after the `denied` from the sending action.

For more on actions, see the 

- [Action object](../../object-definition/action.md) 
- [Button component](../../components-definition/button.md)






