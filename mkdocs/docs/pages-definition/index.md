# Page

A **page** JSON describes a full screen layout.

```json
{
  "version": "1",
  "root": { ... },
  "components": [ ... ],
  "styles": [ ... ],
  "options": { ... },
  "contents": [ ... ],
  "texts": { ... },
  "colors": { ... },
  "dimensions": { ... }
}
```

- The **root** object serves as the entry point for the page.
- The other sections define local reusable objects that can be referenced within the page.
- For shared or common objects, see the relevant *subs* repository 📖 [Subs Definition](../config/subs.md).

Refer to the following for detailed definitions of each object type:

- 📖 [Components](../components-definition/index.md)
- 📖 [Styles](../object-definition/style.md)
- 📖 [Options](../object-definition/option.md)
- 📖 [Contents](../object-definition/content.md)
- 📖 [Texts](../object-definition/text.md)
- 📖 [Colors](../object-definition/color.md)
- 📖 [Dimensions](../object-definition/dimension.md)

All available components can be browsed [here](../components-definition/index.md).

This modular design enables pages to remain lightweight by reusing existing components, styles, validators, and texts.

---

Here some examples:

- 📖 [Page Home](page-home.md)
- 📖 [Page Help](page-help.md)
- 📖 [Page Confirmation](page-confirmation.md)