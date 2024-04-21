# Changelog

## Upcoming
* Anvil Repairs are now shown in EMI
* Reduced Hoglin drops
* Trident
  * Now repairable only up to 25% with prismarine and 50% with Crystals
  * Fixed not being able to repair with Crystals
* Revert 'Reduced cow fluid cooldown from 30 minutes to 20'
* Fixed unbreakable tools sometimes not working
* Fixed regenerating absorption not wrapping after 40
* Fixed new flowers in flower pots

## 3.8.2
* You can now use an empty Name Tag on a mob to remove its name tag
* Livestock
  * Old animals now take more time to produce milk, eggs and to be able to be bred again
  * Reduced cow fluid cooldown from 30 minutes to 20
* Reduced Golden tools efficiency (8 -> 7)
* Reduced Quark's Foxhound and Crab, Autumnity's Turkey drops
* Fixed chickens' laying eggs after a looong time when grown from babies

## 3.8.1
* Reduced Efficiency enchantment bonus speed (coefficient: 8% -> 6%)
* Stats
  * Reduced Gold Efficiency (9 -> 8), Base AD (1.5 -> 1), Attack speed (+10% -> +5%)
  * Reduced Diamond Efficiency (5 -> 4.5)
  * Reduced Netherite Efficiency (6 -> 5)
  * Increased Iron Efficiency (3 -> 3.5)
  * Reduced Gold Armor Armor (Full set: 5 -> 4)
* Wandering Trader Bottle o Enchanting Trade nerfed (3 -> 6 emeralds, max 8 -> 4 uses)
* Items can no longer be repaired with nuggets
  * This made repairing items with nuggets too good when they were enchanted
* Timber Trees
  * Hopefully fixed leaves sometimes not decaying after falling
  * Hopefully fixed blocks being places on water

## 3.8.0 - The Aging animals update
* Ageing animals!
  * The biggest problem with animals is that as long as you have space, you can have infinite of them (especially chickens). With this, you can have infinite but will die eventually
  * After growing from babies, animals will now have a life span, based off the type of animal
  * Cows (and mooshrooms), Chickens, Sheep, Pigs, Rabbits, Quark's Crabs, Autumnity Snails and Turkeys can die of old age
  * Animals have 4 "stages": Young, Adult, Mid age and elder
    * Young animals are considered as such when below 25% of their lifespan. They have normal drops
    * Adult animals are considered as such when between 25%~50% of their lifespan. They have increased drops
    * Mid age animals are considered as such when between 50%~75% of their lifespan. They have normal drops
    * Elder animals are considered as such when above 75% of their lifespan. They have reduced drops and are slowed down
  * If pehkui is installed, animals will also visually grow
* Graves 
  * Now drops only ... the drops instead of breaking the grave
  * Also, as soon as the player dies the drops will drop instead when the chunk is loaded
    * This fixes graves staying in the world even if the owner died, but it's no longer online 
* Changed golden tools mining level from iron tools to stone tools (so gold tools can no longer mine diamonds, coal, emeralds, redstone and gold)
  * The high enchantability and the fact that can mine like an Iron Pick makes them too strong 
* Slightly increased Tiredness debuffs (2% -> 2.5%)
* Arrow nerf now works differently
  * You may ask "In which way it works differently?", well, I don't really know, I just know I've reworked the code and used Mixins, since the old way was not behaving properly
  * HOWEVER, I know for sure that arrow's damage is no longer rounded up to the nearest integer, so between arrows and quartz arrows (from ITE) the difference now actually exists
  * The arrow damage nerf is also applied to crossbows now, but crossbows shoot arrows with more velocity
* Reduced treasure fishing loot's level of enchanting (30 -> 25) and can no longer be enchanted with treasure enchantments
  * Was way too high giving OP items and too easy obtaining treasure enchantments
* Vigour 
  * Vigour effect no longer increases stamina regeneration and uses the formula from the enchantment
  * Re-added back to beacons
  * Fixed enchantment formula (-25%/-40%/-45% -> -25%/-40%/-55% consumption)
  * Fixed enchantment not applying to jumps
