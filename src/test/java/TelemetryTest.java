import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import edu.wpi.first.hal.HAL;
import edu.wpi.first.networktables.NetworkTableType;
import frc.robot.utilities.Telemetry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TelemetryTest {
  @BeforeEach // this method will run before each test
  void setup() {
    assert HAL.initialize(500, 0); // initialize the HAL, crash if failed
  }

  @Test // marks this method as a test
  void addDoubleEntry() {
    Telemetry.addReadableValue("test1", NetworkTableType.kDouble);

    assertEquals(0, Telemetry.getValue("test1", -1.0));
  }

  @Test
  void addStringEntry() {
    Telemetry.addReadableValue("test2", NetworkTableType.kString);

    assertEquals("", Telemetry.getValue("test2", "not default"));
  }

  @Test
  void addBooleanEntry() {
    Telemetry.addReadableValue("test3", NetworkTableType.kBoolean);

    assertEquals(false, Telemetry.getValue("test3", true));
  }

  @Test
  void addIntegerEntry() {
    Telemetry.addReadableValue("test4", NetworkTableType.kInteger);

    assertEquals(0, Telemetry.getValue("test4", -1));
  }

  @Test
  void addDoubleArrayEntry() {
    Telemetry.addReadableValue("test5", NetworkTableType.kDoubleArray);

    double[] nonDefaultValue = {1.0, 2.0, 3.0};
    double[] defaultValue = {};
    assertArrayEquals(defaultValue, Telemetry.getValue("test5", nonDefaultValue));
  }

  @Test
  void addStringArrayEntry() {
    Telemetry.addReadableValue("test6", NetworkTableType.kStringArray);

    String[] nonDefaultValue = {"a", "b", "c"};
    String[] defaultValue = {};
    assertArrayEquals(defaultValue, Telemetry.getValue("test6", nonDefaultValue));
  }

  @Test
  void addBooleanArrayEntry() {
    Telemetry.addReadableValue("test7", NetworkTableType.kBooleanArray);

    boolean[] nonDefaultValue = {true, false, true};
    boolean[] defaultValue = {};
    assertArrayEquals(defaultValue, Telemetry.getValue("test7", nonDefaultValue));
  }

  @Test
  void addIntegerArrayEntry() {
    Telemetry.addReadableValue("test8", NetworkTableType.kIntegerArray);

    long[] nonDefaultValue = {1, 2, 3};
    long[] defaultValue = {};
    assertArrayEquals(defaultValue, Telemetry.getValue("test8", nonDefaultValue));
  }

  @Test
  void addRawEntry() {
    Telemetry.addReadableValue("test9", NetworkTableType.kRaw);

    byte[] nonDefaultValue = {1, 2, 3};
    byte[] defaultValue = {};
    assertArrayEquals(defaultValue, Telemetry.getValue("test9", nonDefaultValue));
  }

  @Test
  void setAndGetDouble() {
    Telemetry.addReadableValue("test10", NetworkTableType.kDouble);
    Telemetry.setValue("test10", 25.0);

    assertEquals(25.0, Telemetry.getValue("test10", 0.0));
  }

  @Test
  void setAndGetString() {
    Telemetry.addReadableValue("test11", NetworkTableType.kString);
    Telemetry.setValue("test11", "hello");

    assertEquals("hello", Telemetry.getValue("test11", "not hello"));
  }

  @Test
  void setAndGetBoolean() {
    Telemetry.addReadableValue("test12", NetworkTableType.kBoolean);
    Telemetry.setValue("test12", true);

    assertEquals(true, Telemetry.getValue("test12", false));
  }

  @Test
  void setAndGetInteger() {
    Telemetry.addReadableValue("test13", NetworkTableType.kInteger);
    Telemetry.setValue("test13", 25);

    assertEquals(25, Telemetry.getValue("test13", 0));
  }

  @Test
  void setAndGetDoubleArray() {
    Telemetry.addReadableValue("test14", NetworkTableType.kDoubleArray);
    double[] value = {1.0, 2.0, 3.0};
    Telemetry.setValue("test14", value);

    double[] defaultValue = {};
    assertArrayEquals(value, Telemetry.getValue("test14", defaultValue));
  }

  @Test
  void setAndGetStringArray() {
    Telemetry.addReadableValue("test15", NetworkTableType.kStringArray);
    String[] value = {"a", "b", "c"};
    Telemetry.setValue("test15", value);

    String[] defaultValue = {};
    assertArrayEquals(value, Telemetry.getValue("test15", defaultValue));
  }

  @Test
  void setAndGetBooleanArray() {
    Telemetry.addReadableValue("test16", NetworkTableType.kBooleanArray);
    boolean[] value = {true, false, true};
    Telemetry.setValue("test16", value);

    boolean[] defaultValue = {};
    assertArrayEquals(value, Telemetry.getValue("test16", defaultValue));
  }

  @Test
  void getValueWithoutAdding() {
    assertEquals("", Telemetry.getValue("test17", "hello"));
  }
}
