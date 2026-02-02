# Configuration

Tuucho allows you to configure several core systems:

- Datastore
- Database
- Image Repository
- Network (*required)
- Ktor client engine

All configuration must be provided through **ModuleGroupCore.Main**.

---

## Datastore Configuration

You can supply a custom datastore file name by providing a `StoreRepositoryModule.Config` implementation.

```kotlin
factory<StoreRepositoryModule.Config> {
    object : StoreRepositoryModule.Config {
        override val fileName = BuildKonfig.localDatastoreFileName
    }
}
```

If no configuration is supplied, Tuucho uses the default datastore file name: **"tuucho-datastore"**

---

## Database Configuration

You can supply a custom database file name by providing a `DatabaseRepositoryModule.Config` implementation.

```kotlin
factory<DatabaseRepositoryModule.Config> {
    object : DatabaseRepositoryModule.Config {
        override val fileName = BuildKonfig.localDatabaseFileName
    }
}
```

If no configuration is supplied, Tuucho uses the default database file name: **"tuucho-database"**

---

## Image Repository

You need to supply a `ImageModule.Config` implementation to activate the image cache. If not, all remote image won't be disk cached.

```kotlin
factory<ImageModule.Config> {
    object : ImageModule.Config {
        override val diskCacheSizeMo = BuildKonfig.imageDiskCacheSizeMo
        override val diskCacheDirectory = BuildKonfig.imageDiskCacheDirectory
    }
}
```

---

## Network Configuration

**Network configuration must be supplied.** Tuucho does **not** provide default values for network settings.

You provide them through a `NetworkRepositoryModule.Config` implementation.

```kotlin
factory<NetworkRepositoryModule.Config> {
    object : NetworkRepositoryModule.Config {
        override val jsonRequestTimeoutMillis = BuildKonfig.serverJsonTimeoutMillis
        override val imageRequestTimeoutMillis = BuildKonfig.serverImageTimeoutMillis
        override val version = BuildKonfig.serverVersion
        override val baseUrl = BuildKonfig.serverBaseUrl
        override val healthEndpoint = BuildKonfig.serverHealthEndpoint
        override val resourceEndpoint = BuildKonfig.serverResourceEndpoint
        override val sendEndpoint = BuildKonfig.serverSendEndpoint
        override val imageEndpoint = BuildKonfig.serverImageEndpoint
    }
}
```

All fields must be supplied:

- **jsonRequestTimeoutMillis**
- **imageRequestTimeoutMillis**
- **version**
- **baseUrl**
- **healthEndpoint**
- **resourceEndpoint**
- **sendEndpoint**
- **imageEndpoint**

Tuucho cannot operate without these values.

---

## Supplying a Custom Ktor Client Engine

You can provide your own platform-specific Ktor `HttpClientEngineFactory`.

### Android Example (OkHttp)

```kotlin
module(ModuleContextCore.Main) {
    factory<HttpClientEngineFactory<*>> {
        OkHttp
    }
}
```

### iOS Example (Darwin)

```kotlin
module(ModuleContextCore.Main) {
    factory<HttpClientEngineFactory<*>> {
        Darwin
    }
}
```

If no engine is supplied, Tuucho automatically creates and manages its **own default engine instance**.

---

## Registering Configuration in Koin

All configuration modules must be registered under **ModuleContextCore.Main**.

```kotlin
module(ModuleContextCore.Main) {

    factory<StoreRepositoryModule.Config> {
        ...
    }
    
    factory<ImageModule.Config> {
        ...
    }

    factory<DatabaseRepositoryModule.Config> {
        ...
    }

    factory<NetworkRepositoryModule.Config> {
        ...
    }
}
```
