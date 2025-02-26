package frc.robot.subsystems.Swerve;

import com.ctre.phoenix6.StatusCode;
import com.ctre.phoenix6.hardware.CANcoder;
import com.ctre.phoenix6.hardware.TalonFX;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.units.measure.LinearAcceleration;
import edu.wpi.first.wpilibj.DriverStation;
import frc.robot.Constants;
import java.util.function.DoubleSupplier;

public class SwerveModule {
  private SwerveModuleConfig config;

  private TalonFX driveMotor;
  private TalonFX steerMotor;
  private CANcoder steerEncoder;

  public final DoubleSupplier totalCurrentDraw = () -> 0;

  public SwerveModule(SwerveModuleConfig config) {
    this.config = config;

    this.driveMotor = config.getDriveMotor();
    this.steerMotor = config.getSteerMotor();
    this.steerEncoder = config.getSteerEncoder();
  }

  public StatusCode configureAll() {
    StatusCode status = StatusCode.OK;
    for (int i = 0; i < Constants.MAX_CONFIG_RETRIES; i++) {
      status = driveMotor.getConfigurator().apply(config.getDriveMotorConfig());

      if (status != StatusCode.OK) {
        return status;
      }

      status = steerMotor.getConfigurator().apply(config.getSteerMotorConfig());

      if (status != StatusCode.OK) {
        return status;
      }

      status = steerEncoder.getConfigurator().apply(config.getSteerEncoderConfig());
    }

    System.out.println(
        "Error configuring "
            + config.getName()
            + " :"
            + status.toString()
            + ", "
            + status.getDescription());
    DriverStation.reportError(
        "Error configuring "
            + config.getName()
            + " :"
            + status.toString()
            + ", "
            + status.getDescription(),
        false);
    return status;
  }

  public void setTargetState(SwerveModuleState state) {
    state.optimize(getPosition().angle);
    state.cosineScale(getPosition().angle);
    // TODO pass state to motors
  }

  public void setTargetState(SwerveModuleState state, LinearAcceleration feedForward) {
    // TODO feedforward handling
  }

  public SwerveModuleState getState() {
    // Get the current state of the module
    return new SwerveModuleState();
  }

  public SwerveModulePosition getPosition() {
    // Get the current position of the module
    return new SwerveModulePosition();
  }

  public void resetEncoders() {
    // Reset the encoders on the module
  }

  public String getName() {
    return config.getName();
  }

  public void updateTelemetry() {
    // TODO update telemetry
  }
}
