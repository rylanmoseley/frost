// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import static edu.wpi.first.units.Units.Degrees;
import static edu.wpi.first.units.Units.DegreesPerSecond;

import com.ctre.phoenix6.CANBus;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.units.measure.AngularVelocity;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.util.Color8Bit;

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

  public static class SwerveConstants {
    public static class CAN {
      public static final CANBus DRIVETRAIN_CANBUS = new CANBus("drivetrain");
      public static final int PIGEON = 0;
    }

    public static class GyroConstants {
      public static final Angle GYRO_ANGLE_TOLERANCE = Degrees.of(0);
      public static final AngularVelocity GYRO_VELOCITY_TOLERANCE = DegreesPerSecond.of(0);
    }
  }
}
