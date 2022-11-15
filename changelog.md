# Changelog

## Upcoming
* Added "Pumped" effect, given when the player sleeps and doesn't have the Tired effect
  * Decreases hunger consumption
* Added `Arrows don't trigger invincibility frames`.
  * Like combat test snapshots, hitting a mob with multiple arrows will no longer bounce them off but actually hit it.
* Removed Regeneration from Sleeping Effect
* Night vision fade out now take 3 seconds instead of 4
* Changed a few materials slowdowns
  * In Plants (15% -> 10%)
  * In Bamboo Saplings (15% -> 10%)
  * On Snow (20% -> 10%)
  * On Snow layers (top_snow) (0% -> 10%)
  * On Wool (20% -> 15%)
  * On Bamboo (10% -> 15%)
* Fixed tags that loaded on config change not working
  * `iguanatweaksreborn:harder_crops`, `iguanatweaksreborn:food_drinks_no_hunger_changes`, `iguanatweaksreborn:no_stack_size_changes`
* Also fixed tags in json not working

## Beta 2.13.2
* Requires InsaneLib 1.7.2
* [Moved many blacklists to data tags](https://github.com/Insane96/IguanaTweaksReborn/wiki/%5B1.19-and-up%5D-Tags) and added some new ones
  * Added tags to disable items' damage and/or efficiency
* [Moved many lists to json files](https://github.com/Insane96/IguanaTweaksReborn/wiki/%5B1.19-and-up%5D-Json-Configs). Also added a few new setting for some of them
* Added a new `Misc` feature under `Miscellaneous` module
  * Adds a feature that prevents entities from catching fire when have fire resistance potion on
* Added a new `Misc` feature under `Client` module
  * Adds a feature that removes the enchanting glint from potions
* Client
  * Fog Under Lava with Fire Resistance is now more foggy
  * Fixed a bug where better Nether Fog would cancel all the other mods fog events (even Tiredness one) even when not in the nether
* Combat
  * Replaced `Reduce Weapon Damage` with `Nerf weapons`
    * Reduced axes damage reduction (-1.5 -> -1) (now like Swords and Tridents)
    * Axes now get -1 attack range
* Farming
  * Disabled hoes are no longer damaged when trying to till. They can still be used to break blocks.
  * Plants growth multipliers are now also affected by sunlight and nighttime
    * Cacti now grow much slower outside their biome
  * Fixed harder crops applying hardness to non-insta-break blocks too
* Hunger & Health
  * 'Food Consuming' and 'Food Hunger' have been merged to a single feature 'Food'
  * Replaced Health Regen presets with "Load Combat Test Config Options"
    * If true, restart the game and the config options will be changed to the ones of the combat test snapshot and then set the config option back to false.
  * You now starve faster if you have negative hunger (obtained by sleeping when too tired and low hunger)
  * Well Fed is no longer applied for low effectiveness (hunger + saturation) food.
  * Injured is no longer applied for low damages (less than 1.5 hearts).
  * Fixed Hunger Consumption Chance always being 0.5
* Mining
  * Tool Efficiencies no longer reduce overall tool efficiency but sets the base tool one.
* Miscellaneous
  * Fixed conduit dealing near to 0 damage (instead of 2 minumum). Also added a few more config options.
* Movement
  * Reduced shield slowdown (~~15%~~ -> 10%)
  * Terrain slowdown is now updated twice as slower
* Sleep & Respawn
  * `Allow Sleeping During Day` is now disabled by default due to Tiredness controlling if you can sleep
  * Beneficial effects are no longer given to players after sleeping if they wake up with empty hunger bar
  * Greatly reduced Energy boost effectiveness (~~-1~~ -> -0.2 tiredness per second)
  * Increased Energy Boost default duration by 5x (and made it configurable)
  * Changed regeneration effect on wake up (~~10~~ -> 60 seconds Regeneration ~~II~~ -> I)
  * Fixed tiredness multiplier not working
* Stack Sizes
  * Fixed General Stacking not working properly (resulting in different stacks each time the game was restarted)
* Fixed some strings not begin translatable
### Known bugs
* Stuff that loads as soon as the configs load doesn't support tags (e.g. Stack sizes blacklist tag or Tool Stats Jsons)

## Alpha 2.13.1
* Requires InsaneLib 1.7.1
* Added back Client module
* Unmending now resets the repair cost of an item instead of capping the repair cost.

## Alpha 2.13.0
* Updated to 1.19.1+
* Increased unmending cap (~~20~~ -> 25 levels)
* Pets are now affected by Beacons