# Template Example

Here is a full template JSON example.  
For details on components and references, see 📖 [`object-definition/id.md`](../object-definition/id.md) and [`components-definition/index.md`](../components-definition/index.md).

```Json
{
  "root": {
    "id": "template-page-default",
    "subset": "layout-linear",
    "style": {
      "orientation": "vertical",
      "background-color": { "id": "*background@why-not-color" },
      "fill-max-size": true
    },
    "content": {
      "items": [
        {
          "subset": "label",
          "style": {
            "id": "*title-label",
            "font-color": "#FF78FF78"
          },
          "content": {
            "id": "*title-content"
          }
        },
        {
          "id": "*item-spacer"
        },
        {
          "subset": "label",
          "style": {
            "font-size": "18",
            "font-color": { "id": "*blue" }
          },
          "content": {
            "id": "*body-content"
          }
        },
        {
          "id": "*item-spacer"
        },
        {
          "subset": "button",
          "content": {
            "id": "*action-content"
          }
        }
      ]
    }
  }
}
```

---

## Usage example

This template is then used by supplying local declarations to fulfill the referenced IDs:

```Json
{
  "root": {
    "id": "*template-page-default"
  },
  "contents": [
    {
      "id": "title-content",
      "value": { "id": "*text-body-title" }
    },
    {
      "id": "body-content",
      "value": { "id": "*text-body-content" }
    },
    {
      "id": "action-content",
      "label": { "id": "*text-action-help" },
      "action": "navigate://url/page-home"
    }
  ],
  "texts": {
    "common": {
      "text-body-title": "Looking for help?",
      "text-body-content": { "id": "*text-body-content-help" },
      "text-action-help": "Navigate to home page"
    }
  }
}
```

---

### Explanation

- The template references IDs like `*title-content`, `*body-content`, and `*action-content`.
- The usage JSON provides local declarations for these IDs inside `contents` and `texts`.
- Any referenced ID not found locally will be resolved from the shared preloaded `subs` objects. 📖 [`components-definition/index.md`](subs.md)
- This allows flexible reuse and override of content per page while keeping a common structure.
