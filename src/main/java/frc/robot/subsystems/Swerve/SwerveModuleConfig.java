package frc.robot.subsystems.Swerve;

import com.ctre.phoenix6.CANBus;
import com.ctre.phoenix6.configs.CANcoderConfiguration;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.hardware.CANcoder;
import com.ctre.phoenix6.hardware.TalonFX;

public class SwerveModuleConfig {
  private String name;
  private CANBus canbus;
  private int driveMotorID;
  private int steerMotorID;
  private int steerEncoderID;
  private double steerEncoderOffset;

  public SwerveModuleConfig(
      String name,
      CANBus canbus,
      int driveMotorID,
      int steerMotorID,
      int steerEncoderID,
      double steerEncoderOffset) {
    this.name = name;
    this.canbus = canbus;
    this.driveMotorID = driveMotorID;
    this.steerMotorID = steerMotorID;
    this.steerEncoderID = steerEncoderID;
    this.steerEncoderOffset = steerEncoderOffset;
  }

  public String getName() {
    return name;
  }

  public int getDriveMotorID() {
    return driveMotorID;
  }

  public int getSteerMotorID() {
    return steerMotorID;
  }

  public int getSteerEncoderID() {
    return steerEncoderID;
  }

  public double getSteerEncoderOffset() {
    return steerEncoderOffset;
  }

  public TalonFX getDriveMotor() {
    return new TalonFX(driveMotorID, canbus);
  }

  public TalonFX getSteerMotor() {
    return new TalonFX(steerMotorID, canbus);
  }

  public CANcoder getSteerEncoder() {
    return new CANcoder(steerEncoderID, canbus);
  }

  public TalonFXConfiguration getDriveMotorConfig() {
    TalonFXConfiguration config = new TalonFXConfiguration();
    // Configure the drive motor here
    return config;
  }

  public TalonFXConfiguration getSteerMotorConfig() {
    TalonFXConfiguration config = new TalonFXConfiguration();
    // Configure the steer motor here
    return config;
  }

  public CANcoderConfiguration getSteerEncoderConfig() {
    CANcoderConfiguration config = new CANcoderConfiguration();
    // Configure the steer encoder here
    return config;
  }
}
