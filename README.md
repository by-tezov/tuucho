## REDACTION WIP

# TUUCHO -ตู้โชว์ - Rendering engine

Application renderer by parsing a json that describe the application.

What is possible now:
- Linear or Horizontal Layout
- Button with navigation action
- Label
- Spacer vertical or horizontal

## General Json Structure that must be served

```json
{
  "root": { **component },
  "components": [ **component ],
  "contents": [ **content ],
  "styles": [ **style ],
  "texts": [ **text ],
  "colors": [ **color ],
  "dimensions": [ **dimension ]
}
```

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

## Object definition

### 0. **id**

TODO

### 1. **component**

- **Description**: Defines UI component that can be renderer.
- **Component Definition** include:
    - `id`: id off the component, not mandatory. Can be a reference that point to another component
    - `subset`: The type of UI element (e.g., `layout-linear`, `button`, `text`)
    - `style`: Style object
    - `content`: Content or child components (can be a value or a reference)

### 2. **style**

- **Description**: Defines style, effect depend of the component subset.
- **Style Definition** include:
    - `id`: id off the style, not mandatory. Can be a reference that point to another style
    - `****`: Check the corresponding component to know which are allowed.

TODO, add json example

### 2. **content**

- **Description**: Defines content, effect depend of the component subset.
- **Style Definition** include:
    - `id`: id off the content, not mandatory. Can be a reference that point to another content
    - `****`: Check the corresponding component to know which are allowed.

TODO, add json example

### 3. **texts**

- **Description**: Defines texts, array of text that can be accessed by reference.
- **Text Definition** include:
    - `id`: to extend another id by defining the source. Check the example of refer to 'id' definition.
    - `default`: default text mandatory
    - `****`: any other language.

. texts can be split in different group, by default all texts are put in common group if no group are defined. All reference that do no explicitly define a group will target the common group
. it can be a simple value if only one language
. id is the key of the text, only source can be defined inside id object iif added

TODO, add json example

### 4. **colors**

TODO

### 5. **dimensions**

TODO

## **Subs**

SUBS are shared library loaded by the application before trying to render a page.

- Used for sharing common components, styles, texts, etc., across multiple pages.
- Reference them using the `*` notation.


## Components definition

### LayoutLinear
   - `subset`: layout-linear

### Label
- `subset`: label

### Button
- `subset`: button

### Spacer
- `subset`: spacer