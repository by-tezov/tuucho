# Image Definition

The `image` object is used to define image content. It supports both direct definitions and references. Local and Remote image are accepted.

---

## 1. Full Object Format

- `id`: follows the same rules outlined in [ID Definition](id.md), meaning it can be either a unique definition, a pointer, or a combination.
- `source`: defines image command (**required**)
- `tags`: array of tags. Tags will be available on presentation projection. `placeholder` tag value will make the image skip, if the final image is already available.
- `tagsExcluder`: array of tags that will exclude this image. By default, all image that doesn't have the `placeholder` will received `placeholder` excluder tag.

Example:

```json
"main-logo-placeholder": {
    "source": "local://img/logo-compose-placeholder",
    "tags": "placeholder"
}

or

"main-logo": {
    "source": "local://img/logo-compose",
    "tagsExcluder": "placeholder"
}
```

---

## 2. String Format (Short Syntax)

You can simplify the declaration by using a single string:

```json
"key-image": "local://img/logo-compose"
```

This is equivalent to:

```json
"key-image": {
  "source": "local://img/logo-compose"
}
```

`tagsExcluder` will be added automatically by the parser. You need to explicitly tag all placeholders.

---

## 3. Pointer Reference (Short Syntax)

Instead of defining image directly, you can reference a image defined elsewhere (in the same page or in Subs) by prefixing the value with `*`.

Example:

```json
"key-image": "*image-banner"
```

```json
"key-image": {
  "id": "*image-banner"
}
```

The engine will resolve the pointer by searching first in the local page, then in the shared Subs.

This approach is useful for maintaining consistency across pages and reducing repetition in large applications.

---

## 4. Source command

There two image command processor available. One for Local image and one for Remote image.


#### `local://target`

Use this when the image is local inside `assets/files`. `target` is relative to this folder. These image will never be cached since they are already embedded with the application.

#### `remote://target`

Use this when the image is on your server. `target` is prefixed with your image endpoint configuration. [Mobile Config](./../mobile-integration/config.md). 
All remote image will be cached an respect the page TTL. [Cache](./../cache.md)

---

## Summary Table

| Format                                                        | Purpose                              | Example                                 |
|---------------------------------------------------------------|--------------------------------------|-----------------------------------------|
| [Full object](#1-full-object-format)                          | content                              | See section 1                           |
| [String value](#2-string-format-short-syntax)                 | Quick/default image                  | `"image": "remote://lobby/logo-compos"` |
| [String starting with `*`](#3-pointer-reference-short-syntax) | Pointer to shared/local image object | `"image": "*shared-image"`              |
| [Source command](#4-source-command)                           | Available processors                 | `local or remote`                       |

---

For more advanced examples and usage patterns, see the [Components Definition](../components-definition/index.md).