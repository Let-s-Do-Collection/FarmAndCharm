[1.1.21]

**Fixed**
* Oatmeal with Strawberries using the wrong tag
* Wild Corn not dropping anything when breaking the top block (Danieltl21)

**Changed**
* Introduction Mincing advancement is now triggered directly when inserting Beef into the Mincer
* Stove can now be ignited manually with ignition items when fuel is present
* Stove can be extinguished with tools like shovels or water

***

[1.1.20]

**Fixed**
* Stove now properly resets its lit state when running out of fuel
* Cooking progress no longer resets when modifying ingredient, fuel, or output slots
* Cooking progress now only resets when the recipe itself changes
* Typo in Water Trough
* Wild plants no longer transform into vanilla tall grass when bonemealed
* Jade flickering when looking at Silos
* Excessive blockstate updates in Silo multiblock structure

**Changed**
* Update ru_ru
* Silo connectivity now updates only on structural changes
* Improved performance of the Silo multiblock system
* Wild Plants now have a 60% chance to receive the plant item back when using bone meal, due to balancing reasons

***

[1.1.19]

**Fixed**
* TeaJugItem not returning empty container on use (thanks to KawaiShio)
* CraftingBowl not properly resetting after taking out ingredients or the result item, preventing the next batch from being stirred without breaking the bowl (thanks to khoidauminh)
* MincerBlock not correctly resetting its state after processing, which could interrupt further usage

**Added**
* Added zh_tw translation (thanks to cherrypuff1120)

**Changed**
* TeaCupItems are now always edible

***

[1.1.18]

**Fixed**
* Fixed a crash that could occur when a cart got stuck while being pulled.
* Dungarees being HUGE when placed inside AlpineWhispers / Meadows wardrobe
* Scarecrow growth exploit caused by rapid breaking and replacing
* Mincer softlock when inserting unsupported items such as shields or interacting rapidly
* Containers such as bottles, bowls and buckets not being returned after cooking
* Title lables not being consistent when opening GUIs

**Added**
* Placeable Wheat Piles 
* Placeable Feather Piles 

**Changed**
* Pack.png

***


[1.1.17]

**Fixed**
* Fixed a crash that could occur when a cart got stuck while being pulled.

**Added**
* Planting crops on Farmland now kicks up subtle soil particles for visual feedback.

***

[1.1.16]

**Fixed**
* Carts being indestructible
* Also they now properly take damage and break as intended

**Changed**
* Reworked Strawberry Texture
* Adjusted pitchfork attributes: slightly increased damage, significantly reduced attack speed

***

[1.1.15]

**Added**
* Animals eating from Feeding Troughs now generate particles while doing so
* Added a Water Trough for animals to drink from, also usable as a water source
* Added Shift tooltips to various blocks and items for in-game information
* Updated Scarecrow interaction: adding and removing Dungarees now works correctly with the new interaction methods
* Added Tooltips for Teas and Pitchfork

**Fixed**
* Removed Apache Commons usage from EffectFood blocks
* Item duplication with the Mincer when inserting non-processable items in Creative
* Stoves appeared lit without consuming fuel and had inconsistent lit state after placement
* Feeding animals using Create Deployers causing the game to crash
* MobEffects were not applied correctly due to invalid effect references
* Ropes are now correctly tagged under `c:ropes`
* Chicken Coop items storing invalid entity data could crash the game when saving. Affected items are now sanitized and stored data is preserved
* Sturdy Ladder placement preview could briefly appear and then disappear when extending from the base
* Fertilized Farmland not bonemealing (thanks to MisledWater79)

**Changed**
* Feeding Troughs now use the `farm_and_charm:feeding_trough_food` item tag instead of relying on `minecraft:villager_plantable_seeds`
* Slightly updated textures for Crafting Bowl and Mincer blocks
* Updated fr_fr translation (thanks to acorsicanfrog)

***

[1.1.14]

**Fixed**
* Excessive saturation sync packets from animals now only send when values change and only to nearby players wearing Dungarees

***

[1.1.13]

**Added**
* **Packed Dirt**: A decorative compacted dirt block that gradually turns into *Trampled Packed Dirt* when walked over.
* **Stablefloor**: A decorative stable ground block that slowly transforms into *Trampled Stablefloor* through frequent foot traffic.

**Fixed**
* Crash when Create Deployer interacted with animals
* Wild Ribwort and Nettle not being bone-mealable
* Wild Corn duplication via shears caused by an incorrect loot table

