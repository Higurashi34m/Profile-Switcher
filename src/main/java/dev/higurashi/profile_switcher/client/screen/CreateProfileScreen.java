package dev.higurashi.profile_switcher.client.screen;

import dev.higurashi.profile_switcher.api.util.PSFileUtils;
import dev.higurashi.profile_switcher.api.common.profile.LocalProfileManager;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.GameType;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.nio.file.Files;
import java.nio.file.Path;

public class CreateProfileScreen extends Screen {
    public static final String SCREEN_NAME = "create_profile";

    private final Screen lastScreen;

    private EditBox nameEdit;
    private GameType gameMode = GameType.SURVIVAL;
    private boolean allowCheats;

    public CreateProfileScreen(@Nullable Screen lastScreen) {
        super(Component.translatable("screen.profile_switcher.create_profile.title"));
        this.lastScreen = lastScreen;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;

        this.nameEdit = new EditBox(this.font, centerX - 105, 60, 210, 20, Component.translatable("selectWorld.enterName"));

        this.nameEdit.setValue("New Profile");
        this.addRenderableWidget(this.nameEdit);

        CycleButton<GameType> gameModeButton = CycleButton
                .builder(GameType::getLongDisplayName)
                .withValues(GameType.SURVIVAL, GameType.ADVENTURE, GameType.CREATIVE)
                .withInitialValue(this.gameMode)
                .create(centerX - 105, 100, 210, 20, Component.translatable("selectWorld.gameMode"), (button, value) -> this.gameMode = value);

        this.addRenderableWidget(gameModeButton);

        CycleButton<Boolean> cheatsButton = CycleButton
                .onOffBuilder()
                .withInitialValue(this.allowCheats)
                .create(centerX - 105, 140, 210, 20, Component.translatable("selectWorld.allowCommands"), (button, value) -> this.allowCheats = value);

        this.addRenderableWidget(cheatsButton);

        this.addRenderableWidget(Button.builder(Component.translatable("button.profile_switcher.select_profile.create"), button -> this.createProfile())
                .bounds(centerX - 105, this.height - 52, 100, 20).build());

        this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, button -> this.onClose()).bounds(centerX + 5, this.height - 52, 100, 20).build());

        this.setInitialFocus(this.nameEdit);
    }

    private void createProfile() {
        String name = this.nameEdit.getValue().trim();
        if (name.isEmpty() || this.minecraft == null) return;

        Path profilesDirectory = PSFileUtils.getProfileDirectory(this.minecraft.gameDirectory).toPath();

        String directoryName = name;
        int count = 1;
        while (Files.exists(profilesDirectory.resolve(directoryName))) {
            directoryName = name + " " + count;
            count++;
        }

        LocalProfileManager.createProfile(profilesDirectory, directoryName, name, this.gameMode, this.allowCheats);
        if (this.lastScreen instanceof SelectProfileScreen) {
            this.minecraft.setScreen(this.lastScreen);
        } else {
            this.minecraft.setScreen(new SelectProfileScreen(this.lastScreen));
        }
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
        guiGraphics.drawString(this.font, Component.translatable("button.profile_switcher.create_profile.editbox"), centerX - 105, 45, 0xFFFFFF);

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }
}