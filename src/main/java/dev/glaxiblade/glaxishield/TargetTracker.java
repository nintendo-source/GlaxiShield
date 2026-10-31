package dev.glaxiblade.glaxishield;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

import java.util.Comparator;
import java.util.UUID;

/**
 * Resolves the player whose local identity profile should be displayed.
 *
 * A manually selected UUID always wins while that player is present and in
 * range. Clearing the manual selection restores automatic nearest-player mode.
 * This class only reads client world state; it never sends packets.
 */
public final class TargetTracker {
    public static final double DEFAULT_RANGE_BLOCKS = 32.0;

    private static UUID manuallySelected;
    private static Player currentTarget;

    private TargetTracker() {}

    public static void select(UUID playerId) {
        manuallySelected = playerId;
        refresh(Minecraft.getInstance());
    }

    public static void clearSelection() {
        manuallySelected = null;
        refresh(Minecraft.getInstance());
    }

    public static UUID getManuallySelectedId() {
        return manuallySelected;
    }

    public static Player getCurrentTarget() {
        return currentTarget;
    }

    public static boolean isAutomatic() {
        return manuallySelected == null;
    }

    public static void refresh(Minecraft client) {
        currentTarget = null;

        if (client == null || client.level == null || client.player == null) {
            return;
        }

        final Player localPlayer = client.player;
        final double maxDistanceSquared = DEFAULT_RANGE_BLOCKS * DEFAULT_RANGE_BLOCKS;

        if (manuallySelected != null) {
            Player selected = client.level.players().stream()
                    .filter(player -> !player.getUUID().equals(localPlayer.getUUID()))
                    .filter(player -> player.getUUID().equals(manuallySelected))
                    .filter(player -> player.distanceToSqr(localPlayer) <= maxDistanceSquared)
                    .findFirst()
                    .orElse(null);

            if (selected != null) {
                currentTarget = selected;
                return;
            }

            // A manual target that left the world/range is no longer valid.
            manuallySelected = null;
        }

        currentTarget = client.level.players().stream()
                .filter(player -> !player.getUUID().equals(localPlayer.getUUID()))
                .filter(player -> player.distanceToSqr(localPlayer) <= maxDistanceSquared)
                .min(Comparator.comparingDouble(player -> player.distanceToSqr(localPlayer)))
                .orElse(null);
    }
}
