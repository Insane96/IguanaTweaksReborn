# Changelog

## 2.6.0

* Combat
    * Added Stats Feature  
      Swords, Axes and Tridents get -1 damage, Diamond armor gets -1.5 toughness, Netherite gets a total of +2 armor
      points and the Protection enchantment is disabled.
* Mining
    * Added Wrong Tool Feature  
      Disabled by default prevents the player from Mining blocks without a proper tool. Mostly for modpack developers
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
      able to generate before deactivating. Also when broken, now spawners give bonus experience the farther from world
      spawn