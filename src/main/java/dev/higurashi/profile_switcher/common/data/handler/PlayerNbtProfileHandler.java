package dev.higurashi.profile_switcher.common.data.handler;

import com.mojang.authlib.GameProfile;
import dev.higurashi.profile_switcher.api.common.profile.data.IProfileDataHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.UUID;

public class PlayerNbtProfileHandler implements IProfileDataHandler {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Set<String> WORLD_KEYS = Set.of(
            "Pos",
            "Rotation",
            "Dimension",
            "SpawnX",
            "SpawnY",
            "SpawnZ",
            "SpawnDimension",
            "SpawnForced",
            "SpawnAngle",
            "LastDeathLocation",
            "playerGameType"
    );

    public static final ResourceLocation ID = ResourceLocation.parse("player_nbt");
    public static final String FILE_NAME = "player_data.dat";

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    // Save
    @Override
    public void saveProfileData(ServerPlayer player, File profileDir) {
        CompoundTag tag = player.saveWithoutId(new CompoundTag());
        WORLD_KEYS.forEach(tag::remove);

        save(tag, new File(profileDir, FILE_NAME));
    }

    @Override
    public void saveWorldData(ServerPlayer player, File worldDir) {
        CompoundTag tag = player.saveWithoutId(new CompoundTag());
        tag.getAllKeys().stream().filter(key -> !WORLD_KEYS.contains(key)).forEach(tag::remove);

        save(tag, new File(worldDir, FILE_NAME));
    }

    private void save(CompoundTag tag, File file) {
        try {
            NbtIo.writeCompressed(tag, file);
        } catch (IOException e) {
            LOGGER.error("Failed to save player NBT: {}", file, e);
        }
    }

    @Override public void loadProfileData(ServerPlayer player, File profileDir) {}
    @Override public void loadWorldData(ServerPlayer player, File worldDir) {}

    // Load
    @Override
    public void load(ServerPlayer player, File profileDir, File worldDir) {
        File profileFile = new File(profileDir, FILE_NAME);
        File worldFile = new File(worldDir, FILE_NAME);

        CompoundTag profileTag;
        CompoundTag worldTag;

        try {
            if (profileFile.exists()) {
                profileTag = NbtIo.readCompressed(profileFile);
            } else {
                profileTag = this.createFreshPlayerTag(player);
            }

            if (worldFile.exists()) {
                worldTag = NbtIo.readCompressed(worldFile);
            } else {
                worldTag = this.createFreshWorldTag(player);
            }

            this.applyWorldData(profileTag, worldTag);

            player.getActiveEffectsMap().values().forEach(effect -> effect.getEffect().removeAttributeModifiers(player, player.getAttributes(), effect.getAmplifier()));
            player.getActiveEffectsMap().clear();

            player.load(profileTag);
        } catch (IOException e) {
            LOGGER.error("Failed to load player NBT", e);
        }
    }

    private CompoundTag createFreshPlayerTag(ServerPlayer player) {
        GameProfile profile = new GameProfile(UUID.randomUUID(), "ProfileSwitcherDummy");
        ServerPlayer fresh = new ServerPlayer(player.server, player.server.overworld(), profile);

        CompoundTag tag = fresh.saveWithoutId(new CompoundTag());
        tag.putUUID("UUID", player.getUUID());

        return tag;
    }

    private CompoundTag createFreshWorldTag(ServerPlayer player) {
        CompoundTag tag = new CompoundTag();
        ServerLevel level = player.serverLevel();

        BlockPos spawnPos = level.getSharedSpawnPos();
        float spawnAngle = level.getSharedSpawnAngle();

        ListTag posList = new ListTag();
        posList.add(DoubleTag.valueOf(spawnPos.getX()));
        posList.add(DoubleTag.valueOf(spawnPos.getY()));
        posList.add(DoubleTag.valueOf(spawnPos.getZ()));
        tag.put("Pos", posList);

        ListTag rotationList = new ListTag();
        rotationList.add(FloatTag.valueOf(spawnAngle));
        rotationList.add(FloatTag.valueOf(0.0f));
        tag.put("Rotation", rotationList);

        tag.putString("Dimension", level.dimension().location().toString());

        return tag;
    }

    private void applyWorldData(CompoundTag profileTag, CompoundTag worldTag) {
        WORLD_KEYS.forEach(key -> {
            if (worldTag.contains(key)) {
                profileTag.put(key, worldTag.get(key).copy());
            } else {
                profileTag.remove(key);
            }
        });
    }
}