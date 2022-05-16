# Changelog

## Upcoming
* Iron armor Toughness increased (~~2~~ -> 4 total)
* Weighted Equipment (renamed from Weighted Armor) 
  * Shield Slowdown moved from Stats to Weighted Equipment and the slowdown is now configurable
  * Shield slowdown reduced (25% -> 18% slowdown)
  * Reduced armor slowdown 
* Soups are now drinked instead of eaten
* Taking damage from non-entities (e.g. falling, poison) will no longer stop eating/drinking

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