**Changed**
* Adjusted Wild Corn loot to match intended drop balance

***

[1.1.12]

**Added**
* A Sturdy Ladder! Freestanding. Can be placed without a support behind it.
  *	Requires either a block underneath or a block adjacent as support.
  *	Right-clicking the bottom with another ladder in hand automatically extends it upwards.
* Cattlegrid: If you have ever been hiking in the Alps, you will know that these are designed to prevent animals from crossing them. These work in the same way. Farm animals (cows, pigs, etc.) cannot cross the block, dogs and players are slowed down... and cats can walk across them as normal!
* Chicken Fence & Iron Divider: Fence Blocks for your Farm.

**Fixed**
* CraftingBowl stirring sometimes didnt trigger. Empty-hand use now reliably starts crafting!
* Crash on player save from chicken coop items by migrating data to CustomData
* Tamed Dogs not eating DogFood.

**Changed**
* Smooth, BE-driven interpolation for CraftingBowl & Mincer renderers
* Lowered Pitchfork Attack Speed

***


[1.1.11]

**Fixed**
* Scarecrows now boost the growth of climbing crops in addition to regular farmland crops.
* Feeding trough can now be refilled after animals eat from it.

***

[1.1.10]

**Fixed**
* `FoodBlock` now properly applies hunger, saturation and effects from its registered item’s `FoodProperties` when eating bites
* `CookingPot` now writes all ingredient effects onto output items (includes base potion effects and custom potion effects)

**Changed**
* Removed unused ArmorMaterial layer handling
* Mixin configs moved from common to loader-specific folders 

***

[1.1.9]

**Fixed**
* Meals and effect foods now restore hunger and saturation correctly
* Fixed crash on startup caused by config values being accessed before load
* Fixed server crash in Roaster caused by illegal access to FoodProperties.PossibleEffect constructor
* Updated EffectFoodHelper to use 1.21.1 FoodProperties.Builder API 
* Stove now properly matches only ingredient slots when checking recipes.
* Ensures Candlelight effect-food blocks use the same BE logic as Farm & Charm.
* Bonemeal can no longer be applied to tomato crops once they have reached their maximum growth stage.

***

[1.1.8]

**Fixed**
* Climbing crops (Tomatoes, Hops) placement now works on all blocks that extend `FarmBlock`
* Removed invalid DataMap entry `farm_and_charm:lettuce_crop` that caused NeoForge startup crashes.

***

[1.1.7.1-Neoforge]

**Fixed**
* _Neoforge Only:_ Compostable items are now properly registered through NeoForge data maps instead of runtime code


***

[1.1.7]

**Fixed**
* Resolved server crash when syncing saturation (`SyncSaturationPacket`) by registering S2C payload type correctly and limiting receiver registration to the client environment
* REI integration now properly handles tag-based ingredients across all custom categories (Cooking Pot, Crafting Bowl, Mincing, Roaster, Stove, Silo)
* REI Result items are now consistently resolved with registryAccess to avoid unstable/null context issues   

***

[1.1.6]

**Fixed**
* Crash when sending `SyncSaturationPacket` due to missing STREAM_CODEC registration on server
* Fixed crash when saving Coop items by moving data to `BLOCK_ENTITY_DATA` and stripping UUIDs.
* Fixed duplicate UUID warnings when releasing chickens.

***

[1.1.5]

**Fixed**
* Network crash on Fabric due to incorrect registration of S2C receivers in PacketHandler

***

[1.1.4]

**Fixed**
* MincerCategory for REI wasn't registered properly
* ArmorType was being registered twice
* Sprinkler now hydrates farmland and extinguishes nearby fire properly
* Leggings renderer now works correctly on Fabric
* Improved IngredientsCheck for CraftingBowl

**Changed**
* Migrated FarmAndCharmIdentifier to ResourceLocation.fromNamespaceAndPath
* Added a "Can be Placed" tooltip for the PetBowl

***

[1.1.3]

**Fixed**
* Another Try for: Crash caused by unregistered custom MobEffects (e.g. `sustenance`) not being saved correctly
* Grandma's Strawberry Pie can now be eaten safely. Enjoy!
* Dungarees not being rendered properly on NeoForge

**Changed**
* Most cooking tools can now be broken instantly and dont require a tool anymore
* All Effects have now unique Particle Effect Colors 

*** 

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
