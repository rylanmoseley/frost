package frc.robot.subsystems.Arm;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import java.util.function.DoubleSupplier;

public class ArmSubsystem extends SubsystemBase {
  private Claw m_claw = new Claw();
  private Stage1 m_stage1 = new Stage1();
  private Stage2 m_stage2 = new Stage2();
  private Stage3 m_Stage3 = new Stage3();

  public ArmSubsystem() {}

  public final DoubleSupplier totalCurrentDraw = () -> 0;
}
