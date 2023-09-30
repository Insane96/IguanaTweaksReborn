# Changelog

## 0.15.1
* Solarium and Keego hoe now have cooldown reduction based off their base ability
* Fixed mod's armors not being trimmable
* Sorted some stuff in the creative tabs
* Adjusted stamina visibility
* Removed Flint blocks
* Electrocution damage is now fixed to 4
* Advancements cleanup
* Decreased back experience to level up

## 0.15.0
* Added Better Beacon
  * A new beacon with more effects, more amplifiers but with a rechargeable time cost
* Added unbreakable items
  * Metal gear (made in a Forge) will no longer break, instead they stay at one durability.  
  * This also applies to any enchanted item
* Added Name Tags feature
  * Retrieve name tags from name tagged entities, from changing their name to their demise
* Seasons 
  * Tall grass now decays in winter and grows back in spring. Saplings also can become dead bushes in Winter
* Added Swift Strike
  * A new "damaging" enchantment that increases attack speed
* Added keego
  * A new gem found stuck in Nether's bedrock. A blast might extract it.
  * Gear helps with momentum
* Soul Steel gear now require an upgrade template like Netherite
  * The upgrade template can be found in fortresses
* Removed Ensorceller and added back normal enchanting table recipe
* Piercing damage now bypasses absorption too
* Rails and Minecarts
  * Rails and powered rails are now slightly cheaper (about 33% cheaper)
  * Powered rails now have different accelerations
  * Also fixed normals rails being too slow for powered rails
