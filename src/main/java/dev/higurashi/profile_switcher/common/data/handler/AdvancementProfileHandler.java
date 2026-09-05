package dev.higurashi.profile_switcher.common.data.handler;

import dev.higurashi.profile_switcher.api.common.profile.data.IProfileDataHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.LevelResource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class AdvancementProfileHandler implements IProfileDataHandler {
    private static final Logger LOGGER = LogManager.getLogger();

    public static final String FILE_NAME = "advancements.json";

    @Override
    public ResourceLocation getId() {
        return ResourceLocation.parse("advancements");
    }

    @Override
    public void saveProfileData(ServerPlayer player, File characterDir) {
        if (player.getServer() == null) return;

        PlayerAdvancements advancements = player.getServer().getPlayerList().getPlayerAdvancements(player);
        advancements.save();

        File sourceFile = new File(player.getServer().getWorldPath(LevelResource.PLAYER_ADVANCEMENTS_DIR).toFile(), player.getUUID() + ".json");

        if (!sourceFile.exists()) {
            LOGGER.warn("Advancement file does not exist: {}", sourceFile);
            return;
        }

        File targetFile = new File(characterDir, FILE_NAME);

        try {
            Files.copy(sourceFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            LOGGER.error("Failed to save advancements data", e);
        }
    }

    @Override
    public void saveWorldData(ServerPlayer player, File worldDir) {

    }

    @Override
    public void loadProfileData(ServerPlayer player, File characterDir) {
        if (player.getServer() == null) return;

        File sourceFile = new File(characterDir, FILE_NAME);

        if (!sourceFile.exists()) {
            LOGGER.warn("Advancement profile file does not exist: {}", sourceFile);
            return;
        }

        File targetFile = new File(player.getServer().getWorldPath(LevelResource.PLAYER_ADVANCEMENTS_DIR).toFile(), player.getUUID() + ".json");

        try {
            Files.copy(sourceFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            PlayerAdvancements advancements = player.getAdvancements();
            advancements.reload(player.getServer().getAdvancements());
        } catch (Exception e) {
            LOGGER.error("Failed to load advancements data", e);
        }
    }

    @Override
    public void loadWorldData(ServerPlayer player, File worldDir) {

    }

    @Override
    public void createGlobalDefault(ServerPlayer player, File characterDir) {
        if (player.getServer() == null) return;

        File targetFile = new File(player.getServer().getWorldPath(LevelResource.PLAYER_ADVANCEMENTS_DIR).toFile(), player.getUUID() + ".json");

        try {
            Files.writeString(targetFile.toPath(), "{}");

            PlayerAdvancements advancements = player.getAdvancements();
            advancements.reload(player.getServer().getAdvancements());
        } catch (Exception e) {
            LOGGER.error("Failed to create default advancements data", e);
        }
    }
}
