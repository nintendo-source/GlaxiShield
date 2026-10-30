    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        super.extractRenderState(graphics, mouseX, mouseY, delta);
        graphics.centeredText(font, "GlaxiShield", width / 2, 18, 0xFF00D9FF);
        graphics.centeredText(font, "Client-side shield customization", width / 2, 34, 0xFFFFFFFF);
        graphics.text(font, "Minecraft username", width / 2 - 100, 43, 0xFFBBBBBB);
        graphics.text(font, "Base color (HEX)", width / 2 - 100, 88, 0xFFBBBBBB);
        drawColorWheel(graphics);
        graphics.centeredText(font, "Color wheel · click to choose", width / 2, 260, 0xFFCCCCCC);
        graphics.centeredText(font, status, width / 2, height - 24, 0xFFFFFFFF);
    }

    private void drawColorWheel(GuiGraphicsExtractor graphics) {
        for (int y = -wheelRadius; y <= wheelRadius; y++) {
            for (int x = -wheelRadius; x <= wheelRadius; x++) {
                double distance = Math.sqrt(x * x + y * y) / wheelRadius;
                if (distance <= 1.0) {
                    float hue = (float) ((Math.atan2(y, x) / (Math.PI * 2) + 1.0) % 1.0);
                    float saturation = (float) Math.min(1.0, distance);
                    int rgb = Color.HSBtoRGB(hue, saturation, 1.0f);
                    graphics.fill(wheelX + x, wheelY + y, wheelX + x + 1, wheelY + y + 1, 0xFF000000 | (rgb & 0xFFFFFF));
                }
            }
        }
        graphics.fill(wheelX - 5, wheelY - 5, wheelX + 6, wheelY + 6, 0xFFFFFFFF);
        graphics.fill(wheelX - 3, wheelY - 3, wheelX + 4, wheelY + 4, selectedColor);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        double mouseX = event.x();
        double mouseY = event.y();
        double dx = mouseX - wheelX, dy = mouseY - wheelY;
        if (event.button() == 1 && Math.sqrt(dx * dx + dy * dy) <= wheelRadius) {
            float hue = (float) ((Math.atan2(dy, dx) / (Math.PI * 2) + 1.0) % 1.0);
            float saturation = (float) Math.min(1.0, Math.sqrt(dx * dx + dy * dy) / wheelRadius);
            selectedColor = Color.HSBtoRGB(hue, saturation, 1.0f);
            String hex = String.format("#%06X", selectedColor & 0xFFFFFF);
            hexField.setValue(hex);
            return true;
        }
        return super.mouseClicked(event, doubleClick);
    }
}
