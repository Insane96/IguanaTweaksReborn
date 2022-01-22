# Changelog

## Upcoming 
(Lines with [1.16] at the end will be backported to 1.16)
* Port to 1.18, requires InsaneLib 1.4.3
* Added a DataPack to get more features
  * Raw Iron smelts to nuggets, so you must find more Iron Ore to get your first Iron Tools (Iron Ore still smelts to 1 ingot, this makes Silk touch the enchantment for Iron)
  * Chain recipe requires 3 nuggets instead of 2 nuggets and 1 ingot
  * Chainmail armor can be crafted with Chains and Leather
* Misc
  * Tool Nerf
    * Added nerf to 'Tools Efficiency'. Reduces Wooden and Stone tools efficiency by 25% and Iron and Diamond tools by 10%
    * Decreased Wooden Axe durability (10 -> 8)
    * Decreased Stone Axe and Shovel Durability (50 -> 48)
    * Decreased Stone Hoe durability (6 -> 3)
    * Increased Stone Pickaxe Durability (6 -> 7)
    * Iron Tools and Sword have increased durability (250 -> 375)
  * Explosion Overhaul
    * Lowered particles from Enable Poof Particles
* Mining
  * Global Hardness
    * Reduced Global Hardness Multiplier (4x in every dimension -> 2.5x in the Overworld and 4x in other dimensions)
    * Changed Depth hardness in the Overworld (0.04x per block below sea level down to Y=12 -> 0.02x per block below sea level down to Y=0)
* Hunger & Health
  * Food Consuming
    * Sped up eating speed (Reduced eating time multiplier (0.15 -> 0.13))
  * Health Regen
    * Fixed a bug where with the Combat Update preset would consume Saturation to regenerate health. [1.16]
    * Added a Max Exhaustion config option
* Experience
  * Global Experience
    * Fixed experience multiplier applying every time a xp orb is loaded