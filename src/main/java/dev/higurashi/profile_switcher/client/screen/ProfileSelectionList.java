package dev.higurashi.profile_switcher.client.screen;

import dev.higurashi.profile_switcher.api.common.profile.Profile;
import dev.higurashi.profile_switcher.api.util.PSFileUtils;
import dev.higurashi.profile_switcher.api.common.profile.LocalProfileManager;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.ProgressScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class ProfileSelectionList extends ObjectSelectionList<ProfileSelectionList.ProfileEntry> {
    private final SelectProfileScreen screen;

    public ProfileSelectionList(SelectProfileScreen screen, Minecraft minecraft, int width, int height, int top, int bottom, int itemHeight, String filter, @Nullable ProfileSelectionList oldList) {
        super(minecraft, width, height, top, bottom, itemHeight);

        this.screen = screen;
        this.fillProfiles(filter);
    }

    private void fillProfiles(String filter) {
        this.clearEntries();

        String lowerFilter = filter.toLowerCase(Locale.ROOT);

        for (Profile profile : this.loadProfiles()) {
            if (profile.name().toLowerCase(Locale.ROOT).contains(lowerFilter)) {
                this.addEntry(new ProfileSelectionList.ProfileEntry(this, profile));
            }
        }

        this.notifyListUpdated();
    }

    private List<Profile> loadProfiles() {
        List<Profile> profiles = PSFileUtils.loadProfiles(this.minecraft.gameDirectory);

        profiles.sort(Comparator.comparing(Profile::name, String.CASE_INSENSITIVE_ORDER));
        return profiles;
    }

    private void notifyListUpdated() {
        this.screen.triggerImmediateNarration(true);
    }

    public void updateFilter(String filter) {
        this.fillProfiles(filter);
    }

    @Override
    public int getRowWidth() {
        return super.getRowWidth() + 50;
    }

    @Override
    public void setSelected(@Nullable ProfileEntry entry) {
        super.setSelected(entry);
        this.screen.updateButtonStatus(entry != null);
    }

    public static class ProfileEntry extends ObjectSelectionList.Entry<ProfileSelectionList.ProfileEntry> {
        public static final String DELETE_SCREEN_NAME = "delete_profile";

        private final ProfileSelectionList list;
        private final Profile profileData;
        private final ProfilePreview preview;

        private long lastClickTime;

        public ProfileEntry(ProfileSelectionList list, Profile profileData) {
            this.list = list;
            this.profileData = profileData;

            this.preview = new ProfilePreview(list.minecraft, profileData.id().toString());
        }

        public void selectProfile() {
            LocalProfileManager.setActiveProfile(this.profileData);
            this.list.screen.onClose();
            ScreenChangeEvent.profileSelected();
            this.list.minecraft.setScreen(new SelectWorldScreen(this.list.screen));
        }

        public void deleteProfile() {
            this.list.minecraft.setScreen(new ConfirmScreen(delete -> {
                if (delete) {
                    this.list.minecraft.setScreen(new ProgressScreen(true));
                    Path profileDirectory = PSFileUtils.findProfileDirectory(this.list.minecraft.gameDirectory, this.profileData.id().toString());

                    if (profileDirectory != null) {
                        PSFileUtils.deleteDirectory(profileDirectory);
                    }
                }

                this.list.minecraft.setScreen(this.list.screen);
            }, Component.translatable("screen.profile_switcher.delete_profile.title"), Component.translatable("screen.profile_switcher.delete_profile.warn", this.profileData.name()), Component.translatable("button.profile_switcher.select_profile.delete"), CommonComponents.GUI_CANCEL));
        }

        public void editProfile() {
            this.list.minecraft.setScreen(new EditProfileScreen(this.list.screen, this.profileData));
        }

        @Override
        public void render(GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovering, float partialTick) {
            String info = PSFileUtils.findProfileDirectory(this.list.minecraft.gameDirectory, this.profileData.id().toString()).getFileName().toString();

            guiGraphics.drawString(this.list.minecraft.font, this.profileData.name(), left + 50, top + 2, 0xFFFFFF);
            guiGraphics.drawString(this.list.minecraft.font, info, left + 50, top + 12, 8421504);
            guiGraphics.drawString(this.list.minecraft.font, this.profileData.gameMode().getLongDisplayName().getString() + (this.profileData.allowCheat() ? ", " + Component.translatable("selectWorld.cheats").getString() : ""), left + 50, top + 22, 8421504);

            this.preview.render(guiGraphics, left + 30, top + 10, mouseX, mouseY, this.isFocused());
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            this.list.setSelected(this);

            if (mouseX - (double) this.list.getRowLeft() <= 32.0D) {
                this.selectProfile();
                return true;
            } else if (Util.getMillis() - this.lastClickTime < 250) {
                this.selectProfile();
                return true;
            } else {
                this.lastClickTime = Util.getMillis();
                return true;
            }
        }

        @Override @NotNull
        public Component getNarration() {
            return Component.literal(this.profileData.name());
        }
    }
}
