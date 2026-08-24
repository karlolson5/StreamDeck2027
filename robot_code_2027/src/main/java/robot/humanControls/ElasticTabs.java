package frc.robot.humanControls;

import com.ctre.phoenix6.swerve.SwerveModule;
import com.ctre.phoenix6.swerve.SwerveRequest;
import edu.wpi.first.wpilibj2.command.Commands;
import frc.lib.util.COColor;
import frc.lib.util.Controls.ElasticButton.ElasticDashboard;
import frc.lib.util.Controls.ElasticButton.ElasticTab;
import frc.robot.Robot;
import frc.robot.RobotContainer;
import frc.robot.RobotState;
import frc.robot.commands.DriveToFuelCommand;
import frc.robot.commands.FlywheelAutoTuneCommand;
import frc.robot.commands.FlywheelAutoTuneConfig;
import frc.robot.commands.ShooterTuningCommand;
import frc.robot.subsystems.box.Box;
import frc.robot.subsystems.drive.Drive;
import frc.robot.subsystems.drive.DriveConstants;
import frc.robot.subsystems.drive.DriveConstants.ClimbSide;
import frc.robot.subsystems.feeder.Feeder;
import frc.robot.subsystems.feeder.FeederManager;
import frc.robot.subsystems.hopper.Hopper;
import frc.robot.subsystems.hopper.HopperManager;
import frc.robot.subsystems.intake.IntakeManager;
import frc.robot.subsystems.intake.IntakePivot;
import frc.robot.subsystems.intake.IntakeRoller;
import frc.robot.subsystems.led.Led;
import frc.robot.subsystems.led.LedConstants.LedStrip;
import frc.robot.subsystems.shooter.Hood;
import frc.robot.subsystems.shooter.HoodConstants;
import frc.robot.subsystems.shooter.Shooter;
import frc.robot.subsystems.shooter.ShooterManager;
import frc.robot.subsystems.vision.Vision;
import frc.robot.subsystems.vision.VisionConstants;

public class ElasticTabs {
    private final RobotContainer container;

    private final IntakePivot intakePivot;
    private final IntakeRoller intakeRoller;
    private final IntakeManager intakeManager;

    private final ElasticDashboard dashboard = new ElasticDashboard();

    public ElasticTabs(RobotContainer container, RobotState robotState) {
        this.container = container;
        this.robotState = robotState;

        intakePivot = container.getIntakePivot();
        intakeRoller = container.getIntakeRoller();
        intakeManager = container.getIntakeManager();

        buildElasticTabs();
    }

    private void buildElasticTabs() {
        // Only build tabs for enabled subsystems
        if (intakeManager.isEnabled()) {
            buildIntakeTab();
        }
        buildUtilTab();
    }

    private void buildIntakeTab() {
        String key = "Intake";
        ElasticTab tab = dashboard.addTab(key, intakeRoller, intakePivot);

        // Configure the while-held behavior
        tab.addHoldButton("Roller Forward (While Held)").whileTrue(intakeRoller.intakeFWDCommand()).onFalse(intakeRoller.intakeSTOPCommand());
        tab.addHoldButton("Roller Reverse (While Held)").whileTrue(intakeRoller.intakeREVCommand()).onFalse(intakeRoller.intakeSTOPCommand());

        tab.addHoldButton("Pivot Voltage Up (While Held)").whileTrue(intakePivot.pivotVoltageUp()).onFalse(intakePivot.pivotStopCommand());
        tab.addHoldButton("Pivot Voltage Down (While Held)").whileTrue(intakePivot.pivotVoltageDown()).onFalse(intakePivot.pivotStopCommand());
        tab.addPressButton("Pivot Stow (When Pressed)").onTrue(intakePivot.manualStowCommand());
        tab.addPressButton("Pivot Deployed Position (When Pressed)").onTrue(intakePivot.manualDeployCommand());
        tab.addPressButton("Pivot Position Command (When Pressed)").onTrue(intakePivot.pivotPositionControl());

        tab.addPressButton("STOW (When Pressed)").onTrue(intakeManager.stowIntake());
        tab.addPressButton("STOW SLOWLY (When Pressed)").onTrue(intakeManager.stowIntakeSlowly());
        tab.addPressButton("INTAKE (When Pressed)").onTrue(intakeManager.deployIntake());
        tab.addPressButton("DEPLOY (When Pressed)").onTrue(intakeManager.deployIntake());

        tab.addPressButton("Print Intake Absolute Rotations").onTrue(intakePivot.printAbsoluteRotations());
    }

    private void buildUtilTab() {
        String key = "Util";
        ElasticTab tab = dashboard.addTab(key);

        tab.addPressButton("Set Box and Hood to Coast (When Pressed)").onTrue(hood.setCoast().alongWith(box.setCoast()));
        tab.addPressButton("Generate Json").onTrue(dashboard.generateDashboardJsonCommand());
    }
}