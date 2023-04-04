# Changelog

## Alpha 3.2.2
* Successfully(?) updated to MC 1.19.4

## Alpha 3.2.1
* Farmland is no longer trampled if the trampler is wearing Feather Falling
* Crops broken by non-entities (e.g. Water) now drop nothing
* Sheep no longer regrow wool 100% of the time after eating
* Tiredness
  * Increased tiredness required to sleep and effect
  * Vigour on wake up changed (Vigour II for 8 minutes -> Vigour I for 20 minutes)
  * Fixed tired applying continuously
* Copper from campfire now requires 4 minutes instead of 5
* Iridium tool durability increased by 10%
* Expanded 
  * Now shows the blocks being broken
  * Tool now takes 1 damage per block broken
  * No longer works if the tool is not correct for the block broken

## Alpha 3.2.0
* Enchantments
  * Added Magnetic enchantment for Pants
  * Added Magic Protection enchantment for armor
  * Added Blasting, a new Pickaxe enchantment that increases efficiency against low explosion resistant blocks (e.g. ores). Mutually exclusive with Efficiency and Expanded
  * Added Expanded, a new tool enchantment that increases the blocks mined. Mutually exclusive with Efficiency and Blasting
* Stack Sizes (module no longer exists, has been moved to Items as a feature)
  * Almost doubled food stack size
  * Saddle and horse armors now stack to 8
  * Eggs now stack to 64
* Added Unfair One-Shots
  * When players' above 7.5 hearts would take enough damage to kill them, they are instead left with half a heart
* Added Fire feature
  * Logs burnt by fire nownhave 25% chance to drop charcoal
  * Two flints in both hands can be used to set on fire blocks. High chance of breaking. 
  * Fire now spreads 4x faster
* Mobs
  * Disabled Creeper Cena and walking fuse creepers now slow down when swelling
  * Skeletons running away from players are now less painful to deal with
  * Removed follow range buff from zombies, also slightly lowered buffs for non-zombies
  * Fixed mobs swim speed begin too high
  * Reduced Witches speed
* Reduced tiredness required before begin able to sleep
* Bone club durability increased but reduced damage
* Removed toughness and bonus toughness from iron and netherite armor
* Increased hoes cooldowns, but now efficiency reduces the cooldown by 1 tick per level
* Blast furnaces now take 2x time to smelt ores
* Added a global switch to disable all datapacks
* Food now takes 2x to cook in a furnace
* Iridium ore drops and ores per chunk increased but decreased ores per vein
* Increased iron nuggets drop with fortune
* Stamina now regenerates faster when locked
* Tridents now have +0.5 attack range
* Removed Iron and Iridium Tools bonus durability
* Torch recipe now crafts 3
* Reduced Tools efficiency and reduced global block hardness, but re-enabled depth hardness

## Alpha 3.1.0
* Added EnhancedAI mod
  * Mobs are tougher to fight as they have a few more tricks up their sleeve
* Added Iridium, a new ore found at any height plus a lot generates hidden about 16 blocks below the sea level
  * Can be applied to Iron equipment in a Smithing Table to get Iridium equipment, slightly better than iron but more durable
* Added Bone Club
  * If you dare to fight a skeleton you can use his bones to get a new weapon, low durability but quite good damage.
* Added "Actually Sweet Berry Bushes"
  * Entities are no longer damaged by berry bushes if they're wearing leggings and boots
* Temp Spawners is now "Spawners"
  * Increased spawning speed of spawners by 400%
  * Spawners now ignore light levels
  * Hugely increased performance when checking if the spawner is disabled
* Iron armor requires leather armor to be made (again)
* Iron tools now have 50% more durability
* Flint shield no longer requires leather to be made
  * Copper shield now does
* Terrain slowdown no longer removes the slowdown when the player is midair
* Grass blocks now slowdown less
* Honey now heals less
* Overhauled textures
* Removed baby zombies
* Reduced health for mobs by 20%
* Dungeon loot is now less purged and fixed too many spawner heads
* Desire paths no longer trigger if going slow enough (e.g. when sneaking)
* Villages no longer generate until I find a way to make them not so much useful

## Alpha 3.0.2
* Added Shields+
  * Removed the shield changes in the mod, using Shields+ ones
  * Added Flint Shields and disabled wooden shields
  * Metal shields require Anvil to be made
* Desire paths now make the player break tall grass
* Added Path to Dirt as dependency
* Added Chained Copper Armor
  * Iron Armor is now made with Chained Copper Armor
* Spiders no longer apply Slowness 2 on hit
* Iron Golems now have Resistance III instead of II
* Villagers loot is now also reduced based on distance from spawn
* Animals no longer spawn in Winter
* Tilling soil animation is no longer played if the hoe is disabled
* Wooden tools are now replaced with stone ones in loot chests
* Furnaces now require copper to make
  * Copper can be made in campfires over 8 minutes with Raw Copper
* Copper now takes 4x time to make in furnace
* Deepslate ore is now less hard to break
* Fixed arrows dealing the wrong amount of damage (and skeletons dealing tons of damage)

## Alpha 3.0.1
* Shears now Require an anvil to make (flint + 2 iron ingots)
  * Durability has been increased (33% -> 50% of vanilla durability)
* Torches in campfire now take less time to make (30 seconds -> 15 seconds)
  * Also torches can now be made with glow lichen
* Fixed tool stats formatting (numbers, color and space)
* Stone tools are now slightly faster (2 -> 2.5 mining speed)
  * Flint tools now have slightly higher durability 33 -> 38 durability
* Grass now slows down more
* Health regen and food
  * Passive regen is now disabled
  * Time to eat has been reduced for high effectiveness foods
  * Food Hunger Multiplier is now 1 and his old value is now applied to 'Food Heal.Health Multiplier'
  * Healing speed is now inverse (high saturation gives slower regen)
  * Fixed raw food not healing
* Iron veins are now slightly bigger but generate less when exposed to air
* Added World module
  * Added Desire paths, walking on the same path over and over has a chance to transform grass into dirt and dirt into coarse dirt
  * Moved loot purger to it

## Alpha 3.0.0
* First Alpha