# Changelog

## 3.11.1
* Global experience multiplier increased (1.3x -> 1.5x)
* Block experience multiplier reduced (2.25x -> 2x)

## 3.11.0
* Added Forge
  * Replaces anvil recipes
* Added Better ladders
  * Descend faster and crouch when inventory is open
* Falling onto a climbable no longer cancels fall damage
* Each cake bite now restores 40% of missing health
* Multiblock furnaces now pick up items from the inside of the structure everytime an item has finished smelting
* Unbreaking now requires fewer levels to pop out
* Increased beeg veins size
* Increased Trident durability
* Increased global experience

## 3.10.2
* Disabled Golden Absorption. Gold armor and shield now grant +2 max health
* Invincibility frames are now synced when hit by arrows
* Glass can now break if entities fall on it
* Re-enabled backwards slowdown
* Reduced experience dropped by players by 5%

## Alpha 3.10.1
* Mithril is now Durium
  * Can be made in a Blast Furnace by alloying Durium Scrap Block, Clay and Sand
* Blast furnaces now require a multi block structure
  * Can recycle gear (75% return)
  * Can alloy stuff
* Added Soul Blast Furnaces. An upgraded version of a Blast Furnace
  * Alloys stuff faster
  * Has 40% chance to output one more ingot when smelting and 20% when alloying ingots
  * Recycling yelds 100% of materials used
  * Requires lava buckets
  * Has more slots
* Netherite can now be alloyed in blast furnaces from less material compared to crafting
* Flint rocks now generate in the world that drop Flint
* Shallow water is no longer safe to fall into
* Firestarters now generate in chests instead of Flint and Steel in the overworld
* Armor is now rendered in place of Hunger bar. That side felt so empty
* Graves can now be smelted/blasted into iron ingots

## Alpha 3.10.0
* Overhauled rails
  * Added Copper Rails and Golden Rails
* Added M.A. Jump enchantment
* Critical Hits now deal 25% more damage instead of 50%
* Fixed timber getting more trees than expected
* Fixed spiders attack reach and skeletons riding them
* Creeper cena now deals more knockback
* Started working on better Blast Furnace

## Alpha 3.9.2
* Added Timber Trees feature
  * Makes trees fall when cut
* Added Smartness enchantment
  * Increases experience dropped by mobs by 50% per level. Incompatible with Looting
* Added durability to gear and increased durability of some materials
* Tiredness is no longer reset on death
* Fixes and advancements

## Alpha 3.9.1
* Added over-easy egg
* Rotten Flesh can now be composted
* Charcoal can now be placed as layer and layers now fall on top of each-other properly
* Ghasts now drop 5x more gunpowder
* Fixes and improvements

## Alpha 3.9.0
* Added Passable Foliage mod
* Added better attack invincibility
  * Attacking faster than 2 hits per second will make entities invulnerable for less time. Only works when wielding weapons.
* Rebalanced Combat stats
  * Players' Entity Reach is now reduced by 0.5 by default
  * Re-enabled Stone and Flint swords
  * Swords, Shovels and Hoes get +0.5 entity reach (attacking with an axe or a pickaxe has reduced range compared to vanilla)
  * Tridents get +1 entity reach (unchanged)
  * Swords get -2 attack damage
  * Pickaxes get -1 attack damage
* Rebalanced tools stats
  * Slightly increased Stone durability and increased Netherite durability
  * Slightly increased copper and flint efficiency
  * Slightly Decreased Netherite efficiency
  * Reduced flint base damage
* Rebalanced armor durability and slowdown
* Chainmail armor is now craftable from leather armor and chains
* Food can no longer be smelted in a furnace. Requires either a campfire or a smoker
* Players' Ghosts now die after 7:30 minutes
* Beeg veins are now variable size and the ore rocks on top now generate more the more ores generated below
* Golden Pickaxes and Golden Axes now have higher attack speed

## Alpha 3.8.1
* Respawn obelisk now requires only at least one catalyst block to activate, and will only try to break one of the catalysts instead of each one of them. Tweaked some chances.
* Slabs (not double) now take half the time to break
* Items from graves now despawn faster
* playerSleepingPercentage gamerule is now set to 1 to let tired player rest
* Fixes

## Alpha 3.8.0
* Changed death (again)
  * Dying now summons a grave. Breaking the grave will give back your items and summon the players' ghost with the xp, like before.
  * As before, if you die again, the grave breaks, dropping everything
