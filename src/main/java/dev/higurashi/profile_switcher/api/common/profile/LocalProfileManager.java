package dev.higurashi.profile_switcher.api.common.profile;

import com.google.gson.JsonObject;
import net.minecraft.world.level.GameType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.UUID;

public final class LocalProfileManager {
    private LocalProfileManager() {}

    private static Profile activeProfile = new Profile(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"), "default", GameType.SURVIVAL, false);

    public static Profile getActiveProfile() {
        return activeProfile;
    }

    public static void setActiveProfile(Profile profile) {
        activeProfile = Objects.requireNonNull(profile);
    }

    public static void createProfile(Path profilesDirectory, String directoryName, String profileName, GameType gameType, boolean allowCheat) {
        String id = UUID.randomUUID().toString();

        Path profileDirectory = profilesDirectory.resolve(directoryName);

        try {
            Files.createDirectories(profileDirectory);

            JsonObject json = new JsonObject();
            json.addProperty("id", id);
            json.addProperty("name", profileName);
            json.addProperty("gameType", gameType.getId());
            json.addProperty("allowCheat", allowCheat);

            Files.writeString(profileDirectory.resolve("profile.json"), json.toString());
        } catch (IOException e) {
            throw new RuntimeException("Failed to create profile: " + profileName, e);
        }
    }
}
