[1.1.27]

**Added**
* Cutting Board: Welcome to chopping, cutting and stripping!
* Iron, Diamond and Netherite Cleavers: the better the cleaver, the fewer chops it needs on the Cutting Board. Mobs and players killed with a cleaver drop their head
* Horse Fodder, Chicken Feed, Cat Food and Dog Food are now assembled on the Cutting Board instead of the Crafting Table or Crafting Bowl. 
* Cat and Dog Food Bags are now made on the Cutting Board from 4 Food and Paper, and unpack into 4 Food
* Flowers can be cut into Dye on the Cutting Board and give twice as much as on the Crafting Table
* More Cutting Board recipes: Porkchop into Bacon, Pumpkin and Melon Slices into Seeds, Sugar Cane into Sugar, Bones into Bone Meal, Mushroom Blocks into Mushrooms, Hay Bales into Wheat and Wool into String. Cutting a Chicken into Chicken Parts now also gives a Feather
* Minced Beef and Lamb Ham can be cooked into Steak and Cooked Mutton on a Campfire, in a Furnace or in a Smoker
* Rope, Compost and Chicken Nest can now also be assembled on the Cutting Board, their Crafting Table recipes stay
* Feathers now fly when taking eggs from a Chicken Nest or putting them in, and when chasing Chickens out of a Chicken Coop with a Pitchfork
* Breaking a Timber Well now splashes out some water
* Cooking and processing now gives experience: every 5 minced items, every 10 cut and every 20 stripped items on the Cutting Board, every 5 whisked dishes in the Crafting Bowl, and every meal from the Cooking Pot and Roaster. The amount is set per recipe with the optional `experience` field
* Mushroom Stew, Beetroot Soup and Rabbit Stew now also give Sustenance on NeoForge
* New config options: Giant Crop chance, Scarecrow interval and range, Wild Crop drop chance, Timber Well groundwater depth and rain fill chance, Chicken Coop capacity, Dog and Cat begging, Pet Bowl search range and feeding times, and switches to turn off the info tooltips of the Cutting Board, Mincer, Crafting Bowl and Silo or to only show them while wearing Dungarees
* VanillaBlend: an optional built-in resource pack with muted, vanilla-friendly colors for food, dishes, teas, Mob Effect icons and more. Enable it in the Resource Packs menu. Palettes inspired by Vanilla, Farmer's Delight, Supplementaries and Create
* Tool Rack now accepts every item in the `c:tools` tag, including Bows, Crossbows, Tridents, Maces, Brushes, Flint and Steel and tools from other mods. Shields are excluded
* Silo now shows an info tooltip when you look at it: how full it is, what is drying, and what is already dried
* Mincer now shows an info tooltip once something is inside: the input, the result and the cranking progress
* Crafting Bowl now shows an info tooltip once something is inside: the ingredients, the result and the stirring progress
* Crafting Bowl now leans and rocks along with the whisk while you stir. The faster you whisk, the more it moves
* The dough in the Crafting Bowl now slowly rises from the bottom while you stir and sloshes along with the whisk, instead of just appearing at the end. It only rises when the ingredients actually make something. Stirring now also splashes little drops of dough out of the bowl
* Ingredients on the Cutting Board now hop up whenever the knife comes down
* Mincer now rocks back and forth in time with the crank while you turn it
* Mincer and Crafting Bowl sound much better: putting ingredients in, cranking, stirring, finishing and taking things out all have their own sounds, the Crafting Bowl gets louder and higher the faster you whisk, and the Mincer squishes meat, crunches wood and plants, grits stone and grinds ores
* Sturdy Ladder now wobbles a little and creaks now and then while someone climbs it
* Water Sprinkler: right-click it to switch between three pressures. Steady Pressure turns evenly, Pulsing Pressure ticks around in small steps like a real impact sprinkler and wobbles a little, High Pressure spins fast, rattles and shoots its water jets further. The pressure only changes the look, not the watering. An info tooltip shows the current pressure and can be turned off in the config. Sprinklers now slowly spin up when placed or when rain starts, every sprinkler turns on its own instead of all of them in sync, and they no longer keep turning while the game is paused
* Scarecrow: Shift + right-click it with an empty hand to switch between three moods. Calm sways gently like before, Windy sways on its post, flaps its arms, nods its head, moves more in rain and thunder and catches a gust now and then, Watchful slowly turns its head towards the nearest player, looks around when nobody is near and throws its arms up when you come too close. The mood only changes the look, not the crop growth. An info tooltip shows the current mood and can be turned off in the config. Every Scarecrow now moves on its own instead of all of them in sync
* Scarecrow is now a two block tall block: it needs free space above it, its head can be clicked and has collision, and no block can be placed into it anymore. Scarecrows placed before this update keep working but only get their upper half once they are picked up and placed again
* 22 new Farm & Charm splash texts on the title screen
* Chicken Nests now work with birds and eggs from other mods. Add the bird to the `farm_and_charm:nest_layers` entity tag and its egg to the `farm_and_charm:nest_eggs` item tag. Eggs in the `c:eggs` tag are accepted automatically
* New common tags for better mod compatibility: `c:foods`, `c:foods/berry`, `c:foods/food_poisoning`, `c:foods/edible_when_placed`, `c:tools`, `c:storage_blocks` (Bags and Bales), `c:crops/lettuce`, `c:seeds/tomato` and `c:seeds/lettuce`

