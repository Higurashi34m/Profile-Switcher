package dev.higurashi.profile_switcher.client.screen;

import com.google.gson.JsonObject;
import dev.higurashi.profile_switcher.api.common.profile.Profile;
import dev.higurashi.profile_switcher.api.util.PSFileUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class EditProfileScreen extends Screen {
    public static final String SCREEN_NAME = "edit_profile";

    private final Screen lastScreen;

    private final Profile oldProfile;

    private EditBox nameEdit;

    public EditProfileScreen(@Nullable Screen lastScreen, Profile profile) {
        super(Component.translatable("screen.profile_switcher.edit_profile.title"));
        this.lastScreen = lastScreen;
        this.oldProfile = profile;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;

        this.nameEdit = new EditBox(this.font, centerX - 105, this.height / 2 - 50, 210, 20, Component.translatable("selectWorld.enterName"));
        this.nameEdit.setValue(this.oldProfile.name());

        this.addRenderableWidget(this.nameEdit);

        this.addRenderableWidget(Button.builder(Component.translatable("button.profile_switcher.edit_profile.confirm"), button -> this.confirmEdit())
                .bounds(centerX - 105, this.height / 2 - 15, 100, 20).build());

        this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, button -> this.onClose())
                .bounds(centerX + 5, this.height / 2 - 15, 100, 20).build());

        this.setInitialFocus(this.nameEdit);
    }

    private void confirmEdit() {
        String name = this.nameEdit.getValue().trim();
        if (name.isEmpty() || this.minecraft == null) return;

        Path profileDirectory = PSFileUtils.findProfileDirectory(this.minecraft.gameDirectory, this.oldProfile.id().toString());
        if (profileDirectory == null) return;

        Path profileFile = profileDirectory.resolve(PSFileUtils.PROFILE_DATA_FILE_NAME);
        Profile profile = PSFileUtils.loadProfileData(profileFile);
        if (profile == null) return;

        Profile editedProfile = new Profile(profile.id(), name, profile.gameMode(), profile.allowCheat());

        JsonObject json = new JsonObject();
        json.addProperty("id", editedProfile.id().toString());
        json.addProperty("name", editedProfile.name());
        json.addProperty("gameType", editedProfile.gameMode().getId());
        json.addProperty("allowCheat", editedProfile.allowCheat());

        try {
            Files.writeString(profileFile, json.toString());
        } catch (IOException e) {
            throw new RuntimeException("Failed to edit profile: " + name, e);
        }

        this.onClose();
    }

    @Override
    public void onClose() {
        if (this.minecraft == null) return;
        this.minecraft.setScreen(this.lastScreen);
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);

        int centerX = this.width / 2;

        guiGraphics.drawCenteredString(this.font, this.title, centerX, 20, 0xFFFFFF);
        guiGraphics.drawString(this.font, Component.translatable("button.profile_switcher.create_profile.editbox"), centerX - 105, this.height / 2 - 65, 0xFFFFFF);

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }
}
