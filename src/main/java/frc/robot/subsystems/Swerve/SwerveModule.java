package frc.robot.subsystems.Swerve;

import static edu.wpi.first.units.Units.Amps;
import static edu.wpi.first.units.Units.Meters;
import static edu.wpi.first.units.Units.MetersPerSecond;
import static edu.wpi.first.units.Units.MetersPerSecondPerSecond;
import static edu.wpi.first.units.Units.Rotations;
import static edu.wpi.first.units.Units.RotationsPerSecond;

import com.ctre.phoenix6.StatusCode;
import com.ctre.phoenix6.controls.ControlRequest;
import com.ctre.phoenix6.controls.MotionMagicVelocityVoltage;
import com.ctre.phoenix6.controls.PositionVoltage;
import com.ctre.phoenix6.controls.VelocityVoltage;
import com.ctre.phoenix6.hardware.CANcoder;
import com.ctre.phoenix6.hardware.TalonFX;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.units.measure.AngularVelocity;
import edu.wpi.first.units.measure.Current;
import edu.wpi.first.units.measure.Distance;
import edu.wpi.first.units.measure.LinearAcceleration;
import edu.wpi.first.units.measure.LinearVelocity;
import edu.wpi.first.wpilibj.DriverStation;
import frc.robot.Constants;
import frc.robot.Constants.SwerveConstants.PIDs;
import frc.robot.Constants.SwerveConstants.PhysicsConstants;
import java.util.function.DoubleSupplier;
import java.util.function.Supplier;

public class SwerveModule {
  private SwerveModuleConfig config;

  private TalonFX driveMotor;
  private TalonFX steerMotor;
  private CANcoder steerEncoder;

  private VelocityVoltage driveVelocityVoltage = new VelocityVoltage(0);
  private MotionMagicVelocityVoltage driveMotionMagicVelocityVoltage =
      new MotionMagicVelocityVoltage(0);
  private PositionVoltage steerPositionVoltage = new PositionVoltage(0);

  private Supplier<Current> driveCurrentSupplier;
  private Supplier<Current> steerCurrentSupplier;
  private Supplier<AngularVelocity> rawDriveVelocitySupplier;
  private Supplier<Angle> rawDrivePositionSupplier;
  private Supplier<Distance> drivePositionSupplier;
  private Supplier<Angle> steerEncoderAngleSupplier;
  private Supplier<LinearVelocity> driveVelocitySupplier;
  private Supplier<Rotation2d> steerPositionSupplier;

  public final DoubleSupplier totalCurrentDraw =
      () -> driveCurrentSupplier.get().in(Amps) + steerCurrentSupplier.get().in(Amps);

  public SwerveModule(SwerveModuleConfig config) {
    this.config = config;

    this.driveMotor = config.createDriveMotor();
    this.steerMotor = config.createSteerMotor();
    this.steerEncoder = config.createSteerEncoder();

    this.driveCurrentSupplier = driveMotor.getStatorCurrent().asSupplier();
    this.steerCurrentSupplier = steerMotor.getStatorCurrent().asSupplier();

    this.rawDriveVelocitySupplier = driveMotor.getVelocity().asSupplier();
    this.rawDrivePositionSupplier = driveMotor.getPosition().asSupplier();
    this.drivePositionSupplier =
        () ->
            Meters.of(
                rawDrivePositionSupplier.get().in(Rotations)
                    * PhysicsConstants.WHEEL_CIRCUMFERENCE.in(Meters));
    this.steerEncoderAngleSupplier = steerEncoder.getPosition().asSupplier();

    this.driveVelocitySupplier =
        () ->
            MetersPerSecond.of(
                rawDriveVelocitySupplier.get().in(RotationsPerSecond)
                    * PhysicsConstants.WHEEL_DIAMETER.in(Meters));
    this.steerPositionSupplier =
        () -> Rotation2d.fromRotations(steerEncoderAngleSupplier.get().in(Rotations));
  }

