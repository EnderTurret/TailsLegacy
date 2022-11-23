# Tails

A Minecraft mod that adds in a variety of tails, ears, wings, and other accessories.

This is a fork of [the original project](https://github.com/kihira/Tails) that has been updated to newer versions of Minecraft and expanded with additional features.

## Differences

There are several major differences in this fork (the mod has effectively been rewritten like five times now), but here are some of the main ones:

* Updated for modern versions of Minecraft
* New tails, "ears," wing textures, etc
* Data-driven part, "sub type", and texture definitions
* A better API for other mods
* Numerous code improvements (partially subjective)
* Better documentation (sometimes)

For user-facing differences (the ones you're most likely to see first):

* "Sub types" are now another set of buttons instead of selecting them in the part list
	* Example: the nine tails sub type is now under the fluffy tail instead of shown separately
* Exporting/importing from skin is completely gone
* Server-side Tails libraries are gone

### Data format

To support all of these changes, the Tails data format has changed (a lot):

* Part "types", "sub types", and textures are now strings, instead of integers
* Part scale is gone (this was never used)
* Empty parts aren't saved
* Part types (ears, tail, muzzle etc; not fluffy tail vs raccoon tail, etc) are inferred from context

Basically, the accessory data is smaller and uses fewer "magic numbers."

Additionally, Tails will upgrade your old library and accessories to the newest format, so you don't have to worry about recreating them from scratch.
This works on theoretically every version since 1.7 -- you can toss a 1.7-era library in the game directory and Tails will upgrade it for you.
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
	localPlayerOutfit = "..."
```

This change means that you won't be able to just drop an old config in the configs folder and expect it to work.
Fortunately, you can still use your old customization data by following following one of these guides:

#### Option 1

1. Run the game with the older version of Tails
2. Save your "outfit" by adding it to your Tails library
3. Move the Tails library file (`tailslibrary.json`) to wherever the newer version of Tails is installed
	* This will be inside the same folder that contains the `mods` folder
4. Your "outfit" will be available in the newer version as an entry in the Tails library

#### Option 2

1. Open the old config (usually at `tails.cfg`)
2. Copy the contents of the "Local Player Data" line (after the `=`)
3. Open the new config (at `tails-client.toml`)
4. Paste the contents into the `localPlayerOutfit` field (between the quotes)
5. Lastly, you need to "escape" all of the quotes in the data (turning all of the `"` into `\"`).
	This can be accomplished manually or by using a search-and-replace tool in a text editor.

Tails will handle upgrading this data to the newer format, so you don't need to do any more work than this.