* Increased Hoes damage modifier (-50% -> -60%)
* Fixed enchantments info not showing on enchanted books
* Fixed feather falling being too rare
* Removed a leftover log

## 3.7.5
* Added `iguanatweaks:explosionMobGriefing` game rule
* Reduced golden tools efficiency and attack speed
* On Unfair One-shot activation the player's now given resistance for a few seconds
* Feather Falling now reduces fall distance by 1 per level
* Changed Endermen drops (1 (+0\~1 per looting level) ender pearls -> 0\~1 (+-1~1 per looting level) ender pearls)
* Fixed vigour preventing stamina from consuming

## 3.7.4
* Reduced Vigour enchantment strength at higher levels (was -25% consumption per level, now is -25%/-45%/-60% at levels I/II/III)
* Reduced axes base damage (3 -> 2)
* Dying when tired II or more now resets the tiredness to Tired II instead of I
* Changed a few tired sounds
* Reduced coal generation on mountains
* Armor damage reduction now scales slightly less
* Fixed beetroots being insta-minable

## 3.7.3
* Repairing enchanted items in an anvil with another item now has a penalty on durability
* Fixed durability not synced client side
* Fixed missing allurement enchantment descriptions

## 3.7.2
* Rebalanced tools
  * Hoes: Increased durability by 30% and attack speed (~10% more). Increased base attack damage to 2 but applied a 50% attack damage reduction to it 
  * Axes: Decreased durability by 30% and changed damage (base damage 5 -> 2.5; bonus damage 0% -> 50%)
  * Pickaxes: Decreased base damage (0.5 -> 0) but added +20% bonus damage. Piercing is now 1 for each pickaxe + an amount given by materials
    * Piercing Pickaxes feature has been changed to Piercing Attribute since Piercing damage is added via item_stats data pack
  * Shovels: Decreased base damage (2.5 -> 1) but added +35% bonus damage
  * Stone tools have +5% attack damage
  * With this, bonus damage enchantments now scale with the percentage modifiers
* Air is now consumed faster and the more you're drowning, the more damage you're dealt
* Wandering traders now sell maps to Trail Ruins
* Slight rebalance of foods (stack sizes, heal over time, instant heal and eating time)
* Trail Ruins suspicious gravel loot has been slightly changed
* Fixed piercing damage not respecting new attack cooldown calculations
* Fixed mending working as both the old and new mending
* Fixed BlockData changing the air block when the id is not valid

## 3.7.1
* Reworked enchantment tooltips
  * Now are shown below the enchantment
* Wandering traders enchanted books can now have Treasure enchantments
* Reverted Mending
  * Mending now works again like vanilla but nerfed: 1 XP = ~~2~~ 1 repair
* Fixed crash when milking cows in multiplayer
* Graves now save the death message when broken
  * Also Graves now drop when players die again
* Fixed mod overwriting the bypasses_cooldown tag
* Fixed Wandering Trades not reloading on /reload

## 3.7.0 - The BlockData update
* Added block data
  * In the `data/block_data` folder can now be placed JSON to modify blocks properties, such as hardness, explosion resistance, speed and jump factor and a few more
  * With this, harder crops, custom hardness and terrain slowdown has been removed and moved to a brand new data pack (block_data)
    * To make crops able to be broken faster by hoes (and not axes) they still must be added in the `iguanatweaksreborn:harder_crops` block tag
* Sponges
  * Wet Sponges now dry when exposed to sun and Sponges now become wet when exposed to rain 
* Graves
  * Graves now save the death message of the player and can be seen when right-clicked
* Tiredness
  * When tired, random sounds will be played to the player
  * Increased stamina consumed and lowered stamina regenerated with the Tired effect
* Mending overhaul changed
  * Player experience **when item's used** instead of repairing on picked up experience
* Client > Misc
  * Player camera no longer tilts when taking specific damage types
* Falling while in a boat will now always break the boat and passenger will take fall damage

