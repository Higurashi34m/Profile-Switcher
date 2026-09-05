package dev.higurashi.profile_switcher.setup;

import dev.higurashi.profile_switcher.ProfileSwitcher;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD, modid = ProfileSwitcher.MOD_ID)
public final class ClientSetup {
    @SubscribeEvent
    protected static void onSetup(final FMLClientSetupEvent event) {

    }
}
