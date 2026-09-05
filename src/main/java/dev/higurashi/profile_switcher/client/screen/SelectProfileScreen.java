package dev.higurashi.profile_switcher.client.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class SelectProfileScreen extends Screen {
    public static final String SCREEN_NAME = "select_profile";

    private final Screen lastScreen;

    private Button deleteButton;
    private Button selectButton;
    private Button editButton;
    private EditBox searchBox;

    private ProfileSelectionList list;

    public SelectProfileScreen(@Nullable Screen lastScreen) {
        super(Component.translatable("screen.profile_switcher.select_profile.title"));
        this.lastScreen = lastScreen;
    }

    @Override
    public void tick() {
        this.searchBox.tick();
    }

    @Override
    protected void init() {
        Minecraft minecraft = this.minecraft;
        if (minecraft == null) return;

        this.searchBox = new EditBox(this.font, this.width / 2 - 100, 22, 200, 20, this.searchBox, Component.translatable("selectWorld.search"));
        this.list = new ProfileSelectionList(this, this.minecraft, this.width, this.height, 48, this.height - 64, 36, this.searchBox.getValue(), this.list);

        this.searchBox.setResponder(this.list::updateFilter);

        this.addWidget(this.searchBox);
        this.addWidget(this.list);

        this.selectButton = this.addRenderableWidget(Button.builder(Component.translatable("button.profile_switcher.select_profile.select"), button -> {
            ProfileSelectionList.ProfileEntry entry = this.list.getSelected();
            if (entry != null) entry.selectProfile();
        }).bounds(this.width / 2 - 150, this.height - 52, 148, 20).build());

        this.addRenderableWidget(Button.builder(Component.translatable("button.profile_switcher.select_profile.create"), button -> {
            minecraft.setScreen(new CreateProfileScreen(this));
        }).bounds(this.width / 2 + 2, this.height - 52, 148, 20).build());

        this.editButton = this.addRenderableWidget(Button.builder(Component.translatable("button.profile_switcher.select_profile.edit"), button -> {
            ProfileSelectionList.ProfileEntry entry = this.list.getSelected();
            if (entry != null) entry.editProfile();
        }).bounds(this.width / 2 - 150, this.height - 28, 98, 20).build());

        this.deleteButton = this.addRenderableWidget(Button.builder(Component.translatable("button.profile_switcher.select_profile.delete"), button -> {
            ProfileSelectionList.ProfileEntry entry = this.list.getSelected();
            if (entry != null) entry.deleteProfile();
        }).bounds(this.width / 2 - 49, this.height - 28, 98, 20).build());

        this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, button -> {
            minecraft.setScreen(this.lastScreen);
        }).bounds(this.width / 2 + 52, this.height - 28, 98, 20).build());

        this.setInitialFocus(this.searchBox);
        this.updateButtonStatus(false);
    }

    public void updateButtonStatus(boolean active) {
        this.selectButton.active = active;
        this.deleteButton.active = active;
        this.editButton.active = active;
    }

    @Override
    public void onClose() {
        if (this.minecraft == null) return;
        this.minecraft.setScreen(this.lastScreen);
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.list.render(guiGraphics, mouseX, mouseY, partialTick);
        this.searchBox.render(guiGraphics, mouseX, mouseY, partialTick);

        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 8, 0xFFFFFF);

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }
}