## 3.6.2
* Eggs no longer stack to 64
* Minecarts, horse armors and saddles now stack less
* Fixed wrong enchantment values on many materials
* Fixed pips crafting from berry good

## 3.6.1
* Fixed crash when joining servers

## 3.6.0 - The Abnormals' Release Update
* Autumnity Integration!
  * Eating with the Foul Taste effect now increases heal by 50%
  * Eating turkey or pancake now heals
* Other minor integrations with Autumnity and Berry Good
* Mending overhaul!
  * Mending now consumes player experience every 2 seconds instead of picked up experience
* Changed fortune formula to be similar to Vanilla but with reduced average
  * From previous version: Fixed increase 30%/60%/90%/120% -> Average increase 20%/50%/85.7%/125%
  * From vanilla: Average increase 25%/75%/120%/167% -> Average increase 20%/50%/85.7%/125%
* If player's stamina is below 20 (2 hearts) and stamina is locked, the player is slowed down by 20%
* Knockback
  * Pickaxes, axes and ITE Forge Hammers now deal reduced knockback
  * Moved all knockback related stuff to knockback feature (removed Bonking Shovels)
* Repairing an enchanted item material's cost is now 5% of total levels of enchantments + 0.5 per levels of enchantments
* Repairing Leather armor now requires fewer rabbit hide but can only be repaired partially
* Equipment dropped by mobs max durability left is now 60%
* Reduced swords base damage (-0.5)
* Reduced smite bonus damage (+1.5 -> +1.25)
* Vigour enchantment is now stronger
* 1 block in the nether is now 4 blocks in the overworld
* Added a flag config option to wipe the Data Pack list of a world, so they are loaded correctly
* Added an option for No Hunger to display on foods the regeneration amount
* Infested blocks now generate in every biome (more in mountains)
* Graves can now be crafted
* Increased material repair cost when repairing enchanted items
* Increased tired debuffs amount (1% -> 2%)
* Increased backward slowdown (25% -> 30%)
* Chance to break glass now increases exponentially
* Added config option to move the regenerating absorption hearts to the right
* Fixed crops being harder to break with axes
* Fixed items being usable when broken
* Fixed breeding broken with ITE installed
* Nerfed hoes

## 3.5.6
* Fortune has been nerfed
  * From 25%/75%/120% drop increase to 30%/60%/90%
* Nerossii can now be crafted into purple dye
* Reduced coal vein size
* Reduced block hardness in the nether
* Conduits no longer deal damage to all entities in the area, but only the nearest
* Reverted "No pillaring no longer has an allowed distance"
  * But jump boost no longer increases the allowed distance
* Dying in the void no longer generates a grave
  * Damage type tag `iguanatweaksreborn:doesnt_spawn_grave`
* Slightly rebalanced hardcore loot
* Air refills faster after 4 seconds that you can breathe
* Crossbows can now be crafted again
* Glowstone can now be mined faster with pickaxes
* Frost walker now decreases slowdown more on ice
* Fixed missing solanum neorossii loot table
* Fixed animals not dropping cooked food

## 3.5.5
* Added Vigour enchantment
  * A new chestplate enchantment that reduces stamina consumption
* Repairing enchanted items in an anvil now costs more materials
* OverTime healing now decays based off exhaustion instead of every tick
* Supplementaries 
  * Flax is now hard to break and grows slower. Also no longer drops more than 1 seed
  * Soap is no longer affected by food stack sizes
* Terrain slowndown now affects entities
* Some items now always drop regardless of the player kill
  * Music Discs
  * More items can be added to the `iguanatweaksreborn:reduced_no_player_kill_drop_blacklist` item tag
* Rebalanced materials enchantability
  * Lower tiers have higher enchantability
* Reduced Arrows and Power strength
* Quark's Forgotten is now affected by Smite
* Added a `StaminaEvent.Consumed` and `StaminaEvent.Regenerated`, two events to change the amount of stamina consumed and regenerated
* Fixed stackable soups having a strange behaviour when eaten and inventory was full
* Fixed desire paths no longer working

