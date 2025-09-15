---
comments: true
---

# Config Definition

The `config` JSON file allow you do define material resource url in order to load, manage, and cache. This (or these files) will be called before to call a page. Any change in the validity key will invalid the corresponding cache.

Additional keys and options will be added as the system evolves.

**material-resource** structure, resources are grouped into **global**, **local**, and **contextual** scopes.

```json
{
  "material-resource": {
    "global": {
      "subs": [
        {
          "validity-key": "any-string",
          "url": "subs/sub-texts",
          "pre-download": true(default)/false
        },...
      ],
      "templates": [
        {
          "validity-key": "1",
          "url": "templates/template-page-default",
          "pre-download": true(default)/false
        },...
      ]
    },
    "local": {
      "pages": [
        {
          "validity-key": "any-string",
          "url": "page-b",
          "pre-download": true(default)/false
        },...
      ]
    },
    "contextual": {
      "all": [
        {
          "validity-key": "any-string",
          "url-origin": "page-home",
          "url": "${url-origin}-contextual-texts",
          "pre-download": true/false(default)
        },...
      ]
    }
  }
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

---

## 1. Global Resources

`global` defines reusable content that can be referenced by **any page**.  
All keys under `global` are arbitrary (you can choose meaningful names like `subs`, `templates`, `shared`, etc.), but each must map to an **array of config objects**.

- Global resources are not wrapped by a `root component` key.
- Typical usage includes `subs`, `templates`, or other reusable building blocks.

---

## 2. Local Resources

`local` defines resources bound to specific **page**.

- Each entry **must include a `root component` key**.
- All other keys (like `pages`, `views`, `dialogs`) are arbitrary, but must resolve to arrays.

---

## 3. Contextual Resources

`contextual` defines resources whose resolution depends on contextual data.

- Each object must include a mandatory `url-origin`.
- `url` is optional:
    - If defined, it will be used directly. You can use ${url-origin} as replacement token
    - If omitted, it defaults to `"${url-origin}-contextual"`.

---

## 4. Validity and Caching

- **`validity-key`**:
    - string used to explicitly invalidate a cached resource.
    - When the key changes, the cache entry is replaced with a fresh downloaded content.

- **Without `validity-key`**:
    - The system relies on **TTL (Time-to-Live)**.
    - If you don’t set TTL, the resource will be cached **forever**.

## 4. Pre Download (optional)

- **`pre-download`**:
    - Boolean flag (`true` / `false`).
    - If `true`, the resource is downloaded and cached immediately at startup.
    - If `false`, it’s fetched on first use.

---

Refer to the [Templates Definition](templates.md) for detailed information and examples.
For detailed page structure and usage, see the [Pages Definition](../pages-definition/index.md).
For more details, see the [Subs Definition](subs.md).
 
