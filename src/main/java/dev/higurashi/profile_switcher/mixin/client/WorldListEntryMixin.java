package dev.higurashi.profile_switcher.mixin.client;

import dev.higurashi.profile_switcher.api.common.profile.LocalProfileManager;
import dev.higurashi.profile_switcher.api.common.profile.Profile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.AlertScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.client.gui.screens.worldselection.WorldSelectionList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.storage.LevelSummary;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldSelectionList.WorldListEntry.class)
public class WorldListEntryMixin {
    @Shadow @Final
    private LevelSummary summary;

    @Shadow @Final
    private Minecraft minecraft;

    @Shadow @Final
    private SelectWorldScreen screen;

    @Inject(method = "joinWorld", at = @At(value = "HEAD"), cancellable = true)
    public void profile_switcher$onJoinWorld(CallbackInfo ci) {
        Profile profile = LocalProfileManager.getActiveProfile();

        boolean playerCheat = profile.allowCheat();
        boolean worldCheat = this.summary.hasCheats();
        boolean sameGameMode = profile.gameMode().equals(this.summary.getGameMode());

        if (playerCheat != worldCheat) {
            Component message = playerCheat
                    ? Component.translatable("screen.profile_switcher.join_world.warn2")
                    : Component.translatable("screen.profile_switcher.join_world.warn3");

            this.minecraft.setScreen(new AlertScreen(() -> this.minecraft.setScreen(this.screen), Component.translatable("screen.profile_switcher.join_world.title"), message));
            ci.cancel();
            return;
        }

        if (playerCheat && worldCheat) return;

        if (!sameGameMode) {
            this.minecraft.setScreen(new AlertScreen(() -> this.minecraft.setScreen(this.screen), Component.translatable("screen.profile_switcher.join_world.title"), Component.translatable("screen.profile_switcher.join_world.warn1")));
            ci.cancel();
        }
    }
}
