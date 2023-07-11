package me.zacharias.speedometer;

import net.minecraft.network.chat.Component;

public enum SpeedTypes {
  MPH(20),
  KMPH(200),
  MPS(10),
  BlockPS(10),
  KNOT(20);

  private final int maxVisual;

  SpeedTypes(int maxVisual){
    this.maxVisual = maxVisual;
  }

  public static Component getName(Enum anEnum) {
    if(anEnum instanceof SpeedTypes speedType) {
      return Component.translatable("speedometer.speed." + switch (speedType) {
        case MPH -> "mph";
        case MPS -> "mps";
        case KMPH -> "kmph";
        case BlockPS -> "bps";
        case KNOT -> "knot";
      });
    }else {
      return Component.translatable("speedometer.speed.error");
    }
  }

  public int gatMaxVisual() {
    return maxVisual;
  }
}