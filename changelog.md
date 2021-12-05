# Changelog

## Upcoming
* Added Tagging feature
  * Players hit by a mob (directly or not) will be slowed down by 30% (Slowness II) for 0.15 seconds per half heart of damage (e.g. if you take 3 hearts of damage you get slowed down for 0.9 seconds)
* Stats
  * Added Crossbow adjustment. Crossbows no longer deal random damage but always ~9 like Bedrock Edition
  * Shields now give -20% movement speed. You like begin immune to anything for just one Iron Ingot uh?
* Explosions Overhaul
  * Fixed explosions causing newly spawned entities to be processed (e.g. Tnts spawned via Explosions or chest content)  
    This can be re-enabled in the config
  * Fixed explosions not dealing damage to entities behind Tile Entities
  * Reduced knockback taken by non-Living Entities to be about like vanilla
* Reduced and renamed Eating Speed Multiplier (now Eating Time Multiplier) (0.18 -> 0.15) 
* Added reagent to re-enable spawners. Disabled by default
* Fixed weather slowdown not registered as feature so not working at all (still disabled by default)
* Fixed disabling Player Experience feature not disabling it
* Fixed experience duplicating when dieying after consuming levels
* Fixed disabling Food Consuming Feature actually disabling nothing
* Fixed entity black/whitelist in temp spawners not taking into account dimensions

## Alpha 2.8.1
* Now requires InsaneLib 1.3.2
  * Jump slowdown is now calculated and set globally
* Armor Slowdown is now showed as kilograms instead of %  
  Also changed default armor weight (4/12/8/16/30/40 -> 4/12/8/15/25/33.3 Kg for leather/chainmail/gold/iron/diamnond/netherite) (2 -> 1.5 Kg per armor point) (+4% -> +3% weight per toughness)
* Temporary Spawners
  * Added entity and dimension black/whitelist
  * Fixed missing 'Minimum Spawnable Mobs' config option and begin wrongly set to 1
  * If spawnable mobs amount has changed then spawners are re-enabled
  * On feature disable spawners from temporary spawners get re-enabled
* Container contents are no longer destroyed with explosions
* Fixed armor with material weight not applying weight correctly if the armor points were changed
* Fixed explosions not working with Savage and Ravage "no block destruction" config
* Fixed tags not working for custom stack sizes
* Internal refactoring of plants growth multiplier

## Alpha 2.8.0
* Now requires InsaneLib 1.2.1
* Some features (Harder Crops, Stack Reduction, Food Hunger, Custom Hardness and Tool Nerf) now require a Minecraft restart if changing the config.
* Added Weather Slowdown Feature. Disabled by default
* Stats Feature
    * Diamond armor no longer gets -1 Toughness. Iron Armor now gets +0.5 toughness
* Armor Weight
    * Fixed material Armor weight set to 0 still calculating default armor weight
* Player Experience
    * Hopefully fixed a dupe bug and fixed 0 experience drop on player death not working
* Nerfed Bonemeal
    * Added Blacklist as whitelist config option
* Food Hunger (renamed from Food Overhaul)
    * Food hunger multiplier increased (0.5 -> 0.63) and now applies without adding +0.5
* Food Conusming
    * Speed multiplier decreased (0.25 -> 0.18)
* Global Hardness
    * Depth Hardness in the overworld now increased down to y 12 instead of 16
    * **Now affects Custom hardness too**
* Stack Reduction
    * Food Stack Reduction formula is now much simpler and can also be slightly configured.

## 2.7.6
* Explosions now generate in the middle of the entity instead of at the bottom (with this, holes made by creepers will change)
* Explosions no longer give knockback if the entity is behind a (non-destroyed) wall
* Shields no longer decrease explosions' knockback
* Fixed Food hunger multiplier set to 1 changing food hunger anyway
* Fixed NONE health regen preset disabling health regen

## 2.7.5
* Reduced Diamond Armor nerf (-1.25 -> -1 toughness)
* Fixed starveSpeed showing up on action bar when hunger <= 4

## 2.7.4
* Hunger Health
    * Added Health Regen feature
        * Has two presets and a huge list of config options
            * Combat Test Preset  
              Health and Hunger work like the Combat Test Snapshots
            * IguanaTweaks Preset (default)
              Health regen boost is removed when hunger at max and the player has saturation.  
              Hunger consumption is continuous when regenerating health instead of only when regenerating one hp.  
              Health regens when hunger >= 7. Health regens one hp every 10 seconds, increased / decreased by Well Fed / Injured effects obtained when eating / taking damage.  
              You start taking Starve damage from hunger 4 instead of 0 (1 damage every 30 seconds at 4 hunger in normal difficulty), less hunger = faster starvation, also in hard you take damage faster while in easy/peaceful slower. Also also you can die out of starvation at any difficulty.  
    * Added Food Consuming Feature
        * Food is consumed slower based on hunger + saturation given
        * Makes Potions and Milk be consumed faster like the Combat Test Snapshots
    * Food healing has been moved to Health Regen Feature. Also the healing formula has changed (33% of hunger restored -> 10% of hunger + saturation restored)
    * Rotten Flesh and potions are no longer blacklisted from saturation and hunger reductions
* Combat
    * Stats Feature
        * Added Power and Arrows nerf
          Power deals half damage and arrows no longer randomly critically strike
* Protection enchantment can have the max level reduced to 3 (disabled by deafult)
* Increased experience dropped on Death (50% -> 80%)
* Milk can no longer cure Debuff Feature's effects
* Fixed wrong config option name for Tool Nerf

## 2.6.0
* Combat
    * Added Stats Feature  
      Swords, Axes and Tridents get -1 damage, Diamond armor gets -1.5 toughness, Netherite gets a total of +2 armor
      points and the Protection enchantment is disabled.
* Mining
    * Added Wrong Tool Feature  
      Disabled by default, prevents the player from Mining blocks without a proper tool. Mostly for modpack developers
* Experience
    * Added Player Experience Feature  
      Experience required to level up now scales less exponentially (vanilla level 30 requires 1392 experience, with
      this feature active it requires about the same: 1395, BUT, vanilla level 100 requires about 31k experience while
      with this active only 15k)  
      On death, players will drop 50% experience instead of at most 7 levels
* Hunger Health
    * Changed Exhaustion on block break to exhaustion while breaking a block. This means that you'll now get exhaustion
      during the breaking of the block instead of when the block breaks
* Misc
    * Added Temporary Spawners  
      Spawners will be disabled after some mobs spawned. The farther from world spawn, the more mobs the spawner will be
      able to generate before deactivating. Also, when broken, now spawners give bonus experience the farther from world
      spawn