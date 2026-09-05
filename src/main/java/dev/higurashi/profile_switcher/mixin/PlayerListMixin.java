package dev.higurashi.profile_switcher.mixin;

import dev.higurashi.profile_switcher.api.common.profile.Profile;
import dev.higurashi.profile_switcher.api.common.profile.data.ProfileDataManager;
import dev.higurashi.profile_switcher.api.util.PSFileUtils;
import dev.higurashi.profile_switcher.common.data.handler.PlayerNbtProfileHandler;
import dev.higurashi.profile_switcher.api.common.profile.LocalProfileManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.level.storage.ServerLevelData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;
import java.io.IOException;

@Mixin(PlayerList.class)
public class PlayerListMixin {
    @Shadow
    @Final
    private MinecraftServer server;

    @Inject(method = "save", at = @At("HEAD"))
    private void profile_switcher$save(ServerPlayer player, CallbackInfo ci) {
        Profile profile = LocalProfileManager.getActiveProfile();
        if (profile == null) return;
        ProfileDataManager.save(player, profile);
    }

    @Inject(method = "load", at = @At("RETURN"))
    private void profile_switcher$load(ServerPlayer player, CallbackInfoReturnable<CompoundTag> cir) {
        Profile profile = LocalProfileManager.getActiveProfile();
        if (profile == null) return;

        ProfileDataManager.load(player, profile);

        CompoundTag tag = cir.getReturnValue();
        if (tag == null) return;

        this.profile_switcher$editTag(tag, profile, player);
    }

    @Unique
    private void profile_switcher$editTag(CompoundTag tag, Profile profile, ServerPlayer player) {
        File worldDir = PSFileUtils.getProfileWorldDirectory(player.getServer(), profile.id().toString());
        File nbtDataDir = worldDir.toPath().resolve(PlayerNbtProfileHandler.ID.toString().replace(':', '_')).resolve(PlayerNbtProfileHandler.FILE_NAME).toFile();
        if (!nbtDataDir.exists()) return;

        String dimension = this.profile_switcher$getDimension(nbtDataDir);
        int gameType = this.profile_switcher$getGameType(nbtDataDir);

        if (dimension != null) {
            tag.putString("Dimension", dimension);
        } else {
            tag.putString("Dimension", player.level().dimension().location().toString());
        }

        if (gameType != -1) {
            tag.putInt("playerGameType", gameType);
        } else {
            tag.putInt("playerGameType", ((ServerLevelData) player.level().getLevelData()).getGameType().getId());
        }
    }

    @Unique
    private String profile_switcher$getDimension(File directory) {
        try {
            CompoundTag tag = NbtIo.readCompressed(directory);
            return tag.getString("Dimension");
        } catch (IOException e) {
            return null;
        }
    }

    @Unique
    private int profile_switcher$getGameType(File directory) {
        try {
            CompoundTag tag = NbtIo.readCompressed(directory);
            return tag.getInt("playerGameType");
        } catch (IOException e) {
            return -1;
        }
    }
}
