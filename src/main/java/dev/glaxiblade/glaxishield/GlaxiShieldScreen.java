package dev.glaxiblade.glaxishield;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import java.awt.Color;

public final class GlaxiShieldScreen extends Screen {
    private EditBox ignField, hexField;
    private int selectedColor = Color.decode("#00D9FF").getRGB();
    private int wheelX, wheelY, wheelRadius = 36;
    private String status = "Enter your IGN and choose a color.";

    public GlaxiShieldScreen() { super(Component.literal("GlaxiID")); }

    @Override
    protected void init() {
        int c = width / 2;
        ignField = new EditBox(font, c - 100, 55, 200, 20, Component.literal("Minecraft IGN"));
        ignField.setMaxLength(16);
        ignField.setValue(GlaxiShieldConfig.ign);
        addRenderableWidget(ignField);

        hexField = new EditBox(font, c - 100, 100, 200, 20, Component.literal("HEX color"));
        hexField.setMaxLength(7);
        hexField.setValue(GlaxiShieldConfig.hex);
        addRenderableWidget(hexField);

        wheelX = c;
        wheelRadius = Math.max(22, Math.min(36, (height - 220) / 2));
        wheelY = Math.max(155, height - 140);

        addRenderableWidget(Button.builder(Component.literal("▣  SAVE CHANGES"), b -> save())
                .bounds(c - 105, height - 72, 210, 22).build());
        addRenderableWidget(Button.builder(Component.literal("Reset defaults"), b -> {
            ignField.setValue("Glaxiblade");
            hexField.setValue("#00D9FF");
            status = "Defaults restored. Press SAVE CHANGES.";
        }).bounds(c - 105, height - 47, 102, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Done"), b -> onClose())
                .bounds(c + 3, height - 47, 102, 20).build());
    }

    private void save() {
        String ign = ignField.getValue().trim();
        String hex = hexField.getValue().trim();
        if (!ign.matches("[A-Za-z0-9_]{1,16}")) {
            status = "Invalid IGN: use 1-16 letters, numbers or underscores.";
            return;
        }
        if (!GlaxiShieldConfig.validHex(hex)) {
            status = "Invalid HEX. Example: #00D9FF";
            return;
        }
        GlaxiShieldConfig.ign = ign;
        GlaxiShieldConfig.hex = hex.toUpperCase();
        GlaxiShieldConfig.save();
        status = "Saved locally. Visual overrides are still being built.";
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor g, int mouseX, int mouseY, float delta) {
        super.extractRenderState(g, mouseX, mouseY, delta);
        g.centeredText(font, "GlaxiID", width / 2, 18, 0xFF00D9FF);
        g.centeredText(font, "Client-side identity profiles", width / 2, 34, 0xFFFFFFFF);
        g.text(font, "Minecraft username", width / 2 - 100, 43, 0xFFBBBBBB);
        g.text(font, "Base color (HEX)", width / 2 - 100, 88, 0xFFBBBBBB);
        drawWheel(g);
        net.minecraft.world.entity.player.Player target = TargetTracker.getCurrentTarget();
        String targetLabel = target == null
                ? "Nearest player: none within 32 blocks"
                : "Nearest player: " + target.getName().getString()
                    + String.format(" (%.1f blocks)", Math.sqrt(target.distanceToSqr(net.minecraft.client.Minecraft.getInstance().player)));
        g.centeredText(font, targetLabel, width / 2, height - 104, 0xFF00D9FF);
        g.centeredText(font, status, width / 2, height - 88, 0xFFFFFFFF);
    }

    private void drawWheel(GuiGraphicsExtractor g) {
        for (int y = -wheelRadius; y <= wheelRadius; y++) {
            for (int x = -wheelRadius; x <= wheelRadius; x++) {
                double d = Math.sqrt(x * x + y * y) / wheelRadius;
                if (d <= 1) {
                    float h = (float)((Math.atan2(y, x) / (Math.PI * 2) + 1) % 1);
                    int rgb = Color.HSBtoRGB(h, (float)Math.min(1, d), 1);
                    g.fill(wheelX+x, wheelY+y, wheelX+x+1, wheelY+y+1, 0xFF000000 | (rgb & 0xFFFFFF));
                }
            }
        }
        g.fill(wheelX-5, wheelY-5, wheelX+6, wheelY+6, 0xFFFFFFFF);
        g.fill(wheelX-3, wheelY-3, wheelX+4, wheelY+4, selectedColor);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        double dx = event.x() - wheelX, dy = event.y() - wheelY;
        if (event.button() == 0 && Math.sqrt(dx*dx + dy*dy) <= wheelRadius) {
            float h = (float)((Math.atan2(dy, dx) / (Math.PI * 2) + 1) % 1);
            float sat = (float)Math.min(1, Math.sqrt(dx*dx + dy*dy) / wheelRadius);
            selectedColor = Color.HSBtoRGB(h, sat, 1);
            hexField.setValue(String.format("#%06X", selectedColor & 0xFFFFFF));
            return true;
        }
        return super.mouseClicked(event, doubleClick);
    }
}
