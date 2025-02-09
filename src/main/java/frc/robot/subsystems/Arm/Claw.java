package frc.robot.subsystems.Arm;

import com.revrobotics.REVLibError;
import com.revrobotics.sim.SparkMaxSim;
import com.revrobotics.spark.SparkAbsoluteEncoder;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkMaxConfig;
import edu.wpi.first.math.system.plant.DCMotor;
import edu.wpi.first.math.system.plant.LinearSystemId;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.networktables.NetworkTableType;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.PneumaticHub;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.simulation.DCMotorSim;
import edu.wpi.first.wpilibj.simulation.DIOSim;
import edu.wpi.first.wpilibj.simulation.DoubleSolenoidSim;
import edu.wpi.first.wpilibj.simulation.PneumaticsBaseSim;
import edu.wpi.first.wpilibj.simulation.RoboRioSim;
import edu.wpi.first.wpilibj.smartdashboard.Mechanism2d;
import edu.wpi.first.wpilibj.smartdashboard.MechanismLigament2d;
import edu.wpi.first.wpilibj.smartdashboard.MechanismRoot2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Command.InterruptionBehavior;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.Constants;
import frc.robot.Constants.ArmConstants.ClawConstants;
import frc.robot.Constants.ArmConstants.ClawConstants.RollerSpeeds;
import frc.robot.Robot;
import frc.robot.utilities.Telemetry;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;

public class Claw extends SubsystemBase {
  /** Master */
  private SparkMax m_rollerLeft = new SparkMax(ClawConstants.CAN.ROLLER_LEFT, MotorType.kBrushless);

  /** Follower */
  private SparkMax m_rollerRight =
      new SparkMax(ClawConstants.CAN.ROLLER_RIGHT, MotorType.kBrushless);

  private SparkAbsoluteEncoder m_rollerLeftEncoder =
      (SparkAbsoluteEncoder) m_rollerLeft.getEncoder();
  private SparkAbsoluteEncoder m_rollerRightEncoder =
      (SparkAbsoluteEncoder) m_rollerRight.getEncoder();

  private PneumaticHub m_pneumaticHub = new PneumaticHub(ClawConstants.CAN.PNEUMATIC_HUB);
  private DoubleSolenoid m_clamp =
      m_pneumaticHub.makeDoubleSolenoid(
          ClawConstants.PNEUMATIC_CHANNEL.CLAMP_FORWARD,
          ClawConstants.PNEUMATIC_CHANNEL.CLAMP_REVERSE);
  private DigitalInput m_beamBreak = new DigitalInput(ClawConstants.DIO.BEAM_BREAK);

  public final Trigger beamBreak =
      new Trigger(() -> m_beamBreak.get() == ClawConstants.BEAM_BREAK_BROKEN_VALUE);

  public final BooleanSupplier isOpen = () -> m_clamp.get() == ClawConstants.CLAW_OPEN;
  public final BooleanSupplier isClosed = () -> m_clamp.get() == ClawConstants.CLAW_CLOSED;
  public final DoubleSupplier rollerOutput = () -> m_rollerLeft.get();

  public final DoubleSupplier totalCurrentDraw =
      () ->
          m_rollerLeft.getOutputCurrent()
              + m_rollerRight.getOutputCurrent()
              + m_pneumaticHub.getCompressorCurrent()
              + m_pneumaticHub.getSolenoidsTotalCurrent();

  // simulation variables
  private final DCMotor m_rollerMotorModel = DCMotor.getNeo550(1);
  private DCMotorSim m_rollerLeftMotorSim =
      new DCMotorSim(
          LinearSystemId.createDCMotorSystem(
              m_rollerMotorModel,
              ClawConstants.SimulationConstants.MOI,
              ClawConstants.SimulationConstants.GEAR_RATIO),
          m_rollerMotorModel,
          0.01,
          0.01);
  private DCMotorSim m_rollerRightMotorSim =
      new DCMotorSim(
          LinearSystemId.createDCMotorSystem(
              m_rollerMotorModel,
              ClawConstants.SimulationConstants.MOI,
              ClawConstants.SimulationConstants.GEAR_RATIO),
          m_rollerMotorModel,
          0.01,
          0.01);
  private SparkMaxSim m_rollerLeftSim = new SparkMaxSim(m_rollerLeft, m_rollerMotorModel);
  private SparkMaxSim m_rollerRightSim = new SparkMaxSim(m_rollerRight, m_rollerMotorModel);
  private DoubleSolenoidSim m_clampSim =
      new DoubleSolenoidSim(
          ClawConstants.CAN.PNEUMATIC_HUB,
          PneumaticsModuleType.REVPH,
          ClawConstants.PNEUMATIC_CHANNEL.CLAMP_FORWARD,
          ClawConstants.PNEUMATIC_CHANNEL.CLAMP_REVERSE);
  private PneumaticsBaseSim m_pneumaticHubSim = m_clampSim.getModuleSim();
  private double m_simulatedPressure = 0;
  private Value m_clampLastState = Value.kOff;

