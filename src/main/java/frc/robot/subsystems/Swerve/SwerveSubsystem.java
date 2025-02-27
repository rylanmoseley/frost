package frc.robot.subsystems.Swerve;

import static edu.wpi.first.units.Units.Degrees;

import com.ctre.phoenix6.StatusCode;
import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.util.DriveFeedforwards;
import edu.wpi.first.math.estimator.SwerveDrivePoseEstimator;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.units.measure.LinearAcceleration;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.Constants.SwerveConstants;
import frc.robot.utilities.Pigeon;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;
import java.util.function.Supplier;

public class SwerveSubsystem extends SubsystemBase {

  private SwerveModule[] m_swerveModules =
      new SwerveModule[] {
        new SwerveModule(SwerveConstants.FRONT_LEFT_CONFIG),
        new SwerveModule(SwerveConstants.FRONT_RIGHT_CONFIG),
        new SwerveModule(SwerveConstants.BACK_LEFT_CONFIG),
        new SwerveModule(SwerveConstants.BACK_RIGHT_CONFIG),
      };
  private SwerveDrivePoseEstimator m_poseEstimator =
      new SwerveDrivePoseEstimator(
          SwerveConstants.KINEMATICS,
          Rotation2d.fromDegrees(Pigeon.getYawSupplier().get().in(Degrees)),
          new SwerveModulePosition[] {
            m_swerveModules[0].getPosition(),
            m_swerveModules[1].getPosition(),
            m_swerveModules[2].getPosition(),
            m_swerveModules[3].getPosition()
          },
          new Pose2d());

  private SendableChooser<Command> m_autoChooser;

  public final DoubleSupplier totalCurrentDraw =
      () -> {
        double draw = 0;
        for (SwerveModule module : m_swerveModules) {
          draw += module.totalCurrentDraw.getAsDouble();
        }
        return draw;
      };

  public final Supplier<Pose2d> poseSupplier = this::getPose;
  public final Supplier<ChassisSpeeds> robotRelativeSpeedsSupplier = this::getRobotRelativeSpeeds;
  public final Supplier<ChassisSpeeds> fieldRelativeSpeedsSupplier = this::getFieldRelativeSpeeds;
  public final Supplier<Rotation2d> yawSupplier =
      () -> Rotation2d.fromDegrees(Pigeon.getYawSupplier().get().in(Degrees));

  public SwerveSubsystem() {
    configureAll();

    System.out.println("Swerve initialized");

    // Reset the pose estimator
    resetOdometry(new Pose2d());

    if (AutoBuilder.isConfigured()) {
      System.out.println("AutoBuilder is already configured, skipping configuration.");
      return;
    }

    AutoBuilder.configure(
        this::getPose,
        this::resetOdometry,
        this::getRobotRelativeSpeeds,
        (speeds, feedForwards) -> driveFromChassisSpeeds(speeds, feedForwards),
        SwerveConstants.HOLONOMIC_DRIVE_CONTROLLER,
        SwerveConstants.robotConfig,
        () -> {
          var alliance = DriverStation.getAlliance();
          if (alliance.isPresent()) {
            return alliance.get() == DriverStation.Alliance.Red;
          }
          return false;
        },
        this);

    m_autoChooser = AutoBuilder.buildAutoChooser();
    System.out.println("Auto chooser initialized");
  }

  private StatusCode configureAll() {
    // Configure all of your necessary components here
    StatusCode status = StatusCode.GeneralError;
    moduleLoop:
    for (SwerveModule module : m_swerveModules) {
      for (int tries = 0; tries < Constants.MAX_CONFIG_RETRIES; tries++) {
        status = module.configureAll();

        if (status == StatusCode.OK) {
          continue moduleLoop;
        }
        DriverStation.reportWarning(
            "Warning: Failure configuring " + module.getName() + ": " + status, false);
      }
      DriverStation.reportError("Error configuring " + module.getName() + ": " + status, false);
    }
    return status;
  }

  public Command driveFromJoystickCommand(
      DoubleSupplier forward,
      DoubleSupplier strafe,
      DoubleSupplier rotation,
      BooleanSupplier fieldRelative) {
    return this.run(
            () -> {
              ChassisSpeeds speeds =
                  new ChassisSpeeds(
                      forward.getAsDouble(), strafe.getAsDouble(), rotation.getAsDouble());
              if (fieldRelative.getAsBoolean()) {
                driveFromChassisSpeeds(
                    ChassisSpeeds.fromFieldRelativeSpeeds(
                        speeds, Rotation2d.fromDegrees(Pigeon.getYawSupplier().get().in(Degrees))));
              } else {
                driveFromChassisSpeeds(speeds);
              }
            })
        .repeatedly()
        .withName("Joystick Drive");
  }

