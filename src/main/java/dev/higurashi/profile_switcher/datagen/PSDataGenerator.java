package dev.higurashi.profile_switcher.datagen;

import dev.higurashi.profile_switcher.ProfileSwitcher;
import dev.higurashi.profile_switcher.datagen.client.lang.EnUsLanguageProvider;
import dev.higurashi.profile_switcher.datagen.client.lang.JaJpLanguageProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = ProfileSwitcher.MOD_ID)
public final class PSDataGenerator {
    @SubscribeEvent
    protected static void onGatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();

        generator.addProvider(event.includeClient(), new EnUsLanguageProvider(output));
        generator.addProvider(event.includeClient(), new JaJpLanguageProvider(output));
    }
}
