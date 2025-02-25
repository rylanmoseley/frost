package frc.robot.utilities;

import static edu.wpi.first.units.Units.Degrees;
import static edu.wpi.first.units.Units.DegreesPerSecond;
import static edu.wpi.first.units.Units.Fahrenheit;
import static edu.wpi.first.units.Units.Volts;

import com.ctre.phoenix6.StatusCode;
import com.ctre.phoenix6.hardware.Pigeon2;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.networktables.NetworkTableType;
import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.units.measure.AngularVelocity;
import edu.wpi.first.units.measure.Temperature;
import edu.wpi.first.units.measure.Voltage;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.Configs;
import frc.robot.Constants;
import frc.robot.Constants.SwerveConstants.CAN;
import frc.robot.Constants.SwerveConstants.GyroConstants;
import java.util.function.Supplier;

public class Pigeon extends SubsystemBase {
  private static Pigeon instance = null;

  private Pigeon2 pigeon;
  private Supplier<Angle> yawSupplier;
  private Supplier<AngularVelocity> yawVelocitySupplier;
  private Supplier<Angle> pitchSupplier;
  private Supplier<AngularVelocity> pitchVelocitySupplier;
  private Supplier<Angle> rollSupplier;
  private Supplier<AngularVelocity> rollVelocitySupplier;
  private Supplier<Integer> faultsSupplier;
  private Supplier<Integer> stickyFaultsSupplier;
  private Supplier<Voltage> voltageSupplier;
  private Supplier<Temperature> temperatureSupplier;

  private Pigeon() {
    pigeon = new Pigeon2(CAN.PIGEON, CAN.DRIVETRAIN_CANBUS);

    yawSupplier = pigeon.getYaw().asSupplier();
    yawVelocitySupplier = pigeon.getAngularVelocityZWorld().asSupplier();
    pitchSupplier = pigeon.getPitch().asSupplier();
    pitchVelocitySupplier = pigeon.getAngularVelocityXWorld().asSupplier();
    rollSupplier = pigeon.getRoll().asSupplier();
    rollVelocitySupplier = pigeon.getAngularVelocityYWorld().asSupplier();
    faultsSupplier = pigeon.getFaultField().asSupplier();
    stickyFaultsSupplier = pigeon.getStickyFaultField().asSupplier();
    voltageSupplier = pigeon.getSupplyVoltage().asSupplier();
    temperatureSupplier = pigeon.getTemperature().asSupplier();

    configureAll();

    // initialize to zero
    pigeon.setYaw(0);

    System.out.println("Pigeon initialized");

    Telemetry.addValue("Pigeon/Pitch", NetworkTableType.kDouble);
    Telemetry.addValue("Pigeon/PitchVelocity", NetworkTableType.kDouble);
    Telemetry.addValue("Pigeon/Yaw", NetworkTableType.kDouble);
    Telemetry.addValue("Pigeon/YawVelocity", NetworkTableType.kDouble);
    Telemetry.addValue("Pigeon/Roll", NetworkTableType.kDouble);
    Telemetry.addValue("Pigeon/RollVelocity", NetworkTableType.kDouble);
    Telemetry.addValue("Pigeon/Faults", NetworkTableType.kDouble);
    Telemetry.addValue("Pigeon/StickyFaults", NetworkTableType.kDouble);
    Telemetry.addValue("Pigeon/SupplyVoltage", NetworkTableType.kDouble);
    Telemetry.addValue("Pigeon/Temperature", NetworkTableType.kDouble);
  }

  // called automatically in constructor
  public StatusCode configureAll() {
    StatusCode status = StatusCode.GeneralError;
    for (int tries = 0; tries < Constants.MAX_CONFIG_RETRIES; tries++) {
      status = pigeon.getConfigurator().apply(Configs.Drive.PIGEON_CONFIG);

      if (status == StatusCode.OK) {
        return status;
      }
      DriverStation.reportWarning("Warning: Failure configuring Pigeon: " + status, false);
    }
    System.out.println("Error configuring Pigeon: " + status);
    DriverStation.reportError("Error configuring Pigeon: " + status, false);
    return status;
  }

