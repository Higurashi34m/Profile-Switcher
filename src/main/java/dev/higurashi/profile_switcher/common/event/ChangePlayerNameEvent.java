package dev.higurashi.profile_switcher.common.event;

import dev.higurashi.profile_switcher.ProfileSwitcher;
import dev.higurashi.profile_switcher.api.common.profile.LocalProfileManager;
import net.minecraft.network.chat.Component;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, modid = ProfileSwitcher.MOD_ID)
public class ChangePlayerNameEvent {
    @SubscribeEvent
    public static void onNameFormat(PlayerEvent.NameFormat event) {
        event.setDisplayname(Component.literal(LocalProfileManager.getActiveProfile().name()));
    }

    @SubscribeEvent
    public static void onTabNameFormat(PlayerEvent.TabListNameFormat event) {
        event.setDisplayName(Component.literal(LocalProfileManager.getActiveProfile().name()));
    }
}
