// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.net.WebServer;
import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.Constants.DashboardConstants;
import frc.robot.Constants.OperatorConstants;
import frc.robot.subsystems.Arm.ArmSubsystem;
import frc.robot.subsystems.LEDSubsystem;
import frc.robot.subsystems.Swerve.SwerveSubsystem;
import frc.robot.utilities.CommandDriverController;
import frc.robot.utilities.CommandOperatorController;
import frc.robot.utilities.RobotEvents;
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

    // start dashboard webserver
    if (DashboardConstants.DASHBOARD_ENABLED) {
      System.out.println("Starting Dashboard Webserver");
      System.out.println(
          "Dashboard directory: "
              + Filesystem.getDeployDirectory()
              + "/"
              + DashboardConstants.DASHBOARD_PATH);
      try {
        WebServer.start(
            DashboardConstants.DASHBOARD_PORT,
            Filesystem.getDeployDirectory() + "/" + DashboardConstants.DASHBOARD_PATH);
        if (Robot.isSimulation()) {
          java.awt.Desktop.getDesktop()
              .browse(java.net.URI.create("http://127.0.0.1:" + DashboardConstants.DASHBOARD_PORT));
        }
      } catch (Exception e) {
        System.out.println("Dashboard Webserver failed to start: " + e.getMessage());
      }
    }
    // host deploy directory webserver
    if (DashboardConstants.DEPLOY_SERVER_ENABLED) {
      System.out.println("Starting Deploy Webserver");
      System.out.println(
          "Deploy directory: "
              + Filesystem.getDeployDirectory()
              + "/"
              + DashboardConstants.DEPLOY_SERVER_PATH);
      try {
        WebServer.start(
            DashboardConstants.DEPLOY_SERVER_PORT,
            Filesystem.getDeployDirectory() + "/" + DashboardConstants.DEPLOY_SERVER_PATH);
        if (Robot.isSimulation()) {
          java.awt.Desktop.getDesktop()
              .browse(
                  java.net.URI.create("http://127.0.0.1:" + DashboardConstants.DEPLOY_SERVER_PORT));
        }
      } catch (Exception e) {
        System.out.println("Deploy Webserver failed to start: " + e.getMessage());
      }
    }

    // Configure the trigger bindings
    configureBindings();

    // LED triggers
    m_operatorController
        .coneMode()
        .and(RobotEvents.endgame.negate())
        .onTrue(m_LEDSubsystem.setCone());
    m_operatorController
        .coneMode()
        .and(RobotEvents.endgame)
        .onTrue(m_LEDSubsystem.setEndgameCone());
    m_operatorController
        .cubeMode()
        .and(RobotEvents.endgame.negate())
        .onTrue(m_LEDSubsystem.setCube());
    m_operatorController
        .cubeMode()
        .and(RobotEvents.endgame)
        .onTrue(m_LEDSubsystem.setEndgameCube());
    RobotEvents.robotInitialized.onTrue(m_LEDSubsystem.setInitialize());
    RobotEvents.teleopEnabled.and(m_armSubsystem.isConeMode).onTrue(m_LEDSubsystem.setCone());
    RobotEvents.teleopEnabled.and(m_armSubsystem.isCubeMode).onTrue(m_LEDSubsystem.setCube());
    RobotEvents.robotDisabled.onTrue(m_LEDSubsystem.setDisabled());
    m_operatorController.selfDestruct().onTrue(m_LEDSubsystem.setSelfDestruct());
    m_operatorController.shwerve().onTrue(m_LEDSubsystem.setShwerve());
    RobotEvents.endgame
        .and(m_armSubsystem.isConeMode)
        .onTrue(
            m_LEDSubsystem
                .setEndgameStart()
                .withDeadline(new WaitCommand(2))
                .andThen(m_LEDSubsystem.setEndgameCone()));
    RobotEvents.endgame
        .and(m_armSubsystem.isCubeMode)
        .onTrue(
            m_LEDSubsystem
                .setEndgameStart()
                .withDeadline(new WaitCommand(2))
                .andThen(m_LEDSubsystem.setEndgameCube()));

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
  private void configureBindings() {
    m_swerveSubsystem.setDefaultCommand(
        m_swerveSubsystem.driveFromJoystickCommand(
            m_driverController.getChassisForward(),
            m_driverController.getChassisStrafe(),
            m_driverController.getChassisRotate(),
            m_driverController.getChassisFieldOrient()));
  }

  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */
  public Command getAutonomousCommand() {
    return new InstantCommand();
  }
}
