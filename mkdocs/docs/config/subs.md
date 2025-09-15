# Subs Definition

The `subs` system defines reusable chunks of content referenced throughout the app by ID. They allow a modular, maintainable, and dynamic way to define UI and content logic.

All subs must be referenced by id pointer where they are needed as replacement 📖 See: [`object-definition/id.md`](../object-definition/id.md)

```json
{
  "components": [ ... ],
  "styles": [ ... ],
  "options": { ... },
  "contents": [ ... ],
  "texts": { ... },
  "colors": { ... },
  "dimensions": { ... }
}
```

Each `subs` file can contain any combination of the following keys:

- `texts` – Translatable strings organized by group. See [Text Object Definition](../object-definition/text.md).
- `colors` – Grouped color values used throughout styles and components. See [Color Object Definition](../object-definition/color.md).
- `dimensions` – Numeric or fractional sizes like font, padding, or spacing. See [Dimension Object Definition](../object-definition/dimension.md).
- `styles` – Reusable visual rules (font-size, color, etc.) referenced by components. See [Style Object Definition](../object-definition/style.md).
- `options` – Define data-driven value lists, such as select dropdown choices. See [Options Object Definition](../object-definition/option.md).
- `contents` – Logical structures for fields (title, placeholder, error messages). See [Content Object Definition](../object-definition/content.md).
- `components` – UI building blocks composed from content, style, and logic. See [Component Object Definition](../object-definition/component.md).

---

## 🔤 Texts

```json
{
  "texts": {
    "common": {
      "text-body-content-help": "There is no help available at the moment",
      "text-body-content-complain": {
        "default": "Mmm, I'm listening, yes.",
        "fr": "Je m'en fou"
      }
    }
  }
}
```

Texts are grouped under categories (`common`, `form`, etc.) and are accessed via ID references like `*form:text-body-content-help`.

> ⚠️ **Important:** If no group is specified in a reference, the system will default to searching in the `common` group.

---

## 🎨 Colors

```json
{
  "colors": {
    "common": {
      "black": "#FF000000",
      "gray": {
        "default": "#FFD9D9D9",
        "dark": "#FF3C3C3C"
      }
    },
    "background": {
      "gray-light": {
        "default": "#FFD9D9D9",
        "dark": "#FF2B2B2B"
      },
      "funny-color": "#FF66D9FF"
    }
  }
}
```

Colors are grouped (e.g. `common`, `background`) and accessed using references like `*common:black` or `*background:funny-color`.

> ⚠️ **Important:** If no group is specified in a reference, the system will default to searching in the `common` group.

---

## 📏 Dimensions

```json
{
  "dimensions": {
    "font": {
      "title": "32",
      "body": {
        "default": "24",
        "huge": "48"
      }
    },
    "padding": {
      "spacer-24": "24",
      "spacer-48": "48"
    }
  }
}
```

Dimensions are numeric or fractional values grouped by purpose (`font`, `padding`, etc.) and referenced via `*font:title` or `*padding:spacer-24`.

> ⚠️ **Important:** If no group is specified in a reference, the system will default to searching in the `common` group.

---

## 🎨 Styles

```json
{
  "styles": [
    {
      "id": "title-label",
      "font-size": "*font:title",
      "font-color": "*blue" // same as "*common:blue"
    }
  ]
}
```

Styles are reusable visual definitions.

---

## 🧱 Contents

```json
{
  "contents": [
    {
      "id": "input-field-age-content",
      "title": "A big title",
      "placeholder": { "id": "*form:text-form-email-placeholder" }
    }
  ]
}
```

Contents define reusable content blocks. A content entry can reference another by `id`. In general, IDs are resolved recursively.

---

## 🧩 Components

```json
{
  "components": [
    {
      "id": "button-label-help-to-home",
      "style": { ... },
      "option": { ... },
      "content": { ... }
    }
  ]
}
```

Components are reusable visual units that reference styles, content, etc... 📖 See: [`components/index.md`](../components-definition/index.md)
