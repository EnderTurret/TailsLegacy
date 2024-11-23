# Tails Legacy

A Minecraft mod that adds in a variety of tails, ears, wings, and other accessories.

This is a fork of [the original project](https://github.com/kihira/Tails) that has been updated to newer versions of Minecraft and expanded with additional features. (This fork is based on the 1.12 branch — not the in-development rewrite.)

## Differences

There are several major differences in this fork (the mod has effectively been rewritten like five times now), but here are some of the main ones:

* Updated for modern versions of Minecraft
* Part types rewritten into attachment points
* New tails, "ears," wing textures, etc
* Data-driven part, "sub type", texture, and attachment point definitions
* A better API for other mods
* Numerous code improvements (partially subjective)
* Better documentation (sometimes)

For user-facing differences (the ones you're most likely to see first):

* "Sub types" are now another set of buttons instead of selecting them in the part list
	* Example: the nine tails sub type is now under the fluffy tail instead of shown separately
* Part type selection is now two "spinners" for selecting attachment point
* Exporting/importing from skin is completely gone
* Server-side Tails libraries are gone
* Importing library entries actually works now

### Data format

To support all of these changes, the Tails data format has changed (a lot):

* Part "types", "sub types", and textures are now strings, instead of integers
* Part scale is gone (this was never used)
* Empty parts aren't saved
* Part types (ears, tail, muzzle etc; not fluffy tail vs raccoon tail, etc) are inferred from context

Basically, the accessory data is smaller and uses fewer "magic numbers."

Additionally, Tails will upgrade your old library and accessories to the newest format, so you don't have to worry about recreating them from scratch.
This works on theoretically every version since 1.7 — you can toss a 1.7-era library in the game directory and Tails will upgrade it for you.
This process is not reversible, so you can't take a new Tails library and use it on an older version.

### Configuration

The original Tails config looked something like this:

```
forcelegacyrendering {
    B:client=false
}

general {
    B:"Enable Library"=true
    S:"Local Player Data"=...
}
```

Forge hasn't supported this esoteric config format since, so this is what the config looks like now:

```toml
[client]
	localPlayerData = "..."
```

This change means that you won't be able to just drop an old config in the configs folder and expect it to work.
Fortunately, you can still use your old customization data by following one of these guides:

#### Option 1

1. Run the game with the older version of Tails
2. Save your customizations by adding it to your Tails library
3. Move the Tails library file (`tailslibrary.json`) to wherever the newer version of Tails is installed
	* This will be inside the same folder that contains the `mods` folder
4. Your customizations will be available in the newer version as an entry in the Tails library

#### Option 2

1. Run the game with the older version of Tails
2. Save your customizations by adding it to your Tails library
3. Export the library entry to clipboard and paste it somewhere (like a text file)
4. Run the game with the newer version of Tails
5. Import the copied text into the Tails library of the newer version
6. Your customizations is now available as an entry in the Tails library

#### Option 3

1. Open the old config (usually at `tails.cfg`)
2. Copy the contents of the "Local Player Data" line (after the `=`)
3. Open the new config (at `tails-client.toml`)
4. Paste the contents into the `localPlayerData` field (between the quotes)
5. Lastly, you need to "escape" all of the quotes in the data (turning all of the `"` into `\"`).
	This can be accomplished manually or by using a search-and-replace tool in a text editor.
6. Boot up the game and see if this process worked

Regardless of option, Tails will handle upgrading the data to the newer format, so you don't need to do any manual updating.

## Data-driven parts

As mentioned earlier, Tails's part system is now completely data-driven.
For most people, the only change will be that resource packs can now define their own parts/subtypes/textures.

There is a guide to creating parts, subtypes, and textures [here](docs/creating_parts.md).