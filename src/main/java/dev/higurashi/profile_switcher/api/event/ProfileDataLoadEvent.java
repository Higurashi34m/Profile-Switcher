package dev.higurashi.profile_switcher.api.event;

import dev.higurashi.profile_switcher.api.common.profile.Profile;
import dev.higurashi.profile_switcher.api.common.profile.data.IProfileDataHandler;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.eventbus.api.Event;

public class ProfileDataLoadEvent extends Event {
    private final ServerPlayer player;
    private final Profile profile;
    private final IProfileDataHandler handler;

    public ProfileDataLoadEvent(ServerPlayer player, Profile profile, IProfileDataHandler handler) {
        this.player = player;
        this.profile = profile;
        this.handler = handler;
    }

    public ServerPlayer getPlayer() {
        return this.player;
    }

    public Profile getProfile() {
        return this.profile;
    }

    public IProfileDataHandler getHandler() {
        return this.handler;
    }

    public static class Pre extends ProfileDataLoadEvent {
        public Pre(ServerPlayer player, Profile profile, IProfileDataHandler handler) {
            super(player, profile, handler);
        }
    }

    public static class Post extends ProfileDataLoadEvent {
        public Post(ServerPlayer player, Profile profile, IProfileDataHandler handler) {
            super(player, profile, handler);
        }
    }
}
