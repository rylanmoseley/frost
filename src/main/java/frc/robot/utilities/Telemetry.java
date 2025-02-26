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
import edu.wpi.first.util.function.FloatSupplier;
import edu.wpi.first.wpilibj.DriverStation;
import frc.robot.Constants.TelemetryConstants;
import java.util.HashMap;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

public class Telemetry {
  private static Telemetry telemetryInstance = null;

  private class TelemetryItem {
    public Publisher publisher;
    public boolean isReadable = false;
    public Subscriber subscriber;
    public NetworkTableType type;

    public TelemetryItem(
        Publisher publisher, Subscriber subscriber, boolean readable, NetworkTableType type) {
      this.publisher = publisher;
      this.subscriber = subscriber;
      this.isReadable = readable;
      this.type = type;
    }

    public void subscribe(Subscriber subscriber) {
      this.subscriber = subscriber;
      isReadable = true;
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
    switch (type) {
      case kBoolean:
        top = table.telemetryTable.getBooleanTopic(name);
        pub = ((BooleanTopic) top).publish(options);
        ((BooleanPublisher) pub).set(false);
        break;
      case kBooleanArray:
        top = table.telemetryTable.getBooleanArrayTopic(name);
        pub = ((BooleanArrayTopic) top).publish(options);
        ((BooleanArrayPublisher) pub).set(new boolean[] {});
        break;
      case kDouble:
        top = table.telemetryTable.getDoubleTopic(name);
        pub = ((DoubleTopic) top).publish(options);
        ((DoublePublisher) pub).set(0);
        break;
      case kDoubleArray:
        top = table.telemetryTable.getDoubleArrayTopic(name);
        pub = ((DoubleArrayTopic) top).publish(options);
        ((DoubleArrayPublisher) pub).set(new double[] {});
        break;
      case kFloat:
        top = table.telemetryTable.getFloatTopic(name);
        pub = ((FloatTopic) top).publish(options);
        ((FloatPublisher) pub).set(0);
        break;
      case kFloatArray:
        top = table.telemetryTable.getFloatArrayTopic(name);
        pub = ((FloatArrayTopic) top).publish(options);
        ((FloatArrayPublisher) pub).set(new float[] {});
        break;
      case kInteger:
        top = table.telemetryTable.getIntegerTopic(name);
        pub = ((IntegerTopic) top).publish(options);
        ((IntegerPublisher) pub).set(0);
        break;
      case kIntegerArray:
        top = table.telemetryTable.getIntegerArrayTopic(name);
        pub = ((IntegerArrayTopic) top).publish(options);
        ((IntegerArrayPublisher) pub).set(new long[] {});
        break;
      case kRaw:
        top = table.telemetryTable.getRawTopic(name);
        pub = ((RawTopic) top).publish(type.getValueStr(), options);
        ((RawPublisher) pub).set(new byte[] {});
        break;
      case kString:
        top = table.telemetryTable.getStringTopic(name);
        pub = ((StringTopic) top).publish(options);
        ((StringPublisher) pub).set("");
        break;
      case kStringArray:
        top = table.telemetryTable.getStringArrayTopic(name);
        pub = ((StringArrayTopic) top).publish(options);
        ((StringArrayPublisher) pub).set(new String[] {});
        break;
      case kUnassigned:
      default:
        DriverStation.reportWarning(
            "Telemetry: invalid type " + type.getValueStr() + " for entry " + name, false);
        return;
    }
    table.entries.put(name, table.new TelemetryItem(pub, null, false, type));
  }