* Respawning now happens in a min and max radius from world spawn/your bed
  * Instead of respawning from 0 to 256 blocks from the spawn point you'll now respawn from 128 to 256 blocks from spawn
  * Fixed respawning in lava
* Changed Beeg Veins feature to Ore Generation feature
  * Added Poor and Rich iron, gold and copper ores. The veins of these ores now generate randomly with poor, normal and rich ores
    * Poor ores drop 50% stuff while rich drops 200%
  * Beeg Veins still exist. They generate with Poor and Rich ores
* Added Nether feature
  * Added Portal requires Gold Block. The nether portal requires Gold blocks in the corner to be lit (only in the Overworld)
  * Added Remove Lava Pockets. This ... removes lava pockets from the nether
  * Moved No nether roof from Data Packs to this
* Added Rich Farmland
  * Use bonemeal on farmland or crouching on crops to transform it into rich farmland. This farmland ticks 3 times faster than normal farmland
* Added Fast Leaf Decay
  * Leaves decay faster
* Fire
  * Added Firestarter, made with 2 flints and one iron ingot
  * Campfires now come unlit and must be lit
* Tiredness
  * Tired effect no longer adds Fog, instead adds an overlay on the screen reducing sight
  * Tiredness now comes earlier but the effect takes more time to come
* Stamina
  * Jumping now consumes stamina
  * Tired effect now affects stamina consumption
  * Swimming now consumes less stamina
* Snow no longer slows down the player if he's wearing Leather Boots
* Florpium is now Mithril
* Skeletons now have a chance to spawn with bow ingredients instead of bows
* Coal is now minable only with tools that can mine diamonds
* Poison now damages entities much slower

## Alpha 3.7.1
* Added Anvil feature
  * An anvil dropping on a block may transform it
* Added charcoal layer
  * When logs burn now generate a charcoal layer instead of dropping charcoal
* Added Flint Block and Polished Flint Block
* Fishing changes
  * Junk chance is greatly increased, Luck of the Sea greatly decreases the junk chance
  * In small bodies of water fishes are rarer
* Shields recipes are now coherent with tools recipes
* Copper shields now block 4 damage instead of 3
* Reduced Fishing Rods Durability
* When you wake up you now get a Regeneration I effect for 15 seconds
* Coal generates much less when exposed now
* Phantoms now vary in size
* Ghost zombies health and damage now scales with the xp stored

## Alpha 3.7.0
* Added Explosive Barrels
  * Highly unstable will explode instantly
* Added Altimeter
  * Displays roughly your altitude
* Added Water Coolant enchantment
  * Deals bonus damage and freezes fire immune and water sensitive mobs
* Added "Piercing Pickaxe"
  * Pickaxes deal bonus damage that bypasses armor
* Removed Bone Club
* Added Red and Brown mushroom stews
* Out of season crops no longer break. They just don't grow
* Mithril now generates much more but no longer drops a sh*t ton of nuggets with fortune
* Players now have reduced block reach
* Player required xp to level up now scales differently (reverted to IguanaTweaks formula)
  * Was 50 xp per level, now is 3 * next_level
  * Formula is now customizable
* Player's Ghost 
  * Are stronger but neutral
  * Can now be right-clicked to get back items
  * Will die and drop all the stuff if the player dies again
  * Now also stores dropped XP. Players now drop more experience on death
* Creepers now drop 1-3 gunpowder instead of 0-2 and have a rare TNT drop
* More advancements work
* Chained copper armor can now be smelted/blasted
* Reduced Pumpkin and Melon generation
* Increased hide dropped from rabbits
* Fixed stuff using tags not syncing to client
* Rain Fog no longer affects players below Y 48

## Alpha 3.6.1
* Some stuff is now synced client-side
* Charcoal can now only be obtained from burning Logs. Also increased chance (40% -> 60%)
  * Also fixed charcoal not dropping from logs
* Increased Gold Armor Durability

## Alpha 3.6.0
* Added Beeg Ore Veins
  * Veins that generate underground with lots of ores. On the surface, rocks can be found that mark the veins below
* Fog now changes when it's raining
  * Colder seasons = more fog
* Unbreaking Overhaul
  * When a tool with unbreaking breaks, an Item fragment pops out with the same enchantments of the item that broke. Can be applied to a new item in an anvil to transfer the enchantments
