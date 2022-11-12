# Changelog

## Upcoming
* Requires InsaneLib 1.7.2
* [Moved many blacklists to data tags](https://github.com/Insane96/IguanaTweaksReborn/wiki/%5B1.19-and-up%5D-Tags) and added some new ones
  * Added tags to disable items' damage and/or efficiency
* [Moved many lists to json files](https://github.com/Insane96/IguanaTweaksReborn/wiki/%5B1.19-and-up%5D-Json-Configs). Also added a few new setting for some of them
* Added a new `Misc` feature under `Miscellaneous` module
  * Adds a feature that prevents entities from catching fire when have fire resistance potion on
* Added a new `Misc` feature under `Client` module
  * Adds a feature that removes the enchanting glint from potions
* Replaced Health Regen presets with "Load Combat Test Config Options"
  * If true, restart the game and the config options will be changed to the ones of the combat test snapshot and then set the config option back to false.
* Replaced `Reduce Weapon Damage` with `Nerf weapons`
  * Reduced axes damage reduction (-1.5 -> -1) (now like Swords and Tridents)
  * Axes now get -1 attack range
* Fixed conduit dealing near to 0 damage (instead of 2 minumum). Also added a few more config options.
* Terrain slowdown is now updated twice as slower
* Disabled hoes are no longer damaged when trying to till. They can still be used to break blocks.
* 'Food Consuming' and 'Food Hunger' have been merged to a single feature 'Food'
* Well Fed is no longer applied for low effectiveness (hunger + saturation) food.
* Injured is no longer applied for low damages (less than 1.5 hearts).
* Greatly reduced Energy boost effectiveness (~~-1~~ -> -0.2 tiredness per second)
* Increased Energy Boost default duration by 5x (and made it configurable)
* Changed regeneration effect on wake up (~~10~~ -> 60 seconds Regeneration ~~II~~ -> I)
* Tool Efficiencies no longer reduce overall tool efficiency but sets the base tool one.
* Plants growth multipliers are now also affected by sunlight and nighttime
* Reduced shield slowdown (~~15%~~ -> 10%)
* Fog Under Lava with Fire Resistance is now slightly more foggy
* Fixed General Stacking not working properly (resulting in different stacks each time the game was restarted)
* Fixed Hunger Consumption Chance always being 0.5
* Fixed some strings not begin translatable
* Fixed tiredness multiplier not working
* Fixed harder crops applying hardness to non-insta-break blocks too

## Alpha 2.13.1
* Requires InsaneLib 1.7.1
* Added back Client module
* Unmending now resets the repair cost of an item instead of capping the repair cost.

## Alpha 2.13.0
* Updated to 1.19.1+
* Increased unmending cap (~~20~~ -> 25 levels)
* Pets are now affected by Beacons