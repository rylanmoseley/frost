package frc.robot;

import com.ctre.phoenix6.configs.Pigeon2Configuration;
import com.revrobotics.spark.config.SparkMaxConfig;

public final class Configs {
  public static final class Arm {
    public static final SparkMaxConfig STAGE_1_CONFIG = new SparkMaxConfig();
    public static final SparkMaxConfig STAGE_2_CONFIG = new SparkMaxConfig();
    public static final SparkMaxConfig STAGE_3_CONFIG = new SparkMaxConfig();
    public static final SparkMaxConfig ROLLER_LEFT_CONFIG = new SparkMaxConfig();
    public static final SparkMaxConfig ROLLER_RIGHT_CONFIG = new SparkMaxConfig();

    static {
    }
  }

  public static final class Drive {
    public static final Pigeon2Configuration PIGEON_CONFIG = new Pigeon2Configuration();

    static {
    }
  }
}
