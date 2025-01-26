package frc.robot.subsystems.Swerve;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import java.util.function.DoubleSupplier;

public class SwerveSubsystem extends SubsystemBase {

  public final DoubleSupplier totalCurrentDraw = () -> 0;
}
