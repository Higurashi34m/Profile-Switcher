package dev.higurashi.profile_switcher.api.common.profile.data;

import dev.higurashi.profile_switcher.api.common.profile.Profile;
import dev.higurashi.profile_switcher.api.event.ProfileDataLoadEvent;
import dev.higurashi.profile_switcher.api.util.PSFileUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;

public final class ProfileDataManager {
    private ProfileDataManager() {}

    private static final Map<ResourceLocation, IProfileDataHandler> HANDLERS = new LinkedHashMap<>();

    public static void registerHandler(IProfileDataHandler handler) {
        if (HANDLERS.containsKey(handler.getId())) {
            throw new IllegalStateException("Duplicate handler id: " + handler.getId());
        }

        HANDLERS.put(handler.getId(), handler);
    }

    public static Map<ResourceLocation, IProfileDataHandler> getHandlers() {
        return Collections.unmodifiableMap(HANDLERS);
    }

    public static void save(ServerPlayer player, Profile profile) {
        if (player.getServer() == null) return;

        File profileGlobal = PSFileUtils.getProfileGlobalDirectory(player.getServer().getServerDirectory(), profile.id().toString());
        File profileWorld = PSFileUtils.getProfileWorldDirectory(player.getServer(), profile.id().toString());

        ProfileDataManager.getHandlers().values().forEach(handler -> {
            File global = PSFileUtils.createOrGetFile(profileGlobal, handler.getId().toString().replace(':', '_'));
            File world = PSFileUtils.createOrGetFile(profileWorld, handler.getId().toString().replace(':', '_'));

            handler.saveProfileData(player, global);
            handler.saveWorldData(player, world);
        });
    }

    public static void load(ServerPlayer player, Profile profile) {
        if (player.getServer() == null) return;

        // Get Profile Base Directory
        File profileGlobal = PSFileUtils.getProfileGlobalDirectory(player.getServer().getServerDirectory(), profile.id().toString());
        File profileWorld = PSFileUtils.getProfileWorldDirectory(player.getServer(), profile.id().toString());

        ProfileDataManager.getHandlers().values()
                .stream()
                .sorted(Comparator.comparingInt(IProfileDataHandler::getPriority))
                .forEach(handler -> {
                    MinecraftForge.EVENT_BUS.post(new ProfileDataLoadEvent.Pre(player, profile, handler));

                    File global = new File(profileGlobal, handler.getId().toString().replace(':', '_'));
                    File world = new File(profileWorld, handler.getId().toString().replace(':', '_'));

                    handler.load(player, global, world);

                    MinecraftForge.EVENT_BUS.post(new ProfileDataLoadEvent.Post(player, profile, handler));
                });
    }
}