package frc.robot.subsystems.Arm;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.ArmConstants;
import frc.robot.Constants.ArmConstants.Mode;
import java.util.function.DoubleSupplier;
import java.util.function.Supplier;

public class ArmSubsystem extends SubsystemBase {
  private Claw m_claw = new Claw();
  private Stage1 m_stage1 = new Stage1();
  private Stage2 m_stage2 = new Stage2();
  private Stage3 m_Stage3 = new Stage3();

  private Mode currentMode = ArmConstants.DEFAULT_MODE;

  public final Supplier<Mode> mode = () -> currentMode;
  public final DoubleSupplier totalCurrentDraw = () -> m_claw.totalCurrentDraw.getAsDouble();

  public ArmSubsystem() {
    m_claw.setDefaultCommand(m_claw.idleCommand());
  }
}
