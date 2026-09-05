package dev.higurashi.profile_switcher.datagen.client.lang;

import dev.higurashi.profile_switcher.client.screen.CreateProfileScreen;
import dev.higurashi.profile_switcher.client.screen.EditProfileScreen;
import dev.higurashi.profile_switcher.client.screen.ProfileSelectionList;
import dev.higurashi.profile_switcher.client.screen.SelectProfileScreen;
import net.minecraft.data.PackOutput;

import java.util.Locale;

public class JaJpLanguageProvider extends BaseLanguageProvider {
    public JaJpLanguageProvider(PackOutput output) {
        super(output, Locale.JAPAN.toString().toLowerCase());
    }

    @Override
    protected void addTranslations() {
        // Screen
        this.addScreenTitle(SelectProfileScreen.SCREEN_NAME, "プロファイルを選択");
        this.addScreenTitle(CreateProfileScreen.SCREEN_NAME, "プロファイル新規作成");
        this.addScreenTitle(EditProfileScreen.SCREEN_NAME, "プロファイルを編集");
        this.addScreenTitle(ProfileSelectionList.ProfileEntry.DELETE_SCREEN_NAME, "このプロファイルを削除しますか?");

        this.addScreenWarn(ProfileSelectionList.ProfileEntry.DELETE_SCREEN_NAME, "「%s」は完全に削除され、復元ができなくなります。");

        // Button
        this.addButton(SelectProfileScreen.SCREEN_NAME, "select", "選択したプロファイルで遊ぶ");
        this.addButton(SelectProfileScreen.SCREEN_NAME, "create", "プロファイル新規作成");
        this.addButton(SelectProfileScreen.SCREEN_NAME, "edit", "編集");
        this.addButton(SelectProfileScreen.SCREEN_NAME, "delete", "削除");

        this.addButton(CreateProfileScreen.SCREEN_NAME, "editbox", "プロファイル名");

        this.addButton(EditProfileScreen.SCREEN_NAME, "confirm", "プロファイル名を変更");
    }
}
