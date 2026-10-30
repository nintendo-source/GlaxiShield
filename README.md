# GlaxiShield — by Glaxiblade

A client-side Fabric mod project for **Minecraft Java 26.1.1**. Press **G** in-game to enter a Minecraft IGN, type a HEX color, or pick a color from the wheel. The default configuration is `Glaxiblade` and `#00D9FF`.

## What it does

- Fetches the selected Java Edition player's skin from Mojang's public profile/session services.
- Crops the front head layer (including the hat layer) from the skin.
- Generates a shield texture using the selected base color and the player's head.
- Writes a resource pack to `.minecraft/resourcepacks/GlaxiShield Generated`.
- Stores the selected IGN and color in `.minecraft/config/glaxishield.json`.

**Important client-side limitation:** the generated resource pack must be enabled once in Minecraft under Options → Resource Packs. It replaces the vanilla shield base textures locally, so it affects shields rendered on your client and does not synchronize your appearance to other players. The generated pack is intentionally separate from the mod JAR so you can disable it at any time.

## Requirements

- Minecraft Java Edition **26.1.1**
- Fabric Loader **0.18.4 or newer**
- Fabric API **0.145.4+26.1.1**
- Java **25** (both `java -version` and your IDE's Gradle JVM)
- Gradle 9.4+ (the Gradle wrapper is recommended)

## Build on Windows

1. Install a JDK 25 distribution (for example, Eclipse Temurin 25).
2. Install IntelliJ IDEA 2025.3+ or use a terminal.
3. Open this `GlaxiShield` folder as a Gradle project.
4. Set the Gradle JVM to Java 25.
5. Run `gradle build` from this folder.
6. The mod JAR will be in `build/libs/`.
7. Copy the JAR into `.minecraft/mods/` alongside Fabric API.
8. Launch the **Fabric 26.1.1** profile and press **G**.

If you use the Gradle wrapper, run `gradlew.bat build` instead. The wrapper scripts/JAR are not bundled in this source archive; generate them once with `gradle wrapper --gradle-version 9.4.0`.

## First use

1. Launch Minecraft with the mod and Fabric API installed.
2. Press **G**.
3. Keep `Glaxiblade` or enter another Java username.
4. Enter a HEX color such as `#00D9FF`, or click the color wheel.
5. Click **Save & Generate Shield Pack** and wait for the completion message.
6. Open Options → Resource Packs and enable **GlaxiShield Generated**.
7. Hold a shield or view it in an inventory to see the custom texture.

## Notes

- A valid online Java profile is required to fetch a skin. Offline/cracked usernames without a Mojang profile cannot be resolved through these endpoints.
- The game must have internet access when generating a new skin texture.
- This initial build generates a simple custom shield artwork texture. It is not a per-player multiplayer cosmetic and does not alter the server.
- The current project is configured specifically for 26.1.1. Do not install its JAR on 1.21.x or other Minecraft versions.

## License

MIT
