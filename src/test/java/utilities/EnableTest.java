package utilities;

import static org.junit.jupiter.api.Assertions.assertEquals;

import edu.wpi.first.hal.HAL;
import edu.wpi.first.wpilibj.simulation.DriverStationSim;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EnableTest {
  @BeforeEach // this method will run before each test
  void setup() {
    assert HAL.initialize(500, 0); // initialize the HAL, crash if failed
  }

  @Test // marks this method as a test
  void simDSEnablesWhenEnabled() {
    DriverStationSim.setEnabled(true);

    assertEquals(true, DriverStationSim.getEnabled());
  }
}
