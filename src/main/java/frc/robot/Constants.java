// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import static edu.wpi.first.units.Units.Degrees;
import static edu.wpi.first.units.Units.DegreesPerSecond;
import static edu.wpi.first.units.Units.FeetPerSecond;
import static edu.wpi.first.units.Units.Inches;
import static edu.wpi.first.units.Units.RPM;

import com.ctre.phoenix6.CANBus;
import com.pathplanner.lib.config.PIDConstants;
import com.pathplanner.lib.config.RobotConfig;
import com.pathplanner.lib.controllers.PPHolonomicDriveController;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.units.measure.AngularVelocity;
import edu.wpi.first.units.measure.Distance;
import edu.wpi.first.units.measure.LinearVelocity;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.util.Color8Bit;
import frc.robot.subsystems.Swerve.SwerveModuleConfig;

/**
 * The Constants class provides a convenient place for teams to hold robot-wide numerical or boolean
 * constants. This class should not be used for any other purpose. All constants should be declared
 * globally (i.e. public static). Do not put anything functional in this class.
 *
 * <p>It is advised to statically import this class (or one of its inner classes) wherever the
 * constants are needed, to reduce verbosity.
 */
public final class Constants {
  public static int MAX_CONFIG_RETRIES = 5;

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

    public static final SwerveModuleConfig FRONT_LEFT_CONFIG = new SwerveModuleConfig();
    public static final SwerveModuleConfig FRONT_RIGHT_CONFIG = new SwerveModuleConfig();
    public static final SwerveModuleConfig BACK_LEFT_CONFIG = new SwerveModuleConfig();
    public static final SwerveModuleConfig BACK_RIGHT_CONFIG = new SwerveModuleConfig();

    public static final Distance WIDTH = Inches.of(0);
    public static final Distance LENGTH = Inches.of(0);
    public static final SwerveDriveKinematics KINEMATICS =
        new SwerveDriveKinematics(
            new Translation2d(WIDTH.div(2), LENGTH.div(2)),
            new Translation2d(WIDTH.div(2), LENGTH.div(2).unaryMinus()),
            new Translation2d(WIDTH.div(2).unaryMinus(), LENGTH.div(2)),
            new Translation2d(WIDTH.div(2).unaryMinus(), LENGTH.div(2).unaryMinus()));

    public static RobotConfig robotConfig;
    public static final PPHolonomicDriveController HOLONOMIC_DRIVE_CONTROLLER =
        new PPHolonomicDriveController( // TODO tune
            new PIDConstants(0, 0, 0),
            new PIDConstants(0, 0, 0));

    static {
      try {
        robotConfig = RobotConfig.fromGUISettings();
      } catch (Exception e) {
        DriverStation.reportError("Failed to load robot config, loading default", false);
        // TODO default config
        robotConfig =
            new RobotConfig(MAX_CONFIG_RETRIES, MAX_CONFIG_RETRIES, null, MAX_CONFIG_RETRIES);
      }
    }

    public static class ModuleConstants {
      public static final double DRIVE_GEAR_RATIO = 0;
      public static final Distance WHEEL_DIAMETER = Inches.of(0);
      public static final Distance WHEEL_CIRCUMFERENCE = WHEEL_DIAMETER.times(Math.PI);
      public static final AngularVelocity MAX_MOTOR_SPEED = RPM.of(0);
      public static final LinearVelocity MAX_STRAFE_SPEED = FeetPerSecond.of(0);
    }
  }
}