**Changed**
* Crafting Bowl recipes now need 200 stirs instead of 50, and how fast you stir counts: stirring quickly and steadily finishes in about 8 seconds, slow stirring takes longer and stopping pauses the progress
* Mincer cranking now also speeds up and slows down with the crank instead of counting at a fixed pace
* Crafting Bowl and Mincer went from almost 20,000 block states down to 5 (Crafting Bowl from 6,666 to 1, Mincer from 13,332 to 4). Their stirring and cranking progress now lives in the block entity, so they also no longer update their block every tick while stirring or cranking, which saves a lot of block and chunk updates. Stirring or cranking progress that was in the middle of an item when updating is reset once
* The config is now split into Farming, Water, Animals, Kitchen, Effects and Food, and every option has an explanation. Options were moved, so changed values in an existing config file are reset to their defaults
* Water drips and splashes from the Timber Well, Sink, Water Sprinkler and troughs now match the biome's water color
* Tool Rack is much easier to take items from due to its increased voxelshape
* Timber Well now has an exact voxelshape that follows its model
* Timber Well messages such as "No groundwater below this Well" now pop up briefly above the Well in a golden info frame and fade out, instead of showing in the action bar
* Advancement descriptions now capitalize item and block names like Vanilla does
* Recipes now use the common tags shared by Fabric and NeoForge (`c:buckets/water`, `c:buckets/milk`, `c:foods/raw_fish`, `c:foods/cooked_fish`, `c:foods/vegetable`, `c:foods/bread`)
* Horses and Cats now behave the same on NeoForge as on Fabric when fed Barley, Oat, Horse Fodder or Cat Food
* Cats now look for their Pet Bowl in a smaller radius, which saves performance
* Polished the English and German translations
* Mincer and Crafting Bowl no longer lose their progress when you pause cranking or stirring
* Pitchfork, Rope, Cooking Pot, Mincer and the Iron and Diamond Cleavers now use vanilla-style material colors

**Fixed**
* The Stove now actually gives the experience shown for its recipes when you take the result or break the Stove
* Rested now actually grants bonus experience on NeoForge
* Crops now actually grow faster in the rain, the config options for it did nothing before
* The Water Sprinkler range config option now actually changes the range of the Water Sprinkler
* Breaking a Timber Well or Water Trough now shows wood particles instead of blue ones
* Chicken Nest now reliably takes and gives eggs no matter where you click on it
* Common tags no longer contain wrong items: dishes were removed from `c:cooked_beef`, `c:cooked_chicken`, `c:cooked_mutton`, `c:cooked_pork` and `c:foods/cooked_meat`, Bacon from `c:raw_pork` and Strawberries from `c:vegetables`. Tomatoes are now vegetables, Strawberries are berries. `c:water_bottles` no longer contains the Water Bucket

***

[1.1.26]

**Added**
* Seeder: a new cart that automatically sows Farmland with seeds from its storage while being driven
* Flooded Farmland: right-click Fertilized Farmland with a Pitchfork to lower it to half height, then fill it with a Water Bucket (or place it next to water) to flood it. Other mods can check the new `lowered` and `waterlogged` block states for paddy-style crops. Lowered Farmland only accepts seeds from the `farm_and_charm:needs_lowered_farmland` item tag
* Tilling Fertilized Soil into Farmland and lowering Fertilized Farmland with a Pitchfork now spawns dirt particles
* Timber Well: pump water up by right-clicking it with an empty hand (with an animated pump lever), as long as there is a water source up to 6 blocks below (dripping water shows it). It also fills up step by step while it rains. Buckets and drinking Livestock use up its water, just like a Water Trough. It now has a recipe, drops itself when broken and is mined faster with an Axe

**Fixed**
* Dungarees no longer stack up to 64, they now stack to 1 like any other armor piece
* Water Trough is now mined faster with an Axe
* Scarecrows now face the player when placed instead of being turned around
* Removed Wheat, Barley, Corn and Oats Silo drying recipes into Bone Meal, since they could silently win over Brewerys recipes for drying the same crops into other results :D

