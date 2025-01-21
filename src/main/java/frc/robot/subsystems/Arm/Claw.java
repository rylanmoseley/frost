package frc.robot.subsystems.Arm;

import com.revrobotics.REVLibError;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkMaxConfig;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.PneumaticHub;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj2.command.button.Trigger;

public class Claw extends SubsystemBase {
  private SparkMax m_rollerLeft = new SparkMax(0, MotorType.kBrushless);
  private SparkMax m_rollerRight = new SparkMax(0, MotorType.kBrushless);
  private PneumaticHub m_pneumaticHub = new PneumaticHub();
  private DoubleSolenoid m_clamp = m_pneumaticHub.makeDoubleSolenoid(0, 0);
  private DigitalInput m_beamBreak = new DigitalInput(0);

  public final Trigger beamBreak = new Trigger(() -> m_beamBreak.get());

  public Claw() {
    System.out.println("Claw instantiated");
  }

  public REVLibError configureAll(
      SparkMaxConfig rollerLeftConfig, SparkMaxConfig rollerRightConfig) {

    REVLibError leftErr =
        m_rollerLeft.configure(
            rollerLeftConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
    REVLibError rightErr =
        m_rollerRight.configure(
            rollerRightConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);

    // return error, if present
    if (leftErr == REVLibError.kOk) {
      return rightErr;
    }
    return leftErr;
  }
}
