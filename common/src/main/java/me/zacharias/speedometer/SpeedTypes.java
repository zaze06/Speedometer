package me.zacharias.speedometer;

import net.minecraft.network.chat.Component;

public enum SpeedTypes {
    MPH,
    KMPH,
    MPS,
    BlockPS,
    KNOT;

    public static Component getName(Enum anEnum) {
        if(anEnum instanceof SpeedTypes speedType) {
            return Component.translatable("speedometer.speed." + switch (speedType) {
                case MPH -> "mph";
                case MPS -> "mps";
                case KMPH -> "kmph";
                case BlockPS -> "bps";
                case KNOT -> "knot";
                default -> "error";
            });
        }else {
            return Component.translatable("speedometer.speed.error");
        }
    }
}