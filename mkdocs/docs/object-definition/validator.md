# Validator Definition

The `validator` object is used to enforce validation rules for input components. It supports multiple formats—from simple strings to detailed object arrays—offering flexibility in how validation logic is defined and reused.

---

## 1. Array Format (Multiple Validators)

You can define multiple validators using an array of objects.

Each validator object must include:

- `type`: the validation type (e.g., `"string-not-null"`, `"string-min-value"`, `"string-email"`, etc.)
- `id-message-error`: a reference to the error message shown when validation fails. This must be an ID pointing to a message defined in the content section. It is context-dependent—refer to the desired component's documentation for more details.
- Additional keys may be required depending on the `type` (e.g., `value` for min/max validators).

Example:

```json
"validator": [
  {
    "type": "string-min-value",
    "value": "18",
    "id-message-error": "*validator-1"
  },
  {
    "type": "string-max-value",
    "value": "65",
    "id-message-error": "*validator-2"
  },
  {
    "type": "string-not-null",
    "id-message-error": "*validator-4"
  }
]
```

---

## 2. Object Format (Single Validator)

When only one validator is needed, you can define it as a single object:

```json
"validator": {
  "type": "string-email",
  "id-message-error": "*validator-1"
}
```

This format behaves the same as the array version, but it's optimized for single-rule use cases.

---

## 3. String Format (Short Syntax)

For basic use cases, you can use a simple string to define the validator type.

In this format:

- The string value is used as the `type`.
- The `id-message-error` is auto-resolved using the first message found in the `content` section.

Example:

```json
"validator": "string-not-null"
```

Equivalent to:

```json
"validator": {
  "type": "string-not-null",
  "id-message-error": /* auto-resolved */
}
```

---

## 4. Supported Validator Types

Below are the currently supported validator types:

- `string-not-null`  
  Ensures the input is not null or empty.

- `string-email`  
  Validates that the input is a valid email format.

- `string-only-digits`  
  Validates that the input contains only digits.

- `string-min-length`  
  Ensures the input length is at least a minimum value.  
  **Requires:** `length` (the minimum length as a number or string).

- `string-max-length`  
  Ensures the input length does not exceed a maximum value.  
  **Requires:** `length` (the maximum length as a number or string).

- `string-min-digit-length`  
  Ensures the number of digits in the input is at least the specified minimum.  
  **Requires:** `length` (the minimum digit count as a number or string).

- `string-min-value`  
  Ensures the numeric value of the input is at least the specified minimum.  
  **Requires:** `value` (the minimum numeric value as a number or string).

- `string-max-value`  
  Ensures the numeric value of the input does not exceed the specified maximum.  
  **Requires:** `value` (the maximum numeric value as a number or string).

---

## Important Notes

- The `type` field is **mandatory** in all formats.
- The `id-message-error` field is also **mandatory**, unless using the short string format—then it is automatically resolved.
- Additional keys like `value` are required depending on the validator `type`.

---

## Summary Table

| Format               | Purpose                          | Example                                         |
|----------------------|-----------------------------------|-------------------------------------------------|
| Array of objects      | Multiple validators               | See section 1                                   |
| Single object         | One validator with full control   | `"validator": { "type": "string-email", ... }`  |
| String (short syntax) | Simple validator with auto-error  | `"validator": "string-not-null"`                |

---

For more details and examples, see the following component definitions that use the `validator` object:
- [Field Component](../components-definition/field.md)

