package dev.higurashi.profile_switcher.api.common.profile;

import net.minecraft.world.level.GameType;

import java.util.UUID;

public record Profile(UUID id, String name, GameType gameMode, boolean allowCheat) {

}