* Added Golden Absorption
  * Wearing Gold Armor or shield gives one regnerating heart of absorption per piece (capped to the player health)
* Starting season is now Mid-Summer
* Breaking a tree without an axe now drops planks
* Buckets now require the Nether to be made
* Sheeps now have a low chance to drop wool on death
* Squids and fishes now come in various sizes
* Fishes no longer spawn in winter
* Removed Gold Nuggets drop from Zombified Piglins
* Miner Zombies now have a higher chance to spawn in Hard

## Alpha 3.5.0
* Added Copper Reinforced Flint Tools
  * Fast but low durability
  * Iron tools are now made from Stone Tools instead of Flint
* Adjusted the formula for healing from food
  * Lower Hunger foods now heal much less than higher hunger foods
* Unbreaking Overhaul
  * Unbreaking max level is now 1
  * Netherite tools durability has been increased (+50%)
* Added Bane of SSSSS. Deals bonus damage to Creepers and Spiders
  * Replaces Bane of Arthropods
* Chickens can no longer be bred, use eggs
* Starting season is now mid-Summer

## Alpha 3.4.1
* Renamed Iridium to Mithril
* Wild crops now generate more commonly
* Fixed crash in creative

## Alpha 3.4.0
* Added Crate
  * An early-mid game shulker to transport items around. Having more than one in the inventory will slow the player down
* Added Wild Crops
  * Crops that generate in the wild and drop seeds
  * Enlarging farms requires exploring / trading
  * Potatoes and carrots can no longer be planted, instead you must use the seeds counterpart
  * Removed wheat seeds from tilling grass
  * Wheat seeds can now be obtained from Wandering Traders
* Mod now runs on servers

## Alpha 3.3.0
* Advancements!
  * First step towards new advancements
* Re-enabled spawn points
  * World and bed respawns are now loose. Players will respawn up to 256 blocks from the world spawn/bed
* Added Respawn Obelisks
  * Structures found in the world which hold a Respawn Obelisk, a new block that allows you to set the spawn point by placing catalysts (precious blocks) nearby.
* Players that die now summon a Ghost with their items
  * Killing the ghost will give back your items
* Reworked Wandering Trader trades
  * Less junk and added items for emeralds trades
* Crops changes
  * Tall grass no longer drops seeds, you must till grass blocks
  * Zombies no longer drop potatoes and carrots
  * Water now hydrates farmland in a 2 block radius instead of 4
* Hoes can now break grass in a radius
* Infinity rework
  * Infinity max level is now 4
  * Now has 1 in level chance to not consume an arrow
* Added Step Up enchantment
  * +0.5 step height
* No Knockback has become Knockback
  * Instead of removing knockback, now attacking without a weapon or spamming has reduced knockback
* Added Nether Infused Powered Rails
  * 2.5x faster than normal rails
* Removed Mending and added Cleansed Lapis
  * New item that can reset the repair cost of items
* Added Ancient Lapis
  * New item crafted from Cleansed Lapis that can increase the level of enchantments over the maximum
* Equipment crafting in Anvil now has 7.5% chance to break anvil
  * Lower, compared to 12% vanilla
* Spiders are now smaller and Zombies can vary in size
* Slight rework of structure loot (more XP Bottles)
* Increased Golden Tools base efficiency
* Sprint jumping is now affected by movement speed
* The nether no longer has the 8 blocks ratio
* Swapped Flint and Stone tools' Durability and Efficiency
* Re-enabled Flint and Stone Hoes
* Smoker recipe now requires mithril

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
* Mithril tool durability increased by 10%
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
  * Logs burnt by fire now have 25% chance to drop charcoal
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
* Mithril ore drops and ores per chunk increased but decreased ores per vein
* Increased iron nuggets drop with fortune
* Stamina now regenerates faster when locked
* Tridents now have +0.5 attack range
* Removed Iron and Mithril Tools bonus durability
* Torch recipe now crafts 3
* Reduced Tools efficiency and reduced global block hardness, but re-enabled depth hardness

## Alpha 3.1.0
* Added EnhancedAI mod
  * Mobs are tougher to fight as they have a few more tricks up their sleeve
* Added Mithril, a new ore found at any height plus a lot generates hidden about 16 blocks below the sea level
  * Can be applied to Iron equipment in a Smithing Table to get Mithril equipment, slightly better than iron but more durable
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