  public Command driveRobotRelativeCommand(Supplier<ChassisSpeeds> chassisSpeeds) {
    return this.run(
            () -> {
              driveFromChassisSpeeds(chassisSpeeds.get());
            })
        .repeatedly()
        .withName("Robot Relative Drive");
  }

  public Command driveFieldRelativeCommand(Supplier<ChassisSpeeds> chassisSpeeds) {
    return this.run(
            () -> {
              driveFromChassisSpeeds(
                  ChassisSpeeds.fromFieldRelativeSpeeds(
                      chassisSpeeds.get(),
                      Rotation2d.fromDegrees(Pigeon.getYawSupplier().get().in(Degrees))));
            })
        .repeatedly()
        .withName("Field Relative Drive");
  }

  public Command driveXCommand() {
    return this.run(
            () -> {
              driveFromModuleStates(
                  new SwerveModuleState[] { // TODO X angles
                    new SwerveModuleState(0, new Rotation2d()),
                    new SwerveModuleState(0, new Rotation2d()),
                    new SwerveModuleState(0, new Rotation2d()),
                    new SwerveModuleState(0, new Rotation2d())
                  });
            })
        .repeatedly()
        .withName("X Drive");
  }

  private void driveFromChassisSpeeds(ChassisSpeeds chassisSpeeds) {
    ChassisSpeeds discreteSpeeds = ChassisSpeeds.discretize(chassisSpeeds, 0.02);
    SwerveModuleState[] states = SwerveConstants.KINEMATICS.toSwerveModuleStates(discreteSpeeds);
    SwerveDriveKinematics.desaturateWheelSpeeds(
        states, SwerveConstants.PhysicsConstants.MAX_STRAFE_SPEED);
    driveFromModuleStates(states);
  }

  private void driveFromModuleStates(SwerveModuleState[] states) {
    for (int module = 0; module < 4; module++) {
      m_swerveModules[module].setTargetState(states[module]);
    }
  }

  private void driveFromChassisSpeeds(ChassisSpeeds chassisSpeeds, DriveFeedforwards feedForwards) {
    ChassisSpeeds discreteSpeeds = ChassisSpeeds.discretize(chassisSpeeds, 0.02);
    SwerveModuleState[] states = SwerveConstants.KINEMATICS.toSwerveModuleStates(discreteSpeeds);
    SwerveDriveKinematics.desaturateWheelSpeeds(
        states, SwerveConstants.PhysicsConstants.MAX_STRAFE_SPEED);
    driveFromModuleStates(states, feedForwards.accelerations());
  }

  private void driveFromModuleStates(
      SwerveModuleState[] states, LinearAcceleration[] feedForwards) {
    for (int module = 0; module < 4; module++) {
      m_swerveModules[module].setTargetState(states[module], feedForwards[module]);
    }
  }

  public ChassisSpeeds getRobotRelativeSpeeds() {
    return SwerveConstants.KINEMATICS.toChassisSpeeds(
        new SwerveModuleState[] {
          m_swerveModules[0].getState(),
          m_swerveModules[1].getState(),
          m_swerveModules[2].getState(),
          m_swerveModules[3].getState()
        });
  }

  public ChassisSpeeds getFieldRelativeSpeeds() {
    return ChassisSpeeds.fromRobotRelativeSpeeds(
        getRobotRelativeSpeeds(),
        Rotation2d.fromDegrees(Pigeon.getYawSupplier().get().in(Degrees)));
  }

  public Pose2d getPose() {
    return m_poseEstimator.getEstimatedPosition();
  }

  public void resetOdometry(Pose2d pose) {
    m_poseEstimator.resetPosition(
        Rotation2d.fromDegrees(Pigeon.getYawSupplier().get().in(Degrees)),
        new SwerveModulePosition[] {
          m_swerveModules[0].getPosition(),
          m_swerveModules[1].getPosition(),
          m_swerveModules[2].getPosition(),
          m_swerveModules[3].getPosition()
        },
        pose);
  }

  /** no subsystem requirement */
  public InstantCommand resetOdometryCommand(Pose2d pose) {
    return new InstantCommand(() -> resetOdometry(pose));
  }

  public SendableChooser<Command> getAutoChooser() {
    return m_autoChooser;
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
    m_poseEstimator.update(
        Rotation2d.fromDegrees(Pigeon.getYawSupplier().get().in(Degrees)),
        new SwerveModulePosition[] {
          m_swerveModules[0].getPosition(),
          m_swerveModules[1].getPosition(),
          m_swerveModules[2].getPosition(),
          m_swerveModules[3].getPosition()
        });

    // TODO system telemetry

    for (SwerveModule module : m_swerveModules) {
      module.updateTelemetry();
    }
  }
}
