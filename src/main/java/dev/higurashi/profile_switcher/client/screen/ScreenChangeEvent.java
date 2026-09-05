package dev.higurashi.profile_switcher.client.screen;

import dev.higurashi.profile_switcher.ProfileSwitcher;
import dev.higurashi.profile_switcher.api.util.PSFileUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE, modid = ProfileSwitcher.MOD_ID)
public final class ScreenChangeEvent {
    private ScreenChangeEvent() {}

    private static boolean profileSelected;

    public static void profileSelected() {
        profileSelected = true;
    }

    @SubscribeEvent
    public static void onScreenChange(ScreenEvent.Opening event) {
        if (event.getScreen() instanceof TitleScreen) {
            profileSelected = false;
            return;
        }

        if (!(event.getNewScreen() instanceof SelectWorldScreen)) return;

        if (event.getScreen() instanceof SelectWorldScreen && profileSelected) return;

        Minecraft minecraft = Minecraft.getInstance();
        if (PSFileUtils.loadProfiles(minecraft.gameDirectory).isEmpty()) {
            event.setNewScreen(new CreateProfileScreen(minecraft.screen));
            return;
        }

        event.setNewScreen(new SelectProfileScreen(minecraft.screen));
    }
}