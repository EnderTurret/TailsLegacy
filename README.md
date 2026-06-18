![Tails Legacy banner](common/src/main/resources/assets/tailslegacy/textures/banner.png)

A Minecraft mod that adds in a variety of recolorable tails, ears, wings, and other accessories.

This is a fork of [the original project](https://github.com/kihira/Tails) which has been updated to newer versions of Minecraft and expanded with additional features.
(This fork is based on [the 1.12 branch](https://github.com/kihira/Tails/tree/913cb5ddff562d0d8eef8260717310dc68eaf623) — not [the in-development rewrite](https://github.com/kihira/Tails/tree/update).)

## Compatibility

Tails Legacy should generally be compatible with everything (even the original Tails), however on older versions certain less well-behaved mods might cause problems (e.g., mods that mess with GL state instead of posing the player model).

| Mod                | Version        | Compatible? | Description                                                                                                 |
| ------------------ | -------------- | ----------- | ----------------------------------------------------------------------------------------------------------- |
| Tails              | Any            | ✓           | Works fine (albeit redundant)                                                                               |
| OptiFine           | 1.12.2, 1.7.10 | ✓           | Works fine                                                                                                  |
| More Player Models | 1.12.2         | ✓           | Works fine                                                                                                  |
| More Player Models | 1.7.10         | ✓           | Works okay, but one might run into issues (especially with [Ears](https://modrinth.com/mod/ears) installed) |
| Smart Moving       | 1.12.2         | ✓           | Works fine                                                                                                  |
| Galacticraft       | 1.12.2         | ✓           | Works fine                                                                                                  |
| Obfuscate          | 1.12.2         | ✓           | Seems to work fine, but not tested thoroughly                                                               |
| Mo' Bends          | 1.12.2         | ✓           | Works fine, but there might be clipping issues with some parts                                              |
| Mo' Bends          | 1.7.10         | x           | Broken (might not be Tails Legacy's fault)                                                                  |

## Differences

There are several major differences in this fork (the mod has effectively been rewritten like six times now), but here are some of the important ones:

* Updated for modern versions of Minecraft
* Part types rewritten into attachment points
* New tails, "ears," wing textures, etc
* Data-driven part, "sub type", texture, and attachment point definitions (via resource packs)

For user-facing differences (the ones you're most likely to see first):

* "Sub-types" are now another set of buttons instead of selecting them in the part list
	* Example: the nine tails sub-type is now under the fluffy tail instead of shown separately
* Part type selection is now two "spinners" for selecting attachment point
* Exporting/importing from skin is completely gone
* Server-side Tails libraries are gone (I'm not sure they ever even did anything?)
* Importing library entries actually works now
* Arrows stuck in players can now render on certain parts (currently only tails)
* One can now rotate the editor's player preview vertically and/or zoom in

### Data format

To support all of these changes, the Tails data format has changed significantly. In short, the part data is smaller and uses fewer "magic numbers."

Additionally, Tails Legacy will upgrade your old library and accessories to the newest format, so you don't have to worry about recreating them from scratch.
This works on theoretically every version since 1.7 — you can toss a 1.7-era library in the game directory and Tails Legacy will upgrade it for you.

## Data-driven parts

As mentioned earlier, Tails Legacy's part system is completely data-driven.
For most people, the only change will be that resource packs can now define their own parts/sub-types/textures.

There is a guide to creating parts, sub-types, and textures [here](docs/parts.md). For making one's own PNG textures for parts, there's an explanation of the triple tint format [here](docs/triple_tint_system.md).

## For mod developers

One can add Tails Legacy to their mod development environment using Modrinth's Maven.
Alternatively, if one is on NeoForge and a compile-time dependency is not needed, then one can simply drop Tails Legacy into the `mods` folder.

```gradle
repositories {
    maven {
        url = 'https://api.modrinth.com/maven'
        content {
            includeGroup 'maven.modrinth'
        }
    }
}

dependencies {
    // NeoForge
    implementation 'maven.modrinth:tails-legacy:<version>'
    // Forge (via ForgeGradle)
    implementation fg.deobf('maven.modrinth:tails-legacy:<version>')
    // Forge 1.7.10 or 1.12.2 (via RetroFuturaGradle)
    implementation rfg.deobf('maven.modrinth:tails-legacy:<version>')
}
```