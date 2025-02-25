// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.Constants.OperatorConstants;
import frc.robot.subsystems.Arm.ArmSubsystem;
import frc.robot.subsystems.LEDSubsystem;
import frc.robot.subsystems.Swerve.SwerveSubsystem;
import frc.robot.utilities.CommandDriverController;
import frc.robot.utilities.CommandOperatorController;
import java.util.function.DoubleSupplier;

/**
 * This class is where the bulk of the robot should be declared. Since Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in the {@link Robot}
 * periodic methods (other than the scheduler calls). Instead, the structure of the robot (including
 * subsystems, commands, and trigger mappings) should be declared here.
 */
public class RobotContainer {
  // The robot's subsystems and commands are defined here...
  private ArmSubsystem m_armSubsystem = new ArmSubsystem();
  private SwerveSubsystem m_swerveSubsystem = new SwerveSubsystem();
  private LEDSubsystem m_LEDSubsystem = new LEDSubsystem();

  private final CommandDriverController m_driverController =
      new CommandDriverController(OperatorConstants.kDriverControllerPort);

  private final CommandOperatorController m_operatorController =
      new CommandOperatorController(
          OperatorConstants.kOperatorControllerLeftPort,
          OperatorConstants.kOperatorControllerRightPort);

  /** The container for the robot. Contains subsystems, OI devices, and commands. */
  public RobotContainer() {
    // Configure the trigger bindings
    configureBindings();

    SmartDashboard.putData("CommandScheduler", CommandScheduler.getInstance());
  }

  /** Accumulates the active current draw of all subsystems/mechanisms */
  public final DoubleSupplier totalRobotCurrentDraw =
      () ->
          m_armSubsystem.totalCurrentDraw.getAsDouble()
              + m_swerveSubsystem.totalCurrentDraw.getAsDouble()
              + m_LEDSubsystem.totalCurrentDraw.getAsDouble();

  /**
   * Use this method to define your trigger->command mappings. Triggers can be created via the
   * {@link Trigger#Trigger(java.util.function.BooleanSupplier)} constructor with an arbitrary
   * predicate, or via the named factories in {@link
   * edu.wpi.first.wpilibj2.command.button.CommandGenericHID}'s subclasses for {@link
   * CommandXboxController Xbox}/{@link edu.wpi.first.wpilibj2.command.button.CommandPS4Controller
   * PS4} controllers or {@link edu.wpi.first.wpilibj2.command.button.CommandJoystick Flight
   * joysticks}.
   */
  private void configureBindings() {}

  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */
  public Command getAutonomousCommand() {
    return new InstantCommand();
  }
}
