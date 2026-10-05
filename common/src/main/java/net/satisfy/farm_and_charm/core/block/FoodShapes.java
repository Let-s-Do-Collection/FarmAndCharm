package net.satisfy.farm_and_charm.core.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class FoodShapes {
    public static final VoxelShape[] POTATO_WITH_ROAST_MEAT = {
            shape(
                    Block.box(1, 0, 1, 15, 2, 15),
                    Block.box(2, 1.95, 5, 5, 4.95, 8),
                    Block.box(3, 1.95, 2, 6, 4.95, 5),
                    Block.box(4, 1.95, 4, 14, 6.95, 6),
                    Block.box(4, 1.95, 6, 14, 6.95, 9),
                    Block.box(4, 1.95, 9, 14, 6.95, 11),
                    Block.box(4, 1.95, 11, 14, 6.95, 14)
            ),
            shape(
                    Block.box(1, 0, 1, 15, 2, 15),
                    Block.box(3, 1.95, 2, 6, 4.95, 5),
                    Block.box(4, 1.95, 6, 14, 6.95, 9),
                    Block.box(4, 1.95, 9, 14, 6.95, 11),
                    Block.box(4, 1.95, 11, 14, 6.95, 14)
            ),
            shape(
                    Block.box(1, 0, 1, 15, 2, 15),
                    Block.box(4, 1.95, 9, 14, 6.95, 11),
                    Block.box(4, 1.95, 11, 14, 6.95, 14)
            ),
            shape(
                    Block.box(1, 0, 1, 15, 2, 15),
                    Block.box(4, 1.95, 11, 14, 6.95, 14)
            )
    };

    public static final VoxelShape[] BAKED_LAMB_HAM = {
            shape(
                    Block.box(1, 0, 1, 15, 2, 15),
                    Block.box(3, 2, 3, 13, 10, 13)
            ),
            shape(
                    Block.box(1, 0, 1, 15, 2, 15),
                    Block.box(3, 2, 6, 13, 10, 13)
            ),
            shape(
                    Block.box(1, 0, 1, 15, 2, 15),
                    Block.box(3, 2, 9, 13, 10, 13)
            ),
            shape(
                    Block.box(1, 0, 1, 15, 2, 15),
                    Block.box(3, 2, 11, 13, 10, 13)
            )
    };

    public static final VoxelShape[] FARMERS_BREAKFAST = {
            shape(
                    Block.box(1, 0, 1, 15, 2, 15),
                    Block.box(1.9, 1.9, 3.9, 4.1, 4.1, 11.1),
                    Block.box(2, 2, 4, 4, 4, 11),
                    Block.box(2.9, 1.9, 11.9, 10.1, 4.1, 14.1),
                    Block.box(3, 2, 12, 10, 4, 14),
                    Block.box(6, 2.05, 6, 9, 3.05, 9),
                    Block.box(10, 2, 6, 14, 6, 13)
            ),
            shape(
                    Block.box(1, 0, 1, 15, 2, 15),
                    Block.box(2.9, 1.9, 11.9, 10.1, 4.1, 14.1),
                    Block.box(3, 2, 12, 10, 4, 14),
                    Block.box(6, 2.05, 6, 9, 3.05, 9),
                    Block.box(10, 2, 6, 14, 6, 13)
            ),
            shape(
                    Block.box(1, 0, 1, 15, 2, 15),
                    Block.box(2.9, 1.9, 11.9, 10.1, 4.1, 14.1),
                    Block.box(3, 2, 12, 10, 4, 14)
            ),
            shape(
                    Block.box(1, 0, 1, 15, 2, 15)
            )
    };

    public static final VoxelShape[] STUFFED_CHICKEN = {
            shape(
                    Block.box(1, 0, 1, 15, 2, 15),
                    Block.box(2, 2, 2, 4, 4, 5),
                    Block.box(2, 4, 10, 4, 7, 14),
                    Block.box(3, 5, 14, 4, 6, 16),
                    Block.box(4, 2, 3, 12, 8, 13),
                    Block.box(5, 8, 4, 11, 9, 12),
                    Block.box(12, 2, 2, 14, 4, 5),
                    Block.box(12, 4, 10, 14, 7, 14),
                    Block.box(12, 5, 14, 13, 6, 16)
            ),
            shape(
                    Block.box(1, 0, 1, 15, 2, 15),
                    Block.box(2, 2, 2, 4, 4, 5),
                    Block.box(4, 2, 3, 12, 8, 13),
                    Block.box(12, 4, 10, 14, 7, 14),
                    Block.box(12, 5, 14, 13, 6, 16)
            ),
            shape(
                    Block.box(1, 0, 1, 15, 2, 15),
                    Block.box(4, 2, 6, 12, 8, 13),
                    Block.box(5, 2.05, 3, 11, 8.05, 6),
                    Block.box(12, 4, 10, 14, 7, 14),
                    Block.box(12, 5, 14, 13, 6, 16)
            ),
            shape(
                    Block.box(1, 0, 1, 15, 2, 15),
                    Block.box(4, 2, 9, 12, 8, 13),
                    Block.box(5, 2.05, 3, 11, 8.05, 9)
            )
    };

    public static final VoxelShape[] STUFFED_RABBIT = {
            shape(
                    Block.box(1, 0, 1, 15, 2, 15),
                    Block.box(4, 2, 2, 6, 4, 6),
                    Block.box(4, 2, 8, 5, 4, 11),
                    Block.box(4, 2, 11, 6, 6, 15),
                    Block.box(5, 2, 4, 11, 7, 14),
                    Block.box(10, 2, 2, 12, 4, 6),
                    Block.box(10, 2, 11, 12, 6, 15),
                    Block.box(11, 2, 8, 12, 4, 11),
                    Block.box(5.5, 7, 2, 10.5, 11, 7)
            ),
            shape(
                    Block.box(1, 0, 1, 15, 2, 15),
                    Block.box(4, 2, 2, 6, 4, 6),
                    Block.box(5, 2, 4, 11, 7, 14),
                    Block.box(10, 2, 2, 12, 4, 6)
            ),
            shape(
                    Block.box(1, 0, 1, 15, 2, 15),
                    Block.box(4, 2, 2, 6, 4, 6),
                    Block.box(5, 2, 4, 11, 7, 10),
                    Block.box(5, 2, 10, 11, 7, 14),
                    Block.box(10, 2, 2, 12, 4, 6)
            ),
            shape(
                    Block.box(1, 0, 1, 15, 2, 15),
                    Block.box(5, 2, 4, 11, 7, 7),
                    Block.box(5, 2, 7, 11, 7, 14),
                    Block.box(10, 2, 2, 12, 4, 6)
            )
    };

    public static final VoxelShape[] FARMERS_BREAD = {
            shape(
                    Block.box(1, 0, 1, 15, 2, 15),
                    Block.box(2, 2, 2, 14, 8, 5),
                    Block.box(2, 2, 5, 14, 8, 8),
                    Block.box(2, 2, 8, 14, 8, 11),
                    Block.box(2, 2, 11, 14, 8, 14)
            ),
            shape(
                    Block.box(1, 0, 1, 15, 2, 15),
                    Block.box(2, 2, 5, 14, 8, 8),
                    Block.box(2, 2, 8, 14, 8, 11),
                    Block.box(2, 2, 11, 14, 8, 14)
            ),
            shape(
                    Block.box(1, 0, 1, 15, 2, 15),
                    Block.box(2, 2, 8, 14, 8, 11),
                    Block.box(2, 2, 11, 14, 8, 14)
            ),
            shape(
                    Block.box(1, 0, 1, 15, 2, 15),
                    Block.box(2, 2, 11, 14, 8, 14)
            )
    };

    public static final VoxelShape[] GRANDMOTHERS_STRAWBERRY_CAKE = {
            shape(
                    Block.box(2, 4, 2, 8, 7, 8),
                    Block.box(2, 4, 8, 8, 7, 14),
                    Block.box(3, 0, 3, 8, 4, 8),
                    Block.box(3, 0, 8, 8, 4, 13),
                    Block.box(8, 0, 3, 13, 4, 8),
                    Block.box(8, 0, 8, 13, 4, 13),
                    Block.box(8, 4, 2, 14, 7, 8),
                    Block.box(8, 4, 8, 14, 7, 14)
            ),
            shape(
                    Block.box(2, 4, 8, 8, 7, 14),
                    Block.box(3, 0, 8, 8, 4, 13),
                    Block.box(8, 0, 3, 13, 4, 8),
                    Block.box(8, 0, 8, 13, 4, 13),
                    Block.box(8, 4, 2, 14, 7, 8),
                    Block.box(8, 4, 8, 14, 7, 14)
            ),
            shape(
                    Block.box(2, 4, 8, 8, 7, 14),
                    Block.box(3, 0, 8, 8, 4, 13),
                    Block.box(8, 0, 8, 13, 4, 13),
                    Block.box(8, 4, 8, 14, 7, 14)
            ),
            shape(
                    Block.box(8, 0, 8, 13, 4, 13),
                    Block.box(8, 4, 8, 14, 7, 14)
            )
    };

    public static final VoxelShape[] OAT_PANCAKE = {
            shape(
                    Block.box(1, 0, 1, 15, 2, 15),
                    Block.box(2, 2, 2, 14, 3, 14)
            ),
            shape(
                    Block.box(1, 0, 1, 15, 2, 15),
                    Block.box(2, 2, 2, 14, 4, 14)
            ),
            shape(
                    Block.box(1, 0, 1, 15, 2, 15),
                    Block.box(2, 2, 2, 14, 5, 14)
            ),
            shape(
                    Block.box(1, 0, 1, 15, 2, 15),
                    Block.box(2, 2, 2, 14, 6, 14)
            ),
            shape(
                    Block.box(1, 0, 1, 15, 2, 15),
                    Block.box(2, 2, 2, 14, 7, 14)
            ),
            shape(
                    Block.box(1, 0, 1, 15, 2, 15),
                    Block.box(2, 2, 2, 14, 8, 14)
            ),
            shape(
                    Block.box(1, 0, 1, 15, 2, 15),
                    Block.box(2, 2, 2, 14, 9, 14)
            ),
            shape(
                    Block.box(1, 0, 1, 15, 2, 15),
                    Block.box(2, 2, 2, 14, 10, 14)
            )
    };

    public static final VoxelShape[] ROASTED_CORN = {
            shape(
                    Block.box(1, 0, 1, 15, 2, 15),
                    Block.box(6.9, 1.9, 3.9, 9.1, 4.1, 11.1),
                    Block.box(7, 2, 4, 9, 4, 11),
                    Block.box(9.9, 1.9, 3.9, 12.1, 4.1, 11.1),
                    Block.box(10, 2, 4, 12, 4, 11)
            ),
            shape(
                    Block.box(1, 0, 1, 15, 2, 15),
                    Block.box(2.9, 1.9, 3.9, 5.1, 4.1, 11.1),
                    Block.box(3, 2, 4, 5, 4, 11),
                    Block.box(6.9, 1.9, 3.9, 9.1, 4.1, 11.1),
                    Block.box(7, 2, 4, 9, 4, 11),
                    Block.box(8.9, 3.9, 3.9, 11.1, 6.1, 11.1),
                    Block.box(9, 4, 4, 11, 6, 11),
                    Block.box(9.9, 1.9, 3.9, 12.1, 4.1, 11.1),
                    Block.box(10, 2, 4, 12, 4, 11)
            ),
            shape(
                    Block.box(1, 0, 1, 15, 2, 15),
                    Block.box(1.706, 3.9, 5.209, 8.906, 6.1, 7.409),
                    Block.box(1.706, 3.9, 8.209, 8.906, 6.1, 10.409),
                    Block.box(1.806, 4, 5.309, 8.806, 6, 7.309),
                    Block.box(1.806, 4, 8.309, 8.806, 6, 10.309),
                    Block.box(2.9, 1.9, 3.9, 5.1, 4.1, 11.1),
                    Block.box(3, 2, 4, 5, 4, 11),
                    Block.box(6.9, 1.9, 3.9, 9.1, 4.1, 11.1),
                    Block.box(7, 2, 4, 9, 4, 11),
                    Block.box(8.9, 3.9, 3.9, 11.1, 6.1, 11.1),
                    Block.box(9, 4, 4, 11, 6, 11),
                    Block.box(9.9, 1.9, 3.9, 12.1, 4.1, 11.1),
                    Block.box(10, 2, 4, 12, 4, 11)
            ),
            shape(
                    Block.box(1, 0, 1, 15, 2, 15),
                    Block.box(1.706, 3.9, 5.209, 8.906, 6.1, 7.409),
                    Block.box(1.706, 3.9, 8.209, 8.906, 6.1, 10.409),
                    Block.box(1.806, 4, 5.309, 8.806, 6, 7.309),
                    Block.box(1.806, 4, 8.309, 8.806, 6, 10.309),
                    Block.box(2.9, 1.9, 3.9, 5.1, 4.1, 11.1),
                    Block.box(3, 2, 4, 5, 4, 11),
                    Block.box(3.9, 5.9, 3.9, 6.1, 8.1, 11.1),
                    Block.box(4, 6, 4, 6, 8, 11),
                    Block.box(6.9, 1.9, 3.9, 9.1, 4.1, 11.1),
                    Block.box(7, 2, 4, 9, 4, 11),
                    Block.box(7.9, 5.9, 3.9, 10.1, 8.1, 11.1),
                    Block.box(8, 6, 4, 10, 8, 11),
                    Block.box(8.9, 3.9, 3.9, 11.1, 6.1, 11.1),
                    Block.box(9, 4, 4, 11, 6, 11),
                    Block.box(9.9, 1.9, 3.9, 12.1, 4.1, 11.1),
                    Block.box(10, 2, 4, 12, 4, 11)
            )
    };

    private FoodShapes() {
    }

    private static VoxelShape shape(VoxelShape first, VoxelShape... rest) {
        return Shapes.or(first, rest);
    }
}

