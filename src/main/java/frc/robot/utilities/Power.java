package frc.robot.utilities;

import edu.wpi.first.hal.PowerDistributionFaults;
import edu.wpi.first.hal.PowerDistributionStickyFaults;
import edu.wpi.first.networktables.NetworkTableType;
import edu.wpi.first.wpilibj.PowerDistribution;
import edu.wpi.first.wpilibj.PowerDistribution.ModuleType;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.PowerConstants;

public class Power extends SubsystemBase {
  private static Power powerInstance = null;

  private PowerDistribution m_pdh;

  private Power() {
    m_pdh = new PowerDistribution(PowerConstants.CAN.PDH, ModuleType.kRev);

    Telemetry.addValue("Power/Voltage", NetworkTableType.kDouble);
    Telemetry.addValue("Power/TotalCurrent", NetworkTableType.kDouble);
    Telemetry.addValue("Power/CurrentArray", NetworkTableType.kDoubleArray);
    Telemetry.addValue("Power/SwitchableChannel", NetworkTableType.kBoolean);
    Telemetry.addValue("Power/Faults", NetworkTableType.kBooleanArray);
    Telemetry.addValue("Power/HasFault", NetworkTableType.kBoolean);
    Telemetry.addValue("Power/StickyFaults", NetworkTableType.kBooleanArray);
    Telemetry.addValue("Power/HasStickyFault", NetworkTableType.kBoolean);
  }

  private static synchronized Power getInstance() {
    if (powerInstance == null) {
      powerInstance = new Power();
    }
    return powerInstance;
  }

  public static double getVoltage() {
    return getInstance().m_pdh.getVoltage();
  }

  public static double getCurrent() {
    return getInstance().m_pdh.getTotalCurrent();
  }

  public static double getCurrent(int channel) {
    return getInstance().m_pdh.getCurrent(channel);
  }

  public static double[] getAllCurrents() {
    return getInstance().m_pdh.getAllCurrents();
  }

  public static boolean getSwitchableChannel() {
    return getInstance().m_pdh.getSwitchableChannel();
  }

  public static void setSwitchableChannel(boolean enabled) {
    getInstance().m_pdh.setSwitchableChannel(enabled);
  }

  public static void enableUnderglow() {
    setSwitchableChannel(true);
  }

  public static void disableUnderglow() {
    setSwitchableChannel(false);
  }

  public static boolean[] getFaults() {
    PowerDistributionFaults faults = getInstance().m_pdh.getFaults();
    boolean[] faultArray = new boolean[26];
    faultArray[0] = faults.Brownout;
    faultArray[1] = faults.CanWarning;
    faultArray[2] = faults.HardwareFault;
    faultArray[3] = faults.Channel0BreakerFault;
    faultArray[4] = faults.Channel1BreakerFault;
    faultArray[5] = faults.Channel2BreakerFault;
    faultArray[6] = faults.Channel3BreakerFault;
    faultArray[7] = faults.Channel4BreakerFault;
    faultArray[8] = faults.Channel5BreakerFault;
    faultArray[9] = faults.Channel6BreakerFault;
    faultArray[10] = faults.Channel7BreakerFault;
    faultArray[11] = faults.Channel8BreakerFault;
    faultArray[12] = faults.Channel9BreakerFault;
    faultArray[13] = faults.Channel10BreakerFault;
    faultArray[14] = faults.Channel11BreakerFault;
    faultArray[15] = faults.Channel12BreakerFault;
    faultArray[16] = faults.Channel13BreakerFault;
    faultArray[17] = faults.Channel14BreakerFault;
    faultArray[18] = faults.Channel15BreakerFault;
    faultArray[19] = faults.Channel16BreakerFault;
    faultArray[20] = faults.Channel17BreakerFault;
    faultArray[21] = faults.Channel18BreakerFault;
    faultArray[22] = faults.Channel19BreakerFault;
    faultArray[23] = faults.Channel20BreakerFault;
    faultArray[24] = faults.Channel21BreakerFault;
    faultArray[25] = faults.Channel22BreakerFault;
    faultArray[26] = faults.Channel23BreakerFault;
    return faultArray;
  }

  public static boolean hasFault() {
    for (boolean fault : getFaults()) {
      if (fault) {
        return true;
      }
    }
    return false;
  }

  public static void clearStickyFaults() {
    getInstance().m_pdh.clearStickyFaults();
  }

  public static boolean[] getStickyFaults() {
    PowerDistributionStickyFaults faults = getInstance().m_pdh.getStickyFaults();
    boolean[] faultArray = new boolean[29];
    faultArray[0] = faults.Brownout;
    faultArray[1] = faults.CanWarning;
    faultArray[2] = faults.HardwareFault;
    faultArray[3] = faults.Channel0BreakerFault;
    faultArray[4] = faults.Channel1BreakerFault;
    faultArray[5] = faults.Channel2BreakerFault;
    faultArray[6] = faults.Channel3BreakerFault;
    faultArray[7] = faults.Channel4BreakerFault;
    faultArray[8] = faults.Channel5BreakerFault;
    faultArray[9] = faults.Channel6BreakerFault;
    faultArray[10] = faults.Channel7BreakerFault;
    faultArray[11] = faults.Channel8BreakerFault;
    faultArray[12] = faults.Channel9BreakerFault;
    faultArray[13] = faults.Channel10BreakerFault;
    faultArray[14] = faults.Channel11BreakerFault;
    faultArray[15] = faults.Channel12BreakerFault;
    faultArray[16] = faults.Channel13BreakerFault;
    faultArray[17] = faults.Channel14BreakerFault;
    faultArray[18] = faults.Channel15BreakerFault;
    faultArray[19] = faults.Channel16BreakerFault;
    faultArray[20] = faults.Channel17BreakerFault;
    faultArray[21] = faults.Channel18BreakerFault;
    faultArray[22] = faults.Channel19BreakerFault;
    faultArray[23] = faults.Channel20BreakerFault;
    faultArray[24] = faults.Channel21BreakerFault;
    faultArray[25] = faults.Channel22BreakerFault;
    faultArray[26] = faults.Channel23BreakerFault;
    faultArray[27] = faults.FirmwareFault;
    faultArray[28] = faults.CanBusOff;
    faultArray[29] = faults.HasReset;
    return faultArray;
  }

  public static boolean hasStickyFault() {
    for (boolean fault : getStickyFaults()) {
      if (fault) {
        return true;
      }
    }
    return false;
  }

  @Override
  public void periodic() {
    Telemetry.setValue("Power/Voltage", getVoltage());
    Telemetry.setValue("Power/TotalCurrent", getCurrent());
    Telemetry.setValue("Power/CurrentArray", getAllCurrents());
    Telemetry.setValue("Power/SwitchableChannel", getSwitchableChannel());
    Telemetry.setValue("Power/Faults", getFaults());
    Telemetry.setValue("Power/HasFault", hasFault());
    Telemetry.setValue("Power/StickyFaults", getStickyFaults());
    Telemetry.setValue("Power/HasStickyFault", hasStickyFault());
  }
}
