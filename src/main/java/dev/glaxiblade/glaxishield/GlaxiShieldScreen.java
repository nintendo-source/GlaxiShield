
package dev.glaxiblade.glaxishield;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

import java.awt.Color;

public final class GlaxiShieldScreen extends Screen {
    private EditBox ignField;
    private EditBox hexField;
    private int selectedColor = Color.decode("#00D9FF").getRGB();
    private int wheelX, wheelY, wheelRadius = 48;
    private String status = "Enter a Minecraft username and choose a color.";

    public GlaxiShieldScreen() {
        super(Component.literal("GlaxiShield"));
    }

    @Override
    protected void init() {
        int center = this.width / 2;

        ignField = new EditBox(this.font, center - 100, 55, 200, 20,
                Component.literal("Minecraft IGN"));
        ignField.setMaxLength(16);
        ignField.setValue(GlaxiShieldConfig.ign);
        addRenderableWidget(ignField);

        hexField = new EditBox(this.font, center - 100, 100, 200, 20,
                Component.literal("HEX color"));
        hexField.setMaxLength(7);
        hexField.setValue(GlaxiShieldConfig.hex);
        addRenderableWidget(hexField);

        wheelX = center;
        wheelY = 205;

        addRenderableWidget(Button.builder(
                Component.literal("Save & Generate Shield Pack"),
                button -> save()
        ).bounds(center - 105, 280, 210, 24).build());

        addRenderableWidget(Button.builder(
                Component.literal("Reset defaults"),
                button -> {
                    ignField.setValue("Glaxiblade");
                    hexField.setValue("#00D9FF");
                    status = "Defaults restored. Press Save to generate.";
                }
        ).bounds(center - 105, 310, 210, 20).build());

        addRenderableWidget(Button.builder(
                Component.literal("Done"),
                button -> onClose()
        ).bounds(center - 40, 340, 80, 20).build());
    }

    private void save() {
        String ign = ignField.getValue().trim();
        String hex = hexField.getValue().trim();

        if (!ign.matches("[A-Za-z0-9_]{1,16}")) {
            status = "Invalid IGN. Use 1-16 letters, numbers or underscores.";
            return;
        }

        if (!GlaxiShieldConfig.validHex(hex)) {
            status = "Invalid HEX. Example: #00D9FF";
            return;
        }

        GlaxiShieldConfig.ign = ign;
        GlaxiShieldConfig.hex = hex.toUpperCase();
        GlaxiShieldConfig.save();

        new Thread(() -> {
            try {
                ShieldPackGenerator.generate(ign, hex);
                Minecraft.getInstance().execute(() ->
                        status = "Generated! Enable 'GlaxiShield Generated' in Resource Packs."
                );
            } catch (Exception exception) {
                Minecraft.getInstance().execute(() ->
                        status = "Generation failed: " + exception.getMessage()
                );
            }
        }, "GlaxiShield-skin-fetch").start();

        status = "Fetching skin and generating resource pack...";
    }

    @Override
    public void extractRenderState(
            GuiGraphicsExtractor graphics,
            int mouseX,
            int mouseY,
            float delta
    ) {
        super.extractRenderState(graphics, mouseX, mouseY, delta);

        graphics.centeredText(font, "GlaxiShield",
                width / 2, 18, 0xFF00D9FF);

        graphics.centeredText(font, "Client-side shield customization",
                width / 2, 34, 0xFFFFFFFF);

        graphics.text(font, "Minecraft username",
                width / 2 - 100, 43, 0xFFBBBBBB);

        graphics.text(font, "Base color (HEX)",
                width / 2 - 100, 88, 0xFFBBBBBB);

        drawColorWheel(graphics);

        graphics.centeredText(font, "Color wheel - click to choose",
                width / 2, 260, 0xFFCCCCCC);

        graphics.centeredText(font, status,
                width / 2, height - 24, 0xFFFFFFFF);
    }

    private void drawColorWheel(GuiGraphicsExtractor graphics) {
        for (int y = -wheelRadius; y <= wheelRadius; y++) {
            for (int x = -wheelRadius; x <= wheelRadius; x++) {
                double distance =
                        Math.sqrt(x * x + y * y) / wheelRadius;

                if (distance <= 1.0) {
                    float hue = (float) (
                            (Math.atan2(y, x) / (Math.PI * 2) + 1.0) % 1.0
                    );

                    float saturation =
                            (float) Math.min(1.0, distance);

                    int rgb = Color.HSBtoRGB(
                            hue, saturation, 1.0f
                    );

                    graphics.fill(
                            wheelX + x,
                            wheelY + y,
                            wheelX + x + 1,
                            wheelY + y + 1,
                            0xFF000000 | (rgb & 0xFFFFFF)
                    );
                }
            }
        }

        graphics.fill(
                wheelX - 5, wheelY - 5,
                wheelX + 6, wheelY + 6,
                0xFFFFFFFF
        );

        graphics.fill(
                wheelX - 3, wheelY - 3,
                wheelX + 4, wheelY + 4,
                selectedColor
        );
    }

    @Override
    public boolean mouseClicked(
            MouseButtonEvent event,
            boolean doubleClick
    ) {
        double mouseX = event.x();
        double mouseY = event.y();

        double dx = mouseX - wheelX;
        double dy = mouseY - wheelY;

        if (event.button() == 0 &&
                Math.sqrt(dx * dx + dy * dy) <= wheelRadius) {

            float hue = (float) (
                    (Math.atan2(dy, dx) / (Math.PI * 2) + 1.0) % 1.0
            );

            float saturation = (float) Math.min(
                    1.0,
                    Math.sqrt(dx * dx + dy * dy) / wheelRadius
            );

            selectedColor = Color.HSBtoRGB(
                    hue, saturation, 1.0f
            );

            String hex = String.format(
                    "#%06X",
                    selectedColor & 0xFFFFFF
            );

            hexField.setValue(hex);
            return true;
        }

        return super.mouseClicked(event, doubleClick);
    }
}
