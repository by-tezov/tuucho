# Dynamic Loading for Contextual Data

When a reference resolver cannot resolve a reference because the data is not present in the cache, the **Shadower** assumes it is **contextual data** and will request it automatically.

- **Default behavior**:  
  If no explicit URL is defined, the system will try `"${current-page}-contextual"` as the endpoint for contextual data.

- **Loading mode**:  
  Contextual data can be loaded **synchronously** (blocking render until data is ready) or **asynchronously** (default, render immediately and update when data arrives).

---

## Enabling the Shadower

Contextual loading is only active if the **Shadower** is explicitly enabled:

- At minimum, enable it on **navigation forward**.
- You can configure this in [Component Setting](object-definition/component-setting.md).

Example:

```json
"shadower": {
  "navigate-forward": {
    "enable": true,
    "wait-done-to-render": false
  }
}
```

---

## Defining Contextual URLs

You can control where contextual data is loaded from:

1. **Page-level setting**  
   Define URLs for `component`, `content`, or `text` by the page’s. See [Component Setting](object-definition/component-setting.md).

2. **Object-level setting**  
   Define URLs directly on objects through the **ID Object**. See [ID Object](object-definition/id.md).

If no explicit URL is provided, the system falls back to `${current-page}-contextual`.

---

## Contextual JSON Structure

Contextual JSON files are structured exactly like **Subs**. See [Subs Definition](config/subs.md) for details.

- They may include any combination of `texts`, `contents`, `components`, etc.
- They can also declare a TTL in [Page setting](object-definition/page-setting.md) to control caching.

---

## Example

This example defines contextual data with a **10-second TTL**, loaded synchronously (the page waits for it before rendering):

```json
{
  "setting": {
    "ttl": {
      "strategy": "transient",
      "transient-value": "10s"
    }
  },
  "texts": {
    "common": {
      "title-body": "contextual data with 10s TTL and wait all available before to render"
    }
  }
}
```
