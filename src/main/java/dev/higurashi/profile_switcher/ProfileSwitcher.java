package dev.higurashi.profile_switcher;

import dev.higurashi.profile_switcher.api.common.profile.data.ProfileDataManager;
import dev.higurashi.profile_switcher.common.data.handler.AdvancementProfileHandler;
import dev.higurashi.profile_switcher.common.data.handler.PlayerNbtProfileHandler;
import dev.higurashi.profile_switcher.common.data.handler.StatsProfileHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ProfileSwitcher.MOD_ID)
public class ProfileSwitcher {
    public static final String MOD_ID = "profile_switcher";

    public ProfileSwitcher(FMLJavaModLoadingContext context) {
        IEventBus eventBus = context.getModEventBus();

        ProfileDataManager.registerHandler(new PlayerNbtProfileHandler());
        ProfileDataManager.registerHandler(new AdvancementProfileHandler());
        ProfileDataManager.registerHandler(new StatsProfileHandler());
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
