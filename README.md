![Tails Legacy banner](common/src/main/resources/assets/tailslegacy/textures/banner.png)

A Minecraft mod that adds in a variety of recolorable tails, ears, wings, and other accessories.

This is a fork of [the original project](https://github.com/kihira/Tails) which has been updated to newer versions of Minecraft and expanded with additional features.
(This fork is based on the 1.12 branch — not the in-development rewrite.)

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

### Data format

To support all of these changes, the Tails data format has changed significantly:

* Part "types", "sub types", and textures are now strings, instead of integers
* Part scale is gone (this was never used)
* Empty parts aren't saved
* Part types (ears, tail, muzzle etc; not fluffy tail vs raccoon tail, etc) are inferred from context

Basically, the accessory data is smaller and uses fewer "magic numbers."

Additionally, Tails Legacy will upgrade your old library and accessories to the newest format, so you don't have to worry about recreating them from scratch.
This works on theoretically every version since 1.7 — you can toss a 1.7-era library in the game directory and Tails Legacy will upgrade it for you.
This process is not reversible, so you can't take a new Tails library and use it on an older version of the mod (or on Tails itself).

## Data-driven parts

As mentioned earlier, Tails Legacy's part system is completely data-driven.
For most people, the only change will be that resource packs can now define their own parts/sub-types/textures.

There is a guide to creating parts, sub-types, and textures [here](docs/creating_parts.md).