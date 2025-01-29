package frc.robot.subsystems.Arm;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Configs;
import frc.robot.Constants.ArmConstants;
import frc.robot.Constants.ArmConstants.ArmStagesConstants;
import frc.robot.Constants.ArmConstants.Mode;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;
import java.util.function.Supplier;

public class ArmSubsystem extends SubsystemBase {
  private Mode currentMode = ArmConstants.DEFAULT_MODE;
  public final Supplier<Mode> mode = () -> currentMode;

  public final BooleanSupplier isConeMode = () -> currentMode == Mode.CONE;
  public final BooleanSupplier isCubeMode = () -> currentMode == Mode.CUBE;

  private Claw m_claw = new Claw();
  private ArmStage m_stage1 = new ArmStage("Stage1", ArmStagesConstants.STAGE_1_CONFIG);
  private ArmStage m_stage2 = new ArmStage("Stage2", ArmStagesConstants.STAGE_2_CONFIG, m_stage1.adjustedPosition);
  private ArmStage m_Stage3 = new ArmStage("Stage3", ArmStagesConstants.STAGE_3_CONFIG, m_stage2.adjustedPosition);

  public final DoubleSupplier totalCurrentDraw =
      () ->
          m_claw.totalCurrentDraw.getAsDouble()
              + m_stage1.totalCurrentDraw.getAsDouble()
              + m_stage2.totalCurrentDraw.getAsDouble()
              + m_Stage3.totalCurrentDraw.getAsDouble();

  public ArmSubsystem() {
    configureAll();

    m_claw.setDefaultCommand(m_claw.idleCommand());
    m_stage1.setDefaultCommand(m_stage1.holdCurrentPositionCommand());
    m_stage2.setDefaultCommand(m_stage2.holdCurrentPositionCommand());
    m_Stage3.setDefaultCommand(m_Stage3.holdCurrentPositionCommand());
  }

  private void configureAll() {
    m_stage1.configureAll();
    m_stage2.configureAll();
    m_Stage3.configureAll();
    m_claw.configureAll(Configs.Arm.ROLLER_LEFT_CONFIG, Configs.Arm.ROLLER_RIGHT_CONFIG);
  }

  private void setMode(Mode mode) {
    currentMode = mode;
  }

  public void setConeMode() {
    setMode(Mode.CONE);
  }

  public void setCubeMode() {
    setMode(Mode.CUBE);
  }
}
