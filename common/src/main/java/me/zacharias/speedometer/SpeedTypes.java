package me.zacharias.speedometer;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

public enum SpeedTypes {
  MPH,
  KMPH,
  MPS,
  BlockPS,
  KNOT;

  public static Component getName(Enum anEnum) {
    if(anEnum instanceof SpeedTypes speedType) {
      return new TranslatableComponent("speedometer.speed." + switch (speedType) {
        case MPH -> "mph";
        case MPS -> "mps";
        case KMPH -> "kmph";
        case BlockPS -> "bps";
        case KNOT -> "knot";
      });
    }else {
      return new TranslatableComponent("speedometer.speed.error");
    }
  }
}