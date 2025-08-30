[1.1.2]

**Fixed**
* Crash caused by unregistered custom MobEffects (e.g. `sustenance`) not being saved correctly
* Stove didn’t accept modded fuels
* Crash when saving StoveBlockEntity if ownerUuid was null
* Crafting Bowl never produced output items after stirring was completed
* Server crash when ticking crops (`NoSuchMethodError: getGrowthSpeed`), fixed by explicitly calling `CropBlock.getGrowthSpeed(...)` in crop blocks

**Changed**
* Tomato crops can no longer be planted on top of other tomato blocks
* Chicken AI goals for locating and entering coops were optimized:
  * Reduced frequency of pathfinding checks with internal cooldown
  * Improved caching of valid coop positions
  * Prevented redundant navigation calls for smoother movement

A big thank you to everyone who has been actively reporting bugs and sharing feedback ❤️

Some issues can easily slip through during development, and your reports help me catch them faster. Your support makes the mod better with every update. I really appreciate it!

***

[1.1.1]

**Fixed**
* Fixed a crash when sending `SyncSaturationPacket` by sending payloads directly.

***

[1.1.0]

**Welcome to 1.21.1**

***

[1.0.12]

**Fixed**
* Farm Animals not being breedable anymore

***

[1.0.11]

**Fixed**
* Fixed a critical issue where the game would crash on servers due to client-only code being called from the `AnimalEntityMixin`. Everything worked fine in singleplayer, but not on dedicated servers.  
  This patch restores proper server compatibility for all new saturation mechanics. :)

_P.S.: Sorry for the hiccup – this ones on me_

***

[1.0.10]

## This small update focuses on adding new mechanics that enhance loot from farm animals, selected F&C crops, and eggs through interaction, care, and environmental factors.

**Added**
* A Pet Bowl! - Feed your cat or dog with a `Pet Bowl`. Occasionally, pets will walk up to an empty bowl and beg for food. Feeding grants temporary bonuses. You can assign a Name Tag to dedicate a bowl to a specific pet.
* Chicken Nest: When placed near chickens, eggs are laid directly into the nest instead of falling to the ground. The nest can hold up to 2 eggs. Occasionally (5% chance), a feather may also be added.* Chicken Coop: Works like similar to a bee nest. Chickens nearby will enter the coop when ready to lay an egg. Holds up to 6 chickens at once.
* Farm animals can now be fed their preferred food. The more often they are fed, the more meat they will drop. Feeding progress is visible only when wearing...
* ...`Dungarees`! – While equipped, shows feed levels above animals. Also prevents trampling farmland while wearing them. They can be bought from Farmer Villagers.
* Certain F&C crops now have a small chance to grow into larger variants when near a water sprinkler or during rain. Larger crops have an increased chance of yielding multiple drops.
* Farmer Villagers have now a Chance to offer several F&C related Items.
* Chicken Coop: Functions similarly to a bee nest. Chickens enter on their own to lay eggs and rest. Up to 9 Eggs can be stored at once. Players can also manually insert a leashed chicken. Eggs are automatically collected, and chickens exit after a short time.
* When placing SugarCare on Fertilized Soil it will grow 20% faster
* Dog & Cat Food can now be crafted into Bags. These can be placed and stacked up to 3 times.
* Support for DoggyTalents
* Japanese translation _(Thanks to PExPE3)_

**Fixed**
* Recipe for Yeast had wrong tags as Ingredients
* fr_fr translation
* EffectFood returns itself after being consumed, which is likely unintended.


***

[1.0.9]

**Fixed**
* Actually fixed recipes this time
* Improve quick move on cooking containers

***

[1.0.8]

**Fixed**
* Recipes are properly recognized

**Changed**
* `Sausage with oat patty` now uses the roaster instead of the cooking pot

***

[1.0.7]

**Added**
* Tooltip showing remaining burn time when hovering over the stove burn icon
* Added pt_br translation (thanks to Coffee-0xFF)
* Updated ru_ru (thanks to Tefnya)

**Changed**
* Stove now uses the same valid fuel items and burn times as the furnace
* Reduced spawn rates for all wild crops
* Implemented templates for most crops, bags, tea, and more — this should slightly improve loading times
* Completely overhauled all tags for much better compatibility with other mods (thanks to Ninjadaj!)
* `Stove` now uses the same Logic as Minecrafts `Furnace`, `Smoker` etc. for the FuelItems

**Fixed**
* Chair blocks no longer block the use of items in your offhand when right-clicking a chair
* `Sustenance` effect now works correctly
* `Cooking Pot` now crafts the correct output
* `Scarecrow` now properly grants a growth boost to nearby crops

***

[1.0.6]

**Added**
* Added the ability to retrieve items from the MincerBlock by Shift-Right Clicking
* Added Composter: A new Item made out of Fertilizer. Has 10 uses. Applies Bone Meal Effect to multiple Crops
* Added Silo Sounds: Opening & Closing Door, inserting Items, crafting finished
* FeedingTrough can now be filled by using Hoppers
* You can now use various Farm&Charm Crops to feed and breed farm and other animals
* Added Particles when eating a StackableEatableBlock - e.g. Pancakes
* Zombies have a really low Chance to spawn wielding a Pitchfork as a Weapon

**Changed**
* Strawberry crop now only drops an Item when age == MAX_AGE
* Tomato crop now only drops an Item when age == MAX_AGE
* Fertilizer works now again similar to Bone Meal and can be stacked again
* Renamed the "get_fertilizer" advancement
* Renamed the "get_minced_beef" advancement
* Renamed the "introduction_drying" advancement
* Renamed the "introduction_mincing" advancement
* Renamed the "use_hoe_on_fertilized_soil" advancement
* Renamed the "place_stove" advancement
* Pitchfork now uses the "handheld" model parent instead of "generated" – wield it like a true weapon! (even if it technically isn't one)
* Slightly raised the position of the particles when stirring the CraftingBowlBlock
* Improved Roaster, Supply Cart, Plates, Mincer, Window Sill & Plow Texture
* Updated following translations: ru_ru (Tefnya), zh_cn (sillymoon), pt_br (GMalvestiti)

**Fixed**
* Added an additional check for a valid recipe before increasing the Stirring value in CraftingBowlBlockEntity
* StoveBlockEntity now properly processes EffectBlockItem and applies stored effects to the crafted result
* Properly registered StorageBlockEntity & StorageBlockRenderer
* ForgeConfig not generating / loading properly
