package dev.higurashi.profile_switcher.common.event;

import dev.higurashi.profile_switcher.ProfileSwitcher;
import dev.higurashi.profile_switcher.api.common.profile.LocalProfileManager;
import dev.higurashi.profile_switcher.api.common.profile.Profile;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.GameType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE, modid = ProfileSwitcher.MOD_ID)
public class KickWhenJoinEvent {
    @SubscribeEvent
    public static void onClientLoggingIn(ClientPlayerNetworkEvent.LoggingIn event) {
        Profile profile = LocalProfileManager.getActiveProfile();
        Minecraft mc = Minecraft.getInstance();

        if (mc.getSingleplayerServer() != null) {
            boolean worldCheat = mc.getSingleplayerServer().getWorldData().getAllowCommands();
            GameType worldMode = mc.getSingleplayerServer().getDefaultGameType();

            boolean playerCheat = profile.allowCheat();
            boolean sameGameMode = profile.gameMode().equals(worldMode);

            boolean isAllowed = false;

            if (playerCheat == worldCheat) {
                if (playerCheat && worldCheat) {
                    isAllowed = true;
                } else {
                    isAllowed = sameGameMode;
                }
            }

            if (!isAllowed) {
                Component message;

                if (playerCheat != worldCheat) {
                    message = playerCheat
                            ? Component.translatable("screen.profile_switcher.join_world.warn2")
                            : Component.translatable("screen.profile_switcher.join_world.warn3");
                } else {
                    message = Component.translatable("screen.profile_switcher.join_world.warn1");
                }

                event.getConnection().disconnect(message);
            }
        }
    }
}
