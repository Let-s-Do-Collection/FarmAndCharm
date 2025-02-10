[Unreleased] - 2025.02.08



**Added**
* Added the ability to retrieve items from the MincerBlock by Shift-Right Clicking
* Added Composter: A new Item made out of Fertilizer. Has 10 uses. Applies Bone Meal Effect to multiple Crops
* Added Silo Sounds: Opening & Closing Door, inserting Items, crafting finished
* FeedingTrough can now be filled by using Hoppers
* You can now use various Farm&Charm Crops to feed and breed farm and other animals
* Added Particles when eating a StackableEatableBlock - e.g. Pancakes

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
* WindowSill White/Blue/Yellow Pot Texture
* Improved Roaster, Supply Cart, Plates, Mincer & Plow Texture
* Updated following translations: ru_ru (Tefnya), zh_cn (sillymoon), pt_br (GMalvestiti)

**Fixed**
* Added an additional check for a valid recipe before increasing the Stirring value in CraftingBowlBlockEntity
* StoveBlockEntity now properly processes EffectBlockItem and applies stored effects to the crafted result
* Properly registered StorageBlockEntity & StorageBlockRenderer
* ForgeConfig not generating / loading properly

**ToDo**
* Additionally, cart movement could be significantly improved, particularly when navigating inclines and declines.
* Make Dog Food, Cat Food and Horse Fodder placeable
* Water Property FeedingtroughBlock