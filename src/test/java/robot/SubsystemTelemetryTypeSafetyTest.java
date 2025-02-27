package robot;

import static org.junit.jupiter.api.Assertions.assertEquals;

import edu.wpi.first.hal.HAL;
import frc.robot.subsystems.Arm.ArmSubsystem;
import frc.robot.subsystems.LEDSubsystem;
import frc.robot.subsystems.Swerve.SwerveSubsystem;
import frc.robot.utilities.CommandDriverController;
import frc.robot.utilities.CommandOperatorController;
import frc.robot.utilities.Pigeon;
import frc.robot.utilities.Telemetry;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

@TestInstance(Lifecycle.PER_CLASS)
class SubsystemTelemetryTypeSafetyTest {
  @BeforeAll // this method will run before each test
  void setup() {
    assert HAL.initialize(500, 0); // initialize the HAL, crash if failed
  }

  @Test // marks this method as a test
  void pigeonTelemetryTypeSafetyCheck() {
    Pigeon.getRotation();
  }

  @Test // marks this method as a test
  void armSubsystemTelemetryTypeSafetyCheck() {
    new ArmSubsystem();
  }

  @Test // marks this method as a test
  void swerveSubsystemTelemetryTypeSafetyCheck() {
    new SwerveSubsystem();
  }

  @Test // marks this method as a test
  void LEDSubsystemTelemetryTypeSafetyCheck() {
    new LEDSubsystem();
  }

  @Test // marks this method as a test
  void DriverControllerTelemetryTypeSafetyCheck() {
    new CommandDriverController(0);
  }

  @Test // marks this method as a test
  void OperatorControllerTelemetryTypeSafetyCheck() {
    new CommandOperatorController(1, 2);
  }

  @AfterEach
  void checkTelemetry() {
    // Check if any warnings were logged
    assertEquals(0, Telemetry.getWarningCount(), 0);
  }

  @AfterAll
  void tearDown() {
    Telemetry.clearWarningCount();
  }
}