  @SuppressWarnings("unused") // yes we want it be quiet linter
  private DIOSim m_beamBreakSim = new DIOSim(ClawConstants.DIO.BEAM_BREAK);

  // Mechanism2d setup
  private Mechanism2d m_clawMechanism =
      new Mechanism2d(
          ClawConstants.SimulationConstants.MECHANISM2D_WIDTH,
          ClawConstants.SimulationConstants.MECHANISM2D_HEIGHT);
  private MechanismRoot2d m_mechClawRoot =
      m_clawMechanism.getRoot(
          "root",
          ClawConstants.SimulationConstants.MECHANISM2D_ROOT.getX(),
          ClawConstants.SimulationConstants.MECHANISM2D_ROOT.getY());
  private MechanismLigament2d m_mechClawLeft =
      m_mechClawRoot.append(
          new MechanismLigament2d(
              "clawLeft",
              ClawConstants.SimulationConstants.MECHANISM2D_CLAW_LENGTH,
              ClawConstants.SimulationConstants.MECHANISM2D_CLAW_ANGLE_OPEN));
  private MechanismLigament2d m_mechRollerLeft =
      m_mechClawLeft.append(
          new MechanismLigament2d(
              "rollerLeft", ClawConstants.SimulationConstants.MECHANISM2D_ROLLER_RADIUS, 0));
  private MechanismLigament2d m_mechClawRight =
      m_mechClawRoot.append(
          new MechanismLigament2d(
              "clawRight",
              ClawConstants.SimulationConstants.MECHANISM2D_CLAW_LENGTH,
              180 - ClawConstants.SimulationConstants.MECHANISM2D_CLAW_ANGLE_OPEN));
  private MechanismLigament2d m_mechRollerRight =
      m_mechClawRight.append(
          new MechanismLigament2d(
              "rollerRight", ClawConstants.SimulationConstants.MECHANISM2D_ROLLER_RADIUS, 0));

  public Claw() {
    System.out.println("Claw instantiated");

    Telemetry.addValue("Arm/Claw/Pneumatics/ForwardVoltage", NetworkTableType.kDouble);
    Telemetry.addValue("Arm/Claw/Pneumatics/ReverseVoltage", NetworkTableType.kDouble);
    Telemetry.addValue("Arm/Claw/Pneumatics/PressureSwitch", NetworkTableType.kBoolean);
    Telemetry.addValue("Arm/Claw/Pneumatics/CompressorCurrent", NetworkTableType.kDouble);
    Telemetry.addValue("Arm/Claw/Pneumatics/InputVoltage", NetworkTableType.kDouble);
    Telemetry.addValue("Arm/Claw/Pneumatics/SolenoidsTotalCurrent", NetworkTableType.kDouble);

    Telemetry.addValue("Arm/Claw/BeamBreak/RawValue", NetworkTableType.kBoolean);
    Telemetry.addValue("Arm/Claw/BeamBreak/LogicValue", NetworkTableType.kBoolean);

    Telemetry.addValue("Arm/Claw/Clamp/IsOpen", NetworkTableType.kBoolean);
    Telemetry.addValue("Arm/Claw/Clamp/IsClosed", NetworkTableType.kBoolean);

    Telemetry.addValue("Arm/Claw/Roller/Output", NetworkTableType.kDouble);
    Telemetry.addValue("Arm/Claw/Roller/LeftCurrentDraw", NetworkTableType.kDouble);
    Telemetry.addValue("Arm/Claw/Roller/RightCurrentDraw", NetworkTableType.kDouble);
    Telemetry.addValue("Arm/Claw/Roller/LeftTemperature", NetworkTableType.kDouble);
    Telemetry.addValue("Arm/Claw/Roller/RightTemperature", NetworkTableType.kDouble);
    Telemetry.addValue("Arm/Claw/Roller/LeftVoltageIn", NetworkTableType.kDouble);
    Telemetry.addValue("Arm/Claw/Roller/RightVoltageIn", NetworkTableType.kDouble);
    Telemetry.addValue("Arm/Claw/Roller/LeftHasFault", NetworkTableType.kBoolean);
    Telemetry.addValue("Arm/Claw/Roller/RightHasFault", NetworkTableType.kBoolean);
    Telemetry.setValue("Arm/Claw/Roller/LeftVelocityRPM", NetworkTableType.kDouble);
    Telemetry.setValue("Arm/Claw/Roller/RightVelocityRPM", NetworkTableType.kDouble);

    Telemetry.addValue("Arm/Claw/TotalCurrentDraw", NetworkTableType.kDouble);

    if (Robot.isSimulation()) {
      Telemetry.addValue("Arm/Claw/Simulation/Pressure", NetworkTableType.kDouble);
    }
  }

