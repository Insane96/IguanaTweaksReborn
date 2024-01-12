# Changelog

## Upcoming
* Fixed diamond generation backport not working

## 3.0.3
* Sleeping now adds 10 minutes to the daytime instead of skipping the day/night
* Leather and chained copper armor are no longer unbreakable
* Slight re-balance of Iron Tools
* Fixed copper and flint tools being unbreakable

## Beta 3.0.2
* Mobs now despawn in a radius (48 for world respawn and 24 for bed respawn) from the player respawn point
* Missing IguanaTweaks Expanded stuff
* Fixed advancement triggers not being registered
* Fixed items tooltips with modifiers in multiple slots showing all in the last slot

## Beta 3.0.1
* Added missing cyan recipe from cyan flower
* General clean up

## Beta 3.0.0
Please note this changelog is based off the latest Survival Reimagined version, and not 1.19.2 ITR version
* Item Stats
  * Now Items statistics are defined via Data Pack
    * `item`: id or item tag
    * `max_stack`: The max stack size of the item
    * `durability`
    * `efficiency` (only for tools)
    * `attack_damage`, `attack_speed`
    * `armor`, `armor_toughness`, `knockback_resistance` (armor only)
    * `modifiers`: A list of modifiers to apply to the tool (stolen from Stats)
* Removed `item_modifiers.json`. Check Item Stats
  * Moved `iguanatweaksreborn:remove_original_modifiers`
* Removed `custom_stack_sizes.json`. Check Item Stats
* Client
  * Added config option to disable the shorter world border
  * Fixed End music being changed even if the Sound feature was disabled
* Added the possibility to change what a pumpkin drops when sheared with the loot table `iguanatweaksreborn:pumpkin_shear`
* The delay between breaking blocks is now reduced with higher speed tools 
* Fixed Pickaxes being OP
  * Too much damage (literally double any other tool / weapon)
  * Piercing damage didn't scale with attack cooldown
* Merged Global, Block, Mobs and Other experience into a single Feature