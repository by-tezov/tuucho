# Config Definition

The `config` JSON file is the main configuration container for the application. It organizes the resources and assets that the app needs to load, manage, and cache.

Additional keys and options will be added as the system evolves.

```json
{
  "preload": { /* preload object */ }
}
```

---

## 1. Preload Key

The `preload` key specifies collections of resources that the application should load and cache immediately upon startup to ensure optimal performance and offline availability.

- The app compares versions to detect updates and downloads the latest resources.
- Resources listed under `preload` are cached immediately to improve performance.
- Resources **not** listed in `preload` remain accessible and are fetched and cached on demand when first used.

```json
{
  "preload": {
    "subs": [
      {
        "version": "1",
        "url": "subs/sub-texts"
      },
      ...
    ],
    "templates": [
      {
        "version": "1",
        "url": "templates/template-page-default"
      },
      ...
    ],
    "pages": [
      {
        "version": "1",
        "url": "page-home"
      },
      ...
    ]
  }
}
```

The `preload` object contains three main arrays:

??? "`subs`: Modular reusable chunks (styles, options, texts, components)"

    The `subs` array includes modular chunks of data or objects such as styles, options, texts, dimensions, colors, contents, or complete components. These are referenced by pages or other objects via ID pointers.

    - Each `sub` object includes a `version` and a `url`.
    - The app checks the version to determine if an update is required.
    - These chunks are reusable building blocks.

    For more details, see the [Subs Definition](subs.md).


??? "`templates`: Full page templates with replaceable parts"

    The `templates` array contains full page templates with replaceable or referenceable parts.

    - Each `template` has a `version` and a `url`.
    - The app verifies versions to detect updates.
    - Pages can source these templates and override or extend parts such as content or style.

    Refer to the [Templates Definition](templates.md) for detailed information and examples.

??? "`pages`: Complete page descriptions"

    The `pages` array lists complete page descriptions to preload.

    - Each page entry contains a `version` and a `url`.
    - The app checks versions to ensure resources are up to date.

    For detailed page structure and usage, see the [Pages Definition](pages.md).



