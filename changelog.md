# Changelog

## Upcoming
* Regenerating absorption no longer uses vanilla absorption
  * Instead, a new shield like icons will appear above the XP bar
* Fixed wandering traders crashing when disabled

## 3.1.13
* I hate client-server stuff

## 3.1.12
* Fixed enchantments not synced between client and server

## 3.1.11
* Fixed crash in multiplayer with Backwards slowdown

## 3.1.10
* Fork me

## 3.1.9
* Crash with ITE

## 3.1.8
* Totem of Undying now gives 5 seconds of Resistance IV
* Fixed fishing rods being usable when broken
* Fixed step sounds heard twice in multiplayer
* Fixed (hopefully and again) item stats not being synced when on server
* Fixed mod crashing the game before the dependencies screen

## 3.1.7
* Golden apples now give Regenerating Absorption instead of absorption
* Regenerating Absorption
  * Golden Apples now give regenerating absorption instead of absorption
  * Effect now gives 10% more regen speed per level
  * Fixed regenerating absorption blinking with floating point value armor
* Minor fixes and balancements
* Sleeping effects is now disabled by default
* Obsidian is less hard to break

## 3.1.6
* Added `iguanatweaks:doLooseRespawn` game rule to disable the loose respawn mechanic

## 3.1.5
* Changed armor (or regen. absorption) for all armor to use decimal values
  * This makes the armor they give more accurate, so it's better to mix and match armor pieces
  * Also minor changes to other stats
* Changed efficiency formula yet again
  * From +50% efficiency per level to +16/40/80/136/208/296% from level I to VI
* Fixed Regenerating Absorption>Un-damaged time to regen not applying if only absorption hearts were damaged
* Budding amethyst are now harder to break
* Tired I no longer applies modifiers until II

## 3.1.4
* Hoes 
  * No longer have +0.5 attack range
  * All hoes are damaged by 4 durability when tilling (no longer reduced for higher tier materials)
  * Reduced cooldown when tilling
    * About -33% cooldown of a ITE hammer
* Protection enchantment now protects from any source outside the minecraft:bypasses_armor damage type tag
* Fixed missing "Phantoms in the End" feature
* Fixed iron tools having -10% attack speed

## 3.1.3
* Increased wooden and stone tools efficiency (1.5 -> 1.7 and 2 -> 2.2)
* Reduced berries from berry bushes
* Slightly increased health healed overtime from foods
* Chickens are now tempted by carrot seeds
* Fixed plants growth multiplier not synced to server

## 3.1.2
* Fixed item stats not syncing the first time a server is joined
* Fixed compatiblity with Paxi
* Fixed stamina overlay not jiggling together with health when low
* Fixed stamina not regenerating correctly when close to locked stamina

## 3.1.1
* Sweet berries can no longer be planted, instead bushes now drop seeds
  * Bone meal has now 50% chance to fail when used on sweet berry bushes
* Protection enchantment is now treasure
* Absorption is now affected only by damage types outside the minecraft:bypasses_armor damage type tag
* Added a config option to disable the replacing of damaging enchantments
* Further reduced Shovels base damage (3 -> 2.5)
* Ancient debris can no longer be smelted in furnaces
* Fixed 'Player's killer bounty' name changing before the player's death
* Fixed diamond and netherite armor having vanilla toughness
* Fixed respawn mobs despawn despawning persistent mobs
* Fixed over easy egg from ITE being cookable in a furnace
* Paxi and Global Data mods now should run after this mod's, allowing for overwriting changes from integrated packs

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