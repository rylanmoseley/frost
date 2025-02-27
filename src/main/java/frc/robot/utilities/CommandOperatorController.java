// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.utilities;

import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.networktables.NetworkTableType;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.button.CommandGenericHID;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import java.util.function.DoubleSupplier;
import java.util.function.Supplier;

public class CommandOperatorController {
  CommandGenericHID leftBoard;
  CommandGenericHID rightBoard;

  public CommandOperatorController(int leftPort, int rightPort) {
    leftBoard = new CommandGenericHID(leftPort);
    rightBoard = new CommandGenericHID(rightPort);

    Telemetry.addValue("buttonBoard/00", NetworkTableType.kBoolean);
    Telemetry.addValue("buttonBoard/01", NetworkTableType.kBoolean);
    Telemetry.addValue("buttonBoard/02", NetworkTableType.kBoolean);
    Telemetry.addValue("buttonBoard/03", NetworkTableType.kBoolean);
    Telemetry.addValue("buttonBoard/04", NetworkTableType.kBoolean);
    Telemetry.addValue("buttonBoard/05", NetworkTableType.kBoolean);
    Telemetry.addValue("buttonBoard/06", NetworkTableType.kBoolean);
    Telemetry.addValue("buttonBoard/07", NetworkTableType.kBoolean);
    Telemetry.addValue("buttonBoard/08", NetworkTableType.kBoolean);
    // 9 intentionally skipped
    Telemetry.addValue("buttonBoard/10", NetworkTableType.kBoolean);
    Telemetry.addValue("buttonBoard/11", NetworkTableType.kBoolean);
    Telemetry.addValue("buttonBoard/12", NetworkTableType.kBoolean);
    Telemetry.addValue("buttonBoard/13", NetworkTableType.kBoolean);
    Telemetry.addValue("buttonBoard/14", NetworkTableType.kBoolean);
    Telemetry.addValue("buttonBoard/15", NetworkTableType.kBoolean);
    Telemetry.addValue("buttonBoard/16", NetworkTableType.kBoolean);
  }

  /** Indexes start at 0 */
  public boolean getLED(int LEDNumber) {
    if (LEDNumber > 16 || LEDNumber < 0 || LEDNumber == 9) {
      DriverStation.reportError("Button Board: [GET] Invalid LED number: " + LEDNumber, false);
      return false;
    }
    return Telemetry.getValue("buttonBoard/" + (LEDNumber < 9 ? "0" : "") + LEDNumber, false);
  }

  /** Indexes start at 0 */
  public void setLED(int LEDNumber, boolean value) {
    if (LEDNumber > 16 || LEDNumber < 0 || LEDNumber == 9) {
      DriverStation.reportError("Button Board: [SET] Invalid LED number: " + LEDNumber, false);
      return;
    }
    Telemetry.setValue("buttonBoard/" + (LEDNumber < 9 ? "0" : "") + LEDNumber, value);
  }

  /** Indexes start at 0 */
  public boolean toggleLED(int LEDNumber) {
    if (LEDNumber > 16 || LEDNumber < 0 || LEDNumber == 9) {
      DriverStation.reportError("Button Board: [TOGGLE] Invalid LED number: " + LEDNumber, false);
      return false;
    }
    if (getLED(LEDNumber)) {
      setLED(LEDNumber, false);
      return false;
    } else {
      setLED(LEDNumber, true);
      return true;
    }
  }

  /** Indexes start at 0 */
  public Trigger button(int buttonNumber) {
    return (buttonNumber < 9)
        ? leftBoard.button(buttonNumber + 1)
        : rightBoard.button(buttonNumber - 9 + 1);
  }

  /** Indexes start at 0 */
  public boolean getRawButton(int buttonNumber) {
    return (buttonNumber < 9)
        ? leftBoard.getHID().getRawButton(buttonNumber + 1)
        : rightBoard.getHID().getRawButton(buttonNumber - 9 + 1);
  }

  /** Value returned is either 0 or ~1 */
  private double getAxis(int axisNumber) {
    if (Math.abs(rightBoard.getRawAxis(axisNumber)) < 0.5) {
      return 0;
    }
    return rightBoard.getRawAxis(axisNumber);
  }

  public DoubleSupplier getAxisSupplier(int axisNumber) {
    return () -> getAxis(axisNumber);
  }

  public Translation2d getJoystick() {
    return new Translation2d(getAxis(0), -getAxis(1));
  }

  public Supplier<Translation2d> getJoystickSupplier() {
    return () -> getJoystick();
  }

  // named button Triggers
  public Trigger substation() {
    return button(0);
  }

  public Trigger floor() {
    return button(1);
  }

  public Trigger floorTilt() {
    return button(3);
  }

  public Trigger scoreHigh() {
    return button(2);
  }

  public Trigger scoreMid() {
    return button(4);
  }

  public Trigger scoreLow() {
    return button(5);
  }

  public Trigger eject() {
    return button(6);
  }

  public Trigger cubeMode() {
    return button(7);
  }

  public Trigger coneMode() {
    return button(8);
  }

  public Trigger manualArmSwitch() {
    return button(9);
  }

  public Trigger manualArmUp() {
    return button(10);
  }

  public Trigger manualArmDown() {
    return button(11);
  }

  public Trigger manualClawToggle() {
    return button(12);
  }

  public Trigger manualRollersIn() {
    return button(13);
  }

  public Trigger manualRollersOut() {
    return button(14);
  }

  public Trigger selfDestruct() {
    return button(15);
  }

  public Trigger shwerve() {
    return button(16);
  }
}
