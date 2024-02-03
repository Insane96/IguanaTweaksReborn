# Changelog

## Upcoming
* Protection enchantment is now treasure
* Fixed 'Player's killer bounty' name changing before the player's death
* Fixed diamond and netherite armor having vanilla toughness

## 3.1.0
* Replaced vanilla weapon enchantments with the mod's ones
  * Bane of SSSSS, Smite and Sharpness bonus damages now scales with the weapon's damage (So a hoe with one of those enchantments will not obliterate monsters)
  * Also, every tool can now get weapon enchantments
  * Knockback and Fire Aspect have also been replaced
    * Fire Aspect now deals damage .5s later so the first fire tick is not lost
  * With this, sharpness has been re-enabled
  * Bonus damage scales with weapon attack damage (every 5 AD). Sharpness bonus damage is 0.75 per level, specific enchantments instead have 1.25 bonus damage per level.
* Replaced vanilla protection enchantments with mod's ones
  * Protection has been re-enabled, max 1 level protects for 6% of damage
  * All the specific protections now have the same chance to appear in the enchanting table and same rarity
  * Projectile protection now reduces monster sight
* Item Stats
  * Now accepts `enchantability` to change the item's enchantability
* Rebalanced all the armors
* If ITE is installed and Harder Torches Data Pack is enabled, torches can be made with Cloth
* Reduced axes base damage (6 -> 5)
* Reduced shovels base damage (3.5 -> 3)
* Added a few config options for Graves and Better Ladders
* Each level of frost walker now reduces slowdown on ice by 25%
* Fall damage now ignores invincibility frames
* Fixed Elytra and fishing rod having wrong durability
* Fixed Infinity overhaul lost in SR split
* Fixed Weighted equipment armor and toughness modifiers not working if Absorption armor is enabled
* Fixed crash with Tiredness and Guard villagers
* Renegerating absorption 
  * 'Un-damaged time to regen' increased (1.5s -> 2.5s) but is now affected by regen speed
  * Fixed regen absorption speed behaving strangely when entity lost health
* Fixed disabling wild crops data pack still preventing potatoes and carrots from planting 

## 3.0.5
* Regeneration in beacon costs much more time
* Beacon now accepts only stacks of 1 item
* Fixed chainmail armor having wrong stats
* Fixed first enchantment on armor being invisible
* Fixed Quark compatibility

## 3.0.4
* Added player's killer bounty
  * Mobs that kill players will not despawn and will drop 4x more experience and loot
* Fixed diamond generation backport not working
* Fixed Wandering Trader trades being not stackable

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