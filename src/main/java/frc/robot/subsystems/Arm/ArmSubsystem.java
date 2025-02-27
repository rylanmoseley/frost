package frc.robot.subsystems.Arm;

import edu.wpi.first.wpilibj.smartdashboard.Mechanism2d;
import edu.wpi.first.wpilibj.smartdashboard.MechanismLigament2d;
import edu.wpi.first.wpilibj.smartdashboard.MechanismRoot2d;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Configs;
import frc.robot.Constants.ArmConstants;
import frc.robot.Constants.ArmConstants.ArmStagesConstants;
import frc.robot.Constants.ArmConstants.Mode;
import frc.robot.Constants.ArmConstants.SimulationConstants;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;
import java.util.function.Supplier;

public class ArmSubsystem extends SubsystemBase {
  private Mode currentMode = ArmConstants.DEFAULT_MODE;
  public final Supplier<Mode> mode = () -> currentMode;

  public final BooleanSupplier isConeMode = () -> currentMode == Mode.CONE;
  public final BooleanSupplier isCubeMode = () -> currentMode == Mode.CUBE;

  private Claw m_claw = null;
  private ArmStage m_stage1 = null;
  private ArmStage m_stage2 = null;
  private ArmStage m_Stage3 = null;

  private Mechanism2d mech2d =
      new Mechanism2d(
          SimulationConstants.MECHANISM2D_WIDTH, SimulationConstants.MECHANISM2D_HEIGHT);
  private MechanismRoot2d mech2dRoot =
      mech2d.getRoot(
          "Arm Root",
          SimulationConstants.MECHANISM2D_ROOT.getX(),
          SimulationConstants.MECHANISM2D_ROOT.getY());
  private MechanismLigament2d stage1Ligament =
      mech2dRoot.append(
          new MechanismLigament2d(
              "Stage1",
              ArmStagesConstants.STAGE_1_CONFIG.getArmLengthInches()
                  * SimulationConstants.MECHANISM2D_PIXELS_PER_INCH,
              0));
  private MechanismLigament2d stage2Ligament =
      stage1Ligament.append(
          new MechanismLigament2d(
              "Stage2",
              ArmStagesConstants.STAGE_2_CONFIG.getArmLengthInches()
                  * SimulationConstants.MECHANISM2D_PIXELS_PER_INCH,
              0));
  private MechanismLigament2d stage3Ligament =
      stage2Ligament.append(
          new MechanismLigament2d(
              "Stage3",
              ArmStagesConstants.STAGE_3_CONFIG.getArmLengthInches()
                  * SimulationConstants.MECHANISM2D_PIXELS_PER_INCH,
              0));

  // TODO pass mech2d to NT

  public final DoubleSupplier totalCurrentDraw =
      () ->
          m_claw.totalCurrentDraw.getAsDouble()
              + m_stage1.totalCurrentDraw.getAsDouble()
              + m_stage2.totalCurrentDraw.getAsDouble()
              + m_Stage3.totalCurrentDraw.getAsDouble();

  public ArmSubsystem() {
    m_claw = new Claw();
    m_stage1 = new ArmStage("Stage1", ArmStagesConstants.STAGE_1_CONFIG);
    m_stage2 = new ArmStage("Stage2", ArmStagesConstants.STAGE_2_CONFIG);
    m_Stage3 = new ArmStage("Stage3", ArmStagesConstants.STAGE_3_CONFIG);

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

  @Override
  public void periodic() {
    if (m_claw == null || m_stage1 == null || m_stage2 == null || m_Stage3 == null) {
      return;
    }

    // TODO straighten out gear ratios
    stage1Ligament.setAngle(
        m_stage1.relativePosition.getAsDouble()
            / ArmConstants.ArmStagesConstants.STAGE_1_CONFIG.getGearRatio());
    stage2Ligament.setAngle(
        m_stage2.relativePosition.getAsDouble() - m_stage1.relativePosition.getAsDouble());
    stage3Ligament.setAngle(
        m_Stage3.relativePosition.getAsDouble()
            - m_stage2.relativePosition.getAsDouble()
            - m_stage1.relativePosition.getAsDouble());
  }
}
