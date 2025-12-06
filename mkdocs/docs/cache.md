# Cache

All JSON resources managed by TUUCHO can be cached to avoid unnecessary network requests and improve performance. The cache system is controlled through two main mechanisms:

- **`ttl`** (Time-To-Live), defined in [Page Setting](object-definition/page-setting.md)
- **`validity-key`**, defined in [Config](config/index.md)

---

## 1. Validity Key

The **`validity-key`** is an string attached to a resource in the config.

- When the key changes, the cached version is immediately invalidated and replaced on next application start.
- Works best for resources that are expected to persist for long periods but may be updated occasionally.
- Recommended for **global** and **local** resources when no ttl are defined

Example:

```json
{
  "material-resource": {
    "global": {
      "templates": [
        {
          "validity-key": "v2",
          "url": "templates/template-page-default"
        }
      ]
    }
  }
}
```

See [Config](config/index.md) for more details.

---

!!! note
    - The validity-key lets you dynamically change any page with JSON content that the current app version is able to render.
    - When upgrading the Tuucho library and delivering an application update, you are still responsible for upgrading your server API version (http://domain/vx/endpoint) so it can serve the appropriate version in production for all users.
---

## 2. TTL (Time-To-Live)

The **`ttl`** setting defines how long a resource remains valid in the cache before being re-fetched.  
It is configured inside the resource’s `"setting"` block.

Two strategies are available:

- **transient** → expires after a duration or at a given date/time.
- **single-use** → removed immediately after first access.

Example:

```json
"setting": {
  "ttl": {
    "strategy": "transient",
    "transient-value": "5mn"
  }
}
```

See [Page Setting](object-definition/page-setting.md) for the complete list of options.

---

**Longer-lived resources** (e.g. days, weeks, months TTL):  
  It is **highly recommended** to add a `validity-key`.  
  Without it, the only expiration will be TTL — and if TTL is long, there will be no way to force invalidation if the resource changes unexpectedly.

---

## 4. Default Rules

- If `ttl` is **absent**:
    - **Global and Local resources** → cached indefinitely, until `validity-key` changes.
    - **Contextual resources** → default to **single-use**.

- If `ttl` is defined:
    - Cache follows the configured strategy (`transient` or `single-use`).

---

