package frc.robot.subsystems.Arm;

import com.revrobotics.REVLibError;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkMaxConfig;
import edu.wpi.first.networktables.NetworkTableType;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.PneumaticHub;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Command.InterruptionBehavior;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.Constants.ArmConstants.ClawConstants;
import frc.robot.Constants.ArmConstants.ClawConstants.RollerSpeeds;
import frc.robot.utilities.Telemetry;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;

public class Claw extends SubsystemBase {
  /** Master */
  private SparkMax m_rollerLeft = new SparkMax(ClawConstants.CAN.ROLLER_LEFT, MotorType.kBrushless);

  /** Follower */
  private SparkMax m_rollerRight =
      new SparkMax(ClawConstants.CAN.ROLLER_RIGHT, MotorType.kBrushless);

  private PneumaticHub m_pneumaticHub = new PneumaticHub(ClawConstants.CAN.PNEUMATIC_HUB);
  private DoubleSolenoid m_clamp =
      m_pneumaticHub.makeDoubleSolenoid(
          ClawConstants.PNEUMATIC_CHANNEL.CLAMP_FORWARD,
          ClawConstants.PNEUMATIC_CHANNEL.CLAMP_REVERSE);
  private DigitalInput m_beamBreak = new DigitalInput(ClawConstants.DIO.BEAM_BREAK);

  public final Trigger beamBreak =
      new Trigger(() -> m_beamBreak.get() == ClawConstants.BEAM_BREAK_BROKEN_VALUE);

  public final BooleanSupplier isOpen = () -> m_clamp.get() == ClawConstants.CLAW_OPEN;
  public final BooleanSupplier isClosed = () -> m_clamp.get() == ClawConstants.CLAW_CLOSED;
  public final DoubleSupplier rollerOutput = () -> m_rollerLeft.get();

  public final DoubleSupplier totalCurrentDraw =
      () ->
          m_rollerLeft.getOutputCurrent()
              + m_rollerRight.getOutputCurrent()
              + m_pneumaticHub.getCompressorCurrent()
              + m_pneumaticHub.getSolenoidsTotalCurrent();

  // TODO simulation setup

  public Claw() {
    System.out.println("Claw instantiated");

    Telemetry.addValue("Arm/Claw/Pneumatics/ForwardVoltage", NetworkTableType.kDouble);
    Telemetry.addValue("Arm/Claw/Pneumatics/ReverseVoltage", NetworkTableType.kDouble);
    Telemetry.addValue("Arm/Claw/Pneumatics/PressureSwitch", NetworkTableType.kBoolean);
    Telemetry.addValue("Arm/Claw/Pneumatics/CompressorCurrent", NetworkTableType.kDouble);
    Telemetry.addValue("Arm/Claw/Pneumatics/InputVoltage", NetworkTableType.kDouble);
    Telemetry.addValue("Arm/Claw/Pneumatics/SolenoidsTotalCurrent", NetworkTableType.kDouble);

    Telemetry.addValue("Arm/Claw/BeamBreak/RawValue", NetworkTableType.kBoolean);
    Telemetry.addValue("Arm/Claw/BeamBreak/LogicValue", NetworkTableType.kBoolean);

    Telemetry.addValue("Arm/Claw/Clamp/IsOpen", NetworkTableType.kBoolean);
    Telemetry.addValue("Arm/Claw/Clamp/IsClosed", NetworkTableType.kBoolean);

    Telemetry.addValue("Arm/Claw/Roller/Output", NetworkTableType.kDouble);
    Telemetry.addValue("Arm/Claw/Roller/LeftCurrentDraw", NetworkTableType.kDouble);
    Telemetry.addValue("Arm/Claw/Roller/RightCurrentDraw", NetworkTableType.kDouble);
    Telemetry.addValue("Arm/Claw/Roller/LeftTemperature", NetworkTableType.kDouble);
    Telemetry.addValue("Arm/Claw/Roller/RightTemperature", NetworkTableType.kDouble);
    Telemetry.addValue("Arm/Claw/Roller/LeftVoltageIn", NetworkTableType.kDouble);
    Telemetry.addValue("Arm/Claw/Roller/RightVoltageIn", NetworkTableType.kDouble);
    Telemetry.addValue("Arm/Claw/Roller/LeftHasFault", NetworkTableType.kBoolean);
    Telemetry.addValue("Arm/Claw/Roller/RightHasFault", NetworkTableType.kBoolean);

    Telemetry.addValue("Arm/Claw/TotalCurrentDraw", NetworkTableType.kDouble);
  }

