package frc.robot.subsystems.Arm;

import com.revrobotics.spark.config.SparkMaxConfig;
import frc.robot.Constants.ArmConstants.ArmStagesConstants.POSITIONS;
import java.util.Map;

public class ArmStageConfig {
  private int m_canID;
  private int m_absoluteEncoderDIO;
  private SparkMaxConfig m_motorConfig;
  private Map<POSITIONS, Double> m_positionMap;
  private double m_gearRatio;
  private double m_positionMargin;
  private double m_velocityMargin;

  public ArmStageConfig(
      int canID, double gearRatio, SparkMaxConfig motorConfig, Map<POSITIONS, Double> positionMap, double positionMargin, double velocityMargin) {
    m_canID = canID;
    m_motorConfig = motorConfig;
    m_gearRatio = gearRatio;
    m_positionMap = positionMap;
    m_positionMargin = positionMargin;
    m_velocityMargin = velocityMargin;
  }

  /** Motor CAN ID */
  public int getCanID() {
    return m_canID;
  }

  /** Absolute encoder DIO port */
  public int getDIO() {
    return m_absoluteEncoderDIO;
  }

  public SparkMaxConfig getMotorConfig() {
    return m_motorConfig;
  }

  public double getGearRatio() {
    return m_gearRatio;
  }

  public Map<POSITIONS, Double> getPositionMap() {
    return m_positionMap;
  }

  public double getPositionMargin() {
    return m_positionMargin;
  }

  public double getVelocityMargin() {
    return m_velocityMargin;
  }
}
