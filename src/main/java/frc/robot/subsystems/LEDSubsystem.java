package frc.robot.subsystems;

import edu.wpi.first.networktables.NetworkTableType;
import edu.wpi.first.wpilibj.AddressableLED;
import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj.LEDPattern;
import edu.wpi.first.wpilibj.LEDPattern.GradientType;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.LEDConstants;
import frc.robot.utilities.Power;
import frc.robot.utilities.Telemetry;
import java.util.function.DoubleSupplier;

public class LEDSubsystem extends SubsystemBase {
  private AddressableLED m_led = null;
  private AddressableLEDBuffer m_ledBuffer;

  private final LEDPattern initializePattern =
      LEDPattern.gradient(GradientType.kContinuous, Color.kGreen, Color.kBlue)
          .scrollAtRelativeSpeed(LEDConstants.SCROLL_SPEED);
  private final LEDPattern demoPattern =
      LEDPattern.gradient(GradientType.kContinuous, Color.kGreen, Color.kBlue)
          .scrollAtRelativeSpeed(LEDConstants.SCROLL_SPEED);
  private final LEDPattern conePattern =
      LEDPattern.gradient(GradientType.kContinuous, Color.kYellow, Color.kGreen, Color.kBlue)
          .scrollAtRelativeSpeed(LEDConstants.SCROLL_SPEED);
  private final LEDPattern cubePattern =
      LEDPattern.gradient(GradientType.kContinuous, Color.kPurple, Color.kGreen, Color.kBlue)
          .scrollAtRelativeSpeed(LEDConstants.SCROLL_SPEED);
  private final LEDPattern endgameStartPattern =
      LEDPattern.gradient(GradientType.kContinuous, Color.kOrange, Color.kRed)
          .scrollAtRelativeSpeed(LEDConstants.FAST_SCROLL_SPEED);
  private final LEDPattern endgameConePattern =
      LEDPattern.gradient(GradientType.kContinuous, Color.kYellow, Color.kGreen, Color.kBlue)
          .scrollAtRelativeSpeed(LEDConstants.FAST_SCROLL_SPEED);
  private final LEDPattern endgameCubePattern =
      LEDPattern.gradient(GradientType.kContinuous, Color.kPurple, Color.kGreen, Color.kBlue)
          .scrollAtRelativeSpeed(LEDConstants.FAST_SCROLL_SPEED);
  private final LEDPattern shwervePattern =
      LEDPattern.gradient(GradientType.kContinuous, Color.kGreen, Color.kBlue)
          .blink(LEDConstants.SHWERVE_BLINK_TIME);
  private final LEDPattern selfDestructPattern =
      LEDPattern.gradient(GradientType.kContinuous, Color.kRed, Color.kOrange, Color.kYellow)
          .scrollAtRelativeSpeed(LEDConstants.SCROLL_SPEED)
          .breathe(LEDConstants.SELF_DESTRUCT_BREATHE_TIME);
  private final LEDPattern disabledPattern =
      LEDPattern.gradient(GradientType.kContinuous, Color.kRed, Color.kOrange)
          .scrollAtRelativeSpeed(LEDConstants.SCROLL_SPEED)
          .blink(LEDConstants.SELF_DESTRUCT_BREATHE_TIME);

  public LEDSubsystem() {
    m_led = new AddressableLED(LEDConstants.LED_PORT);
    m_ledBuffer = new AddressableLEDBuffer(LEDConstants.LED_LENGTH);
    m_led.setLength(m_ledBuffer.getLength());
    m_led.start();

    enableUnderglow().run();

    System.out.println("LED Subsystem Initialized");

    Telemetry.addValue("LEDs/Pattern", NetworkTableType.kString);
    Telemetry.addValue("LEDs/IsUnderglowEnabled", NetworkTableType.kBoolean);

    setDefaultCommand(setInitialize());
  }

  public Command setOff() {
    return this.run(
            () -> {
              LEDPattern.kOff.applyTo(m_ledBuffer);
            })
        .withName("Off");
  }

  public Command setInitialize() {
    return this.run(
            () -> {
              initializePattern.applyTo(m_ledBuffer);
            })
        .withName("Initialize");
  }

  public Command setDemo() {
    return this.run(
            () -> {
              demoPattern.applyTo(m_ledBuffer);
            })
        .withName("Demo");
  }

  public Command setCone() {
    return this.run(
            () -> {
              conePattern.applyTo(m_ledBuffer);
            })
        .withName("Cone");
  }

  public Command setCube() {
    return this.run(
            () -> {
              cubePattern.applyTo(m_ledBuffer);
            })
        .withName("Cube");
  }

  public Command setEndgameStart() {
    return this.run(
            () -> {
              endgameStartPattern.applyTo(m_ledBuffer);
            })
        .withName("Endgame Start");
  }

  public Command setEndgameCone() {
    return this.run(
            () -> {
              endgameConePattern.applyTo(m_ledBuffer);
            })
        .withName("Endgame Cone");
  }

  public Command setEndgameCube() {
    return this.run(
            () -> {
              endgameCubePattern.applyTo(m_ledBuffer);
            })
        .withName("Endgame Cube");
  }

  public Command setShwerve() {
    return this.run(
            () -> {
              shwervePattern.applyTo(m_ledBuffer);
            })
        .withName("Shwerve");
  }

  public Command setSelfDestruct() {
    return this.run(
            () -> {
              selfDestructPattern.applyTo(m_ledBuffer);
            })
        .withName("Self Destruct");
  }

  public Command setDisabled() {
    return this.run(
            () -> {
              disabledPattern.applyTo(m_ledBuffer);
            })
        .withName("Disabled");
  }

  public Runnable enableUnderglow() {
    return () -> {
      System.out.println("Enabling Underglow");
      Power.enableUnderglow();
    };
  }

  public Runnable disableUnderglow() {
    return () -> {
      System.out.println("Disabling Underglow");
      Power.disableUnderglow();
    };
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
    if (m_led != null) m_led.setData(m_ledBuffer);

    Telemetry.setValue(
        "LEDs/Pattern",
        this.getCurrentCommand() == null ? "null" : this.getCurrentCommand().getName());
    Telemetry.setValue("LEDs/IsUnderglowEnabled", Power.getSwitchableChannel());
  }

  public final DoubleSupplier totalCurrentDraw = () -> 0; // no tracking available
}
