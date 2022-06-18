# Changelog

## Upcoming
* Reduced slowness per armor and toughness to be in line with the vanilla armors
* Experience 
  * Global experience is now enabled by default with 25% more experience from any source
  * Decreased Block Dropped Experience (+200% -> +120%)
  * Decreased Mobs from Spawners Experience (-33.3% -> -50%)
  * Reduced bonus experience from xp bottles (23 -> 18)
  * Increased Player dropped experience (80% -> 85%)
  * Fixed Global Experience duping player's experience
* Datapack is now included in the mod and can be disabled via command or on world creation screen
  * Crossbows are no longer craftable
  * Shields require leather and more iron to be crafted
  * Dark Prismarine requires less dye to be crafted
  * Clay block can be crafted to 4 balls
  * Leads and Sticky Pistons can be crafted with honey
* Fixed begin able to sleep during the day but waking up instantly if not Tired

## 2.11.5
* Added Creeper Collateral option. Disabled by default. If enabled will make creepers' explosion drop no blocks.
* Tiredness
  * Tiredness is now shown as Zs above the hunger bar. Light blue means not tired enough, Blue means can sleep and red mean Tired effect.  
    Also shown in the debug screen
  * You can now sleep during daytime only if you have the Tired effect and not if you're tired
  * Tired effect now slows player's mining speed by 5% per level
* Updated effects icons
* Food is now eaten slightly faster
* Freeze no longer applies injured
* Effects are no longer applied if duration is 0
* When setting spawn point but can't sleep (not tired or sleeping disabled) the "Respawn point set" message will now show up
* Custom Hardness now actually changes blocks hardness instead of using the BreakSpeed event
* Fixed global and custom hardness being applied in the wrong order

## 2.11.4
* Requires InsaneLib 1.5.1. Fix tags not working for blacklists.

## 2.11.3
* Fixed various features not begin activable/deactivable due to config load
* Explosion Overhaul
  * Shields no longer block damage from explosions by default (still blocking 5 damage from the Shield Feature)

## 2.11.2
* Tiredness
  * Tiredness > 400 now gives Tired effect. Every 20 more tiredness gives one more level.  
    Slows you down by 8% per level and slows down attack speed by 5% per level.  
    From Tired II view distance is decreased down to 8 blocks at Tired V (max level at tiredness >= 500)
  * You can now sleep during daytime if you're tired
  * You can now configure tiredness required to sleep, effect and per effect level
  * Spawn point is now set by default if not tired enough. Can now be configured
* Removed Tiredness from Debuff
* Fixed a bug where changing stack reductions wouldn't work

## Beta 2.11.1
* Added Shields Feature in Combat
  * Shields now only block up to 5 damage. Explosion overhaul shield reduced damage is now 0.6 and no longer 0.5.
  * Shields now always disable with Axes for 1.6 seconds, like Combat Test Snapshots
  * Shields no longer start working after .25 seconds
* Added Tiredness in Sleep & Respawn
  * Player can only sleep when he's tired enough.  
    If too tired a blindness effect gets applied now and then.  
    Tiredness is gained at the same rate as Exhaustion.  
    Sleeping resets tiredness
* Debuffs
  * If tiredness > 400 player now gets slowness effect
* Explosion Overhaul
  * Fixed a huge bug where when blocking with a shield the explosion damage would bypass armor
  * Added more knockup speed on explosions
* Increased Stone Pickaxe durability (~~7~~ -> 8)
* Unusable hoes no longer instantly destroy
* Slightly increased wooden tools efficiency
* Tagging slowness level is now I instead of II, but it's applied for 1 more tick per point of damage
* Fixed higher levels of well fed not behaving properly (well fed 4 was insta-heal)

## Alpha 2.11.0
* Requires InsaneLib 1.5.0
* Added Fog and Light Feature in a brand new Client Module
  * Fog feature increases visibility in the Nether and also in lava when the player has Fire Resistance
  * Light feature disables the Night vision blinking effect. The Night vision will just fade out on the last 4 seconds of the effect
* Fixed Mining Misc not reloading config properly

## 2.10.2
* Halved injured duration
* Well fed duration no longer stacks when eating multiple foods
* Fixed depth hardness default making blocks around Y 16 harder than blocks below Y 0

## 2.10.1
* Added Effective Hunger
  * Amplifies hunger consumption when under the Hunger effect
* Reduced Depth Hardness
* Sped up stone tools

