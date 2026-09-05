package dev.higurashi.profile_switcher.api.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.higurashi.profile_switcher.api.common.profile.Profile;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.storage.LevelResource;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

public final class PSFileUtils {
    private PSFileUtils() {}

    public static final String BASE_FOLDER_NAME = "profile_switcher";
    public static final String PROFILE_FOLDER_NAME = "profiles";
    public static final String PROFILE_DATA_FILE_NAME = "profile.json";

    public static File getGlobalDirectory(File globalDirectory) {
        return createOrGetFile(globalDirectory, BASE_FOLDER_NAME);
    }

    public static File getProfileDirectory(File globalDirectory) {
        return createOrGetFile(PSFileUtils.getGlobalDirectory(globalDirectory), PROFILE_FOLDER_NAME);
    }

    public static File getWorldDirectory(MinecraftServer server) {
        File worldFile = server.getWorldPath(LevelResource.ROOT).toFile();
        return createOrGetFile(worldFile, BASE_FOLDER_NAME);
    }

    public static File getProfileGlobalDirectory(File globalDirectory, String profileId) {
        Path profileDirectory = findProfileDirectory(globalDirectory, profileId);
        if (profileDirectory == null) return null;

        return profileDirectory.toFile();
    }

    public static File getProfileWorldDirectory(MinecraftServer server, String profileId) {
        return createOrGetFile(getWorldDirectory(server), profileId);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static File createOrGetFile(File parent, String name) {
        File directory = new File(parent, name);
        if (!directory.exists()) directory.mkdirs();
        return directory;
    }

    @Nullable
    public static Profile loadProfileData(Path profileFile) {
        try {
            String json = Files.readString(profileFile);
            JsonObject object = JsonParser.parseString(json).getAsJsonObject();

            String id = object.get("id").getAsString();
            String name = object.get("name").getAsString();
            int gameType = object.get("gameType").getAsInt();
            boolean allowCheat = object.get("allowCheat").getAsBoolean();

            return new Profile(UUID.fromString(id), name, GameType.byId(gameType), allowCheat);
        } catch (Exception e) {
            return null;
        }
    }

    public static List<Profile> loadProfiles(File globalDirectory) {
        Path profilesDirectory = PSFileUtils.getProfileDirectory(globalDirectory).toPath();
        List<Profile> profiles = new ArrayList<>();

        if (!Files.isDirectory(profilesDirectory)) return profiles;

        try (Stream<Path> files = Files.list(profilesDirectory)) {
            files.forEach(path -> {
                if (!Files.isDirectory(path)) return;

                Path profileFile = path.resolve(PROFILE_DATA_FILE_NAME);
                if (!Files.isRegularFile(profileFile)) return;

                Profile profile = loadProfileData(profileFile);
                if (profile != null) profiles.add(profile);
            });
        } catch (IOException e) {
            throw new RuntimeException("Failed to load profiles.", e);
        }

        return profiles;
    }

    @Nullable
    public static Path findProfileDirectory(File globalDirectory, String profileId) {
        Path profilesDirectory = PSFileUtils.getProfileDirectory(globalDirectory).toPath();
        if (!Files.isDirectory(profilesDirectory)) return null;

        try (Stream<Path> files = Files.list(profilesDirectory)) {
            Iterator<Path> iterator = files.iterator();

            while (iterator.hasNext()) {
                Path path = iterator.next();
                if (!Files.isDirectory(path)) continue;

                Path profileFile = path.resolve(PROFILE_DATA_FILE_NAME);
                if (!Files.isRegularFile(profileFile)) continue;

                Profile profile = PSFileUtils.loadProfileData(profileFile);
                if (profile == null) continue;

                if (profile.id().toString().equals(profileId)) return path;
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to find profile directory.", e);
        }

        return null;
    }

    public static void deleteDirectory(Path path) {
        if (Files.isDirectory(path)) {
            try (Stream<Path> files = Files.list(path)) {
                files.forEach(PSFileUtils::deleteDirectory);
            } catch (IOException e) {
                throw new RuntimeException("Failed to access directory: " + path, e);
            }
        }

        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete: " + path, e);
        }
    }
}
