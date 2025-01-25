// Rylan 2025

/*
 *
 * NT4 Made Easy
 *
 * This is great for simple telemetry, where all you need is to add, set, and get values
 * It's basically NT3 but on NT4
 *
 */

package frc.robot.utilities;

import edu.wpi.first.networktables.*;
import edu.wpi.first.wpilibj.DriverStation;
import frc.robot.Constants.TelemetryConstants;
import java.util.HashMap;

public class Telemetry {
  private static Telemetry telemetryInstance = null;

  private class TelemetryItem {
    public Publisher publisher;
    public Subscriber subscriber;
    public NetworkTableType type;

    public TelemetryItem(Publisher publisher, Subscriber subscriber, NetworkTableType type) {
      this.publisher = publisher;
      this.subscriber = subscriber;
      this.type = type;
    }
  }

  private NetworkTableInstance ntInstance = NetworkTableInstance.getDefault();
  private NetworkTable telemetryTable = ntInstance.getTable(TelemetryConstants.ROOT_NAME);
  private HashMap<String, TelemetryItem> entries = new HashMap<String, TelemetryItem>();

  private Telemetry() {
    System.out.println(
        "Telemetry singleton instantiated, writing to /" + TelemetryConstants.ROOT_NAME + "/");
  }

  private static synchronized Telemetry getTelemetry() {
    if (telemetryInstance == null) telemetryInstance = new Telemetry();

    return telemetryInstance;
  }

  public static void startSimulationServer() {
    getTelemetry().ntInstance.startServer();
  }

  public static void addValue(String name, NetworkTableType type, PubSubOption... options) {
    Telemetry table = getTelemetry();
    Topic top;
    Publisher pub;
    Subscriber sub;
    switch (type) {
      case kBoolean:
        top = table.telemetryTable.getBooleanTopic(name);
        pub = ((BooleanTopic) top).publish(options);
        sub = ((BooleanTopic) top).subscribe(false, options);
        ((BooleanPublisher) pub).set(false);
        break;
      case kBooleanArray:
        top = table.telemetryTable.getBooleanArrayTopic(name);
        pub = ((BooleanArrayTopic) top).publish(options);
        sub = ((BooleanArrayTopic) top).subscribe(new boolean[] {}, options);
        ((BooleanArrayPublisher) pub).set(new boolean[] {});
        break;
      case kDouble:
        top = table.telemetryTable.getDoubleTopic(name);
        pub = ((DoubleTopic) top).publish(options);
        sub = ((DoubleTopic) top).subscribe(0, options);
        ((DoublePublisher) pub).set(0);
        break;
      case kDoubleArray:
        top = table.telemetryTable.getDoubleArrayTopic(name);
        pub = ((DoubleArrayTopic) top).publish(options);
        sub = ((DoubleArrayTopic) top).subscribe(new double[] {}, options);
        ((DoubleArrayPublisher) pub).set(new double[] {});
        break;
      case kFloat:
        top = table.telemetryTable.getFloatTopic(name);
        pub = ((FloatTopic) top).publish(options);
        sub = ((FloatTopic) top).subscribe(0, options);
        ((FloatPublisher) pub).set(0);
        break;
      case kFloatArray:
        top = table.telemetryTable.getFloatArrayTopic(name);
        pub = ((FloatArrayTopic) top).publish(options);
        sub = ((FloatArrayTopic) top).subscribe(new float[] {}, options);
        ((FloatArrayPublisher) pub).set(new float[] {});
        break;
      case kInteger:
        top = table.telemetryTable.getIntegerTopic(name);
        pub = ((IntegerTopic) top).publish(options);
        sub = ((IntegerTopic) top).subscribe(0, options);
        ((IntegerPublisher) pub).set(0);
        break;
      case kIntegerArray:
        top = table.telemetryTable.getIntegerArrayTopic(name);
        pub = ((IntegerArrayTopic) top).publish(options);
        sub = ((IntegerArrayTopic) top).subscribe(new long[] {}, options);
        ((IntegerArrayPublisher) pub).set(new long[] {});
        break;
      case kRaw:
        top = table.telemetryTable.getRawTopic(name);
        pub = ((RawTopic) top).publish(type.getValueStr(), options);
        sub = ((RawTopic) top).subscribe(type.getValueStr(), new byte[] {}, options);
        ((RawPublisher) pub).set(new byte[] {});
        break;
      case kString:
        top = table.telemetryTable.getStringTopic(name);
        pub = ((StringTopic) top).publish(options);
        sub = ((StringTopic) top).subscribe("", options);
        ((StringPublisher) pub).set("");
        break;
      case kStringArray:
        top = table.telemetryTable.getStringArrayTopic(name);
        pub = ((StringArrayTopic) top).publish(options);
        sub = ((StringArrayTopic) top).subscribe(new String[] {}, options);
        ((StringArrayPublisher) pub).set(new String[] {});
        break;
      case kUnassigned:
      default:
        DriverStation.reportWarning(
            "Telemetry: invalid type " + type.getValueStr() + " for entry " + name, false);
        return;
    }
    table.entries.put(name, table.new TelemetryItem(pub, sub, type));
  }

