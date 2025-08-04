[Unreleased] - 2025.02.07

* The StoveBlockEntity needs testing with Candlelight to ensure compatibility.
* Additionally, cart movement could be significantly improved, particularly when navigating inclines and declines.


**Added**
* Added the ability to retrieve items from the MincerBlock by Shift-Right Clicking
* Added Composter: A new Item made out of Fertilizer. Has 10 uses. Applies Bone Meal Effect to multiple Crops

**Changed**
* Strawberry crop now only drops an Item when age == MAX_AGE
* Tomato crop now only drops an Item when age == MAX_AGE
* Fertilizer works now again similar to Bone Meal and can be stacked again
* Renamed the "get_fertilizer" advancement
* Renamed the "get_minced_beef" advancement
* Renamed the "introduction_drying" advancement
* Renamed the "introduction_mincing" advancement

**Fixed**
* Added an additional check for a valid recipe before increasing the Stirring value in CraftingBowlBlockEntity
* StoveBlockEntity now properly processes EffectBlockItem and applies stored effects to the crafted result
* Properly registered StorageBlockEntity & StorageBlockRenderer