## 3.5.4
* Spawners now drop 1 echo shard when finish empowered. Also reduced activation range
* Regenerating absorption now applies before damage reduction from armor and enchantments
* BoneMeal event is ignored if other mods have allowed or denied the use of bone meal
* Reduced Feather Falling cost
* Rebalanced some materials
* No pillaring no longer has an allowed distance
* Reduced spawners player range

## 3.5.3
* Moved chicken food items to item tag `iguanatweaksreborn:chicken_food_items`
  * Also fixed not being able to use any item on chickens
* Increased experience from experience bottles
* Regenerating absorption
  * Added sound effect when regen absorption hearts are damaged
  * Revert regenerating absorption base speed (0.25/s -> 0.2/s)
  * Increased time to not get damaged before regenerating (5s -> 7.5s)
* Armor
  * Changed again armor formula
    * Toughness has been removed from the calculation and damage reduction has been increased
  * Toughness has been replaced with Regenerating absorption
  * Rebalanced armors accordingly
* Item statistics now accept `regenerating_abosorption`, `regenerating_absorption_speed` and `movement_speed_penalty`
* Moved HardCrops feature to Crops
* Reduced Critical hits damage (25% -> 20%)
* Fixed missing Leather armor repair data
  * Leather armor can now be repaired either with leather or with rabbit hide (with lower cost)
* Fixed stack sizes not syncing properly on clients

## 3.5.2
* Players are no longer able to pillar **only if** there are monsters nearby
* Instant heal food now heal more
* Reduced the speed at which over heal decays
* Reduced Honey bottle healing
* Reduced flint and steel durability
* Items on which enchantments can go now are fully data packable
  * Item tags: `iguanatweaksreborn:enchanting/accepts_damage_enchantments`, `iguanatweaksreborn:enchanting/accepts_fire_aspect`, `iguanatweaksreborn:enchanting/accepts_knockback`, `iguanatweaksreborn:enchanting/accepts_luck`
* Reduced thorns damage
* Fixed hoes having too much Attack Damage
  * With this, attack speed has been reverted to 2.5
* Fixed repairing enchanted items with non-enchanted ones costing levels
  * Also can no longer merge broken items
* Fixed enchantments not scaling with attack cooldown

## 3.5.1
* Spawners no longer give loot but give more experience
* Added back the "You should really get some sleep" message when reaching Tired V
  * Also the overlay is no longer applied at Tired I
* Over time heal decay when full health is now affected by regen speed
  * So with lower regen speed, the decay is slower
* Damaging enchantments are now applied earlier for various checks to apply
* Heavily reduced knockback without a weapon
* The player no longer has -0.5 attack reach 
  * Weapons have been changed accordingly to have the same reach as before
* Better Climbable (aka Better Ladders) now disables itself if Quark is installed
* Removed Hard mode
* Removed TnT from creepers drops
* Broken items can no longer attack
* Added a new flower can be obtained by breaking wild potatoes
  * Also new textures for wild crops
* Fortune (or Luck) now affects crops drops
* Fixed hoes putting out fire in a radius
* Fixed drop_multiplier Loot modifier applying to unstackable items
* Fixed snowballs dealing reduced damage to blazes

## 3.5.0
* Reworked spawners
  * Spawners now generate empowered and will spawn mobs faster  
    After 24 mobs spawned the spawner will slowdown but still remain active and award experience and loot
  * Also increased the range for activation from 16 to 32
  * Spawners no longer disable themselves after various mobs spawned and no longer give bonus experience when broken
* Tiredness
  * Energy boost effect is now stronger
  * On death only one level worth of Tired is removed
  * Now renders a texture overlay instead of darkening the screen
  * Reduced attack speed, movement speed, mining speed and stamina reduction
* Hunger and health
  * Foods with saturation below 4 now heal instantly, while >= 4 heal only overtime. Heal over time is also kept when full health and only slowly decays
  * Disabled vigour effect
  * If you die, you now respawn with less health based off difficulty
  * No Hunger can now be enabled/disabled with a gamerule