  public static void setValue(String name, boolean value) {
    Telemetry table = getTelemetry();
    TelemetryItem item = table.entries.get(name);
    if (item.type == NetworkTableType.kBoolean) {
      ((BooleanPublisher) item.publisher).set(value);
    } else {
      DriverStation.reportWarning(
          "Telemetry: type mismatch during get "
              + name
              + ": expected boolean, got "
              + item.type.getValueStr(),
          false);
    }
  }

  public static void setValue(String name, boolean[] value) {
    Telemetry table = getTelemetry();
    TelemetryItem item = table.entries.get(name);
    if (item.type == NetworkTableType.kBooleanArray) {
      ((BooleanArrayPublisher) item.publisher).set(value);
    } else {
      DriverStation.reportWarning(
          "Telemetry: type mismatch during get "
              + name
              + ": expected boolean[], got "
              + item.type.getValueStr(),
          false);
    }
  }

  public static void setValue(String name, double value) {
    Telemetry table = getTelemetry();
    TelemetryItem item = table.entries.get(name);
    if (item.type == NetworkTableType.kDouble) {
      ((DoublePublisher) item.publisher).set(value);
    } else {
      DriverStation.reportWarning(
          "Telemetry: type mismatch during get "
              + name
              + ": expected double, got "
              + item.type.getValueStr(),
          false);
    }
  }

  public static void setValue(String name, double[] value) {
    Telemetry table = getTelemetry();
    TelemetryItem item = table.entries.get(name);
    if (item.type == NetworkTableType.kDoubleArray) {
      ((DoubleArrayPublisher) item.publisher).set(value);
    } else {
      DriverStation.reportWarning(
          "Telemetry: type mismatch during get "
              + name
              + ": expected double[], got "
              + item.type.getValueStr(),
          false);
    }
  }

  public static void setValue(String name, float value) {
    Telemetry table = getTelemetry();
    TelemetryItem item = table.entries.get(name);
    if (item.type == NetworkTableType.kFloat) {
      ((FloatPublisher) item.publisher).set(value);
    } else {
      DriverStation.reportWarning(
          "Telemetry: type mismatch during get "
              + name
              + ": expected float, got "
              + item.type.getValueStr(),
          false);
    }
  }

  public static void setValue(String name, float[] value) {
    Telemetry table = getTelemetry();
    TelemetryItem item = table.entries.get(name);
    if (item.type == NetworkTableType.kFloatArray) {
      ((FloatArrayPublisher) item.publisher).set(value);
    } else {
      DriverStation.reportWarning(
          "Telemetry: type mismatch during get "
              + name
              + ": expected float[], got "
              + item.type.getValueStr(),
          false);
    }
  }

