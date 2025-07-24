# Page - Help - Example

```json
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
      "label": { "id": "*button-label-help-to-home" },
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

Using the template loaded with subs 📖 [Subs Definition](../config/subs.md).


```json
{
  "version": "1",
  "root": {
    "id": "template-page-default",
    "subset": "layout-linear",
    "style": {
      "orientation": "vertical",
      "background-color": { "id": "*background:why-not-color" },
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
            "font-color": {"id":"*blue"}
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