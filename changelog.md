### Changes

* Fix a potential error when a mod tries to render an invisible player (e.g., First Person Model)
* Fix Tails Legacy crashing whenever part data fails to load
* 1.7.10, 1.12.2: Fix failing to load part data when any resource pack has a file directly in the `assets` folder
* 1.7.10: Fix Tails Legacy attempting to load its language data too early when Angelica loads vanilla language data early
* 1.12.2: Also apply the 1.7.10 language fix just in case
* Renamed mixin method prefix from `tails$` to `tailslegacy$` (note: this does not touch the existing `t$` prefix on Tails Legacy bindings)