  public static void setValue(String name, int value) {
    Telemetry table = getTelemetry();
    TelemetryItem item = table.entries.get(name);
    if (item.type == NetworkTableType.kInteger) {
      ((IntegerPublisher) item.publisher).set(value);
    } else {
      DriverStation.reportWarning(
          "Telemetry: type mismatch during get "
              + name
              + ": expected int, got "
              + item.type.getValueStr(),
          false);
    }
  }

  public static void setValue(String name, long[] value) {
    Telemetry table = getTelemetry();
    TelemetryItem item = table.entries.get(name);
    if (item.type == NetworkTableType.kIntegerArray) {
      ((IntegerArrayPublisher) item.publisher).set(value);
    } else {
      DriverStation.reportWarning(
          "Telemetry: type mismatch during get "
              + name
              + ": expected int[], got "
              + item.type.getValueStr(),
          false);
    }
  }

  public static void setValue(String name, String value) {
    Telemetry table = getTelemetry();
    TelemetryItem item = table.entries.get(name);
    if (item.type == NetworkTableType.kString) {
      ((StringPublisher) item.publisher).set(value);
    } else {
      DriverStation.reportWarning(
          "Telemetry: type mismatch during get "
              + name
              + ": expected String, got "
              + item.type.getValueStr(),
          false);
    }
  }

  public static void setValue(String name, String[] value) {
    Telemetry table = getTelemetry();
    TelemetryItem item = table.entries.get(name);
    if (item.type == NetworkTableType.kStringArray) {
      ((StringArrayPublisher) item.publisher).set(value);
    } else {
      DriverStation.reportWarning(
          "Telemetry: type mismatch during get "
              + name
              + ": expected String[], got "
              + item.type.getValueStr(),
          false);
    }
  }

  public static void setValue(String name, byte[] value) {
    Telemetry table = getTelemetry();
    TelemetryItem item = table.entries.get(name);
    if (item.type == NetworkTableType.kRaw) {
      ((RawPublisher) item.publisher).set(value);
    } else {
      DriverStation.reportWarning(
          "Telemetry: type mismatch during get "
              + name
              + ": expected byte[], got "
              + item.type.getValueStr(),
          false);
    }
  }

  public static void setValue(String name, Object value) {
    DriverStation.reportWarning(
        "Telemetry: generic setValue() called for "
            + name
            + " with type "
            + value.getClass().getName()
            + ", expected primitive",
        false);
  }

  public static void setValue(String name, Object[] value) {
    DriverStation.reportWarning(
        "Telemetry: generic setValue() called for "
            + name
            + " with type "
            + value.getClass().getName()
            + "[], expected primitive[]",
        false);
  }

  public static double getValue(String name, double defaultValue) {
    Telemetry table = getTelemetry();

    if (!table.entries.containsKey(name)) {
      DriverStation.reportWarning(
          "Telemetry: entry " + name + " does not exist, creating with type double", false);
      System.out.println("Telemetry: entry " + name + " does not exist, creating with type double");
      addValue(name, NetworkTableType.kDouble);
    }

    TelemetryItem item = table.entries.get(name);
    if (item.type == NetworkTableType.kDouble) {
      return ((DoubleSubscriber) item.subscriber).get(defaultValue);
    } else {
      DriverStation.reportWarning(
          "Telemetry: type mismatch during get "
              + name
              + ": expected double, got "
              + item.type.getValueStr(),
          false);
      return defaultValue;
    }
  }

  public static boolean getValue(String name, boolean defaultValue) {
    Telemetry table = getTelemetry();

    if (!table.entries.containsKey(name)) {
      DriverStation.reportWarning(
          "Telemetry: entry " + name + " does not exist, creating with type boolean", false);
      System.out.println(
          "Telemetry: entry " + name + " does not exist, creating with type boolean");
      addValue(name, NetworkTableType.kBoolean);
    }

    TelemetryItem item = table.entries.get(name);
    if (item.type == NetworkTableType.kBoolean) {
      return ((BooleanSubscriber) item.subscriber).get(defaultValue);
    } else {
      DriverStation.reportWarning(
          "Telemetry: type mismatch during get "
              + name
              + ": expected boolean, got "
              + item.type.getValueStr(),
          false);
      return defaultValue;
    }
  }

