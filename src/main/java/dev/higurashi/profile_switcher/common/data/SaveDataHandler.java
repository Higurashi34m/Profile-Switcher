package dev.higurashi.profile_switcher.common.data;

import dev.higurashi.profile_switcher.api.common.profile.data.ProfileDataManager;
import dev.higurashi.profile_switcher.api.common.profile.LocalProfileManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class SaveDataHandler {
    @SubscribeEvent
    protected static void onSave(LevelEvent.Save event) {
        event.getLevel().players().forEach(player -> {
            if (!(player instanceof ServerPlayer serverPlayer)) return;
            ProfileDataManager.save(serverPlayer, LocalProfileManager.getActiveProfile());
        });
    }
}
