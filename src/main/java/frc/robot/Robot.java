/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                                */
/*----------------------------------------------------------------------------*/
package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  private static final String kDefaultAuto = "Default";
  private static final String kCustomAuto = "My Auto";
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();

  MagicElevator ELEVATOR;
  MagicInput INPUT;  
  MagicVision VISION = new MagicVision(115200, 200, 1, 300);
  MagicOutput OUTPUT;
  MagicPneumatics PNEUMATICS;
  Compressor compressor = new Compressor(0);
  DoubleSolenoid leftSolenoid = new DoubleSolenoid(4,5);
  DoubleSolenoid rightSolenoid = new DoubleSolenoid(6,7);
  long cycles = 0;
  double forward;
  double turn;
  
  WPI_TalonSRX driveFL = new WPI_TalonSRX(1); //Forward left tank drive motor
  WPI_TalonSRX driveRL = new WPI_TalonSRX(2); //Rear left tank drive motor
  WPI_TalonSRX driveFR = new WPI_TalonSRX(3); //Forward Right tank drive motor
  WPI_TalonSRX driveRR = new WPI_TalonSRX(4); //Rear Right left tank drive motor
  // WPI_TalonSRX testLeft = new WPI_TalonSRX(10);
  // WPI_TalonSRX testRight = new WPI_TalonSRX(11);

  WPI_TalonSRX testElevator = new WPI_TalonSRX(12);

  SpeedControllerGroup leftSide = new SpeedControllerGroup(driveFL, driveRL);
  SpeedControllerGroup rightSide = new SpeedControllerGroup(driveFR, driveRR);
  DifferentialDrive chassisDrive = new DifferentialDrive(leftSide, rightSide);
  
  /**
   * This function is run when the robot is first started up and should be used
   * for any initialization code.
   */
  @Override
  public void robotInit() {
    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("My Auto", kCustomAuto);
    SmartDashboard.putData("Auto choices", m_chooser);
    INPUT = new MagicInput();
    OUTPUT = new MagicOutput(INPUT);
    ELEVATOR = new MagicElevator(testElevator, INPUT);
    PNEUMATICS = new MagicPneumatics(compressor, leftSolenoid, rightSolenoid);
    driveFL.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative);
    
  }

  /**
   * This function is called every robot packet, no matter the mode. Use this for
   * items like diagnostics that you want ran during disabled, autonomous,
   * teleoperated and test.
   *
   * <p>
   * This runs after the mode specific periodic functions, but before LiveWindow
   * and SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
    INPUT.updates(); //Update the toggling booleen
    OUTPUT.checkCamSwap();
    cycles++;
   // System.out.println(driveFL.getSelectedSensorVelocity(0));
  }
  /**
   * This autonomous (along with the chooser code above) shows how to select
   * between different autonomous modes using the dashboard. The sendable chooser
   * code works with the Java SmartDashboard. If you prefer the LabVIEW Dashboard,
   * remove all of the chooser code and uncomment the getString line to get the
   * auto name from the text box below the Gyro
   *
   * <p>
   * You can add additional auto modes by adding additional comparisons to the
   * switch structure below with additional strings. If using the SendableChooser
   * make sure to add them to the chooser code above as well.
   */

  @Override
  public void autonomousInit() {
    System.out.println("Starting autonomousInit - " + m_autoSelected);
    chassisDrive.arcadeDrive(forward, turn);
    m_autoSelected = m_chooser.getSelected();
     //autoSelected = SmartDashboard.getString("Auto Selector", defaultAuto);
    System.out.println("Auto selected: " + m_autoSelected);
    VISION.getArduino();
  }

  /**
   * This function is called periodically during autonomous.
   */
  @Override
  public void autonomousPeriodic() {
    switch (m_autoSelected) {
    case kCustomAuto:
      // Put custom auto code here
      // System.out.println("Starting autonomousPeriodic - Custom auto.");
      break;
    case kDefaultAuto:
    default:
      // Put default auto code here
      // System.out.println("Starting autonomousPeriodic - Default auto.");
      break;
    } // switch (m_autoSelected)
    if (INPUT.isButtonPressed(ButtonEnum.testBool) == true) {
      VISION.parseVal(1, 1, VISION.getArduino());
      VISION.parseVal(2, 1, VISION.getArduino());
      VISION.parseVal(5, 1, VISION.getArduino());
      VISION.parseVal(6, 1, VISION.getArduino());
    }
    // System.out.println("auto periodic loop counter: " + counting);
  } 
  @Override
  public void teleopInit() {
  }

  /**
   * This function is called periodically during operator control.
   */
  @Override
  public void teleopPeriodic() {
    //Drive code: Jack says that's all I need
    chassisDrive.arcadeDrive(INPUT.getDrive(), INPUT.getTurn());
    if (INPUT.isButtonPressed(ButtonEnum.IntakeIn)) {
      PNEUMATICS.setCyl(1, 1);
      PNEUMATICS.setCyl(0, -1);
    } else if (INPUT.isButtonPressed(ButtonEnum.IntakeOut)) {
      PNEUMATICS.setCyl(1,-1);
      PNEUMATICS.setCyl(0, 1);
    } else {
      PNEUMATICS.setCyl(1,0);
      PNEUMATICS.setCyl(0, 0);
    }
  }
  



  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {/* 
//chassisDrive.arcadeDrive(0.4, 0, false);
leftSide.set(.4);
rightSide.set(-.4);
    //test current draw
 // System.out.println("Input voltage = " + PDPJNI.getPDPVoltage(m_handle));
  System.out.println("total current of all monitored PDP channels = " + testPDPJNI.getPDPTotalCurrent(m_handle));
  //System.out.println("total energy in J of monitored channels = " + PDPJNI.getPDPTotalEnergy(m_handle));
  System.out.println(
    "current of: \nch.0 = " + PDPJNI.getPDPChannelCurrent((byte) 0,  m_handle)
   //+ ",\nch. 1 = " +PDPJNI.getPDPChannelCurrent((byte) 1,  m_handle)
   // + ",\nch. 2 = " + PDPJNI.getPDPChannelCurrent((byte) 2,  m_handle) + 
  +  ", \nch. 3 = " + PDPJNI.getPDPChannelCurrent((byte) 3,  m_handle) );
//System.out.println("total power(W):" + pdp.getTotalPower());
if (PDPJNI.getPDPChannelCurrent((byte) 0,  m_handle) != 0.0) {testItCh0++;}
if (PDPJNI.getPDPChannelCurrent((byte) 1,  m_handle) != 0.0) {testItCh1++;}
if (PDPJNI.getPDPChannelCurrent((byte) 2,  m_handle) != 0.0) {testItCh2++;}
if (PDPJNI.getPDPChannelCurrent((byte) 3,  m_handle) != 0.0) {testItCh3++;
System.out.println("channel 3 has run for " + testItCh3 + " iterations");} */
}
@Override
public void disabledInit() {

}
}
