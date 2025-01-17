package frc.robot.subsystems.Arm;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import java.util.function.DoubleSupplier;

public class ArmSubsystem extends SubsystemBase {

  public final DoubleSupplier simulationCurrentDraw = () -> 0;
}
