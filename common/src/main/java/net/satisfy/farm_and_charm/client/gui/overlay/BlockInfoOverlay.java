package net.satisfy.farm_and_charm.client.gui.overlay;

import dev.architectury.event.events.client.ClientGuiEvent;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.TooltipRenderUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public final class BlockInfoOverlay {
    private static final int SEPARATOR_HEIGHT = 9;
    private static final int MIN_WIDTH = 56;
    private static final int COLOR_SEPARATOR = 0x40FFFFFF;
    private static final int PANEL_GAP = 6;
    private static final int SCREEN_MARGIN = 4;
    private static final double ABOVE_OFFSET = 0.45;
    private static final double SIDE_OFFSET = 0.7;
    private static final float[] ABOVE_SCALES = {1.0F, 0.8F, 0.65F};
    private static final float SIDE_SCALE = 0.8F;

    private static final List<BlockInfoProvider> PROVIDERS = new ArrayList<>();
    private static final List<Supplier<Collection<BlockPos>>> TRACKERS = new ArrayList<>();
    private static boolean initialized;

    private BlockInfoOverlay() {
    }

    public static void init() {
        if (!initialized) {
            initialized = true;
            ClientGuiEvent.RENDER_HUD.register(BlockInfoOverlay::render);
        }
    }

    public static void registerProvider(BlockInfoProvider provider) {
        PROVIDERS.add(provider);
    }

    public static void registerTracker(Supplier<Collection<BlockPos>> tracker) {
        TRACKERS.add(tracker);
    }

    private static void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null || minecraft.player == null || minecraft.options.hideGui) {
            return;
        }
        Level level = minecraft.level;
        BlockPos targeted = null;
        if (minecraft.hitResult instanceof BlockHitResult hit && hit.getType() == HitResult.Type.BLOCK) {
            targeted = hit.getBlockPos();
            drawFirst(graphics, minecraft, level, targeted, hit);
        }
        for (Supplier<Collection<BlockPos>> tracker : TRACKERS) {
            for (BlockPos pos : tracker.get()) {
                if (!pos.equals(targeted)) {
                    drawFirst(graphics, minecraft, level, pos, null);
                }
            }
        }
    }

    private static void drawFirst(GuiGraphics graphics, Minecraft minecraft, Level level, BlockPos pos, BlockHitResult hit) {
        BlockState state = level.getBlockState(pos);
        for (BlockInfoProvider provider : PROVIDERS) {
            List<InfoSection> sections = provider.describe(level, pos, state, hit);
            if (!sections.isEmpty()) {
                drawPanel(graphics, minecraft, pos, state, provider, sections);
                return;
            }
        }
    }

    private static void drawPanel(GuiGraphics graphics, Minecraft minecraft, BlockPos pos, BlockState state, BlockInfoProvider provider, List<InfoSection> sections) {
        VoxelShape shape = state.getShape(minecraft.level, pos);
        double top = shape.isEmpty() ? 1.0 : shape.max(Direction.Axis.Y);
        Vec3 center = Vec3.atBottomCenterOf(pos).add(0.0, top / 2.0, 0.0);
        Optional<float[]> above = project(minecraft, graphics, Vec3.atBottomCenterOf(pos).add(0.0, top + ABOVE_OFFSET, 0.0));
        Optional<float[]> middle = project(minecraft, graphics, center);
        if (above.isEmpty() || middle.isEmpty()) {
            return;
        }
        Font font = minecraft.font;
        int width = Math.max(MIN_WIDTH, sections.stream().mapToInt(section -> section.width(font)).max().orElse(0));
        int height = 0;
        for (int i = 0; i < sections.size(); i++) {
            height += sections.get(i).height() + (i > 0 ? SEPARATOR_HEIGHT : 0);
        }
        float scale = SIDE_SCALE;
        float x;
        float y = -1.0F;
        for (float candidate : ABOVE_SCALES) {
            float panelTop = above.get()[1] - height * candidate - PANEL_GAP;
            if (panelTop >= SCREEN_MARGIN) {
                scale = candidate;
                y = panelTop;
                break;
            }
        }
        if (y >= 0.0F) {
            x = above.get()[0] - width * scale / 2.0F;
        } else {
            Optional<float[]> edge = project(minecraft, graphics, center.add(new Vec3(minecraft.gameRenderer.getMainCamera().getLeftVector()).scale(-SIDE_OFFSET)));
            x = (edge.isPresent() ? edge.get()[0] : middle.get()[0] + width * scale) + PANEL_GAP;
            y = middle.get()[1] - height * scale / 2.0F;
        }
        x = Math.clamp(x, SCREEN_MARGIN, graphics.guiWidth() - width * scale - SCREEN_MARGIN);
        y = Math.clamp(y, SCREEN_MARGIN, graphics.guiHeight() - height * scale - SCREEN_MARGIN);
        graphics.pose().pushPose();
        graphics.pose().translate(x, y, 0.0F);
        graphics.pose().scale(scale, scale, 1.0F);
        provider.beforeBackground(minecraft.level, pos, state);
        TooltipRenderUtil.renderTooltipBackground(graphics, 0, 0, width, height, 0);
        int cursor = 0;
        for (int i = 0; i < sections.size(); i++) {
            if (i > 0) {
                graphics.fill(4, cursor + SEPARATOR_HEIGHT / 2, width - 4, cursor + SEPARATOR_HEIGHT / 2 + 1, COLOR_SEPARATOR);
                cursor += SEPARATOR_HEIGHT;
            }
            sections.get(i).draw(graphics, font, cursor, width);
            cursor += sections.get(i).height();
        }
        graphics.pose().popPose();
    }

    private static Optional<float[]> project(Minecraft minecraft, GuiGraphics graphics, Vec3 point) {
        Camera camera = minecraft.gameRenderer.getMainCamera();
        if (!camera.isInitialized() || minecraft.player == null) {
            return Optional.empty();
        }
        Vec3 offset = point.subtract(camera.getPosition());
        Vector3f look = camera.getLookVector();
        Vector3f up = camera.getUpVector();
        Vector3f left = camera.getLeftVector();
        double depth = offset.x * look.x() + offset.y * look.y() + offset.z * look.z();
        if (depth <= 0.05) {
            return Optional.empty();
        }
        double side = (offset.x * left.x() + offset.y * left.y() + offset.z * left.z()) / depth;
        double vertical = (offset.x * up.x() + offset.y * up.y() + offset.z * up.z()) / depth;
        double fov = minecraft.options.fov().get() * minecraft.player.getFieldOfViewModifier();
        double scale = (graphics.guiHeight() / 2.0) / Math.tan(Math.toRadians(fov) / 2.0);
        return Optional.of(new float[]{(float) (graphics.guiWidth() / 2.0 - side * scale), (float) (graphics.guiHeight() / 2.0 - vertical * scale)});
    }
}
