[1.0.9]
*Fixed*
* Actually fixed recipes this time

[1.0.8]
**Fixed**
* Recipes are properly recognized

**Changed**
* `Sausage with oat patty` now uses the roaster instead of the cooking pot

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
