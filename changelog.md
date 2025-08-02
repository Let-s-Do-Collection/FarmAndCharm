[1.0.10]

## This small update focuses on adding new mechanics that enhance loot from farm animals, selected F&C crops, and eggs through interaction, care, and environmental factors.

**Added**
* A Pet Bowl! - Feed your cat or dog with a `Pet Bowl`. Occasionally, pets will walk up to an empty bowl and beg for food. Feeding grants temporary bonuses. You can assign a Name Tag to dedicate a bowl to a specific pet.
* Chicken Nest: When placed near chickens, laid eggs are stored directly inside the nest instead of dropping to the ground. Maximum capacity: 2 eggs.
* Chicken Coop: Works like similar to a bee nest. Chickens nearby will enter the coop when ready to lay an egg. Holds up to 6 chickens at once.
* Farm animals can now be fed their preferred food. The more often they are fed, the more meat they will drop. Feeding progress is visible only when wearing...
* `Dungarees`! – While equipped, shows feed levels above animals. Also prevents trampling farmland while wearing them. They can be bought from Farmer Villagers.
* Certain F&C crops now have a small chance to grow into larger variants when near a water sprinkler or during rain. Larger crops have an increased chance of yielding multiple drops.
* Farmer Villagers have now a Chance to offer several F&C related Items.
* When placing SugarCare on Fertilized Soil it will grow 20% faster
* Support for DoggyTalents

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

[1.0.7] – 2025.05.06

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

[1.0.6] - 2025.02.15

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
