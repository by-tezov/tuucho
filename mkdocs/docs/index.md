# TUUCHO - ตู้โชว์ - Rendering Engine

Application renderer by parsing a JSON that describes the UI layout.

What is possible now:
  - Linear or Horizontal Layout
  - Button with navigation action
  - Label
  - Spacer vertical or horizontal

## Overview

This project renders UI from JSON describing layouts, components, styles, texts, and more.

```json
{
  "root": { **component** },
  "components": [ **component** ],
  "contents": [ **content** ],
  "styles": [ **style** ],
  "texts": [ **text** ],
  "colors": [ **color** ],
  "dimensions": [ **dimension** ]
}
```

Each `PAGE` **must** have at least a `root` key. The `root` defines the top-level component. Other optional keys include: `components`, `contents`, `styles`, `texts`, `colors`, `dimensions`.
`root` is not mandatory inside `SUBS`, .

- `PAGE` are full screen rendered component
- `SUBS` are shared object that can be accessed by reference (id starting by '*')

All `id` starting by "*" are references. When the parser encounter one, it will look first inside the current `page` if the reference exist, then inside the `subs`.


## **Subs**

SUBS are shared library loaded by the application before trying to render a page.

- Used for sharing common components, styles, texts, etc., across multiple pages.
- Reference them using the `*` notation.

---

## Documentation Menu

- [Object Definition](object-definition/index.md)
- [Components Definition](components-definition/index.md)
