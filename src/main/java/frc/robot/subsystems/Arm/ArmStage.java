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
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.ArmConstants.ArmStagesConstants.POSITIONS;
import java.util.function.DoubleSupplier;

public class ArmStage extends SubsystemBase {
  private SparkMax m_motor;
  private SparkClosedLoopController m_controller;
  private SparkRelativeEncoder m_relativeEncoder;
  private SparkAbsoluteEncoder m_absoluteEncoder;
  private ArmStageConfig m_config;

  public DoubleSupplier relativePosition = () -> m_relativeEncoder.getPosition();
  public DoubleSupplier absolutePosition = () -> m_absoluteEncoder.getPosition();

  public ArmStage(ArmStageConfig config) {
    m_config = config;

    m_motor = new SparkMax(config.getCanID(), MotorType.kBrushless);
    m_relativeEncoder = (SparkRelativeEncoder) m_motor.getEncoder();
    m_absoluteEncoder = m_motor.getAbsoluteEncoder();
    m_controller = m_motor.getClosedLoopController();
  }

  public REVLibError configureAll() {
    return m_motor.configure(
        m_config.getMotorConfig(), ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
  }

  private Command goToPositionCommand(POSITIONS position) {
    return this.run(
            () -> {
              m_controller.setReference(
                  m_config.getPositionMap().get(position), ControlType.kMAXMotionPositionControl);
            })
        .withName("Go to position " + position);
  }
}
