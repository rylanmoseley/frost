package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import java.util.function.DoubleSupplier;

public class LEDSubsystem extends SubsystemBase {

  public final DoubleSupplier totalCurrentDraw = () -> 0;
}