* Stamina is now unlocked at 75% max stamina instead of full
* Solarium
  * Efficiency, damage and blocked damage have been reduced but the effect has been doubled (so it's better under the sun but not without it)
  * Bonus now counts 12+ light level as max bonus speed
  * Armor now gives less armor/regenerating absorption and less movement speed
  * Fixed solarium items resetting breaking blocks when healed
  * Lowered solium moss boulders generation and moss generated on boulders
* Stats
  * Axes have reduced attack speed
  * Gold Tools and Sword now have +1.5 damage (similar to flint tools)
  * Chainmail armor now gives toughness (or regenerating absorption speed) but has reduced durability
  * Leather armor has increased durability
* Copper tools now last ~~3%~~ -> 4% more per block below sea level (~~+200%~~ -> +350% at y=0 and ~~+350%~~ -> +600% at y=-56)
* Smartness can now be applied to tools too
* Vigour is now the exact opposite of Tired and increases attack speed, movement speed and mining speed
* Slightly adjusted food eating speed
* Changed one stone layer from the altimeter to water
* Fix missing recipe for bundle with rabbit hide
* Coated copper can no longer be repaired with copper
* Spawners now always drop 40 xp
* Further reduced durium generation
* Increased experience required to level up (~~35~~ -> 40)
* Coal veins are now much rarer but much bigger
* Removed increased experience from ores
* Fixed music not playing more often
* Melee protection now increases attack speed by 2% per level
* Tridents can now be repaired with Prismarine Crystals
* Removed sharpness
* Forge Hammers can no longer be used as weapons
* If you have a tool in your hand while in creative you now break blocks faster

## 0.14.9
* Weather fog no longer shows up if the player is 16 below the surface (was fixed below Y 32)
* Toughness to regeneration speed changed (+0.05 -> +20% regen speed per toughness)
* Witches now drop potions
* Increased forging xp 
* Added `/sr` command 
* Synced terrain and armor slowdown to clients
* Timber trees no longer get multiple types of leaves

## 0.14.8
* Heavily increased cleansed lapis drop and fixed dropping with silk touch
* Fixed items stack desync client side
* Fixed Expanded mining non-stone blocks

## 0.14.7
* Wandering Traders now sell explorer maps (ocean, woodland, desert and cold plains)
* Rebalanced Blasting. The bonus mining speed is now based off tool's mining speed
* Backported 1.20.2 diamond generation
* Increased meat drop from animals/fishes
* Golden shield now blocks more damage (1 -> 3)
* Copper tools now last 3% more per block below sea level (+~200% at y=0 and +~350% at y=-56)
* Desert Wells are now 10x more common
* Seeds now always drop if aren't broken by an entity

## 0.14.6
* EXPERIMENTAL Absorption armor
  * Armor no longer gives protection, instead gives regenerating absorption hearts
  * Toughness, instead, increases the regen speed of the absorption hearts
* Added a `remove_origial_modifiers` item tag
  * Any item in this tag will have it's original modifiers removed before applying custom ones
* Energy Boost items effect duration now stacks
* Added a new `effect` object in `food_properties.json` to override the vanilla effects given by food
* You now start starving at 4 hunger instead of 3 (halved speed at 4)
* Timber trees 
  * Falling trees now try to stack on the sides instead of breaking
  * Will no longer work if too many sideways logs are found

## 0.14.5
* Added Snowballs
  * Snowballs now deal 0.5 damage
  * Also freeze entities for 1 second (stacking)
* Added Regenerating absorption
  * A new attribute that gives some regenerating absorption hearts
  * Absorption now only absorbs entity damage (e.g. no longer fall or poison)
  * Gold armor and shield now give +2 regenerating absorption hearts. This now also works on mobs.
  * Golden armor no longer gives +6% movement speed and golden shields no longer blocks 100 damage.
* Food can now take 1.2 seconds to eat if low saturation and hunger enough (was 1.6) (fast food like kelp still doesn't go below 0.8 secs)
* Solarium shield now blocks +2 more damage when in sunlight
* Ensorceller now gets power from nearby sunflowers, making lower level rolls available earlier and later in the day
* Doubled the bell range to glow raiders
* Reduced arrows from bows base damage
* Lowered hidden Durium generation but increased xp drop from 1~4 to 2~4
* Reduced bonus durability when merging two items in an Anvil (20% -> 15%)
* Fixed MC-152728
* Fixed ensorceller crash

## 0.14.4
* Vigour
  * Eating food that restores at least 7 saturation now gives Vigour
  * Vigour now increases health regen
  * Vigour is no longer given on wake up
* Bone meal now has only 25% chance to grow glow berries and 80% chance to grow saplings
* Increased Regeneration duration on wake up (15 -> 45 seconds) 
* Reduced Tiredness overlay being too strong at level II
* Removed Injured

## 0.14.3
* Added Healthy enchantment
  * Increases max health by half a heart per level
  * _Max Health Fix_ is a new dependency that fixes MC-17876
  * Incompatible with Protections and thorns
* Solarium mega buff
  * Armor now gives up to 10% movement speed when walking in sunlight
  * Tools now mine up to 75% faster when used in sunlight
  * Weapons damage is increased up to 25% when used in sunlight
  * Increased repair chance (~~75%~~ -> 150% at full sunlight (so 100% at 10 sunlight))
  * Nighttime and thunder weather now disable the solarium boosts
  * Reduced tools durability
  * Solarium ball requires 9 moss again
  * Solarium now repairs even if in a Tool Belt
* Ensorceller
  * No longer requires bookshelves, instead requires sunlight
  * Higher cost now multiplies the roll (so if you spent 3 levels to rolls, instead of getting 2 to 5, you'll get 6 to 15)
  * Item now renders on the ensorceller
* Increased experience from smelting Durium and Soul Steel
* Golden tools/weapons now have innate Fortune/Looting I instead of II but tools can now mine stuff like an Iron Pickaxe
* Increased bonus speed from Golden Armor (+5% -> +6%)
* Crops are now 50% harder to break
* Altimeter now shows a tooltip with the rough height
* Spawners now drop more experience if not disabled
* Pillagers outposts no longer generate (also disabled Bad Omen)
* Zombie Villagers no longer spawn
* Increased experience from smelting/blasting iron (0.7 -> 1), gold (1 -> 2), ancient debris (2 -> 4), durium (2 -> 5), soul steel (5 -> 10)
* Re-added "Actual redstone component"
* Fixed Forge's items on top being rotated wrongly
* Sculk Catalyst now give MUCH more experience when broken
* Decreased entity reach of hammers by 0.25
* Fixed Forge and Ensorceller not syncing items on world load
* Hopefully fixed client-side crash when interacting with Wandering Trader on a server
* Fixed hunger depletion not working properly

## 0.14.2
* Cost for repairing a tool now scales with the percentage durability repaired
  * E.g. repairing a tool for half it's durability will cost only half levels
  * Fixed the anvil running vanilla behaviour when leaving the mixin
* Ensorceller
  * Items enchanted in an ensorceller can no longer be merged
  * Increased max pool from 12 to 15 (16 to 20 for full pool)
  * Books can no longer be enchanted in an ensorceller
* Leveling up now always requires 35 xp instead of the previous formula `(2 * current_level) + 5`
* Poison damaging speed has been increased (and made configurable) (1 damage every ~~4~~ -> 3 seconds)
* Hard Mode
  * Mobs now drop double loot
  * Mobs now drop 100% more experience instead of 75% in hard mode
* Players now starve at 3 hunger (instead of 0) every 16 seconds, the speed is doubled every hunger lower than 3
* Forging now consumes hunger
* Block hardness multiplier in the nether increased (2x -> 3x)
* Golden tools/weapons now have Fortune/Looting II instead of I
* Some new durium textures, thanks to e___kho
* Removed "Actual redstone component".
* Fixed creeper explosions dropping almost no blocks

## 0.14.1
* Setting your spawn point on a respawn Obelisk now saves the previous one
  * So if you set the respawn on a bed and the obelisk discharges, you'll respawn at the bed
* Reduced depth hardness
* Leather armor durability has been increased back to vanilla one
* Fixed Grave not spawning when dying in the void
* Fixed crash when an animal died on fire

## 0.14.0
* Anvils now have only 7.5% chance to wear down when used
* Coated Copper Tools finished
  * Shield parrying now can electrocute enemies
  * Textures
* Copper tools
  * Are now slightly slower BUT mine faster the deeper you mine
* Solarium
  * Solium moss now generates on boulders around the world instead of on the ground
  * Solium moss no longer grows naturally, only with bone meal
  * Solarium ball can now be crafted with 4 moss instead of 9
  * Fixed solium moss not dropping as many items as the sides covered
* Slight rework to Tiredness
  * As long as you're tired, the effect is applied, there's no longer a period between "You're tired and can sleep" and the effect
  * Effect is now applied indefinitely, and removed on wake up
  * Re-added Tiredness overlay (no longer shows up at Tired I)
* Added a json config to allow changing block xp drops
  * With this, Iron, Coal and Gold ores now drop XP
  * Other ores drop more xp
* Added back "In terrain slowdown"
  * Snow and powder snow slow down
* Added Melee Protection enchantment
* World border
  * Is no longer rendered way up in the air and down to the bottom of the world 
  * Is now 60% transparent
* Increased flint base damage by 0.5
* Ensorceller 
  * Cost to roll is now 3 base (unchanged) and decreases by one every ~~3~~ -> 4 bookshelves around
  * Rolling at higher costs rolls higher die
  * Cost to roll can now be configured
  * Now keeps levels rolled when broken
* Mushrooms are now bone meal-able only when on mycelium
* Arrows can now be made from cloth in a fletching table
* Disabled Well Fed and Injured
* Slight increase to Unbreaking formula (x1.29, x1.67, x2.11, x2.5 -> x1.33, x1.82, x2.5, x3.33 durability at levels I to IV)
* Slightly decreased Durium Ore XP drop
* Small iron veins now generate smaller but are no longer discarded on air
* Graves now break blocks they are placed in
* Blasting is now affected by Water, midair, haste and mining fatigue
* Pillagers now drop emeralds and arrows
* Phantoms now actually spawn in The End
* Fixed data packs not disabling if they were already enabled in a world

## 0.13.0
* Update to MC 1.20.1
* Added Coated Copper Tools
  * Can electrocute enemies
* Added Enchanting Feature
  * Added Ensorceller
    * A low level enchanting table for early game, made with stone, solarium and iron
  * Enchanting Table now requires Nether access to be made
  * Added compatibility with Enchanting Infuser
* Renamed `Misc.Misc` feature to `Misc.Tweaks`
  * Sponges now soak more blocks
* Client > Sounds renamed to Sounds & Music
  * Music now plays thrice as often
* Nerfs
  * Added "Remove Falling Block Dupe across dimensions"
  * Added "Remove Piston exploits" (removes TNT duping)
  * When you fish, there's a small chance to fish a Guardian
* Anvils
  * Items repair now take the same amount of materials used to craft them (e.g. an Iron pickaxe only costs 3 iron ingots to be repaired)
    * This is done via data packs within a new Data Pack folder "anvil_repairs"
  * Merging enchanted tools cost is now calculated from the result, and not the base items
  * Merging items in an anvil now gives 20% bonus durability (instead of 12% vanilla)
  * Fixed fixing anvils opening the interface
* Added "Phantoms in the End"
  * Makes Phantoms spawn in The End instead of the Overworld (doesn't work as latest Forge, use NeoForge if possible)
* Cloth: Zombies no longer drop rotten flesh and reduced cloth drop
* Mobs not killed by players will now drop nothing 75% of the time
* Shields can now be recycled in blast and soul furnaces
* Ground flint is now actual ground flint
* Fixed some missing blasting recipes and doubled (and halved) the output of rich and poor ores
* Solarium now slowly repairs overtime when exposed to skylight
* You can no longer forge if you r-click on the sides of forge
* Blocks no longer take 50% more time to break, but are still harder to break the deeper you mine
  * Nether multiplier reduced to 2 from 4
* Players' swing animation is now slower when tired
* Unbreaking formula lowered again
  * A few adjustments to tool durability have been made
* Reduced armor slowdown
* Wandering Trader
  * 2 buying trades are now guaranteed
  * Now buys the same stuff as snapshot 23w31a
  * No longer sells an enchanted book with unbreaking (replaced with a low level enchanted book)
  * Now sells cleansed/ancient lapis as rare trade
  * Trades have been rebalanced as snapshot 23w31a
* Block stack reduction can no longer be reduced by material (as materials no longer exist)
* Hunger Depleted on Wake Up now properly consumes Saturation first and then hunger 
* Powder Snow is now harder to break 
* Removed bonus health from mobs when not in hard mode
* Solium moss now generates more per patch
* Tool belts are now added to graves instead of dropping
* Stairs are now mined faster
* Miner zombies now spawn more frequently in caves
* You can no longer wood cut pressure plates and buttons
* Fixed crash when placing Iron Blocks
* Fixed Soul Steel being recyclable for more nuggets than used
* Fixed glass panes not begin breakable faster by pickaxe
* Fixed multi-block furnaces not ejecting items to hoppers below
* Fixed durium lodestone recipe
* Fixed night vision not fading out properly
* Fixed animals not dropping cooked food

## 0.12.4
* Small Thorns overhaul
  * Incompatible with Protections, it now triggers each time instead of a random chance and deals damage based off 
  * Still doesn't damage armor when triggered
* Made repairing smithen items even better
  * Items are now repaired for 50% durability when using the material of the tool (instead of 25%)
  * Items can now be partially repaired up to 50% instead of 75%
* Experience obtained is now increased by 25%
* Terrain slowdown is now enabled again with ice slowing down a lot
* Players are now 5% slower
* Chained copper armor now requires nuggets instead of chains
* Fixed combat test snapshot health regen changes not applying until config reload
* If Corail woodcutter is installed a new advancement will pop out
* Removed random power (0.5) bonus damage
* Poisonous raw food is now part of Food & Drinks instead of No Hunger

## 0.12.3
* Re-Enabled hunger
  * Slower poison has been moved to Misc.Misc
  * No Hunger is now disabled by default
  * Health regen is no longer disabled by default
* Huge changes to Unbreaking Overhaul
  * Item Fragments no longer exist
  * Unbreaking is now back to max lvl III
  * Formula has changed, from 50%/66.7%/75%/80%/... to 25%/45%/60%/70%/... (at levels I/II/III/IV)
* Added Critical enchantment
  * Increases critical damage, incompatible with other damage enchantments
* Experience to level up has been drastically reduced (from `3 * next level` to `2 * next level + 5`)
  * Removed bonus experience from blocks, entities and any source. Bonus experience from XP Bottles is still active
* Critical and Water Coolant enchantments now give off magic crit particles
* Items in forge are now shown on the forge
* You can now repair anvils by right-clicking them with an iron block
* Levels required to repair an item are lowered by 30%
* Forging now resets the durability of the tool
* 50% of mobs has now xray (100% in hard mode), and instead of 16 (in normal) range, it's between 8 and 16 (in normal)
* Increased chance for creepers to be able to breach (7.5% -> 40%)
* Easy Magic is now a recommended mod
  * Lets you re-roll enchantments, keeps items in enchanting table and ignores no collision blocks between enchanting table and bookshelves

## 0.12.2
* Disabled Terrain slowdown. Would no longer work in 1.20
  * With this, armor slowdown has been increased
* Copper tools no longer require a Forge to be made
  * Lowered efficiency and durability
* Added Combat Test Strength
  * ~~+3 attack damage~~ per level -> +20% attack damage per level
* Rebalanced Tier 3 and 4 materials
  * (Armor) Diamond is now the only tier 3 material and has lower protection than vanilla. Can be upgraded to Soul Steel, Netherite or Keego, which are now alternative tier 4 materials
  * (Tools and Weapons) Diamond is now the only tier 3 material and has average stats, same as Armor for Tier 4 changes
* Increased mobs follow range
* Soul Steel now requires Durium to be alloyed instead of iron 
* Removed Stats > "Adjust weapons". It's now done with item_modifiers.json

## 0.12.1
* Added Hard mode
  * When the dragon is killed, mobs will gain 2x health, speed, etc. and 20% more attack damage. They will also drop 75% more xp
  * With that, the base buffs have been reduced
  * Mobs bonus speed is now the same from the start and no longer increases over 60 minutes
* Added Bonking Shovels
  * Shovels deal bonus knockback
* Increased the percentage of creeper that can walk fuse (~~10%~~ -> 75%)
* Reduced Durium Base AD (2.5 -> 2)
* Reduced late game hammers damage
* Axe damage reduced by 1
* Fixed Cyan Flower dropping nothing
* Fixed copper tools advancement
* Fixed hammers taking no damage when used as weapon

## 0.12.0
* Added support for Tool Belt mod
  * Adds advancements
* Passable Foliage is no longer a hard dependency
* Added Solarium Equipment
  * Solium moss can be found in hot biomes lying on the ground, 9 solium moss make a Solarium Ball, which can upgrade iron equipment in a smithing table
  * Solarium equipment takes 1/4 damage when used in the sun (reduced by nighttime, rain and thunder)
  * Grows (better) in full sunlight
* Added Fletching Table
  * Craft more arrows
  * 4 new types of arrows
  * Torch arrows place a torch on the point they land on or set on fire entities hit (stacking)
* Added Cloth
  * Drops from zombies
  * Required to make a bed
  * Can be used to make Chainmail armor and bundles
* Renamed Ancient Lapis feature to Lapis
  * Moved Cleansed Lapis here and changed what it does: now increases the level of an enchantment by 1, up to max level
  * Ancient lapis can now only increase enchantments level above the max (no longer increases any enchantment by 1)
* Added mining charge
  * Breaks blocks in a 3x3x5 area
* Added Cyan Flower
  * Generates in snowy biomes
* Added Gravity Defying enchantment
* Added Elytra Nerf Feature
  * When using an elytra in non-End dimensions, the player falls faster
* More Anvils overhaul
  * Added "No repair cost increase and repair cost based off Enchantments"
    * Makes XP cost for repairing in an anvil no longer increase and based off enchantments
  * Lowered enchantments' rarity costs (and made them configurable)
  * Items upgraded in Smithing Table can now be partially repaired with base material (e.g. Netherite tools can be repaired with Diamonds)
  * Moved "free rename" and "repair cap" to this
* Smelting and Blasting
  * Ore blocks blasted in a blast furnace now have 40% chance to double the output and will always give two ingots in a soul furnace
  * Copper now smelts / blasts twice as faster
  * Gold can no longer be smelted in a Furnace
* Added "Reduced mob cramming"
  * Entity cramming is set to 6
* Added The End feature
  * Enables a data pack that increases end cities generation
* Better End City loot
* Forge recipes now award experience
* On Death experience is now dropped like normal, instead of spawning a ghost on grave break
* Reduced explosions damage (configurable)
* Cleansed lapis now has greatly increased chance to drop
* Buffed Feather Falling
* Horses have now buffed speed
* Expanded now works properly when looking down, and works as vein mining when on an Axe/Hoe. Also Expanded III now mines more blocks instead of being the same as lvl II
* New worlds now start in early summer instead of mid-summer
* Lodestones are now cheaper to craft
* Tentative shields durability balance changes
* Increased Flint, Stone and Copper reinforced flint tools' durability
* Slightly increased Stone tools efficiency
* Logs now drop logs instead of planks
* Health regeneration is now faster with better foods
* Firestarter now properly lit up TNT and Mining charges
* Cutting pumpkins with shears now gives edible Pumpkin Pulp, instead of seeds
* Pumpkins and Melons crafting now only give one seed, and melon seeds require a melon and no longer a slice
* Infinity is now more common at lower levels
* Thorns Enchantment no longer damages items
* Firestarter now takes 3 seconds instead of 4 to light up fire
* Falling trees now fall better
* Golden armor now give speed
* Golden shield now protects 100 damage
* Increased random tick speed to 2 from 1
  * Doubles the speed of plants, crops and much more stuff growing
* Reduced poor ore generation
* Heavily reduced armor slowdown
* Increased Chainmail armor durability
* Netherite is now upgraded from Soul Steel
* Levers and Glass can now be mined efficiently with Pickaxes
* Creeper Cena explosion is now stronger (breaks more blocks, deals the same damage as a normal creeper)
* Rebalanced Forge Hammers and Hoes durability multipliers
* Disabled creeper collateral
* You can now bone meal dirt adjacent to grass to grow grass
* Sped up hammers forging speed
* Increased Spiders and Skeletons loot
* Coal ore is no longer discarded when exposed to air
* Spawner's mobs now drop 1x the experience normal mobs drop (was 0.5x but due to a bug was actually 1x)
* Non-spawners mobs now drop 50% more experience
* Soul Furnaces now consume twice the fuel but smelt and recycle twice as fast
* Minor balance fixes

## 0.11.1
* Fishing is now slowed down based off season
* Hammers are now usable as weapons
* Overall reduced block hardness
* Efficiency now scales exponentially again (still different from vanilla)
  * Formula changed from +`(75 * lvl)`% efficiency to +`(15 * (lvl * lvl + 1))`% efficiency
* Global experience multiplier increased (1.3x -> 1.5x)
* Block experience multiplier reduced (2.25x -> 2x)
* Overall reduced forge smashes required
* Portal now requires either Gold Blocks or Crying obsidian
* Falling on leaves now cancels fall damage only from 8 blocks and has only 20% damage reduction instead of 50%
* Heavily nerfed drowned trident's and slightly nerfed skeleton's damages

## 0.11.0
* Added Forge
  * Replaces anvil recipes
* Added Better ladders
  * Descend faster and crouch when inventory is open
* Falling onto a climbable no longer cancels fall damage
* Each cake bite now restores 40% of missing health
* Multi block furnaces now pick up items from the inside of the structure everytime an item has finished smelting
* Unbreaking now requires fewer levels to pop out
* Increased beeg veins size
* Increased Trident durability
* Increased global experience

## 0.10.2
* Disabled Golden Absorption. Gold armor and shield now grant +2 max health
* Invincibility frames are now synced when hit by arrows
* Glass can now break if entities fall on it
* Re-enabled backwards slowdown
* Reduced experience dropped by players by 5%

## 0.10.1
* Mithril is now Durium
  * Can be made in a Blast Furnace by alloying Durium Scrap Block, Clay and Sand
* Blast furnaces now require a multi block structure
  * Can recycle gear (75% return)
  * Can alloy stuff
* Added Soul Blast Furnaces. An upgraded version of a Blast Furnace
  * Alloys stuff faster
  * Has 40% chance to output one more ingot when smelting and 20% when alloying ingots
  * Recycling yields 100% of materials used
  * Requires lava buckets
  * Has more slots
* Netherite can now be alloyed in blast furnaces from less material compared to crafting
* Flint rocks now generate in the world that drop Flint
* Shallow water is no longer safe to fall into
* Firestarters now generate in chests instead of Flint and Steel in the overworld
* Armor is now rendered in place of Hunger bar. That side felt so empty
* Graves can now be smelted/blasted into iron ingots

## 0.10.0
* Overhauled rails
  * Added Copper Rails and Golden Rails
* Added M.A. Jump enchantment
* Critical Hits now deal 25% more damage instead of 50%
* Fixed timber getting more trees than expected
* Fixed spiders attack reach and skeletons riding them
* Creeper cena now deals more knockback
* Started working on better Blast Furnace

## 0.9.2
* Added Timber Trees feature
  * Makes trees fall when cut
* Added Smartness enchantment
  * Increases experience dropped by mobs by 50% per level. Incompatible with Looting
* Added durability to gear and increased durability of some materials
* Tiredness is no longer reset on death
* Fixes and advancements

## 0.9.1
* Added over-easy egg
* Rotten Flesh can now be composted
* Charcoal can now be placed as layer and layers now fall on top of each-other properly
* Ghasts now drop 5x more gunpowder
* Fixes and improvements

## 0.9.0
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

## 0.8.1
* Respawn obelisk now requires only at least one catalyst block to activate, and will only try to break one of the catalysts instead of each one of them. Tweaked some chances.
* Slabs (not double) now take half the time to break
* Items from graves now despawn faster
* playerSleepingPercentage gamerule is now set to 1 to let tired player rest
* Fixes

## 0.8.0
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
  * Use bone meal on farmland or crouching on crops to transform it into rich farmland. This farmland ticks 3 times faster than normal farmland
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

## 0.7.1
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

## 0.7.0
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

## 0.6.1
* Some stuff is now synced client-side
* Charcoal can now only be obtained from burning Logs. Also increased chance (40% -> 60%)
  * Also fixed charcoal not dropping from logs
* Increased Gold Armor Durability

## 0.6.0
* Added Beeg Ore Veins
  * Veins that generate underground with lots of ores. On the surface, rocks can be found that mark the veins below
* Fog now changes when it's raining
  * Colder seasons = more fog
* Unbreaking Overhaul
  * When a tool with unbreaking breaks, an Item fragment pops out with the same enchantments of the item that broke. Can be applied to a new item in an anvil to transfer the enchantments
* Added Golden Absorption
  * Wearing Gold Armor or shield gives one regenerating heart of absorption per piece (capped to the player health)
* Starting season is now Mid-Summer
* Breaking a tree without an axe now drops planks
* Buckets now require the Nether to be made
* Sheep now have a low chance to drop wool on death
* Squids and fishes now come in various sizes
* Fishes no longer spawn in winter
* Removed Gold Nuggets drop from Zombified Piglins
* Miner Zombies now have a higher chance to spawn in Hard

## 0.5.0
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

## 0.4.1
* Renamed Iridium to Mithril
* Wild crops now generate more commonly
* Fixed crash in creative

## 0.4.0
* Added Crate
  * An early-mid game shulker to transport items around. Having more than one in the inventory will slow the player down
* Added Wild Crops
  * Crops that generate in the wild and drop seeds
  * Enlarging farms requires exploring / trading
  * Potatoes and carrots can no longer be planted, instead you must use the seeds counterpart
  * Removed wheat seeds from tilling grass
  * Wheat seeds can now be obtained from Wandering Traders
* Mod now runs on servers

## 0.3.0
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

## 0.2.2
* Successfully(?) updated to MC 1.19.4

## 0.2.1
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

## 0.2.0
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
* Added a global switch to disable all data packs
* Food now takes 2x to cook in a furnace
* Mithril Ore's drops and ores per chunk increased but decreased ores per vein
* Increased iron nuggets drop with fortune
* Stamina now regenerates faster when locked
* Tridents now have +0.5 attack range
* Removed Iron and Mithril Tools bonus durability
* Torch recipe now crafts 3
* Reduced Tools efficiency and reduced global block hardness, but re-enabled depth hardness

## 0.1.0
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

## 0.0.2
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

## 0.0.1
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

## 0.0.0
* First Alpha