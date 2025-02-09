package frc.robot.utilities;

import edu.wpi.first.wpilibj.PowerDistribution;
import edu.wpi.first.wpilibj.PowerDistribution.ModuleType;
import frc.robot.Constants.PowerConstants;

public class Power {
  private static Power powerInstance = null;

  private PowerDistribution m_pdh;

  private Power() {
    m_pdh = new PowerDistribution(PowerConstants.CAN.PDH, ModuleType.kRev);
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
}
