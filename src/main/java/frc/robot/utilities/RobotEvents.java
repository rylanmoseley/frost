package frc.robot.utilities;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.Robot;

public class RobotEvents {
  public static Trigger robotInitialized = new Trigger(() -> true);
  public static Trigger realRobot = new Trigger(Robot::isReal);
  public static Trigger simulationRobot = new Trigger(Robot::isSimulation);
  public static Trigger robotEnabled = new Trigger(DriverStation::isEnabled);
  public static Trigger robotDisabled = new Trigger(DriverStation::isDisabled);
  public static Trigger autonomousEnabled = new Trigger(DriverStation::isAutonomousEnabled);
  public static Trigger autonomous = new Trigger(DriverStation::isAutonomous);
  public static Trigger dsAttached = new Trigger(DriverStation::isDSAttached);
  public static Trigger fmsAttached = new Trigger(DriverStation::isFMSAttached);
  public static Trigger testEnabled = new Trigger(DriverStation::isTestEnabled);
  public static Trigger test = new Trigger(DriverStation::isTest);
  public static Trigger teleopEnabled = new Trigger(DriverStation::isTeleopEnabled);
  public static Trigger teleop = new Trigger(DriverStation::isTeleop);
  public static Trigger emergencyStopped = new Trigger(DriverStation::isEStopped);

  private static double lastMatchTime = 0;

  /** Driver Station Teleop Mode */
  public static Trigger DSTeleop =
      new Trigger(
              () -> {
                if (!teleopEnabled.getAsBoolean()) {
                  return false;
                }
                if (lastMatchTime == 0) {
                  lastMatchTime = DriverStation.getMatchTime();
                  return DriverStation.getMatchTime()
                      <= 10; // 10 is arbitrary, but should distinguish between starting at 0 and
                  // starting at the full match time, to determine if counting up or down
                }
                boolean ds = DriverStation.getMatchTime() - lastMatchTime >= 0;
                lastMatchTime = DriverStation.getMatchTime();
                return ds;
              })
          .and(teleopEnabled);

  /** FMS or Practice Mode */
  public static Trigger FMSTeleop = DSTeleop.negate().and(teleopEnabled);

  public static Trigger endgame =
      new Trigger(() -> DriverStation.getMatchTime() < 30).and(FMSTeleop);
}