## 2.10.0
* Misc
  * Added Beacon Feature. Beacon range is now calculated based off the blocks that make the pyramid. Can reach much higher ranges. Full iron block pyramid gives about the same range as vanilla.
    * Full Iron pyramid: 51 range
    * Full Emerald pyramid: 59.2 range
    * Full Gold Pyramid: 83.8 range
    * Full Diamond Pyramid: 136 range
    * Full Netherite Pyramid: 178 range
    * Full Cobalt (TiCon 3) Pyramid: 102.4 range
    * Full Queen's Slime (TiCon 3) Pyramid: 136 range
    * Full Hepatizon (TiCon 3) Pyramid: 123.4 range
    * Full Manyullyn (TiCon 3) Pyramid: 148.6 range
* Mining
  * Renamed Insta-Mine Silverfish to Misc
    * Heads and skulls can now be insta-mined too
* Hunger & Health
  * Health Regen
    * Well-fed is now applied for x seconds equal to the total hunger and saturation restored. Always applied at level I, no longer based off hunger restored.
    * Well-fed now increases regen speed by 25% (was a log10 calc), Injured reduces regen speed by 20% (UNCHANGED)
    * Removed Iguana Tweaks preset, the combat test is pretty balanced. Well Fed and Injured can be enabled/disabled with Combat Snapshot too.
* Reduced backward slowdown (~~25%~~ -> 20%) and fixed slowdown applying when flying

## 2.9.8
* Reduced Depth Hardness (~~+0.03x~~ -> +0.025x per block below sea level)
* Reduced Deepslate ores custom hardness (about 12% less harder)
* Reduced Shield Slowdown (18% -> 15%)
* Fixed all foods stacking up to 16 if food stack reduction was disabled

## 2.9.7
* Iron armor Toughness increased (~~2~~ -> 4 total)
* Weighted Equipment (renamed from Weighted Armor) 
  * Shield Slowdown moved from Stats to Weighted Equipment and the slowdown is now configurable
  * Shield slowdown reduced (25% -> 18% slowdown)
  * Reduced armor slowdown 
* Stack Sizes
  * General Stacking (renamed from Stack Reduction)
    * Items and Blocks less stacking is now disabled by default
    * Stews now stack up to 16 if Food Stack Reduction is disabled, configurable
  * Custom Stacking
    * Potions, Minecarts (and variants) now stack up to 16
    * Snowballs now stack up to 64
    * Saddles now stack up to 8
* Sleeping Slowness is now I instead of II
* Soups are now drinked instead of eaten
* Taking damage from non-entities (e.g. falling, poison) will no longer stop eating/drinking
* Combat test is now the default Health Regeneration preset

## 2.9.6
* Food Consuming
  * Added Stop consuming on hit. When the player is hit eating/drinking will reset
* Misc
  * Explosion Overhaul
    * Wither and Dragon are no longer knockbacked so much by explosions. Added a blacklist that takes reduced knockback from explosions
    * Added entity blacklist. Entities in this list will not use the mod's explosion
    * Reduced poof particles and poof particles no longer spawn when explosion is weak

## 2.9.5
* Added a priority to Hardness (should fix a problem with Tinkers Construct where you could mine blocks even if your mining speed was 0)
* Fixed Forge not begin able to show the "missing dependency" screen due to Protection mixin (Thanks Mrbysco)

## 2.9.4
* Ported to 1.18.2

## 2.9.3
* Farming
  * Nerfed Bonemeal
    * Fixed Bonemeal not working on dry crops when crops growth feature was disabled 
* Misc
  * ITExplosions can now deal no knockback
* Mining
  * Obsidian now matches Bedrock Edition Hardness
  * Fixed a bug where Custom Hardness would still check for Global Hardness
  * Fixed a bug where instabreak would fail on Custom Hardness when breaking the block with efficient tool
* Data Pack
  * Stone now has 10% chance (+1.67% per level of looting) to drop iron nugget
  * Basalt now has 10% chance (+1.67% per level of looting) to drop gold nugget
  * Cake now drops if not eaten when broken
  * Silverfishes now drop 0-2 iron nuggets
  * Crossbows can no longer be crafted, must be obtained from pillagers / Outposts
  * Rails are simpler and slightly less expensive to craft
  * Beetroot soups now require only 3 beetroots to be crafted
  * Sandstone and Red Sandstone recipes now output 2 instead of 1
  * Dark Prismarine is now crafted with Prismarine instead of shards
  * Clay can be broken down to balls in the crafting table
  * Sticky Pistons and Leads can now be crafted with Honey

