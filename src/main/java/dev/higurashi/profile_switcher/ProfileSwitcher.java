package dev.higurashi.profile_switcher;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ProfileSwitcher.MOD_ID)
public class ProfileSwitcher {
    public static final String MOD_ID = "profile_switcher";

    public ProfileSwitcher(FMLJavaModLoadingContext context) {
        IEventBus eventBus = context.getModEventBus();
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
