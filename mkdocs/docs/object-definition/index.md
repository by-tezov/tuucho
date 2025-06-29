# Object Definition

This section describes the main JSON keys used in TUUCHO rendering engine.

- [component](component.md) — UI elements like layouts, buttons, labels
- [style](style.md) — Style definitions for components
- [content](content.md) — Content data for components
- [texts](texts.md) — Multilingual text references
- [colors](colors.md) — Color definitions
- [dimensions](dimensions.md) — Size and spacing definitions



Each `PAGE` **must** have at least a `root` key. The `root` defines the top-level component. Other optional keys include: `components`, `contents`, `styles`, `texts`, `colors`, `dimensions`.
`root` is not mandatory inside `SUBS`, .

 - `PAGE` are full screen rendered component
 - `SUBS` are shared object that can be accessed by reference (id starting by '*')

All `id` starting by "*" are references. When the parser encounter one, it will look first inside the current `page` if the reference exist, then inside the `subs`.

### Example Page

```json
{
  "root": {
    "id": "page-home",
    "subset": "layout-linear",
    "style": {
      "orientation": "vertical"
    },
    "content": {
      "items": [
        {
          "id": "title-content",
          "subset": "label",
          "style": {
            "id": "*style-title-label"
          },
          "content": {
            "value": { "id": "*text-title-home" }
          }
        },
        /* other components */ 
      ]
    }
  },
  "styles": [
    {
      "id": "style-title-label",
      "font-size": "24",
      "font-color": "0xFFD9D9D9"
    }
  ], 
  "texts": {
    "common": {
      "text-title-home": "title label"
    }
  }
}
```