## 2.9.2
* Experience
  * Added Other Experience Feature. Lets you change other experience sources
    * Bottle o' enchanting will now give on average 30 xp instead of 7
  * Players now pickup experience faster
* Movement
  * Added Terrain Slowdown
    * Based on the terrain you're walking on, you'll get slowed down a bit
  * Added Backwards Slowdown
    * Walking backwards will slow down the player by 25%
* Added Passive exhaustion, each second players will get 0.005 exhaustion
* Added Nerf feature
  * Sheep no longer drop wool when killed
  * Boats no longer go stupidly fast on ice
  * Iron from golems only when killed by players has been moved here from Villager Nerf feature
* Reduced Well Fed duration
* Tagging has now config options. Also increased slowdown duration (0.25 -> 0.30 seconds for each half heart of damage taken)
* Data Pack
  * Shield recipe has changed to require Logs and Leather
  * Chain armor recipe is now unlocked when the player obtains chains
* Removed some leftover logging

## Beta 2.9.1
* Misc
  * Added Villager Nerf Feature
    * Lock Trades: As soon as a Villager gets a profession, his trades will get locked and will not change by destroying/placing the Workstation
    * Always Convert Zombie: Villagers will always convert to zombie villager at any difficulty
    * Max Discount Percentage: A percentage between 0% and 100% setting the max discount a trade can get
    * Iron from Golems only when killed by Player
    * Clamp Negative Demand: in short, the demand will not go below a certain value to prevent an infinite amount of trades where the price will never increase
* Added back Stack Size Module
  * Items now stack more (32 -> 48)
  * Blocks now stack more (16~32 -> 32~48 for most blocks)
* Global Hardness
  * Changed Depth Hardness (0.025 -> 0.03 multiplier increase per block below sea level down to 0 -> 5. [NEW] Multiplier reduced by 1.24 below Y = 5 (Deepslate will no longer be that hard to break))
    * Basically at Y = 5 multiplier will be 4.24 (global hardness added too) and from Y = 4 and below multiplier will be 3 (since deepslate is harder)
* Food Hunger
  * Rotten flesh now heals only 2 hunger instead of 4

## Alpha 2.9.0 
* Port to 1.18, requires InsaneLib 1.4.3.  
  [Missing Features](https://github.com/Insane96/IguanaTweaksReborn/issues/204): Whole Stack Size module, Wrong Tool Feature, Weather Slowdown feature, previously missing features from 1.12.
* Added a DataPack to get more features
  * Raw Iron smelts to nuggets, so you must find more Iron Ore to get your first Iron Tools (Iron Ore still smelts to 1 ingot, this makes Silk touch the enchantment for Iron)
  * Chain recipe requires 3 nuggets instead of 2 nuggets and 1 ingot
  * Chainmail armor can be crafted with Chains and Leather
* Combat
  * Stats
    * Item stats can now be manually set via config (E.g. 'Armor Adjustments' is no longer a thing, instead armor modifiers are defined in 'Item Modifiers')
* Misc
  * Tool Nerf
    * Added nerf to 'Tools Efficiency'. Reduces Wooden and Stone tools efficiency by 25% and Iron and Diamond tools by 10%
    * Decreased Elytra durability (432 (vanilla) -> 144)
    * Decreased Wooden Axe durability (10 -> 8)
    * Decreased Stone Axe and Shovel Durability (50 -> 48)
    * Increased Stone Pickaxe Durability (6 -> 7)
    * Iron Tools and Sword have increased durability (250 -> 375)
  * Explosion Overhaul
    * Lowered particles from Enable Poof Particles
* Mining
  * Added Insta-Mine Silverfish
    * Makes Infested blocks insta-mine like pre-1.17
  * Global Hardness
    * Reduced Global Hardness Multiplier (4x in every dimension -> 2.5x in the Overworld and 4x in other dimensions)
    * Changed Depth hardness in the Overworld (0.04x per block below sea level down to Y=12 -> 0.02x per block below sea level down to Y=0)
* Hunger & Health
  * Food Consuming
    * Sped up eating speed (Reduced eating time multiplier (0.15 -> 0.13))
  * Health Regen
    * Fixed a bug where with the Combat Update preset would consume Saturation to regenerate health.
    * Added a Max Exhaustion config option
* Movement
  * Tagging
    * Increased slowdown duration when hit (0.15 -> 0.25 seconds for each half heart of damage taken)
* Experience
  * Global Experience
    * Fixed experience multiplier applying every time a xp orb is loaded
