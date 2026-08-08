### Changes

* Ported to NeoForge 26.2, Fabric 26.1, and Fabric 26.2
	* Note: support for 26.2 will likely be dropped in favor of 26.3 whenever it comes out
* Made parts data syncing not re-sync a player's part data to themself
	* Clients will also now ignore part data sent to them with the local player's `UUID` — in other words, the server can't remotely set one's own parts data
* Fixed the editor resetting parts data when clicking "Done", causing the selected parts data to only appear when the server resynced it (which the server no longer does)
	* This also fixes the part "flickering" issue (where after clicking "Done" one may still see the original parts for a moment), and by extension the animator reset issue (where after clicking "Done" the animations on the parts sometimes visually reset)