  public static double[] getValue(String name, double[] defaultValue) {
    Telemetry table = getTelemetry();

    if (!table.entries.containsKey(name)) {
      DriverStation.reportWarning(
          "Telemetry: entry " + name + " does not exist, creating with type double[]", false);
      System.out.println(
          "Telemetry: entry " + name + " does not exist, creating with type double[]");
      addValue(name, NetworkTableType.kDoubleArray);
    }

    TelemetryItem item = table.entries.get(name);
    if (item.type == NetworkTableType.kDoubleArray) {
      return ((DoubleArraySubscriber) item.subscriber).get(defaultValue);
    } else {
      DriverStation.reportWarning(
          "Telemetry: type mismatch during get "
              + name
              + ": expected double[], got "
              + item.type.getValueStr(),
          false);
      return defaultValue;
    }
  }

  public static boolean[] getValue(String name, boolean[] defaultValue) {
    Telemetry table = getTelemetry();

    if (!table.entries.containsKey(name)) {
      DriverStation.reportWarning(
          "Telemetry: entry " + name + " does not exist, creating with type boolean[]", false);
      System.out.println(
          "Telemetry: entry " + name + " does not exist, creating with type boolean[]");
      addValue(name, NetworkTableType.kBooleanArray);
    }

    TelemetryItem item = table.entries.get(name);
    if (item.type == NetworkTableType.kBooleanArray) {
      return ((BooleanArraySubscriber) item.subscriber).get(defaultValue);
    } else {
      DriverStation.reportWarning(
          "Telemetry: type mismatch during get "
              + name
              + ": expected boolean[], got "
              + item.type.getValueStr(),
          false);
      return defaultValue;
    }
  }

  public static float getValue(String name, float defaultValue) {
    Telemetry table = getTelemetry();

    if (!table.entries.containsKey(name)) {
      DriverStation.reportWarning(
          "Telemetry: entry " + name + " does not exist, creating with type float", false);
      System.out.println("Telemetry: entry " + name + " does not exist, creating with type float");
      addValue(name, NetworkTableType.kFloat);
    }

    TelemetryItem item = table.entries.get(name);
    if (item.type == NetworkTableType.kFloat) {
      return ((FloatSubscriber) item.subscriber).get(defaultValue);
    } else {
      DriverStation.reportWarning(
          "Telemetry: type mismatch during get "
              + name
              + ": expected float, got "
              + item.type.getValueStr(),
          false);
      return defaultValue;
    }
  }

  public static float[] getValue(String name, float[] defaultValue) {
    Telemetry table = getTelemetry();

    if (!table.entries.containsKey(name)) {
      DriverStation.reportWarning(
          "Telemetry: entry " + name + " does not exist, creating with type float[]", false);
      System.out.println(
          "Telemetry: entry " + name + " does not exist, creating with type float[]");
      addValue(name, NetworkTableType.kFloatArray);
    }

    TelemetryItem item = table.entries.get(name);
    if (item.type == NetworkTableType.kFloatArray) {
      return ((FloatArraySubscriber) item.subscriber).get(defaultValue);
    } else {
      DriverStation.reportWarning(
          "Telemetry: type mismatch during get "
              + name
              + ": expected float[], got "
              + item.type.getValueStr(),
          false);
      return defaultValue;
    }
  }

  public static long getValue(String name, int defaultValue) {
    Telemetry table = getTelemetry();

    if (!table.entries.containsKey(name)) {
      DriverStation.reportWarning(
          "Telemetry: entry " + name + " does not exist, creating with type int", false);
      System.out.println("Telemetry: entry " + name + " does not exist, creating with type int");
      addValue(name, NetworkTableType.kInteger);
    }

    TelemetryItem item = table.entries.get(name);
    if (item.type == NetworkTableType.kInteger) {
      return ((IntegerSubscriber) item.subscriber).get(defaultValue);
    } else {
      DriverStation.reportWarning(
          "Telemetry: type mismatch during get "
              + name
              + ": expected int, got "
              + item.type.getValueStr(),
          false);
      return defaultValue;
    }
  }

