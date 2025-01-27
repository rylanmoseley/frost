package frc.robot.subsystems.Arm;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.ArmConstants;
import frc.robot.Constants.ArmConstants.ArmStagesConstants;
import frc.robot.Constants.ArmConstants.Mode;
import java.util.function.DoubleSupplier;
import java.util.function.Supplier;

public class ArmSubsystem extends SubsystemBase {
  private Claw m_claw = new Claw();
  private ArmStage m_stage1 = new ArmStage(ArmStagesConstants.STAGE_1_CONFIG);
  private ArmStage m_stage2 = new ArmStage(ArmStagesConstants.STAGE_2_CONFIG);
  private ArmStage m_Stage3 = new ArmStage(ArmStagesConstants.STAGE_3_CONFIG);

  private Mode currentMode = ArmConstants.DEFAULT_MODE;

  public final Supplier<Mode> mode = () -> currentMode;
  public final DoubleSupplier totalCurrentDraw = () -> m_claw.totalCurrentDraw.getAsDouble();

  public ArmSubsystem() {
    configureAll();

    m_claw.setDefaultCommand(m_claw.idleCommand());
  }

  private void configureAll() {
    m_stage1.configureAll();
    m_stage2.configureAll();
    m_Stage3.configureAll();
    m_claw.configureAll(null, null);
  }
}
