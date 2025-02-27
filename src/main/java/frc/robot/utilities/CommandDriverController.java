package frc.robot.utilities;

import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;

public class CommandDriverController {
  private CommandXboxController controller;
  private boolean fieldOrient = true;

  public CommandDriverController(int port) {
    controller = new CommandXboxController(port);
  }

  // TODO deadzones, scaling, etc
  public DoubleSupplier getChassisForward() {
    return controller::getLeftX;
  }

  public DoubleSupplier getChassisStrafe() {
    return controller::getLeftY;
  }

  public DoubleSupplier getChassisRotate() {
    return controller::getRightX;
  }

  public BooleanSupplier getChassisFieldOrient() {
    return () -> fieldOrient;
  }

  public BooleanSupplier isIdle() {
    return () ->
        getChassisForward().getAsDouble() == 0
            && getChassisStrafe().getAsDouble() == 0
            && getChassisRotate().getAsDouble() == 0;
  }

  public final Trigger idle = new Trigger(isIdle());

  // TODO button triggers
}