  public static long[] getValue(String name, long[] defaultValue) {
    Telemetry table = getTelemetry();

    if (!table.entries.containsKey(name)) {
      DriverStation.reportWarning(
          "Telemetry: entry " + name + " does not exist, creating with type int[]", false);
      System.out.println("Telemetry: entry " + name + " does not exist, creating with type int[]");
      addValue(name, NetworkTableType.kIntegerArray);
    }

    TelemetryItem item = table.entries.get(name);
    if (item.type == NetworkTableType.kIntegerArray) {
      return ((IntegerArraySubscriber) item.subscriber).get(defaultValue);
    } else {
      DriverStation.reportWarning(
          "Telemetry: type mismatch during get "
              + name
              + ": expected int[], got "
              + item.type.getValueStr(),
          false);
      return defaultValue;
    }
  }

  public static String getValue(String name, String defaultValue) {
    Telemetry table = getTelemetry();

    if (!table.entries.containsKey(name)) {
      DriverStation.reportWarning(
          "Telemetry: entry " + name + " does not exist, creating with type String", false);
      System.out.println("Telemetry: entry " + name + " does not exist, creating with type String");
      addValue(name, NetworkTableType.kString);
    }

    TelemetryItem item = table.entries.get(name);
    if (item.type == NetworkTableType.kString) {
      return ((StringSubscriber) item.subscriber).get(defaultValue);
    } else {
      DriverStation.reportWarning(
          "Telemetry: type mismatch during get "
              + name
              + ": expected String, got "
              + item.type.getValueStr(),
          false);
      return defaultValue;
    }
  }

  public static String[] getValue(String name, String[] defaultValue) {
    Telemetry table = getTelemetry();

    if (!table.entries.containsKey(name)) {
      DriverStation.reportWarning(
          "Telemetry: entry " + name + " does not exist, creating with type String[]", false);
      System.out.println(
          "Telemetry: entry " + name + " does not exist, creating with type String[]");
      addValue(name, NetworkTableType.kStringArray);
    }

    TelemetryItem item = table.entries.get(name);
    if (item.type == NetworkTableType.kStringArray) {
      return ((StringArraySubscriber) item.subscriber).get(defaultValue);
    } else {
      DriverStation.reportWarning(
          "Telemetry: type mismatch during get "
              + name
              + ": expected String[], got "
              + item.type.getValueStr(),
          false);
      return defaultValue;
    }
  }

  public static byte[] getValue(String name, byte[] defaultValue) {
    Telemetry table = getTelemetry();

    if (!table.entries.containsKey(name)) {
      DriverStation.reportWarning(
          "Telemetry: entry " + name + " does not exist, creating with type byte[]", false);
      System.out.println("Telemetry: entry " + name + " does not exist, creating with type byte[]");
      addValue(name, NetworkTableType.kRaw);
    }

    TelemetryItem item = table.entries.get(name);
    if (item.type == NetworkTableType.kRaw) {
      return ((RawSubscriber) item.subscriber).get(defaultValue);
    } else {
      DriverStation.reportWarning(
          "Telemetry: type mismatch during get "
              + name
              + ": expected byte[], got "
              + item.type.getValueStr(),
          false);
      return defaultValue;
    }
  }

  public static Object getValue(String name, Object defaultValue) {
    DriverStation.reportWarning(
        "Telemetry: generic getValue() called for "
            + name
            + " with type "
            + defaultValue.getClass().getName()
            + ", expected primitive",
        false);
    return defaultValue;
  }

  public static Object getValue(String name, Object[] defaultValue) {
    DriverStation.reportWarning(
        "Telemetry: generic getValue() called for "
            + name
            + " with type "
            + defaultValue.getClass().getName()
            + "[], expected primitive[]",
        false);
    return defaultValue;
  }
}
