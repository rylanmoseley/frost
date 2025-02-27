package robot;

import static org.junit.jupiter.api.Assertions.assertEquals;

import edu.wpi.first.hal.HAL;
import frc.robot.subsystems.Arm.ArmSubsystem;
import frc.robot.subsystems.LEDSubsystem;
import frc.robot.subsystems.Swerve.SwerveSubsystem;
import frc.robot.utilities.Pigeon;
import frc.robot.utilities.Telemetry;
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

    assertEquals(0, Telemetry.getValue("diagnostics/telemetry/warningCount", 0));
  }

  @Test // marks this method as a test
  void armSubsystemTelemetryTypeSafetyCheck() {
    new ArmSubsystem();

    assertEquals(0, Telemetry.getValue("diagnostics/telemetry/warningCount", 0));
  }

  @Test // marks this method as a test
  void swerveSubsystemTelemetryTypeSafetyCheck() {
    new SwerveSubsystem();

    assertEquals(0, Telemetry.getValue("diagnostics/telemetry/warningCount", 0));
  }

  @Test // marks this method as a test
  void LEDSubsystemTelemetryTypeSafetyCheck() {
    new LEDSubsystem();

    assertEquals(0, Telemetry.getValue("diagnostics/telemetry/warningCount", 0));
  }
}