* Tools no longer take 2 damage when hurting entities
* Reduced Hoes base attack speed (2.5 -> 2.4) and knockback (-60% -> -75%)
* Reduced trident attack speed (1.1 -> 1.0), durability (475 -> 315) and attack damage (8 -> 6)
* Unbreaking formula changed from +`50% * level` to +`35% * level + 35% * (level - 1)`
* Changed armor formula
  * Armor reduces damage by a percentage with diminishing return
  * Toughness reduces a flat amount of damage
* Item's stats are now ordered
* Moved IEnchantmentTooltip to InsaneLib
* Fixed Graves keeping vanishing curse items
* Fixed Serializable trades not reading item_a_tag
* Fixed Netherite armor having knockback resistance

## 3.4.2
* Coal veins are now rarer
* Better Loot data pack now disables itself if ITE is present

## 3.4.1
* Added Better Haste / Mining Fatigue
  * Removes the attack speed changes from these effects
* Halved Ancient Debris hardness
* Slowed down spawners
* Fixed chickens feedable with the wrong seeds

## 3.4.0
* Reworked armor and toughness
  * Armor now reduces damage by a percentage
  * Toughness reduces a flat amount of damage
  * Absorption armor has been disabled
  * Golden armor now gives +2 regenerating absorption and +20% regen absorption speed instead of +5 max health
* Wandering Traders can now sell Jungle temple maps
* "Can be repaired with: ..." in an Anvil now shows if a material only partially repairs the item
* Regenerating absorption effect no longer gives regen speed
* Reduced netherite enchantability (13 -> 7)
* Fixed netherite stuff having wrong repair amount for diamonds
* Fixed infinity overhaul broken?

## 3.3.1
* Added No fish if fishing in the same spot
  * If fishing too many times in the same spot you'll start getting nothing
  * Disabled Fishing Guardian
* Nether portals now require Crying Obsidian only instead of Gold blocks as alternative
  * Also made the blocks required configurable with the iguanatweaksreborn:portal_corners block tag

## 3.3.0
* Added Luck enchantment
  * Unifies Looting, Fortune and Luck of the Sea
* Rebalanced Heal over time and instant heal
  * Increased Instant heal
  * Slightly reduced heal over time amount and strength
* Increased Smite bonus damage from 1.25 to 1.5 per level
* Knockback enchantment now scales with weapon damage (like Smite like enchantments)
* If hit, Regenerating Absorption now goes on cooldown for 4 seconds instead of 2
* Players now lose 20% xp on death instead of 15%
* Bows and crossbows now have halved durability
* Broken items can now still attack with 1 damage and reduced knockback if the Knockback feature is enabled
* Leaves are now twice as dull
* Tools and weapons now show attack and mine reach
* Fixed crash with Crossroads mod

## 3.2.1
* Added `iguanatweaks:deathGrave` game rule
  * If enabled the grave will generate on death
  * Removed the config option to disable graves
* Coal veins are much much bigger now
* Fixed crash with Aphotheosis
  * When apotheosis is present, enchantment value of items from Item Stats will not be applied
* World border height is now capped to 128 blocks
* Fixed instant heal foods leaving behind a never ending arrow
* Fixed 'Cap to health' config option not working
* Fixed breakable items showing 0 durability left
* Fixed Regenerating Absorption not decaying when at 0

## 3.2.0
* Added Chicken from egg chance
* Added Player no damage when spamming
* Plants Growth multipliers and Livestock data have been moved to data packs
  * A data pack is included that adds the same multiplier as before
* Regenerating absorption no longer uses vanilla absorption
  * Instead, a new shield like icons will appear above the XP bar
* Lower saturation foods now instantly heal the full amount instead of having an over time amount
* Nether wart no longer grows in the overworld
* Fixed wandering traders crashing when disabled
* Fixed cows milk cooldown not synced to clients
* Reduced priority for EnchantmentHelperMixin. Should fix incompatibility with apotheosis

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