  public REVLibError configureAll(
      SparkMaxConfig rollerLeftConfig, SparkMaxConfig rollerRightConfig) {
    REVLibError leftErr = REVLibError.kError;
    REVLibError rightErr = REVLibError.kError;

    for (int i = 0; i < Constants.MAX_CONFIG_RETRIES; i++) {
      leftErr =
          m_rollerLeft.configure(
              rollerLeftConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
      rightErr =
          m_rollerRight.configure(
              rollerRightConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);

      if (leftErr == REVLibError.kOk && rightErr == REVLibError.kOk) {
        return rightErr;
      }
    }

    System.out.println("Error configuring Claw: " + leftErr + ", " + rightErr);
    DriverStation.reportError("Error configuring Claw: " + leftErr + ", " + rightErr, false);

    // return error, if present
    if (leftErr == REVLibError.kOk) {
      return rightErr;
    }
    return leftErr;
  }

  public Command prepareForCubeCommand() {
    return this.run(
            () -> {
              m_clamp.set(ClawConstants.CLAW_OPEN);
              m_rollerLeft.set(RollerSpeeds.CUBE_INTAKE);
            })
        .withName("Prepare for Cube")
        .withInterruptBehavior(InterruptionBehavior.kCancelSelf);
  }

  public Command prepareForConeCommand() {
    return this.run(
            () -> {
              m_clamp.set(ClawConstants.CLAW_OPEN);
              m_rollerLeft.set(RollerSpeeds.CONE_INTAKE);
            })
        .withName("Prepare for Cone")
        .withInterruptBehavior(InterruptionBehavior.kCancelSelf);
  }

  public Command receiveCubeCommand() {
    return this.run(
            () -> {
              m_clamp.set(ClawConstants.CLAW_OPEN);
              m_rollerLeft.set(RollerSpeeds.CUBE_IDLE);
            })
        .withName("Receive Cube")
        .withInterruptBehavior(InterruptionBehavior.kCancelSelf);
  }

  public Command holdCubeCommand() {
    return this.runOnce(
            () -> {
              m_clamp.set(ClawConstants.CLAW_OPEN);
              m_rollerLeft.set(RollerSpeeds.CUBE_IDLE);
            })
        .withName("Hold Cube")
        .withInterruptBehavior(InterruptionBehavior.kCancelSelf)
        .repeatedly();
  }

  public Command receiveConeCommand() {
    return this.runOnce(
            () -> {
              m_clamp.set(ClawConstants.CLAW_CLOSED);
              m_rollerLeft.set(RollerSpeeds.CONE_IDLE);
            })
        .withName("Receive Cone")
        .withInterruptBehavior(InterruptionBehavior.kCancelSelf);
  }

  public Command holdConeCommand() {
    return this.run(
            () -> {
              m_clamp.set(ClawConstants.CLAW_CLOSED);
              m_rollerLeft.set(RollerSpeeds.CONE_IDLE);
            })
        .withName("Hold Cone")
        .withInterruptBehavior(InterruptionBehavior.kCancelSelf)
        .repeatedly();
  }

  public Command ejectCubeCommand() {
    return this.run(
            () -> {
              m_clamp.set(ClawConstants.CLAW_OPEN);
              m_rollerLeft.set(RollerSpeeds.CUBE_EJECT);
            })
        .withName("Eject Cube")
        .withInterruptBehavior(InterruptionBehavior.kCancelSelf);
  }

  public Command ejectConeCommand() {
    return this.run(
            () -> {
              m_clamp.set(ClawConstants.CLAW_OPEN);
              m_rollerLeft.set(RollerSpeeds.CONE_EJECT);
            })
        .withName("Eject Cone")
        .withInterruptBehavior(InterruptionBehavior.kCancelSelf);
  }

  public Command openClawCommand() {
    return this.runOnce(
            () -> {
              m_clamp.set(ClawConstants.CLAW_OPEN);
            })
        .withName("Open Claw")
        .withInterruptBehavior(InterruptionBehavior.kCancelSelf);
  }

  public Command closeClawCommand() {
    return this.runOnce(
            () -> {
              m_clamp.set(ClawConstants.CLAW_CLOSED);
            })
        .withName("Close Claw")
        .withInterruptBehavior(InterruptionBehavior.kCancelSelf);
  }

  /** To stop rollers, call {@link Claw#stopRollersCommand()} instead */
  public Command setRollerSpeedCommand(DoubleSupplier speed) {
    return this.runOnce(
            () -> {
              m_rollerLeft.set(speed.getAsDouble());
            })
        .withName("Custom roller speed")
        .withInterruptBehavior(InterruptionBehavior.kCancelSelf);
  }

  public Command stopRollersCommand() {
    return this.runOnce(
            () -> {
              m_rollerLeft.set(0);
              // stop the follower too, just in case
              m_rollerRight.set(0);
            })
        .withName("Stop rollers")
        .withInterruptBehavior(InterruptionBehavior.kCancelSelf);
  }

  // on idle, stop rollers and open claw
  public Command idleCommand() {
    return stopRollersCommand()
        .andThen(openClawCommand())
        .andThen(
            this.run(
                    () -> {
                      // do nothing
                    })
                .withName("Idle"))
        .withInterruptBehavior(InterruptionBehavior.kCancelSelf);
  }

  @Override
  public void periodic() {
    SmartDashboard.putData("Arm/Claw/ClawSubsystem", this);

    Telemetry.setValue(
        "Arm/Claw/Pneumatics/ForwardVoltage",
        m_pneumaticHub.getAnalogVoltage(ClawConstants.PNEUMATIC_CHANNEL.CLAMP_FORWARD));
    Telemetry.setValue(
        "Arm/Claw/Pneumatics/ReverseVoltage",
        m_pneumaticHub.getAnalogVoltage(ClawConstants.PNEUMATIC_CHANNEL.CLAMP_REVERSE));
    Telemetry.setValue("Arm/Claw/Pneumatics/PressureSwitch", m_pneumaticHub.getPressureSwitch());
    Telemetry.setValue(
        "Arm/Claw/Pneumatics/CompressorCurrent", m_pneumaticHub.getCompressorCurrent());
    Telemetry.setValue("Arm/Claw/Pneumatics/InputVoltage", m_pneumaticHub.getInputVoltage());
    Telemetry.setValue(
        "Arm/Claw/Pneumatics/SolenoidsTotalCurrent",
        m_pneumaticHub.getPressure(ClawConstants.PNEUMATIC_CHANNEL.PRESSURE_SWITCH_PORT));

    Telemetry.setValue("Arm/Claw/BeamBreak/RawValue", m_beamBreak.get());
    Telemetry.setValue("Arm/Claw/BeamBreak/LogicValue", beamBreak.getAsBoolean());

    Telemetry.setValue("Arm/Claw/Clamp/IsOpen", isOpen.getAsBoolean());
    Telemetry.setValue("Arm/Claw/Clamp/IsClosed", isClosed.getAsBoolean());

    Telemetry.setValue("Arm/Claw/Roller/Output", rollerOutput.getAsDouble());
    Telemetry.setValue("Arm/Claw/Roller/LeftCurrentDraw", m_rollerLeft.getOutputCurrent());
    Telemetry.setValue("Arm/Claw/Roller/RightCurrentDraw", m_rollerRight.getOutputCurrent());
    Telemetry.setValue("Arm/Claw/Roller/LeftTemperature", m_rollerLeft.getMotorTemperature());
    Telemetry.setValue("Arm/Claw/Roller/RightTemperature", m_rollerRight.getMotorTemperature());
    Telemetry.setValue("Arm/Claw/Roller/LeftVoltageIn", m_rollerLeft.getBusVoltage());
    Telemetry.setValue("Arm/Claw/Roller/RightVoltageIn", m_rollerRight.getBusVoltage());
    Telemetry.setValue("Arm/Claw/Roller/LeftHasFault", m_rollerLeft.hasActiveFault());
    Telemetry.setValue("Arm/Claw/Roller/LeftHasStickyFault", m_rollerLeft.hasStickyFault());
    Telemetry.setValue("Arm/Claw/Roller/RightHasFault", m_rollerRight.hasActiveFault());
    Telemetry.setValue("Arm/Claw/Roller/RightHasStickyFault", m_rollerRight.hasStickyFault());
    Telemetry.setValue("Arm/Claw/Roller/LeftVelocityRPM", m_rollerLeftEncoder.getVelocity());
    Telemetry.setValue("Arm/Claw/Roller/RightVelocityRPM", m_rollerRightEncoder.getVelocity());

    Telemetry.setValue("Arm/Claw/TotalCurrentDraw", totalCurrentDraw.getAsDouble());

    // Mechanism2d update
    m_mechClawLeft.setAngle(
        isOpen.getAsBoolean()
            ? ClawConstants.SimulationConstants.MECHANISM2D_CLAW_ANGLE_OPEN
            : ClawConstants.SimulationConstants.MECHANISM2D_CLAW_ANGLE_CLOSED);
    m_mechClawRight.setAngle(
        isClosed.getAsBoolean()
            ? 180 - ClawConstants.SimulationConstants.MECHANISM2D_CLAW_ANGLE_OPEN
            : 180 - ClawConstants.SimulationConstants.MECHANISM2D_CLAW_ANGLE_CLOSED);

    if (beamBreak.getAsBoolean()) {
      m_mechClawLeft.setColor(ClawConstants.SimulationConstants.CLAW_FULL_COLOR);
      m_mechClawRight.setColor(ClawConstants.SimulationConstants.CLAW_FULL_COLOR);
      m_mechRollerLeft.setColor(ClawConstants.SimulationConstants.CLAW_FULL_COLOR);
      m_mechRollerRight.setColor(ClawConstants.SimulationConstants.CLAW_FULL_COLOR);
    } else {
      m_mechClawLeft.setColor(ClawConstants.SimulationConstants.CLAW_EMPTY_COLOR);
      m_mechClawRight.setColor(ClawConstants.SimulationConstants.CLAW_EMPTY_COLOR);
      m_mechRollerLeft.setColor(ClawConstants.SimulationConstants.CLAW_EMPTY_COLOR);
      m_mechRollerRight.setColor(ClawConstants.SimulationConstants.CLAW_EMPTY_COLOR);
    }

    m_mechRollerLeft.setAngle(
        (Units.radiansToDegrees(
                        Units.rotationsPerMinuteToRadiansPerSecond(
                            m_rollerLeftEncoder.getVelocity()))
                    * .02 // degrees per 20ms
                + m_mechRollerLeft.getAngle())
            % 360);
    m_mechRollerRight.setAngle(
        (Units.radiansToDegrees(
                        Units.rotationsPerMinuteToRadiansPerSecond(
                            m_rollerRightEncoder.getVelocity()))
                    * .02 // degrees per 20ms
                + m_mechRollerRight.getAngle())
            % 360);
  }

  @Override
  public void simulationPeriodic() {
    Telemetry.setValue("Arm/Claw/Simulation/Pressure", m_simulatedPressure);

    // In this method, we update our simulation of what our arm is doing
    // First, we set our "inputs" (voltages)
    m_rollerLeftMotorSim.setInput(m_rollerLeft.getAppliedOutput() * RoboRioSim.getVInVoltage());
    m_rollerRightMotorSim.setInput(m_rollerRight.getAppliedOutput() * RoboRioSim.getVInVoltage());

    // Next, we update it. The standard loop time is 20ms.
    m_rollerLeftMotorSim.update(0.02);
    m_rollerRightMotorSim.update(0.02);

    // Now, we update the Spark Flex
    m_rollerLeftSim.iterate(
        m_rollerLeftMotorSim.getAngularVelocityRPM(),
        RoboRioSim.getVInVoltage(), // Simulated battery voltage, in Volts
        0.02); // Time interval, in Seconds
    m_rollerRightSim.iterate(
        m_rollerRightMotorSim.getAngularVelocityRPM(),
        RoboRioSim.getVInVoltage(), // Simulated battery voltage, in Volts
        0.02); // Time interval, in Seconds

    if (m_simulatedPressure < 120) {
      m_pneumaticHubSim.setPressureSwitch(true);
    } else {
      m_pneumaticHubSim.setPressureSwitch(false);
    }

    if (m_pneumaticHub.getPressureSwitch()) {
      m_pneumaticHubSim.setCompressorOn(true);
      m_pneumaticHubSim.setCompressorCurrent(
          ClawConstants.SimulationConstants.COMPRESSOR_ON_CURRENT);
    } else {
      m_pneumaticHubSim.setCompressorOn(false);
      m_pneumaticHubSim.setCompressorCurrent(0);
    }

    if (m_pneumaticHub.getCompressor()) {
      m_simulatedPressure += ClawConstants.SimulationConstants.PRESSURE_INCREMENT;
    }

    if (m_clamp.get() != m_clampLastState) {
      m_simulatedPressure -= ClawConstants.SimulationConstants.PRESSURE_PISTON_DROP;
      m_clampLastState = m_clamp.get();
    }
  }
}
