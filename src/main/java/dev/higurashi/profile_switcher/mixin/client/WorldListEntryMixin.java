package dev.higurashi.profile_switcher.mixin.client;

import dev.higurashi.profile_switcher.api.common.profile.Profile;
import dev.higurashi.profile_switcher.api.common.profile.LocalProfileManager;
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

        if (!profile.gameMode().equals(this.summary.getGameMode())) {
            if (!(profile.allowCheat() && this.summary.hasCheats())) {
                this.minecraft.setScreen(new AlertScreen(() -> this.minecraft.setScreen(this.screen), Component.literal("はいれないよ"), Component.literal("ゲームモード違うでしょ\n 同じゲームモードかワールドもプロファイルもチート使えないと入れないよ")));
                ci.cancel();
            }
        }

        if (profile.allowCheat()) {
            if (!this.summary.hasCheats()) {
                this.minecraft.setScreen(new AlertScreen(() -> this.minecraft.setScreen(this.screen), Component.literal("はいれないよ"), Component.literal("あんたチート使ってるでしょ")));
                ci.cancel();
            }
        } else {
            if (this.summary.hasCheats()) {
                this.minecraft.setScreen(new AlertScreen(() -> this.minecraft.setScreen(this.screen), Component.literal("はいれないよ"), Component.literal("あんたチート使えないでしょ")));
                ci.cancel();
            }
        }
    }
}