  public StatusCode configureAll() {
    StatusCode status = StatusCode.OK;

    // Configure the drive motor
    for (int i = 0; i < Constants.MAX_CONFIG_RETRIES; i++) {
      status = driveMotor.getConfigurator().apply(config.getDriveMotorConfig());
      if (status == StatusCode.OK) {
        break;
      } else {
        DriverStation.reportWarning(
            "Warning: Failure configuring "
                + config.getName()
                + " Drive Motor :"
                + status.toString()
                + ", "
                + status.getDescription(),
            false);
      }
    }

    if (status != StatusCode.OK) {
      DriverStation.reportError(
          "Error configuring "
              + config.getName()
              + " Drive Motor :"
              + status.toString()
              + ", "
              + status.getDescription(),
          false);
      return status;
    }

    // Configure the steer motor
    for (int i = 0; i < Constants.MAX_CONFIG_RETRIES; i++) {
      status = steerMotor.getConfigurator().apply(config.getSteerMotorConfig());
      if (status == StatusCode.OK) {
        break;
      } else {
        DriverStation.reportWarning(
            "Warning: Failure configuring "
                + config.getName()
                + " Steer Motor :"
                + status.toString()
                + ", "
                + status.getDescription(),
            false);
      }
    }

    if (status != StatusCode.OK) {
      DriverStation.reportError(
          "Error configuring "
              + config.getName()
              + " Steer Motor :"
              + status.toString()
              + ", "
              + status.getDescription(),
          false);
      return status;
    }

    // Configure the steer encoder
    for (int i = 0; i < Constants.MAX_CONFIG_RETRIES; i++) {
      status = steerEncoder.getConfigurator().apply(config.getSteerEncoderConfig());
      if (status == StatusCode.OK) {
        break;
      } else {
        DriverStation.reportWarning(
            "Warning: Failure configuring "
                + config.getName()
                + " Steer Encoder :"
                + status.toString()
                + ", "
                + status.getDescription(),
            false);
      }
    }

    if (status != StatusCode.OK) {
      DriverStation.reportError(
          "Error configuring "
              + config.getName()
              + " Steer Encoder :"
              + status.toString()
              + ", "
              + status.getDescription(),
          false);
      return status;
    }

    return status;
  }

  private StatusCode commandMotorsFromRequests(
      ControlRequest driveVelocityVoltage, ControlRequest steerPositionVoltage) {
    // Set the target state for the drive motor
    StatusCode driveStatus = driveMotor.setControl(driveVelocityVoltage);

    if (driveStatus != StatusCode.OK) {
      DriverStation.reportError(
          "Error setting target state for "
              + config.getName()
              + " Drive Motor :"
              + driveStatus.toString()
              + ", "
              + driveStatus.getDescription(),
          false);
    }

    // Set the target state for the steer motor
    StatusCode steerStatus = steerMotor.setControl(steerPositionVoltage);

    if (steerStatus != StatusCode.OK) {
      DriverStation.reportError(
          "Error setting target state for "
              + config.getName()
              + " Steer Motor :"
              + steerStatus.toString()
              + ", "
              + steerStatus.getDescription(),
          false);
    }

    // Check for errors
    if (driveStatus != StatusCode.OK) {
      return driveStatus;
    } else {
      return steerStatus;
    }
  }

  public StatusCode setTargetState(SwerveModuleState state) {
    state.optimize(getPosition().angle);
    state.cosineScale(getPosition().angle);

    // Set the target state for the drive motor
    return commandMotorsFromRequests(
        driveVelocityVoltage.withVelocity(
            state.speedMetersPerSecond / PhysicsConstants.WHEEL_DIAMETER.in(Meters)),
        steerPositionVoltage.withPosition(state.angle.getRotations()));
  }

  public StatusCode setTargetState(SwerveModuleState state, LinearAcceleration feedForward) {
    state.optimize(getPosition().angle);
    state.cosineScale(getPosition().angle);

    // Set the target state for the drive motor
    return commandMotorsFromRequests(
        driveMotionMagicVelocityVoltage
            .withVelocity(state.speedMetersPerSecond / PhysicsConstants.WHEEL_DIAMETER.in(Meters))
            .withFeedForward(feedForward.in(MetersPerSecondPerSecond) * PIDs.DriveMotor.kA),
        steerPositionVoltage.withPosition(state.angle.getRotations()));
  }

  public SwerveModuleState getState() {
    // Get the current state of the module
    return new SwerveModuleState(driveVelocitySupplier.get(), steerPositionSupplier.get());
  }

  public SwerveModulePosition getPosition() {
    // Get the current position of the module
    return new SwerveModulePosition(drivePositionSupplier.get(), steerPositionSupplier.get());
  }

  // Resets drive encoder
  public void resetEncoders() {
    driveMotor.setPosition(0);
  }

  public String getName() {
    return config.getName();
  }

  public void updateTelemetry() {
    // TODO update telemetry
  }
}
