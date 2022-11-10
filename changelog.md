# Changelog

## Upcoming
* Requires InsaneLib 1.7.2
* [Moved many blacklists to data tags](https://github.com/Insane96/IguanaTweaksReborn/wiki/%5B1.19-and-up%5D-Tags) and added some new ones
  * Added tags to disable items' damage and/or efficiency
* Added a new `Misc` feature under `Miscellaneous` module
  * Adds a feature that prevents entities from catching fire when have fire resistance potion on
* Replaced Health Regen presets with "Load Combat Test Config Options"
  * If true, restart the game and the config options will be changed to the ones of the combat test snapshot and then set the config option back to false.
* Replaced `Reduce Weapon Damage` with `Nerf weapons`
  * Reduced axes damage reduction (-1.5 -> -1) (like Swords and Tridents)
  * Axes now get -1 attack range
* Disabled hoes are no longer damaged when trying to till. They can still be used to break blocks.
* 'Food Consuming' and 'Food Hunger' have been merged to a single feature 'Food'
* Plants growth multipliers are now also affected by sunlight and nighttime
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