  public static Trigger rollZero =
      new Trigger(
          () ->
              getInstance()
                  .rollSupplier
                  .get()
                  .isNear(Degrees.of(0), GyroConstants.GYRO_ANGLE_TOLERANCE));

  public static Trigger rollStopped =
      new Trigger(
          () ->
              getInstance()
                  .rollVelocitySupplier
                  .get()
                  .isNear(DegreesPerSecond.of(0), GyroConstants.GYRO_VELOCITY_TOLERANCE));

  public static Trigger pitchZero =
      new Trigger(
          () ->
              getInstance()
                  .pitchSupplier
                  .get()
                  .isNear(Degrees.of(0), GyroConstants.GYRO_ANGLE_TOLERANCE));

  public static Trigger pitchStopped =
      new Trigger(
          () ->
              getInstance()
                  .pitchVelocitySupplier
                  .get()
                  .isNear(DegreesPerSecond.of(0), GyroConstants.GYRO_VELOCITY_TOLERANCE));

  private static Pigeon getInstance() {
    if (instance == null) {
      instance = new Pigeon();
    }
    return instance;
  }

  public static Supplier<Angle> getYawSupplier() {
    return getInstance().yawSupplier;
  }

  public static Supplier<AngularVelocity> getYawVelocitySupplier() {
    return getInstance().yawVelocitySupplier;
  }

  public static Supplier<Angle> getPitchSupplier() {
    return getInstance().pitchSupplier;
  }

  public static Supplier<AngularVelocity> getPitchVelocitySupplier() {
    return getInstance().pitchVelocitySupplier;
  }

  public static Supplier<Angle> getRollSupplier() {
    return getInstance().rollSupplier;
  }

  public static Supplier<AngularVelocity> getRollVelocitySupplier() {
    return getInstance().rollVelocitySupplier;
  }

  public static Supplier<Integer> getFaultsSupplier() {
    return getInstance().faultsSupplier;
  }

  public static Supplier<Integer> getStickyFaultsSupplier() {
    return getInstance().stickyFaultsSupplier;
  }

  public static Supplier<Voltage> getVoltageSupplier() {
    return getInstance().voltageSupplier;
  }

  public static Supplier<Temperature> getTemperatureSupplier() {
    return getInstance().temperatureSupplier;
  }

  public static Rotation3d getRotation() {
    return getInstance().pigeon.getRotation3d();
  }

  public static Runnable resetHeading() {
    return () -> getInstance().pigeon.setYaw(0);
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
    Telemetry.setValue("Pigeon/Pitch", pitchSupplier.get().in(Degrees));
    Telemetry.setValue("Pigeon/PitchVelocity", pitchVelocitySupplier.get().in(DegreesPerSecond));
    Telemetry.setValue("Pigeon/Yaw", yawSupplier.get().in(Degrees));
    Telemetry.setValue("Pigeon/YawVelocity", yawVelocitySupplier.get().in(DegreesPerSecond));
    Telemetry.setValue("Pigeon/Roll", rollSupplier.get().in(Degrees));
    Telemetry.setValue("Pigeon/RollVelocity", rollVelocitySupplier.get().in(DegreesPerSecond));
    Telemetry.setValue("Pigeon/Faults", faultsSupplier.get());
    Telemetry.setValue("Pigeon/StickyFaults", stickyFaultsSupplier.get());
    Telemetry.setValue("Pigeon/SupplyVoltage", voltageSupplier.get().in(Volts));
    Telemetry.setValue("Pigeon/Temperature", temperatureSupplier.get().in(Fahrenheit));
  }

  @Override
  public void simulationPeriodic() {
    // This method will be called once per scheduler run during simulation
  }
}
