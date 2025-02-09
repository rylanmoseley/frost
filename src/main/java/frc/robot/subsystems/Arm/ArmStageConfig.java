package frc.robot.subsystems.Arm;

import com.revrobotics.spark.config.SparkMaxConfig;
import edu.wpi.first.math.util.Units;
import frc.robot.Constants.ArmConstants.ArmStagesConstants.POSITIONS;
import java.util.Map;

public class ArmStageConfig {
  private int m_canID;
  private int m_absoluteEncoderDIO;

  private SparkMaxConfig m_motorConfig;

  private Map<POSITIONS, Double> m_positionMap;

  private double m_gearRatio;
  private double m_absoluteEncoderToMotorRatio;

  private double m_positionMargin;
  private double m_velocityMargin;

  private double m_armLength;
  private double m_armMass;
  private double m_minAngleRads;
  private double m_maxAngleRads;

  public ArmStageConfig(
      int canID,
      int absoluteEncoderDIO,
      SparkMaxConfig motorConfig,
      Map<POSITIONS, Double> positionMap,
      double gearRatio,
      double absoluteEncoderToMotorRatio,
      double positionMargin,
      double velocityMargin,
      double armLength,
      double armMass,
      double minAngleRads,
      double maxAngleRads) {
    m_canID = canID;
    m_absoluteEncoderDIO = absoluteEncoderDIO;

    m_motorConfig = motorConfig;

    m_positionMap = positionMap;

    m_gearRatio = gearRatio;
    m_absoluteEncoderToMotorRatio = absoluteEncoderToMotorRatio;

    m_positionMargin = positionMargin;
    m_velocityMargin = velocityMargin;

    m_armLength = armLength;
    m_armMass = armMass;
    m_minAngleRads = minAngleRads;
    m_maxAngleRads = maxAngleRads;
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

  public double getArmLengthMeters() {
    return m_armLength;
  }

  public double getArmLengthInches() {
    return Units.metersToInches(getArmLengthMeters());
  }

  public double getArmMass() {
    return m_armMass;
  }

  public double getMinAngleRads() {
    return m_minAngleRads;
  }

  public double getMaxAngleRads() {
    return m_maxAngleRads;
  }

  public double getMinAngleDegrees() {
    return Math.toDegrees(m_minAngleRads);
  }

  public double getMaxAngleDegrees() {
    return Math.toDegrees(m_maxAngleRads);
  }

  public double getAbsoluteEncoderToMotorRatio() {
    return m_absoluteEncoderToMotorRatio;
  }
}
