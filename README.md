# Tails

A Minecraft mod that adds in a variety of tails, ears, wings, and other accessories.

This is a fork of [the original project](https://github.com/kihira/Tails) that has been updated to newer versions of Minecraft and expanded with additional features.

### Differences

There are several major differences in this fork (the mod has effectively been rewritten like five times now), but here are some of the main ones:

* Updated for modern versions of Minecraft
* New tails, "ears," wing textures, etc
* Data-driven part definitions
* A better API for other mods
* Better documentation (sometimes)

For user-facing differences (the ones you're most likely to see first):

* "Sub types" are now another set of buttons instead of selecting them in the part list
	* Example: the nine tails sub type is now under the fluffy tail instead of shown separately
* Exporting/importing from skin is completely gone
* Server-side Tails libraries are gone

#### Data format

To support all of these changes, the Tails data format has changed (a lot):

* Part "types" are now strings, instead of integers
* Part "sub types" are also now strings
* Part textures are similarly also strings
* Part scale is gone (this was never used)

Basically, the accessory data is smaller and uses fewer "magic numbers."

Additionally, Tails will upgrade your old library and accessories to the newest format, so you don't have to worry about recreating them from scratch.
Unfortunately, this process is not reversible; you can't take a new Tails library and use it on an older version.