# Page Setting

page setting is setting key at level 0 inside the json file

```json
"setting": {
  "ttl": { ... }
}
```

---

## TTL

The `ttl` object defines the **lifetime policy** for cached resources.  
It controls how long a resource remains valid before the system considers it stale and requests a fresh copy.

### Strategies

The `strategy` field defines the caching rule to apply:

1. **Transient**
The resource is cached for a limited duration, defined by `transient-value`.

- `transient-value` accepts multiple formats:
    - Relative durations:
        - `10s` = 10 seconds
        - `5mn` = 5 minutes
        - `7d` = 7 days
        - `2mth` = 2 months
    - Absolute ISO UTC datetime:
        - `"2025-09-15T00:00:00Z"`
    - Date:
        - 00h00 is applied.
    - Time of day:
        - `"03:00"` → if before current time, same day is applied;  
          if above current time, next day is applied.

```json
"setting": {
  "ttl": {
    "strategy": "transient",
    "transient-value": "5mn"
  }
}
```

---

2. **Single-use**
The resource is valid only for a single access. After being used once, it is purged from the cache.

```json
"setting": {
  "ttl": {
    "strategy": "single-use"
  }
}
```

---

### Default Behavior

If `ttl` is **absent**:

- **Global and Local resources**:  
  Cached indefinitely, until the **`validity-key`** defined in the config changes. Refer to the [Config](../config/index.md)
  No expiration is applied unless explicitly declared.

- **Contextual resources**:  
  Cached with a **single-use** policy by default, even without explicit `ttl`.

---

## Summary

- `ttl` is optional but recommended for dynamic resources.
- Strategies:
    - **transient** → expires after a duration (`transient-value`) or at a given date/time.
    - **single-use** → removed from cache immediately after first use.
- Defaults:
    - Global/Local = infinite until `validity-key` changes.
    - Contextual = single-use if not specified.
- Use ISO UTC datetimes or shorthand units (`s`, `mn`, `d`, `mth`) for `transient-value`.

---
