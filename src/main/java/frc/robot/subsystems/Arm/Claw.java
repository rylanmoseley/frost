package frc.robot.subsystems.Arm;

import com.revrobotics.REVLibError;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkMaxConfig;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.PneumaticHub;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Command.InterruptionBehavior;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.Constants.ArmConstants.ClawConstants;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;

public class Claw extends SubsystemBase {
  // TODO Constants
  private SparkMax m_rollerLeft = new SparkMax(0, MotorType.kBrushless);
  private SparkMax m_rollerRight = new SparkMax(0, MotorType.kBrushless);
  private PneumaticHub m_pneumaticHub = new PneumaticHub();
  private DoubleSolenoid m_clamp = m_pneumaticHub.makeDoubleSolenoid(0, 0);
  private DigitalInput m_beamBreak = new DigitalInput(0);

  public final Trigger beamBreak = new Trigger(() -> m_beamBreak.get());

  public final BooleanSupplier isOpen = () -> m_clamp.get() == ClawConstants.clawOpen;
  public final BooleanSupplier isClosed = () -> m_clamp.get() == ClawConstants.clawClosed;
  public final DoubleSupplier rollerOutput = () -> m_rollerLeft.get();

  // TODO dashboard/NT setup
  public Claw() {
    System.out.println("Claw instantiated");
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
              m_clamp.set(ClawConstants.clawOpen);
            })
        .withName("Prepare for Cube")
        .withInterruptBehavior(InterruptionBehavior.kCancelSelf);
  }

  public Command prepareForConeCommand() {
    return this.run(() -> {})
        .withName("Prepare for Cone")
        .withInterruptBehavior(InterruptionBehavior.kCancelSelf);
  }

  public Command receiveCubeCommand() {
    return this.run(() -> {})
        .withName("Receive Cube")
        .withInterruptBehavior(InterruptionBehavior.kCancelSelf);
  }

  public Command receiveConeCommand() {
    return this.run(() -> {})
        .withName("Receive Cone")
        .withInterruptBehavior(InterruptionBehavior.kCancelSelf);
  }

  public Command ejectCubeCommand() {
    return this.run(() -> {})
        .withName("Eject Cube")
        .withInterruptBehavior(InterruptionBehavior.kCancelSelf);
  }

  public Command ejectConeCommand() {
    return this.run(() -> {})
        .withName("Eject Cone")
        .withInterruptBehavior(InterruptionBehavior.kCancelSelf);
  }

  public Command openClawCommand() {
    return this.run(() -> {})
        .withName("Open Claw")
        .withInterruptBehavior(InterruptionBehavior.kCancelSelf);
  }

  public Command closeClawCommand() {
    return this.run(() -> {})
        .withName("Close Claw")
        .withInterruptBehavior(InterruptionBehavior.kCancelSelf);
  }

  @Override
  public void periodic() {
    // TODO update dashboard/NT
  }
}