  public static void addReadableValue(String name, NetworkTableType type, PubSubOption... options) {
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
    table.entries.put(name, table.new TelemetryItem(pub, sub, true, type));
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

  public static DoubleSupplier subscribe(String name, double defaultValue) {
    Telemetry table = getTelemetry();

    if (!table.entries.containsKey(name)) {
      DriverStation.reportWarning(
          "Telemetry: entry " + name + " does not exist, creating with type double", false);
      System.out.println("Telemetry: entry " + name + " does not exist, creating with type double");
      addReadableValue(name, NetworkTableType.kDouble);
    }

    TelemetryItem item = table.entries.get(name);

    DoubleSubscriber sub;

    if (item.type != NetworkTableType.kDouble) {
      DriverStation.reportWarning(
          "Telemetry: type mismatch during subscribe "
              + name
              + ": expected double, got "
              + item.type.getValueStr()
              + ", returning default value",
          false);
      return () -> defaultValue;
    }

    if (item.isReadable == false) {
      sub = ((DoubleTopic) item.publisher.getTopic()).subscribe(defaultValue);
      item.subscribe(sub);
    } else {
      sub = (DoubleSubscriber) item.subscriber;
    }

    return sub;
  }

  public static Supplier<double[]> subscribe(String name, double[] defaultValue) {
    Telemetry table = getTelemetry();

    if (!table.entries.containsKey(name)) {
      DriverStation.reportWarning(
          "Telemetry: entry " + name + " does not exist, creating with type double[]", false);
      System.out.println(
          "Telemetry: entry " + name + " does not exist, creating with type double[]");
      addReadableValue(name, NetworkTableType.kDoubleArray);
    }

    TelemetryItem item = table.entries.get(name);

    DoubleArraySubscriber sub;

    if (item.type != NetworkTableType.kDoubleArray) {
      DriverStation.reportWarning(
          "Telemetry: type mismatch during subscribe "
              + name
              + ": expected double[], got "
              + item.type.getValueStr()
              + ", returning default value",
          false);
      return () -> defaultValue;
    }

    if (item.isReadable == false) {
      sub = ((DoubleArrayTopic) item.publisher.getTopic()).subscribe(defaultValue);
      item.subscribe(sub);
    } else {
      sub = (DoubleArraySubscriber) item.subscriber;
    }

    return sub;
  }

  public static FloatSupplier subscribe(String name, float defaultValue) {
    Telemetry table = getTelemetry();

    if (!table.entries.containsKey(name)) {
      DriverStation.reportWarning(
          "Telemetry: entry " + name + " does not exist, creating with type float", false);
      System.out.println("Telemetry: entry " + name + " does not exist, creating with type float");
      addReadableValue(name, NetworkTableType.kFloat);
    }

    TelemetryItem item = table.entries.get(name);

    FloatSubscriber sub;

    if (item.type != NetworkTableType.kFloat) {
      DriverStation.reportWarning(
          "Telemetry: type mismatch during subscribe "
              + name
              + ": expected float, got "
              + item.type.getValueStr()
              + ", returning default value",
          false);
      return () -> defaultValue;
    }

    if (item.isReadable == false) {
      sub = ((FloatTopic) item.publisher.getTopic()).subscribe(defaultValue);
      item.subscribe(sub);
    } else {
      sub = (FloatSubscriber) item.subscriber;
    }

    return sub;
  }

  public static Supplier<float[]> subscribe(String name, float[] defaultValue) {
    Telemetry table = getTelemetry();

    if (!table.entries.containsKey(name)) {
      DriverStation.reportWarning(
          "Telemetry: entry " + name + " does not exist, creating with type float[]", false);
      System.out.println(
          "Telemetry: entry " + name + " does not exist, creating with type float[]");
      addReadableValue(name, NetworkTableType.kFloatArray);
    }

    TelemetryItem item = table.entries.get(name);

    FloatArraySubscriber sub;

    if (item.type != NetworkTableType.kFloatArray) {
      DriverStation.reportWarning(
          "Telemetry: type mismatch during subscribe "
              + name
              + ": expected float[], got "
              + item.type.getValueStr()
              + ", returning default value",
          false);
      return () -> defaultValue;
    }

    if (item.isReadable == false) {
      sub = ((FloatArrayTopic) item.publisher.getTopic()).subscribe(defaultValue);
      item.subscribe(sub);
    } else {
      sub = (FloatArraySubscriber) item.subscriber;
    }

    return sub;
  }

  public static LongSupplier subscribe(String name, int defaultValue) {
    Telemetry table = getTelemetry();

    if (!table.entries.containsKey(name)) {
      DriverStation.reportWarning(
          "Telemetry: entry " + name + " does not exist, creating with type int", false);
      System.out.println("Telemetry: entry " + name + " does not exist, creating with type int");
      addReadableValue(name, NetworkTableType.kInteger);
    }

    TelemetryItem item = table.entries.get(name);

    IntegerSubscriber sub;

    if (item.type != NetworkTableType.kInteger) {
      DriverStation.reportWarning(
          "Telemetry: type mismatch during subscribe "
              + name
              + ": expected int, got "
              + item.type.getValueStr()
              + ", returning default value",
          false);
      return () -> defaultValue;
    }

    if (item.isReadable == false) {
      sub = ((IntegerTopic) item.publisher.getTopic()).subscribe(defaultValue);
      item.subscribe(sub);
    } else {
      sub = (IntegerSubscriber) item.subscriber;
    }

    return sub;
  }

  public static Supplier<long[]> subscribe(String name, long[] defaultValue) {
    Telemetry table = getTelemetry();

    if (!table.entries.containsKey(name)) {
      DriverStation.reportWarning(
          "Telemetry: entry " + name + " does not exist, creating with type int[]", false);
      System.out.println("Telemetry: entry " + name + " does not exist, creating with type int[]");
      addReadableValue(name, NetworkTableType.kIntegerArray);
    }

    TelemetryItem item = table.entries.get(name);

    IntegerArraySubscriber sub;

    if (item.type != NetworkTableType.kIntegerArray) {
      DriverStation.reportWarning(
          "Telemetry: type mismatch during subscribe "
              + name
              + ": expected int[], got "
              + item.type.getValueStr()
              + ", returning default value",
          false);
      return () -> defaultValue;
    }

    if (item.isReadable == false) {
      sub = ((IntegerArrayTopic) item.publisher.getTopic()).subscribe(defaultValue);
      item.subscribe(sub);
    } else {
      sub = (IntegerArraySubscriber) item.subscriber;
    }

    return sub;
  }

  public static Supplier<String> subscribe(String name, String defaultValue) {
    Telemetry table = getTelemetry();

    if (!table.entries.containsKey(name)) {
      DriverStation.reportWarning(
          "Telemetry: entry " + name + " does not exist, creating with type String", false);
      System.out.println("Telemetry: entry " + name + " does not exist, creating with type String");
      addReadableValue(name, NetworkTableType.kString);
    }

    TelemetryItem item = table.entries.get(name);

    StringSubscriber sub;

    if (item.type != NetworkTableType.kString) {
      DriverStation.reportWarning(
          "Telemetry: type mismatch during subscribe "
              + name
              + ": expected String, got "
              + item.type.getValueStr()
              + ", returning default value",
          false);
      return () -> defaultValue;
    }

    if (item.isReadable == false) {
      sub = ((StringTopic) item.publisher.getTopic()).subscribe(defaultValue);
      item.subscribe(sub);
    } else {
      sub = (StringSubscriber) item.subscriber;
    }

    return sub;
  }

  public static Supplier<String[]> subscribe(String name, String[] defaultValue) {
    Telemetry table = getTelemetry();

    if (!table.entries.containsKey(name)) {
      DriverStation.reportWarning(
          "Telemetry: entry " + name + " does not exist, creating with type String[]", false);
      System.out.println(
          "Telemetry: entry " + name + " does not exist, creating with type String[]");
      addReadableValue(name, NetworkTableType.kStringArray);
    }

    TelemetryItem item = table.entries.get(name);

    StringArraySubscriber sub;

    if (item.type != NetworkTableType.kStringArray) {
      DriverStation.reportWarning(
          "Telemetry: type mismatch during subscribe "
              + name
              + ": expected String[], got "
              + item.type.getValueStr()
              + ", returning default value",
          false);
      return () -> defaultValue;
    }

    if (item.isReadable == false) {
      sub = ((StringArrayTopic) item.publisher.getTopic()).subscribe(defaultValue);
      item.subscribe(sub);
    } else {
      sub = (StringArraySubscriber) item.subscriber;
    }

    return sub;
  }

  public static Supplier<byte[]> subscribe(String name, byte[] defaultValue, String typeString) {
    Telemetry table = getTelemetry();

    if (!table.entries.containsKey(name)) {
      DriverStation.reportWarning(
          "Telemetry: entry " + name + " does not exist, creating with type byte[]", false);
      System.out.println("Telemetry: entry " + name + " does not exist, creating with type byte[]");
      addReadableValue(name, NetworkTableType.kRaw);
    }

    TelemetryItem item = table.entries.get(name);

    RawSubscriber sub;

    if (item.type != NetworkTableType.kRaw) {
      DriverStation.reportWarning(
          "Telemetry: type mismatch during subscribe "
              + name
              + ": expected byte[], got "
              + item.type.getValueStr()
              + ", returning default value",
          false);
      return () -> defaultValue;
    }

    if (item.isReadable == false) {
      sub = ((RawTopic) item.publisher.getTopic()).subscribe(typeString, defaultValue);
      item.subscribe(sub);
    } else {
      sub = (RawSubscriber) item.subscriber;
    }

    return sub;
  }

  public static BooleanSupplier subscribe(String name, boolean defaultValue) {
    Telemetry table = getTelemetry();

    if (!table.entries.containsKey(name)) {
      DriverStation.reportWarning(
          "Telemetry: entry " + name + " does not exist, creating with type boolean", false);
      System.out.println(
          "Telemetry: entry " + name + " does not exist, creating with type boolean");
      addReadableValue(name, NetworkTableType.kBoolean);
    }

    TelemetryItem item = table.entries.get(name);

    BooleanSubscriber sub;

    if (item.type != NetworkTableType.kBoolean) {
      DriverStation.reportWarning(
          "Telemetry: type mismatch during subscribe "
              + name
              + ": expected boolean, got "
              + item.type.getValueStr()
              + ", returning default value",
          false);
      return () -> defaultValue;
    }

    if (item.isReadable == false) {
      sub = ((BooleanTopic) item.publisher.getTopic()).subscribe(defaultValue);
      item.subscribe(sub);
    } else {
      sub = (BooleanSubscriber) item.subscriber;
    }

    return sub;
  }

  public static Supplier<boolean[]> subscribe(String name, boolean[] defaultValue) {
    Telemetry table = getTelemetry();

    if (!table.entries.containsKey(name)) {
      DriverStation.reportWarning(
          "Telemetry: entry " + name + " does not exist, creating with type boolean[]", false);
      System.out.println(
          "Telemetry: entry " + name + " does not exist, creating with type boolean[]");
      addReadableValue(name, NetworkTableType.kBooleanArray);
    }

    TelemetryItem item = table.entries.get(name);

    BooleanArraySubscriber sub;

    if (item.type != NetworkTableType.kBooleanArray) {
      DriverStation.reportWarning(
          "Telemetry: type mismatch during subscribe "
              + name
              + ": expected boolean[], got "
              + item.type.getValueStr()
              + ", returning default value",
          false);
      return () -> defaultValue;
    }

    if (item.isReadable == false) {
      sub = ((BooleanArrayTopic) item.publisher.getTopic()).subscribe(defaultValue);
      item.subscribe(sub);
    } else {
      sub = (BooleanArraySubscriber) item.subscriber;
    }

    return sub;
  }

  public static void subscribe(String name, Object defaultValue) {
    DriverStation.reportWarning(
        "Telemetry: generic subscribe() called for "
            + name
            + " with type "
            + defaultValue.getClass().getName()
            + ", expected primitive",
        false);
  }

  public static void subscribe(String name, Object[] defaultValue) {
    DriverStation.reportWarning(
        "Telemetry: generic subscribe() called for "
            + name
            + " with type "
            + defaultValue.getClass().getName()
            + "[], expected primitive[]",
        false);
  }

  public static double getValue(String name, double defaultValue) {
    Telemetry table = getTelemetry();

    if (!table.entries.containsKey(name)) {
      DriverStation.reportWarning(
          "Telemetry: entry " + name + " does not exist, creating with type double", false);
      System.out.println("Telemetry: entry " + name + " does not exist, creating with type double");
      addReadableValue(name, NetworkTableType.kDouble);
    }

    TelemetryItem item = table.entries.get(name);
    if (item.isReadable == false) {
      DriverStation.reportWarning(
          "Telemetry: entry "
              + name
              + " [double] is not readable. Create with addReadableValue or subscribe before reading.",
          false);
      return defaultValue;
    }

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
      addReadableValue(name, NetworkTableType.kBoolean);
    }

    TelemetryItem item = table.entries.get(name);

    if (item.isReadable == false) {
      DriverStation.reportWarning(
          "Telemetry: entry "
              + name
              + " [boolean] is not readable. Create with addReadableValue or subscribe before reading.",
          false);
      return defaultValue;
    }

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
      addReadableValue(name, NetworkTableType.kDoubleArray);
    }

