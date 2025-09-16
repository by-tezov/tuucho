# Action Definition

The `action` object defines an executable command with optional parameters. It supports specifying a mandatory command via the `value` key and additional context-dependent parameters in the `params` object.

---

## 1. Full Object Format

- `value` (**required**): specifies the action command using the format `command://authority/target`.
- `other` (optional): any key containing value whose depends on the specific action command.

Example for the `form-send` action:

```json
"action": {
  "value": "form-send://url/form-from-page-home",
  "action-validated": "navigate://url/page-confirmation",
  "action-denied": "navigate://url/page-failure"
}
```

This example instructs to send a form from the home page URL and navigate to a confirmation page upon validation.

---

## 2. String Format (Short Syntax)

When no parameters are needed, you can use a simple string with the action command:

```json
"action": "navigate://url/page-help"
```

This is equivalent to:

```json
"action": {
  "value": "navigate://url/page-help"
```

---

## 3. Command Format

The `value` follows the command syntax: command://authority/target

- **command**: the action type (e.g., `form-send`, `navigate`, etc.)
- **authority**: the resource category or domain (e.g., `url`)
- **target**: the specific target or resource identifier

---

## Summary Table

| Format                       | Purpose                           | Example                                     |
|-----------------------------|---------------------------------|---------------------------------------------|
| Full object with params      | Complex action with parameters    | See section 1                               |
| String value                | Simple action without parameters  | `"action": "form-send://url/form-from-page-home"` |
| Command format              | Syntax for the action command     | `command://authority/target`                 |

---

For more details and examples, see the following component definitions that use the `action` object:
- [Button](../components-definition/button.md)

More components using `action` will be added soon.
