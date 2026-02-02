# Subs Definition

The `subs` system defines reusable chunks of content referenced throughout the app by ID.  
They allow a modular, maintainable, and dynamic way to define UI and content logic.

All subs must be referenced by ID pointer where they are needed as replacement 📖 See: [`object-definition/id.md`](../object-definition/id.md)

```Json
{
    "components": { ... },
    "styles": { ... },
    "options": { ... },
    "contents": { ... },
    "texts": { ... },
    "images": { ... },
    "colors": { ... },
    "dimensions": { ... },
    "actions": { ... },
    "states": { ... }
}
```

Each `subs` file can contain any combination of the following keys:

- `actions` – Translatable strings organized by group. See [Action Object Definition](../object-definition/action.md).
- `colors` – Grouped color values used throughout styles and components. See [Color Object Definition](../object-definition/color.md).
- `components` – UI building blocks composed from content, style, and logic. See [Component Object Definition](../object-definition/component.md).
- `contents` – Logical structures for fields (title, placeholder, error messages). See [Content Object Definition](../object-definition/content.md).
- `dimensions` – Numeric or fractional sizes like font, padding, or spacing. See [Dimension Object Definition](../object-definition/dimension.md).
- `options` – Define data-driven value lists, such as select dropdown choices. See [Options Object Definition](../object-definition/option.md).
- `styles` – Reusable visual rules (font-size, color, etc.) referenced by components. See [Style Object Definition](../object-definition/style.md).
- `texts` – Translatable strings organized by group. See [Text Object Definition](../object-definition/text.md).
- `images` – images local or remote organized by group. See [Image Object Definition](../object-definition/image.md).
- `states` – Initial state value. See [State Object Definition](../object-definition/state.md).

---

## 🔤 Texts

```Json
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

Texts are grouped under categories (`common`, `form`, etc.) and are accessed via ID references like `*form@text-body-content-help`.

> ⚠️ **Important:** `common` is the default group. If no group is specified in a reference, the system will default to searching in the `common` group.  
> Example: `*my-id` or `*common@my-id` are the same reference.

---

## 🔤 Images

```Json
{
    images": {
        "common": {
            "main-logo": "remote://lobby/logo-compose-wrong-url",
            "main-logo-placeholder": {
              "source": "local://img/logo-compose-placeholder",
              "tags": "placeholder"
            },
            "small-logo": "local://img/logo-koin"
        }
    }
}
```

Images are grouped under categories (`common`, `feat-a`, etc.) and are accessed via ID references like `*feat-a@main-logo`.

> ⚠️ **Important:** `common` is the default group. If no group is specified in a reference, the system will default to searching in the `common` group.  
> Example: `*my-id` or `*common@my-id` are the same reference.

---


## 🎨 Colors

```Json
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

Colors are grouped (e.g. `common`, `background`) and accessed using references like `*common@black` or `*background@funny-color`.

> ⚠️ **Important:** `common` is the default group.  
> If no group is specified in a reference, the system will default to searching in the `common` group.  
> Example: `*my-id` or `*common@my-id` are the same reference.

---

## 📏 Dimensions

```Json
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

Dimensions are numeric or fractional values grouped by purpose (`font`, `padding`, etc.) and referenced via `*font@title` or `*padding@spacer-24`.

> ⚠️ **Important:** `common` is the default group.  
> If no group is specified in a reference, the system will default to searching in the `common` group.  
> Example: `*my-id` or `*common@my-id` are the same reference.

---

## 🎨 Styles

```Json
{
    "styles": {
        "common": {
            "title-label": {
                "font-size": "*font@title",
                "font-color": "*blue" // same as "*common@blue"
            }
        }
    }
}
```

Styles are component context values grouped by purpose (`feat-a`, `feat-b`, etc.) and referenced via `*feat-a@title-label`, etc.

> ⚠️ **Important:** `common` is the default group.  
> If no group is specified in a reference, the system will default to searching in the `common` group.  
> Example: `*my-id` or `*common@my-id` are the same reference.

---

## 🧱 Contents

```Json
{
    "contents": {
        "common": {
            "input-field-age-content": {
                "title": "A big title",
                "placeholder": { "id": "*form@text-form-email-placeholder" }
            }
        }
    }
}
```

Contents are component context values grouped by purpose (`feat-a`, `feat-b`, etc.) and referenced via `*feat-a@input-field-age-content`, etc.

> ⚠️ **Important:** `common` is the default group.  
> If no group is specified in a reference, the system will default to searching in the `common` group.  
> Example: `*my-id` or `*common@my-id` are the same reference.

---

## 🧩 Components

```Json
{
    "components": {
        "common": {
            "button-label-help-to-home": {
                "id": "button-label-help-to-home",
                "style": { ... },
                "option": { ... },
                "content": { ... }
            }
        }
    }
}
```

Components are reusable visual units that reference styles, content, etc.  
They are grouped by purpose (`feat-a`, `feat-b`, etc.) and referenced via `*feat-a@component-checkbox`, etc.

> ⚠️ **Important:** `common` is the default group.  
> If no group is specified in a reference, the system will default to searching in the `common` group.  
> Example: `*my-id` or `*common@my-id` are the same reference.

---

### 🗒️ Notes

## extension

```Json
{
  "contents": {
    "common": {
      "input-field-age-content": {
        "id": "*input-field-age-content-base", <--- Here we say 'this object will complete all missing from this other object'
        "title": "A big title overwritten"
      }
    }
  }  
}
```

To extend an object, you add the id field. Only a reference is accepted, because the id of the current object is the key 'input-field-age-content'

Check [ID Definition](../object-definition/id.md) to get more information about id key.

## array format

For all these keys (`styles`, `contents`, etc.), arrays are also accepted:

```Json
{
    "styles": [
        {
            "id": "title-label",
            "font-size": "*font@title",
            "font-color": "*blue" // same as "*common@blue"
        }
    ]
}
```

or

```Json
{
    "contents": [
        {
            "id": {
              "value": "group@input-field-age-content",
              "source": "*group@input-field-age-content-base"
            },
            "title": "A big title",
            "placeholder": { "id": "*form@text-form-email-placeholder" }
        }
    ]
}
```

If you use this array-based structure, you can still use group references.  
If no group is provided, they will automatically be placed inside the `common` group.