    TelemetryItem item = table.entries.get(name);

    if (item.isReadable == false) {
      DriverStation.reportWarning(
          "Telemetry: entry "
              + name
              + " [double[]] is not readable. Create with addReadableValue or subscribe before reading.",
          false);
      return defaultValue;
    }

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
      addReadableValue(name, NetworkTableType.kBooleanArray);
    }

    TelemetryItem item = table.entries.get(name);

    if (item.isReadable == false) {
      DriverStation.reportWarning(
          "Telemetry: entry "
              + name
              + " [boolean[]] is not readable. Create with addReadableValue or subscribe before reading.",
          false);
      return defaultValue;
    }

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
      addReadableValue(name, NetworkTableType.kFloat);
    }

    TelemetryItem item = table.entries.get(name);

    if (item.isReadable == false) {
      DriverStation.reportWarning(
          "Telemetry: entry "
              + name
              + " [float] is not readable. Create with addReadableValue or subscribe before reading.",
          false);
      return defaultValue;
    }

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
      addReadableValue(name, NetworkTableType.kFloatArray);
    }

    TelemetryItem item = table.entries.get(name);

    if (item.isReadable == false) {
      DriverStation.reportWarning(
          "Telemetry: entry "
              + name
              + " [float[]] is not readable. Create with addReadableValue or subscribe before reading.",
          false);
      return defaultValue;
    }

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
      addReadableValue(name, NetworkTableType.kInteger);
    }

    TelemetryItem item = table.entries.get(name);

    if (item.isReadable == false) {
      DriverStation.reportWarning(
          "Telemetry: entry "
              + name
              + " [int] is not readable. Create with addReadableValue or subscribe before reading.",
          false);
      return defaultValue;
    }

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
      addReadableValue(name, NetworkTableType.kIntegerArray);
    }

    TelemetryItem item = table.entries.get(name);

    if (item.isReadable == false) {
      DriverStation.reportWarning(
          "Telemetry: entry "
              + name
              + " [int[]] is not readable. Create with addReadableValue or subscribe before reading.",
          false);
      return defaultValue;
    }

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
      addReadableValue(name, NetworkTableType.kString);
    }

    TelemetryItem item = table.entries.get(name);

    if (item.isReadable == false) {
      DriverStation.reportWarning(
          "Telemetry: entry "
              + name
              + " [String] is not readable. Create with addReadableValue or subscribe before reading.",
          false);
      return defaultValue;
    }

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
      addReadableValue(name, NetworkTableType.kStringArray);
    }

    TelemetryItem item = table.entries.get(name);

    if (item.isReadable == false) {
      DriverStation.reportWarning(
          "Telemetry: entry "
              + name
              + " [String[]] is not readable. Create with addReadableValue or subscribe before reading.",
          false);
      return defaultValue;
    }

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
      addReadableValue(name, NetworkTableType.kRaw);
    }

    TelemetryItem item = table.entries.get(name);

    if (item.isReadable == false) {
      DriverStation.reportWarning(
          "Telemetry: entry "
              + name
              + " [byte[]] is not readable. Create with addReadableValue or subscribe before reading.",
          false);
      return defaultValue;
    }

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
