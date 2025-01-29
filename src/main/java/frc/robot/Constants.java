// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.util.Color8Bit;
import frc.robot.subsystems.Arm.ArmStageConfig;
import java.util.HashMap;
import java.util.Map;

/**
 * The Constants class provides a convenient place for teams to hold robot-wide numerical or boolean
 * constants. This class should not be used for any other purpose. All constants should be declared
 * globally (i.e. public static). Do not put anything functional in this class.
 *
 * <p>It is advised to statically import this class (or one of its inner classes) wherever the
 * constants are needed, to reduce verbosity.
 */
public final class Constants {
  public static class OperatorConstants {
    public static final int kDriverControllerPort = 0;
    public static final int kOperatorControllerLeftPort = 1;
    public static final int kOperatorControllerRightPort = 2;
  }

  public static class TelemetryConstants {
    public static final String ROOT_NAME = "telemetry";
  }

  public static class ArmConstants {
    public enum Mode {
      CUBE,
      CONE
    }

    public static Mode DEFAULT_MODE = Mode.CUBE;

    public static class SimulationConstants {
      /** Pixels */
      public static final double MECHANISM2D_WIDTH = 0;

      /** Pixels */
      public static final double MECHANISM2D_HEIGHT = 0;

      public static final double MECHANISM2D_PIXELS_PER_INCH = 0;

      /** Pixels */
      public static final Translation2d MECHANISM2D_ROOT = new Translation2d(0, 0);
    }

    public static class ArmStagesConstants {
      public static ArmStageConfig STAGE_1_CONFIG =
          new ArmStageConfig(
              0, 0, Configs.Arm.STAGE_1_CONFIG, StagePositions.STAGE_1, 0, 0, 0, 0, 0, 0, 0, 0);
      public static ArmStageConfig STAGE_2_CONFIG =
          new ArmStageConfig(
              0, 0, Configs.Arm.STAGE_2_CONFIG, StagePositions.STAGE_2, 0, 0, 0, 0, 0, 0, 0, 0);
      public static ArmStageConfig STAGE_3_CONFIG =
          new ArmStageConfig(
              0, 0, Configs.Arm.STAGE_3_CONFIG, StagePositions.STAGE_3, 0, 0, 0, 0, 0, 0, 0, 0);

      public enum POSITIONS {
        IDLE {
          @Override
          public String toString() {
            return "Idle";
          }
        },
        HIGH_CUBE {
          @Override
          public String toString() {
            return "High Cube";
          }
        },
        HIGH_CONE {
          @Override
          public String toString() {
            return "High Cone";
          }
        },
        MID_CUBE {
          @Override
          public String toString() {
            return "Mid Cube";
          }
        },

        MID_CONE {
          @Override
          public String toString() {
            return "Mid Cone";
          }
        },
        LOW_CUBE {
          @Override
          public String toString() {
            return "Low Cube";
          }
        },
        LOW_CONE {
          @Override
          public String toString() {
            return "Low Cone";
          }
        },
        SUBSTATION {
          @Override
          public String toString() {
            return "Substation";
          }
        },
        GROUND {
          @Override
          public String toString() {
            return "Ground";
          }
        },
        GROUND_TILT {
          @Override
          public String toString() {
            return "Ground Tilt";
          }
        };

        public String toString() {
          return "Unknown";
        }
      }

      public static final class StagePositions {
        public static Map<POSITIONS, Double> STAGE_1 = new HashMap<>();
        public static Map<POSITIONS, Double> STAGE_2 = new HashMap<>();
        public static Map<POSITIONS, Double> STAGE_3 = new HashMap<>();

        static {
          // TODO add positions
        }
      }
    }

    public static class ClawConstants {

      public static class CAN {
        public static int ROLLER_LEFT = 0;
        public static int ROLLER_RIGHT = 0;
        public static int PNEUMATIC_HUB = 0;
      }

      public static class PNEUMATIC_CHANNEL {
        public static int CLAMP_FORWARD = 0;
        public static int CLAMP_REVERSE = 0;

        public static int PRESSURE_SWITCH_PORT = 0; // analog channel
      }

      public static class DIO {
        public static int BEAM_BREAK = 0;
      }

      public static final boolean BEAM_BREAK_BROKEN_VALUE = true;

      public static final Value CLAW_OPEN = Value.kForward;
      public static final Value CLAW_CLOSED = Value.kReverse;

      public static class RollerSpeeds {
        public static final double CONE_IDLE = 0;
        public static final double CONE_INTAKE = 0;
        public static final double CONE_EJECT = 0;

        public static final double CUBE_IDLE = 0;
        public static final double CUBE_INTAKE = 0;
        public static final double CUBE_EJECT = 0;
      }

      public static class SimulationConstants {
        public static final double GEAR_RATIO = 1;
        public static final double MOI = 1;

        public static final double COMPRESSOR_ON_CURRENT = 0;
        public static final double PRESSURE_INCREMENT = 0.1;
        public static final double PRESSURE_PISTON_DROP = 5;

        /** Pixels */
        public static final double MECHANISM2D_WIDTH = 0;

        /** Pixels */
        public static final double MECHANISM2D_HEIGHT = 0;

        public static final double MECHANISM2D_PIXELS_PER_INCH = 0;

        /** Pixels */
        public static final Translation2d MECHANISM2D_ROOT = new Translation2d(0, 0);

        public static final Color8Bit CLAW_EMPTY_COLOR = new Color8Bit(0, 0, 0);
        public static final Color8Bit CLAW_FULL_COLOR = new Color8Bit(0, 0, 0);

        /** Inches */
        public static final double MECHANISM2D_CLAW_LENGTH = 0;

        /** Degrees */
        public static final double MECHANISM2D_CLAW_ANGLE_OPEN = 0;

        /** Degrees */
        public static final double MECHANISM2D_CLAW_ANGLE_CLOSED = 0;

        /** Inches */
        public static final double MECHANISM2D_ROLLER_RADIUS = 0;
      }
    }
  }
}
