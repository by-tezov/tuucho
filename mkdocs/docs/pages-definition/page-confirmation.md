# Page - Confirmation - Example

```json
{
  "root": {
    "subset": "layout-linear",
    "style": {
      "orientation": "vertical",
      "background-color": { "id": "*background:funny-color" },
      "fill-max-size": true
    },
    "content": {
      "items": [
        {
          "subset": "label",
          "style": {
            "id": "*title-label"
          },
          "content": {
            "value": "Confirmation:"
          }
        },
        {
          "id": "*item-spacer-half"
        },
        {
          "subset": "label",
          "style": {
            "id": "*title-body",
            "font-color": {"id":"*black"}
          },
          "content": {
            "value": { "default": "Merci, vous avez bien été ajouté à notre newsletter" }
          }
        },
        {
          "id": "*item-spacer"
        },
        {
          "subset": "button",
          "content": {
            "label": {
              "style": { "id": "*label-main-button-back" },
              "content": {
                "value": "Navigate to home page"
              }
            },
            "action": "navigate://url/page-home"
          }
        }
      ]
    }
  },
  "components": {
    "common" : {
      "item-spacer-half": {
        "subset": "spacer",
        "style": {
          "weight": "0.5"
        }
      }
    }
  }
}

```

---

Elements than doesn't exist in the json are resolved from the subs 📖 [Subs Definition](../config/subs.md).