  public REVLibError configureAll(
      SparkMaxConfig rollerLeftConfig, SparkMaxConfig rollerRightConfig) {

    REVLibError leftErr =
        m_rollerLeft.configure(
            rollerLeftConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
    REVLibError rightErr =
        m_rollerRight.configure(
            rollerRightConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);

    // return error, if present
    if (leftErr == REVLibError.kOk) {
      return rightErr;
    }
    return leftErr;
  }

  public Command prepareForCubeCommand() {
    return this.run(
            () -> {
              m_clamp.set(ClawConstants.CLAW_OPEN);
              m_rollerLeft.set(RollerSpeeds.CUBE_INTAKE);
            })
        .withName("Prepare for Cube")
        .withInterruptBehavior(InterruptionBehavior.kCancelSelf);
  }

  public Command prepareForConeCommand() {
    return this.run(
            () -> {
              m_clamp.set(ClawConstants.CLAW_OPEN);
              m_rollerLeft.set(RollerSpeeds.CONE_INTAKE);
            })
        .withName("Prepare for Cone")
        .withInterruptBehavior(InterruptionBehavior.kCancelSelf);
  }

  public Command receiveCubeCommand() {
    return this.run(
            () -> {
              m_clamp.set(ClawConstants.CLAW_OPEN);
              m_rollerLeft.set(RollerSpeeds.CUBE_IDLE);
            })
        .withName("Receive Cube")
        .withInterruptBehavior(InterruptionBehavior.kCancelSelf);
  }

  public Command holdCubeCommand() {
    return this.runOnce(
            () -> {
              m_clamp.set(ClawConstants.CLAW_OPEN);
              m_rollerLeft.set(RollerSpeeds.CUBE_IDLE);
            })
        .withName("Hold Cube")
        .withInterruptBehavior(InterruptionBehavior.kCancelSelf)
        .repeatedly();
  }

  public Command receiveConeCommand() {
    return this.runOnce(
            () -> {
              m_clamp.set(ClawConstants.CLAW_CLOSED);
              m_rollerLeft.set(RollerSpeeds.CONE_IDLE);
            })
        .withName("Receive Cone")
        .withInterruptBehavior(InterruptionBehavior.kCancelSelf);
  }

  public Command holdConeCommand() {
    return this.run(
            () -> {
              m_clamp.set(ClawConstants.CLAW_CLOSED);
              m_rollerLeft.set(RollerSpeeds.CONE_IDLE);
            })
        .withName("Hold Cone")
        .withInterruptBehavior(InterruptionBehavior.kCancelSelf)
        .repeatedly();
  }

  public Command ejectCubeCommand() {
    return this.run(
            () -> {
              m_clamp.set(ClawConstants.CLAW_OPEN);
              m_rollerLeft.set(RollerSpeeds.CUBE_EJECT);
            })
        .withName("Eject Cube")
        .withInterruptBehavior(InterruptionBehavior.kCancelSelf);
  }

  public Command ejectConeCommand() {
    return this.run(
            () -> {
              m_clamp.set(ClawConstants.CLAW_OPEN);
              m_rollerLeft.set(RollerSpeeds.CONE_EJECT);
            })
        .withName("Eject Cone")
        .withInterruptBehavior(InterruptionBehavior.kCancelSelf);
  }

  public Command openClawCommand() {
    return this.runOnce(
            () -> {
              m_clamp.set(ClawConstants.CLAW_OPEN);
            })
        .withName("Open Claw")
        .withInterruptBehavior(InterruptionBehavior.kCancelSelf);
  }

  public Command closeClawCommand() {
    return this.runOnce(
            () -> {
              m_clamp.set(ClawConstants.CLAW_CLOSED);
            })
        .withName("Close Claw")
        .withInterruptBehavior(InterruptionBehavior.kCancelSelf);
  }

  /** To stop rollers, call {@link Claw#stopRollersCommand()} instead */
  public Command setRollerSpeedCommand(DoubleSupplier speed) {
    return this.runOnce(
            () -> {
              m_rollerLeft.set(speed.getAsDouble());
            })
        .withName("Custom roller speed")
        .withInterruptBehavior(InterruptionBehavior.kCancelSelf);
  }

  public Command stopRollersCommand() {
    return this.runOnce(
            () -> {
              m_rollerLeft.set(0);
              // stop the follower too, just in case
              m_rollerRight.set(0);
            })
        .withName("Stop rollers")
        .withInterruptBehavior(InterruptionBehavior.kCancelSelf);
  }

  // on idle, stop rollers and close claw
  public Command idleCommand() {
    return stopRollersCommand()
        .andThen(closeClawCommand())
        .andThen(
            this.run(
                    () -> {
                      // do nothing
                    })
                .withName("Idle"))
        .withInterruptBehavior(InterruptionBehavior.kCancelSelf);
  }

  @Override
  public void periodic() {
    SmartDashboard.putData("Arm/Claw/ClawSubsystem", this);

    Telemetry.setValue(
        "Arm/Claw/Pneumatics/ForwardVoltage",
        m_pneumaticHub.getAnalogVoltage(ClawConstants.PNEUMATIC_CHANNEL.CLAMP_FORWARD));
    Telemetry.setValue(
        "Arm/Claw/Pneumatics/ReverseVoltage",
        m_pneumaticHub.getAnalogVoltage(ClawConstants.PNEUMATIC_CHANNEL.CLAMP_REVERSE));
    Telemetry.setValue("Arm/Claw/Pneumatics/PressureSwitch", m_pneumaticHub.getPressureSwitch());
    Telemetry.setValue(
        "Arm/Claw/Pneumatics/CompressorCurrent", m_pneumaticHub.getCompressorCurrent());
    Telemetry.setValue("Arm/Claw/Pneumatics/InputVoltage", m_pneumaticHub.getInputVoltage());
    Telemetry.setValue(
        "Arm/Claw/Pneumatics/SolenoidsTotalCurrent",
        m_pneumaticHub.getPressure(ClawConstants.PNEUMATIC_CHANNEL.PRESSURE_SWITCH_PORT));

    Telemetry.setValue("Arm/Claw/BeamBreak/RawValue", m_beamBreak.get());
    Telemetry.setValue("Arm/Claw/BeamBreak/LogicValue", beamBreak.getAsBoolean());

    Telemetry.setValue("Arm/Claw/Clamp/IsOpen", isOpen.getAsBoolean());
    Telemetry.setValue("Arm/Claw/Clamp/IsClosed", isClosed.getAsBoolean());

    Telemetry.setValue("Arm/Claw/Roller/Output", rollerOutput.getAsDouble());
    Telemetry.setValue("Arm/Claw/Roller/LeftCurrentDraw", m_rollerLeft.getOutputCurrent());
    Telemetry.setValue("Arm/Claw/Roller/RightCurrentDraw", m_rollerRight.getOutputCurrent());
    Telemetry.setValue("Arm/Claw/Roller/LeftTemperature", m_rollerLeft.getMotorTemperature());
    Telemetry.setValue("Arm/Claw/Roller/RightTemperature", m_rollerRight.getMotorTemperature());
    Telemetry.setValue("Arm/Claw/Roller/LeftVoltageIn", m_rollerLeft.getBusVoltage());
    Telemetry.setValue("Arm/Claw/Roller/RightVoltageIn", m_rollerRight.getBusVoltage());
    Telemetry.setValue("Arm/Claw/Roller/LeftHasFault", m_rollerLeft.hasActiveFault());
    Telemetry.setValue("Arm/Claw/Roller/RightHasFault", m_rollerRight.hasActiveFault());

    Telemetry.setValue("Arm/Claw/TotalCurrentDraw", totalCurrentDraw.getAsDouble());
  }

  @Override
  public void simulationPeriodic() {
    // TODO update simulation
  }
}
