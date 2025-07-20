# WIP TODO

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
          "style": "*style-title-label",
          "content": {
            "value": "*text-title-home"
          }
        }
        /* other components */
      ]
    }
  },
  "styles": [
    {
      "id": "style-title-label",
      "font-size": "24",
      "font-color": "*black"
    }
  ],
  "colors": {
    "common": {
      "black": "#FF000000"
    }
  },
  "texts": {
    "common": {
      "text-title-home": "title label"
    }
  }
}
```