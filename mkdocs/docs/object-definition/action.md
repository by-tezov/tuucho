# Action Definition

The `action` object is used to define executable command. It supports both full object definitions and pointer references, promoting reuse and modular organization across screens or subs.

## Available Actions

Actions define how components trigger behavior within the application — such as navigation, data storage, or form submission.  
Each action follows teh pattern `command://authority/target?{key}={value}&{key}={value}...` and can be attached to interactive elements like buttons or form responses.

---

### 🧭 Navigation Actions

#### `navigate://url/{the_page_to_reach}`
Used to navigate to another page within the application by specifying the destination page url endpoint

**Supported by:**
- [Button](../components-definition/button.md)
- [Form Send/Response](../components-definition/form/index.md)

---

#### `navigate://local-destination/{back or finish}`
Used for local navigation logic:
- `back` – moves one step back in the navigation stack
- `finish` – finish Tuucho Engine

**Supported by:**
- [Button](../components-definition/button.md)
- [Form Send/Response](../components-definition/form/index.md)

---

### 📤 Form Actions

#### `form-send://url/{the_server_endpoint}`
Sends form data to the specified server endpoint.  
Commonly used with buttons or automatic form submissions to trigger backend processing.

**Supported by:**
- [Button](../components-definition/button.md)

---

### 💾 Store Actions

#### `store://key-value/save?{key}={value}&{key}={value}...`
Stores one or more key/value pairs locally on the device.  
Useful for caching user preferences, temporary data, or server responses.

**Supported by:**
- [Button](../components-definition/button.md)
- [Form Send/Response](../components-definition/form/index.md)

---

#### `store://key-value/remove?{key}={value}&{key}={value}...`
Removes one or more stored key/value pairs from the local store.

**Supported by:**
- [Button](../components-definition/button.md)
- [Form Send/Response](../components-definition/form/index.md)

---

## 1. Full Object Format

- `id`: follows the same rules outlined in [ID Definition](id.md). It can be a unique ID, a pointer, or a combination.
- `primary` (**required**): specifies actions command using the format `command://authority/target?{key}={value}&{key}={value}...`.

Example for the `form-send` action:

```json
"action": {
  "id": /* id object */,
  "primaries": ["form-send://url/form-from-page-home"],
  "validated": "form-send://url/form-from-page-home"
}
```

This example instructs to send a form from the home page URL and navigate to a confirmation page upon validation. If only one action, it can be a string value as `validated`. 

---

## 2. String Format (Short Syntax)

When only one action are needed, you can use a simple string with the action command:

```json
"action": "navigate://url/page-help"
```

This is equivalent to:

```json
"action": {
  "primaries": "navigate://url/page-help"
```

---

## 3. Command Format

The `primary` and all context component action (example: `validated` or `denied`) follows the command syntax: command://authority/target?key1=value&key?=value2

- **command**: the action type (e.g., `form-send`, `navigate`, etc.)
- **authority**: the resource category or domain (e.g., `url`)
- **target**: the specific target or resource identifier
- **query**: key/value pair

---

## Summary Table

| Format                       | Purpose                           | Example                                           |
|-----------------------------|---------------------------------|---------------------------------------------------|
| Full object with params      | Complex action with parameters    | See section 1                                     |
| String value                | Simple action without parameters  | `"action": "form-send://url/form-from-page-home"` |
| Command format              | Syntax for the action command     | `command://authority/target?queries`              |

---

More components using `action` will be added soon.
