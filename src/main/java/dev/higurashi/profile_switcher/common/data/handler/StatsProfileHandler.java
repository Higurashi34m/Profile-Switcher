package dev.higurashi.profile_switcher.common.data.handler;

import dev.higurashi.profile_switcher.api.common.profile.data.IProfileDataHandler;
import dev.higurashi.profile_switcher.mixin.StatsCounterAccessor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.ServerStatsCounter;
import net.minecraft.world.level.storage.LevelResource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class StatsProfileHandler implements IProfileDataHandler {
    private static final Logger LOGGER = LogManager.getLogger();

    public static final String FILE_NAME = "stats.json";
    public static final ResourceLocation ID = ResourceLocation.parse("stats");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public void saveProfileData(ServerPlayer player, File characterDir) {
        if (player.getServer() == null) return;

        ServerStatsCounter statsCounter = player.getServer().getPlayerList().getPlayerStats(player);
        statsCounter.save();

        File sourceFile = new File(player.getServer().getWorldPath(LevelResource.PLAYER_STATS_DIR).toFile(), player.getUUID() + ".json");

        if (!sourceFile.exists()) {
            LOGGER.warn("Stats file does not exist: {}", sourceFile);
            return;
        }

        File targetFile = new File(characterDir, FILE_NAME);

        try {
            Files.copy(sourceFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            LOGGER.error("Failed to save stats data", e);
        }
    }

    @Override
    public void saveWorldData(ServerPlayer player, File worldDir) {

    }

    @Override
    public void loadProfileData(ServerPlayer player, File profileDir) {
        if (player.getServer() == null) return;

        File sourceFile = new File(profileDir, FILE_NAME);
        if (!sourceFile.exists()) {
            LOGGER.warn("Stats profile file does not exist: {}", sourceFile);
            return;
        }

        File targetFile = new File(player.getServer().getWorldPath(LevelResource.PLAYER_STATS_DIR).toFile(), player.getUUID() + ".json");
        try {
            File parent = targetFile.getParentFile();
            if (parent != null && !parent.exists()) parent.mkdirs();

            Files.copy(sourceFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            ServerStatsCounter statsCounter = player.getServer().getPlayerList().getPlayerStats(player);

            String fileContent = Files.readString(targetFile.toPath());
            statsCounter.parseLocal(player.getServer().getFixerUpper(), fileContent);
            statsCounter.markAllDirty();
        } catch (Exception e) {
            LOGGER.error("Failed to load stats data", e);
        }
    }

    @Override
    public void loadWorldData(ServerPlayer player, File worldDir) {

    }

    @Override
    public void createGlobalDefault(ServerPlayer player, File characterDir) {
        if (player.getServer() == null) return;

        ServerStatsCounter statsCounter = player.getServer().getPlayerList().getPlayerStats(player);
        ((StatsCounterAccessor) statsCounter).getStats().clear();
    }
}