**Changed**
* Crafting Bowl now uses a single entity texture; the dough is a separate model part that only renders once stirring is finished. Resource packs should move their `crafting_bowl_full.png` content into `crafting_bowl.png`

***

[1.1.25]

**Fixed**
* Fully built Silos (3x3x9) no longer crash the game when sneak-right-clicking to take a finished item out, and the first finished-item slot is no longer skipped
* Tomato Crop now drops its Tomato, Tomato Seeds and Rope instead of nothing when broken
* Chairs are no longer left behind or duplicated by Sable/Create: Aeronautics contraptions
* Fixed `c:grains/wheats` incorrectly containing Oat, Barley and Corn in addition to Wheat
* `c:grain` and `c:strawberry` now correctly alias `c:grains` and `c:strawberries` instead of duplicating an incomplete item list
* Plow Cart now plows its second row relative to its actual driving direction instead of always offsetting towards world-east
* Plow Cart now breaks flowers and other bushes on top of grass/dirt when plowing instead of leaving them floating on the new Farmland

**Changed**
* `c:crops` now also includes Barley, Oat and Strawberry alongside the existing Cabbage, Corn, Onion and Tomato
* Food items are now properly tagged under the common `c:foods` tag and Fertilizer/Compost are now tagged as `c:fertilizers`

***

[1.1.24]

**Fixed**
* Nutrition and saturation values from the config file now actually apply on NeoForge — food items were registered before the config finished loading, so edits were silently ignored
* Chickens no longer keep a lead attached to their previous holder after re-emerging from a Chicken Coop, even though the lead had already dropped and popped off when they entered
* Tomato Crop no longer destroys itself right after growing a new segment, since the block below a growing head is now correctly recognized as valid support
* Mincer no longer strips data components (such as attribute modifiers forwarded by other mods' recipes) from items dropped out of its output slot. (Thanks to odderb)

**Added**
* Wheat, Barley, Corn, Oat, Strawberry, Lettuce, Tomato and Onion can now be dried into Bone Meal in the Silo, matching their wild counterparts

***

[1.1.23]

**Fixed**
* Container GUIs no longer render the background twice, preventing overly dark backgrounds and improving compatibility with background blur mods such as Blur+. (Thanks to amiralimollaei)
* Stove recipes now require an exact ingredient match, preventing unintended crafting results when extra ingredients are present. (Thanks to rumi-sh)
* Resolved a duplication glitch affecting storage blocks when used with Sable from Create: Aeronautics. (Thanks to Daudeuf)
* Removed an unnecessary `ItemStack` mixin, resolving compatibility issues with Create: Aeronautics and Sable. (Thanks to dynamiteOpanty)
* Cooking Pot and Roaster no longer create infinite containers — empty glass bottles and bowls are now always consumed when required by recipes
* Plows now use the block they are actually standing on, so they harvest crops while sitting fully on farmland and no longer till the soil underneath farmland
* Chicken Coop no longer crashes the server when its block entity ticks after the block has been removed or replaced
* Chickens no longer get permanently stuck on a coop that was broken, moved, filled up or became unreachable — the outdated target is now discarded so they can look for another coop
* Fixed a server crash caused by cart tracking becoming corrupted when a pulled cart detached mid-tick, for example when it got stuck against a block

**Added**
* Added Italian (`it_it`) localization. (Thanks to serenautilus)

**Changed**
* Added plural common tags (`c:flours` and `c:doughs`) while keeping the existing singular tags as legacy aliases for improved cross-mod compatibility. (Thanks to RooftopThinker)

**Improved**
* Improved Cattle Grid behavior by replacing the velocity-based restriction with collision walls, preventing mobs from getting permanently stuck while preserving its intended functionality. (Thanks to divaltor)

***

[1.1.22]

**Fixed**
* Visual glitches with crank and bowl animations (khoidauminh)
* Compatibility with Sable/Create Aeronautics (lukeelrod)

**Changed**
* Water Sprinkler now hydrates all farmland blocks extending FarmBlock

**Improved**
* Mincer interaction and usability (khoidauminh)

***

[1.1.21]

**Fixed**
* Oatmeal with Strawberries using the wrong tag
* Wild Corn not dropping anything when breaking the top block (Danieltl21)

**Changed**
* Introduction Mincing advancement is now triggered directly when inserting Beef into the Mincer
* Stove can now be ignited manually with ignition items when fuel is present
* Stove can be extinguished with tools like shovels or water without immediately relighting
* Bowl recipe checks now only run once when the required stir count is reached

**Improved**
* Interacting with a finished bowl now always pops out its items
* Adding ingredients to a bowl resets the STIRRED property
* Remainder items now stay inside the bowl and are ejected with the result
* Bowls can now be stirred even while holding an item

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
