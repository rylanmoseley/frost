package frc.robot.subsystems.Swerve;

import com.ctre.phoenix6.StatusCode;
import com.pathplanner.lib.util.DriveFeedforwards;

import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.units.measure.LinearAcceleration;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import java.util.function.DoubleSupplier;

// TODO

public class SwerveModule {
  private SwerveModuleConfig config;

  public final DoubleSupplier totalCurrentDraw = () -> 0;

  public SwerveModule(SwerveModuleConfig config) {
    this.config = config;
    // Initialize all of your necessary components here
  }

  public StatusCode configureAll() {
    // Configure all of your necessary components here
    return StatusCode.OK;
  }

  public void setTargetState(SwerveModuleState state) {
    state.optimize(getPosition().angle);
    state.cosineScale(getPosition().angle);
    //TODO pass state to motors
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

  public String getName () {
    // TODO get name from config
    return "TODO";
  }

  public void updateTelemetry () {
    // TODO update telemetry
  }
}
