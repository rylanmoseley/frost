package frc.robot.subsystems.Arm;

import com.revrobotics.REVLibError;
import com.revrobotics.spark.SparkAbsoluteEncoder;
import com.revrobotics.spark.SparkBase.ControlType;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.SparkClosedLoopController;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkRelativeEncoder;
import edu.wpi.first.networktables.NetworkTableType;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Command.InterruptionBehavior;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.Constants.ArmConstants.ArmStagesConstants.POSITIONS;
import frc.robot.utilities.Telemetry;
import java.util.function.DoubleSupplier;

public class ArmStage extends SubsystemBase {
  private SparkMax m_motor;
  private SparkClosedLoopController m_controller;
  private SparkRelativeEncoder m_relativeEncoder;
  private SparkAbsoluteEncoder m_absoluteEncoder;
  private ArmStageConfig m_config;
  private String m_name;

  private double m_targetPosition;

  public DoubleSupplier relativePosition = () -> m_relativeEncoder.getPosition();
  public DoubleSupplier absolutePosition = () -> m_absoluteEncoder.getPosition();
  public Trigger atPosition =
      new Trigger(
          () ->
              Math.abs(m_targetPosition - m_relativeEncoder.getPosition())
                  < m_config.getPositionMargin());
  public Trigger notMoving =
      new Trigger(() -> Math.abs(m_relativeEncoder.getVelocity()) < m_config.getVelocityMargin());

  public final DoubleSupplier totalCurrentDraw = () -> m_motor.getOutputCurrent();

  public ArmStage(String name, ArmStageConfig config) {
    super(name);
    m_name = name;
    m_config = config;

    m_motor = new SparkMax(config.getCanID(), MotorType.kBrushless);
    m_relativeEncoder = (SparkRelativeEncoder) m_motor.getEncoder();
    m_absoluteEncoder = m_motor.getAbsoluteEncoder();
    m_controller = m_motor.getClosedLoopController();

    System.out.println("ArmStage " + m_name + " instantiated");

    Telemetry.addValue("Arm/" + m_name + "/RelativePosition", NetworkTableType.kDouble);
    Telemetry.addValue("Arm/" + m_name + "/AbsolutePosition", NetworkTableType.kDouble);
    Telemetry.addValue("Arm/" + m_name + "/RelativeVelocityRPM", NetworkTableType.kDouble);
    Telemetry.addValue("Arm/" + m_name + "/TargetPosition", NetworkTableType.kDouble);
    Telemetry.addValue("Arm/" + m_name + "/Output", NetworkTableType.kDouble);
    Telemetry.addValue("Arm/" + m_name + "/CurrentDraw", NetworkTableType.kDouble);
    Telemetry.addValue("Arm/" + m_name + "/Temperature", NetworkTableType.kDouble);
    Telemetry.addValue("Arm/" + m_name + "/HasFault", NetworkTableType.kDouble);
    Telemetry.addValue("Arm/" + m_name + "/VoltageIn", NetworkTableType.kDouble);

    Telemetry.addValue("Arm/" + m_name + "/TotalCurrentDraw", NetworkTableType.kDouble);
  }

  public REVLibError configureAll() {
    return m_motor.configure(
        m_config.getMotorConfig(), ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
  }

  private Command goToNumericPosition(Double position) {
    m_targetPosition = position;
    return this.run(
            () -> {
              m_controller.setReference(m_targetPosition, ControlType.kMAXMotionPositionControl);
            })
        .until(atPosition.and(notMoving))
        .withInterruptBehavior(InterruptionBehavior.kCancelSelf)
        .withName("Go to numeric position " + position);
  }

  private Command goToNamedPositionCommand(POSITIONS position) {
    return goToNumericPosition(m_config.getPositionMap().get(position))
        .withName("Go to " + position);
  }

  public Command goToIdlePositionCommand() {
    return goToNamedPositionCommand(POSITIONS.IDLE);
  }

  public Command goToHighCubePositionCommand() {
    return goToNamedPositionCommand(POSITIONS.HIGH_CUBE);
  }

  public Command goToHighConePositionCommand() {
    return goToNamedPositionCommand(POSITIONS.HIGH_CONE);
  }

  public Command goToMidCubePositionCommand() {
    return goToNamedPositionCommand(POSITIONS.MID_CUBE);
  }

  public Command goToMidConePositionCommand() {
    return goToNamedPositionCommand(POSITIONS.MID_CONE);
  }

  public Command goToLowCubePositionCommand() {
    return goToNamedPositionCommand(POSITIONS.LOW_CUBE);
  }

  public Command goToLowConePositionCommand() {
    return goToNamedPositionCommand(POSITIONS.LOW_CONE);
  }

  public Command goToSubstationPositionCommand() {
    return goToNamedPositionCommand(POSITIONS.SUBSTATION);
  }

  public Command goToGroundPositionCommand() {
    return goToNamedPositionCommand(POSITIONS.GROUND);
  }

  public Command goToGroundTiltPositionCommand() {
    return goToNamedPositionCommand(POSITIONS.GROUND_TILT);
  }

  public Command goToPosition(double position) {
    return goToNumericPosition(position);
  }

  public Command holdCurrentPositionCommand() {
    return goToNumericPosition(m_targetPosition).repeatedly().withName("Hold current position");
  }

  public Command manualControlCommand(DoubleSupplier target) {
    return this.run(
            () -> {
              m_targetPosition = target.getAsDouble();
              m_controller.setReference(m_targetPosition, ControlType.kMAXMotionPositionControl);
            })
        .repeatedly()
        .withInterruptBehavior(InterruptionBehavior.kCancelSelf)
        .withName("Manual control");
  }

  @Override
  public void periodic() {
    Telemetry.setValue("Arm/" + m_name + "/RelativePosition", m_relativeEncoder.getPosition());
    Telemetry.setValue("Arm/" + m_name + "/AbsolutePosition", m_absoluteEncoder.getPosition());
    Telemetry.setValue("Arm/" + m_name + "/RelativeVelocityRPM", m_relativeEncoder.getVelocity());
    Telemetry.setValue("Arm/" + m_name + "/TargetPosition", m_targetPosition);
    Telemetry.setValue("Arm/" + m_name + "/Output", m_motor.getAppliedOutput());
    Telemetry.setValue("Arm/" + m_name + "/CurrentDraw", m_motor.getOutputCurrent());
    Telemetry.setValue("Arm/" + m_name + "/Temperature", m_motor.getMotorTemperature());
    Telemetry.setValue("Arm/" + m_name + "/HasFault", m_motor.hasActiveFault());
    Telemetry.setValue("Arm/" + m_name + "/HasStickyFault", m_motor.hasStickyFault());
    Telemetry.setValue("Arm/" + m_name + "/VoltageIn", m_motor.getBusVoltage());

    Telemetry.setValue("Arm/" + m_name + "/TotalCurrentDraw", totalCurrentDraw.getAsDouble());
  }
}
