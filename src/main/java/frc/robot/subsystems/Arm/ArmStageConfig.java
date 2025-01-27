package frc.robot.subsystems.Arm;

import com.revrobotics.spark.config.SparkMaxConfig;

public class ArmStageConfig {
    private int m_canID;
    private int m_absoluteEncoderDIO;
    private SparkMaxConfig m_motorConfig;
    private double m_gearRatio;

    public ArmStageConfig(int canID, double gearRatio, SparkMaxConfig motorConfig) {
        m_canID = canID;
        m_motorConfig = motorConfig;
        m_gearRatio = gearRatio;
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
}
