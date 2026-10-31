package dev.glaxiblade.glaxishield;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

public final class GlaxiShieldClient implements ClientModInitializer {
    private static KeyMapping openScreenKey;
    private static final KeyMapping.Category CATEGORY = KeyMapping.Category.register(
            Identifier.fromNamespaceAndPath("glaxishield", "main"));

    @Override
    public void onInitializeClient() {
        GlaxiShieldConfig.load();
        openScreenKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.glaxishield.open", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_G, CATEGORY));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            TargetTracker.refresh(client);
            while (openScreenKey.consumeClick()) {
                if (client.screen == null) client.setScreen(new GlaxiShieldScreen());
            }
        });
    }
}
