package dev.higurashi.profile_switcher.datagen.client.lang;

import dev.higurashi.profile_switcher.ProfileSwitcher;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.LanguageProvider;

public abstract class BaseLanguageProvider extends LanguageProvider {
    private static final String modId = ProfileSwitcher.MOD_ID;

    public BaseLanguageProvider(PackOutput output, String locale) {
        super(output, modId, locale);
    }

    public String getScreenDescId(String screenName) {
        return "screen." + modId + "." + screenName;
    }

    public void addScreenTitle(String screenName, String name) {
        this.add(this.getScreenDescId(screenName) + ".title", name);
    }

    public void addScreenWarn(String screenName, String name) {
        this.add(this.getScreenDescId(screenName) + ".warn", name);
    }

    public void addButton(String screenName, String buttonName, String button) {
        this.add("button." + modId + "." + screenName + "." + buttonName, button);
    }
}
