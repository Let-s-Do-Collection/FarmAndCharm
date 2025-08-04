package net.satisfy.farm_and_charm.core.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

public interface IMultiBlockEntityContainer {

    BlockPos getController();

    <T extends BlockEntity & IMultiBlockEntityContainer> T getControllerBE();

    boolean isController();

    void setController(BlockPos pos);

    void removeController(boolean keepContents);

    void preventConnectivityUpdate();

    void notifyMultiUpdated();

    @Nullable
    default Object getExtraData() {
        return null;
    }

    default void setExtraData(@Nullable Object data) {
    }

    default Object modifyExtraData(Object data) {
        return data;
    }


    Direction.Axis getMainConnectionAxis();

    int getMaxLength(Direction.Axis longAxis, int width);

    int getMaxWidth();

    int getHeight();

    void setHeight(int height);

    int getWidth();

    void setWidth(int width);

    interface Inventory extends IMultiBlockEntityContainer {
        default boolean hasInventory() {
            return false;
        }
    }
}
