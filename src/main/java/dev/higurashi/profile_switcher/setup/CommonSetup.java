package dev.higurashi.profile_switcher.setup;

import dev.higurashi.profile_switcher.ProfileSwitcher;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = ProfileSwitcher.MOD_ID)
public final class CommonSetup {
    @SubscribeEvent
    protected static void onSetup(final FMLCommonSetupEvent event) {

    }
}
