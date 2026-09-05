package dev.higurashi.profile_switcher.api.common.profile.data;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.io.File;

public interface IProfileDataHandler {
    // Setting
    ResourceLocation getId();

    default int getPriority() {
        return 0;
    }

    // Save
    void saveProfileData(ServerPlayer player, File profileDir);

    void saveWorldData(ServerPlayer player, File worldDir);

    // Load
    default void load(ServerPlayer player, File profileDir, File worldDir) {
        if (this.containsData(profileDir)) this.loadProfileData(player, profileDir);
        else this.createGlobalDefault(player, profileDir);

        if (this.containsData(worldDir)) this.loadWorldData(player, worldDir);
        else this.createWorldDefault(player, worldDir);
    }

    void loadProfileData(ServerPlayer player, File profileDir);

    void loadWorldData(ServerPlayer player, File worldDir);

    // Initialize
    default void createGlobalDefault(ServerPlayer player, File profileDir) {}

    default void createWorldDefault(ServerPlayer player, File worldDir) {}


    default boolean containsData(File directory) {
        File[] files = directory.listFiles();
        return files != null && files.length > 0;
    }
}
