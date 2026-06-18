# Tails Sync Service

Tails Legacy provides the `ITailsSyncService` API, for mods to implement custom sync services when the Minecraft server cannot be used for syncing (e.g., Hypixel). The current sync service can be set using `ITailsLegacyAccess`.

### Local sync service

For convenience, Tails Legacy provides a built-in implementation that uses json files in `tailslegacy-sync` in the config directory. This implementation is only useful if everyones' customizations are known ahead of time and will never change.

This sync service can be enabled by adding the JVM argument `-Dtailslegacy.local-sync.enabled=true`, which will cause Tails Legacy to select this sync service.