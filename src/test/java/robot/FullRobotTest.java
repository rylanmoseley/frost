package robot;

import static org.junit.jupiter.api.Assertions.assertEquals;

import edu.wpi.first.hal.HAL;
import edu.wpi.first.wpilibj.RobotBase;
import frc.robot.Robot;
import frc.robot.utilities.Telemetry;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FullRobotTest {
  static Thread robotThread = new Thread(() -> RobotBase.startRobot(Robot::new));

  @BeforeEach // this method will run before each test
  void setup() {
    assert HAL.initialize(500, 0); // initialize the HAL, crash if failed
    robotThread.start();
  }

  @Test // marks this method as a test
  void fiveSecondTest() {
    long startTime = System.currentTimeMillis();
    long timeout = 5000; // 5 seconds timeout
    while (System.currentTimeMillis() - startTime < timeout) {
      if (robotThread.getState() == Thread.State.TERMINATED) {
        break;
      }
    }

    assertEquals(0, Telemetry.getWarningCount(), 0);
    return;
  }

  @AfterEach
  void tearDown() {
    Telemetry.clearWarningCount();
  }

  @AfterAll
  static void cleanup() {
    // Stop the robot thread
    if (robotThread.isAlive()) {
      robotThread.interrupt();
    }

    HAL.exitMain();
    HAL.shutdown();
  }
}
