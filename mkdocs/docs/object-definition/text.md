# Text Definition

The `text` object is used to define multilingual textual content. It supports both direct definitions and references, allowing for localization and reusability across screens or subs.

---

## 1. Full Object Format

- `id`: follows the same rules outlined in [ID Definition](id.md), meaning it can be either a unique definition, a pointer, or a combination.
- `default`: defines the fallback or main text (**required**)
- Additional keys (e.g., `en`, `fr`, `ja`, etc.) define translations for different language codes

Example:

```json
"key-text": {
  "id": "title",
  "default": "Click here",
  "en": "Click here",
  "fr": "Cliquez ici",
  "ja": "ここをクリック"
}
```

This format ensures the application can render text based on the active language. If no translation is found for the current language, the `default` value is used.

- `key-text` depend of the components. Check the component definition.

---

## 2. String Format (Short Syntax)

You can simplify the declaration by using a single string:

```json
"key-text": "Click here"
```

This is equivalent to:

```json
"key-text": {
  "default": "Click here"
}
```

This format is useful when you're not using multilingual support or when you want to define quick UI prototypes.

---

## 3. Pointer Reference (Short Syntax)

Instead of defining text directly, you can reference a text defined elsewhere (in the same page or in Subs) by prefixing the value with `*`.

Example:

```json
"key-text": "*text-button-login"
```

```json
"key-text": {
  "id": "*text-button-login"
}
```

The engine will resolve the pointer by searching first in the local page, then in the shared Subs.

This approach is useful for maintaining consistency across pages and reducing repetition in large applications.

---

## Summary Table

| Format                                                        | Purpose                             | Example                                   |
|---------------------------------------------------------------|-------------------------------------|-------------------------------------------|
| [Full object with translations](#1-full-object-format)        | Multilingual content                | See section 1                             |
| [String value](#2-string-format-short-syntax)                 | Quick/default text                  | `"text": "Submit"`                        |
| [String starting with `*`](#3-pointer-reference-short-syntax) | Pointer to shared/local text object | `"text": "*shared-title"`                 |


---

For more advanced examples and usage patterns, see the [Components Definition](../components-definition/index.md) or the tutorial section (coming soon).