package dev.higurashi.profile_switcher.datagen.client.lang;

import dev.higurashi.profile_switcher.client.screen.CreateProfileScreen;
import dev.higurashi.profile_switcher.client.screen.EditProfileScreen;
import dev.higurashi.profile_switcher.client.screen.ProfileSelectionList;
import dev.higurashi.profile_switcher.client.screen.SelectProfileScreen;
import net.minecraft.data.PackOutput;

import java.util.Locale;

public class EnUsLanguageProvider extends BaseLanguageProvider {
    public EnUsLanguageProvider(PackOutput output) {
        super(output, Locale.US.toString().toLowerCase());
    }

    @Override
    protected void addTranslations() {
        // Screen
        this.addScreenTitle(SelectProfileScreen.SCREEN_NAME, "Select Profile");
        this.addScreenTitle(CreateProfileScreen.SCREEN_NAME, "Create New Profile");
        this.addScreenTitle(EditProfileScreen.SCREEN_NAME, "Edit Profile");
        this.addScreenTitle(ProfileSelectionList.ProfileEntry.DELETE_SCREEN_NAME, "Are you sure you want to delete this profile?");

        this.addScreenWarn(ProfileSelectionList.ProfileEntry.DELETE_SCREEN_NAME, "\"%s\" will be lost forever! (A long time!)");

        // Button
        this.addButton(SelectProfileScreen.SCREEN_NAME, "select", "Play Selected Profile");
        this.addButton(SelectProfileScreen.SCREEN_NAME, "create", "Create New Profile");
        this.addButton(SelectProfileScreen.SCREEN_NAME, "edit", "Edit");
        this.addButton(SelectProfileScreen.SCREEN_NAME, "delete", "Delete");

        this.addButton(CreateProfileScreen.SCREEN_NAME, "editbox", "Profile Name");

        this.addButton(EditProfileScreen.SCREEN_NAME, "confirm", "Change Profile Name");
    }
}
