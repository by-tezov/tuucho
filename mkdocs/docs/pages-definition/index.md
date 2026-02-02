---
comments: true
---

# Page

A **page** JSON describes a full screen layout.

```json
{
  "setting": { ... },
  "root": { ... },
  "components": [ ... ],
  "styles": [ ... ],
  "options": [ ... ],
  "contents": [ ... ],
  "texts": [ ... ],
  "images": [ ... ],
  "colors": [ ... ],
  "dimensions": [ ... ],
  "actions": [ ... ]
}
```

- The **root** object serves as the entry point for the page.
- The other sections define local reusable objects that can be referenced within the page.
- For shared or common objects, see the relevant *subs* repository 📖 [Subs Definition](../config/subs.md).

Refer to the following for detailed definitions of each object type:

-  [Actions](../object-definition/action.md)
-  [Colors](../object-definition/color.md)
-  [Components](../components-definition/index.md)
-  [Contents](../object-definition/content.md)
-  [Dimensions](../object-definition/dimension.md)
-  [Options](../object-definition/option.md)
-  [Setting](../object-definition/page-setting.md)
-  [Styles](../object-definition/style.md)
-  [Texts](../object-definition/text.md)
-  [Images](../object-definition/image.md)

All available components can be browsed [here](../components-definition/index.md).

This modular design enables pages to remain lightweight by reusing existing components, styles, validators, and texts.

---

Here some examples:

-  [Page Home](page-home.md)
-  [Page Help](page-help.md)
-  [Page Confirmation](page-confirmation.md)