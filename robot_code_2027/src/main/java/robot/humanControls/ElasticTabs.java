package frc.robot.humanControls;

import com.ctre.phoenix6.swerve.SwerveModule;
import com.ctre.phoenix6.swerve.SwerveRequest;
import frc.lib.util.Controls.ElasticButton.ElasticDashboard;
import frc.lib.util.Controls.ElasticButton.ElasticTab;
import frc.robot.RobotContainer;
import frc.robot.RobotState;
import frc.robot.subsystems.box.Box;
import frc.robot.subsystems.intake.IntakePivot;
import frc.robot.subsystems.intake.IntakeRoller;
import frc.robot.subsystems.shooter.Hood;

public class ElasticTabs {
    private final RobotContainer container;
    private final RobotState robotState;

    private final IntakePivot intakePivot;
    private final IntakeRoller intakeRoller;
    private final IntakeManager intakeManager;
    private final Hood hood;
    private final Box box;

    private final ElasticDashboard dashboard = new ElasticDashboard();

    public ElasticTabs(RobotContainer container, RobotState robotState) {
        this.container = container;
        this.robotState = robotState;

        intakePivot = container.getIntakePivot();
        intakeRoller = container.getIntakeRoller();
        intakeManager = container.getIntakeManager();
        hood = container.getHood();
        box = container.getBox();

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