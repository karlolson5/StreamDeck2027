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
    private final Drive drive;

    private final IntakePivot intakePivot;
    private final IntakeRoller intakeRoller;
    private final IntakeManager intakeManager;

    private final Hood hood;
    private final Shooter shooter;
    private final ShooterManager shooterManager;

    private final Feeder feeder;
    private final FeederManager feederManager;

    private final Box box;
    private final Hopper hopper;
    private final HopperManager hopperManager;

    private final RobotState robotState;
    private final Led led;
    private final Vision vision;

    private final ElasticDashboard dashboard = new ElasticDashboard();

    public ElasticTabs(RobotContainer container, RobotState robotState) {
        this.container = container;
        this.robotState = robotState;
        drive = container.getDrive();

        intakePivot = container.getIntakePivot();
        intakeRoller = container.getIntakeRoller();
        intakeManager = container.getIntakeManager();

        box = container.getBox();

        hood = container.getHood();
        shooter = container.getShooter();
        shooterManager = container.getShooterManager();

        feeder = container.getFeeder();
        feederManager = container.getFeederManager();

        hopper = container.getHopper();
        hopperManager = container.getHopperManager();

        // TODO: all the managers
        vision = container.getVision();
        led = container.getLed();
        buildElasticTabs();
    }

    private void buildElasticTabs() {
        // Only build tabs for enabled subsystems
        if (intakeManager.isEnabled()) {
            buildIntakeTab();
        }
        if (hopperManager.isEnabled()) {
            buildHopperTab();
        }
        if (shooterManager.isEnabled()) {
            buildShooterTab();
        }
        if (feederManager.isEnabled()) {
            buildFeederTab();
        }
        if (box.isEnabled()) {
            buildBoxTab();
        }
        buildDriveTab(); // Drive is always enabled
        buildLedTab(); // LED is always enabled
        buildTestTab();
    }

    private void buildIntakeTab() {
        String key = "Intake";
        ElasticTab tab = dashboard.addTab(key, intakeRoller, intakePivot);

        // Configure the while-held behavior
        tab.addButton("Roller Forward (While Held)")
                .whileTrue(intakeRoller.intakeFWDCommand()).onFalse(intakeRoller.intakeSTOPCommand());
        tab.addButton("Roller Reverse (While Held)")
                .whileTrue(intakeRoller.intakeREVCommand()).onFalse(intakeRoller.intakeSTOPCommand());

        tab.addButton("Pivot Voltage Up (While Held)")
                .whileTrue(intakePivot.pivotVoltageUp()).onFalse(intakePivot.pivotStopCommand());
        tab.addButton("Pivot Voltage Down (While Held)")
                .whileTrue(intakePivot.pivotVoltageDown()).onFalse(intakePivot.pivotStopCommand());
        tab.addButton("Pivot Stow (When Pressed)").onTrue(intakePivot.manualStowCommand());
        tab.addButton("Pivot Deployed Position (When Pressed)").onTrue(intakePivot.manualDeployCommand());
        tab.addButton("Pivot Position Command (When Pressed)").onTrue(intakePivot.pivotPositionControl());

        tab.addButton("STOW (When Pressed)").onTrue(intakeManager.stowIntake());
        tab.addButton("STOW SLOWLY (When Pressed)").onTrue(intakeManager.stowIntakeSlowly());
        tab.addButton("INTAKE (When Pressed)").onTrue(intakeManager.deployIntake());
        tab.addButton("DEPLOY (When Pressed)").onTrue(intakeManager.deployIntake());

        tab.addButton("Print Intake Absolute Rotations").onTrue(intakePivot.printAbsoluteRotations());
    }

    private void buildHopperTab() {
        String key = "Hopper";
        ElasticTab tab = dashboard.addTab(key, hopper);

        tab.addButton("Hopper Voltage FWD (While Held)")
                .whileTrue(hopper.hopperSTARTCommand()).onFalse(hopper.hopperSTOPCommand());
        tab.addButton("Hopper Voltage REV (While Held)")
                .whileTrue(hopper.hopperREVCommand()).onFalse(hopper.hopperSTOPCommand());
    }

    private void buildShooterTab() {
        String key = "Shooter";
        ElasticTab tab = dashboard.addTab(key, hood, shooter);

        tab.addButton("Hood Voltage UP Command (While Held)")
                .whileTrue(hood.hoodVoltageUP()).onFalse(hood.hoodVoltageSTOP());
        tab.addButton("Hood Voltage DOWN Command (While Held)")
                .whileTrue(hood.hoodVoltageDOWN()).onFalse(hood.hoodVoltageSTOP());
        tab.addButton("Hood Max Up (When Pressed)")
                .onTrue(hood.positionSetpointCommand(() -> HoodConstants.kForwardLimitRads));
        tab.addButton("Hood Max Down (When Pressed)")
                .onTrue(hood.positionSetpointCommand(() -> HoodConstants.kHoodStowedAngleRadians));
        tab.addButton("Move Hood Position (When Pressed)").onTrue(hood.hoodPositionControl());
        tab.addButton("Set Current Hood Position as Zero (WhenPressed)")
                .onTrue(hood.setHoodZeroCommand());
        tab.addButton("Hood Zero Command (When Pressed)").onTrue(hood.zeroHoodCommand());

        tab.addButton("Set Hood To Zero")
                .onTrue(Commands.runOnce(() -> hood.setCurrentPosition(HoodConstants.kReverseLimitRads)));

        tab.addButton("Set Shooter Torque FOC (While Held)")
                .whileTrue(shooter.shooterTorqueON()).onFalse(shooter.shooterSTOP());

        tab.addButton("Set Shooter Torque FoC Velocity (While Held)")
                .whileTrue(shooter.shooterTorqueFoCVelocityON()).onFalse(shooter.shooterSTOP());

        tab.addButton("AutoTune Flywheel (While Held)")
                .whileTrue(
                        new FlywheelAutoTuneCommand(
                                shooter,
                                Robot.isSimulation()
                                        ? FlywheelAutoTuneConfig.forShooterSim()
                                        : FlywheelAutoTuneConfig.forShooter()))
                .onFalse(
                        shooter.shooterSTOP());

        // tab.addButton("Shooter Max Speed (While Held)")
        //         .whileTrue(shooter.shooterON()).onFalse(shooter.shooterSTOP());
        tab.addButton("Shooter Voltage Command (While Held)")
                .whileTrue(shooter.shooterVoltageON()).onFalse(shooter.shooterSTOP());
        // tab.addButton("Shooting with Lookup Table (While
        // Held)").whileTrue(shooterManager.startShooting())

        tab.addButton("LUT Tuning (While Held)")
                .whileTrue(
                        new ShooterTuningCommand(shooter, hood, shooterManager, feederManager, hopperManager, feeder))
                .onFalse(
                        Commands.parallel(
                                shooter.shooterSTOP(),
                                hood.positionSetpointCommand(() -> HoodConstants.kReverseLimitRads)));
    }

    private void buildFeederTab() {
        String key = "Feeder";
        ElasticTab tab = dashboard.addTab(key, feeder);

        tab.addButton("Feeder Up Velocity torque (While Held)")
                .whileTrue(feeder.feederVelocityControlUP()).onFalse(feeder.feederSTOPCommand());
        tab.addButton("Feeder Down Velocity torque (While Held)")
                .whileTrue(feeder.feederVelocityControlDOWN()).onFalse(feeder.feederSTOPCommand());
        tab.addButton("Feeder Up Voltage (While Held)")
                .whileTrue(feeder.feederUPCommand()).onFalse(feeder.feederSTOPCommand());
        tab.addButton("Feeder Down Voltage (While Held)")
                .whileTrue(feeder.feederDOWNCommand()).onFalse(feeder.feederSTOPCommand());
        tab.addButton("Feeder UP Current (While Held)")
                .whileTrue(feeder.feederCurrentUPCommand()).onFalse(feeder.feederSTOPCommand());
        tab.addButton("Feeder DOWN Current (While Held)")
                .whileTrue(feeder.feederCurrentDOWNCommand()).onFalse(feeder.feederSTOPCommand());

        tab.addButton("AutoTune Feeder (While Held)")
                .whileTrue(
                        new FlywheelAutoTuneCommand(
                                feeder,
                                Robot.isSimulation()
                                        ? FlywheelAutoTuneConfig.forShooterSim()
                                        : FlywheelAutoTuneConfig.forFeeder()))
                .onFalse(
                        feeder.feederSTOPCommand());
    }

    private void buildBoxTab() {
        String key = "Box";
        ElasticTab tab = dashboard.addTab(key, box);

        tab.addButton("Box Extend Command (While Held)").onTrue(box.setExtend());
        tab.addButton("Box Stow Command (While Held)").onTrue(box.setPushingStow());
        tab.addButton("Box Climb Command (When Pressed)").onTrue(box.setClimb());
        tab.addButton("Box Zero Command (When Pressed)").onTrue(box.zeroBoxCommand());
        tab.addButton("Set Current Box Position To Zero (When Pressed)")
                .onTrue(Commands.runOnce(() -> box.setCurrentPosition(0.0)));
        tab.addButton("Box Voltage UP Command (While Held)").whileTrue(box.boxUPCommand());
        tab.addButton("Box Voltage DOWN Command (While Held)").whileTrue(box.boxDOWNCommand());
    }

    private void buildDriveTab() {
        String key = "Drive";
        ElasticTab tab = dashboard.addTab(key);
        SwerveRequest.FieldCentric fieldCentricReq =
                new SwerveRequest.FieldCentric().withDriveRequestType(SwerveModule.DriveRequestType.Velocity);
        // tab.addButton("Characterize
        // Feedforward").whileTrue(DriveCommands.feedforwardCharacterization(drive));
        // tab.addButton("Characterize Slip Current").whileTrue(
        //     Commands.print("running slip current test")
        //     .andThen(DriveCommands.slipCurrentCharacterization(drive)));
        // tab.addButton("Characterize Wheel Radius")
        //     .whileTrue(DriveCommands.wheelRadiusCharacterization(drive));
        // tab.addButton("Drive Stop X").onTrue(
        //     Commands.runOnce(
        //         drive.setControl(
        //             fieldCentricReq
        //             .withVelocityX(0)
        //         )
        //     )

        //     );
        tab.addButton("Drive Forward Half Speed (While Held)")
                .whileTrue(
                        Commands.run(
                                () -> drive.setControl(fieldCentricReq
                                        .withVelocityX(0.5 * DriveConstants.kDriveMaxSpeed)
                                        .withVelocityY(0)
                                        .withRotationalRate(0)),
                                drive))
                .onFalse(
                        Commands.run(
                                () -> drive.setControl(fieldCentricReq
                                        .withVelocityX(0)
                                        .withVelocityY(0)
                                        .withRotationalRate(0)),
                                drive));

        tab.addButton("Drive Turn Clockwise (While Held)")
                .whileTrue(
                        Commands.run(
                                () -> drive.setControl(fieldCentricReq
                                        .withVelocityX(0)
                                        .withVelocityY(0)
                                        .withRotationalRate(0.5 * DriveConstants.kDriveMaxAngularRate)),
                                drive))
                .onFalse(
                        Commands.run(
                                () -> drive.setControl(fieldCentricReq
                                        .withVelocityX(0)
                                        .withVelocityY(0)
                                        .withRotationalRate(0)),
                                drive));

        tab.addButton("Drive Robot Relative")
                .whileTrue(drive.joystickDriveRobotRelative(
                        () -> -container.getController().getLeftY(),
                        () -> -container.getController().getLeftX(),
                        () -> -container.getController().getRightX()));

        // tab.addButton("Align to hub");

        tab.addButton("Reset Pose To Vision")
                .onTrue(Commands.runOnce(
                        () -> {
                            var visionPose = RobotState.getVisionPose(VisionConstants.visionPoseThresholdSeconds);
                            if (visionPose.isPresent()) {
                                drive.resetOdometry(visionPose.get());
                            }
                        },
                        drive));

        tab.addButton("Test Drive To Pose").whileTrue(new DriveToFuelCommand(container));
        tab.addButton("Toggle rotate in movement direction").onTrue(Commands.runOnce(() -> {
            drive.toggleRotateHeading();
        }));
        tab.addButton("Left Climb").whileTrue(box.setExtend().andThen(drive.autoClimb(ClimbSide.LEFT)));
        tab.addButton("Right Climb").whileTrue(box.setExtend().andThen(drive.autoClimb(ClimbSide.RIGHT)));
    }

    private void buildLedTab() {
        String key = "Led";
        ElasticTab tab = dashboard.addTab(key);

        tab.addButton("Solid Red").onTrue(led.commandSolidColor(COColor.kRed));
        tab.addButton("Right Red")
                .onTrue(led.commandSolidColor(COColor.kRed, LedStrip.RIGHT));
        tab.addButton("Left Yellow").onTrue(led.commandSolidColor(COColor.kYellow, LedStrip.LEFT));
        tab.addButton("Right Red Left Yellow").onTrue(led.commandSolidColor(COColor.kRed, LedStrip.RIGHT)
                        .andThen(led.commandSolidColor(COColor.kYellow, LedStrip.LEFT)));
        tab.addButton("Solid Orange").onTrue(led.commandSetOrange());
        tab.addButton("Solid Teal").onTrue(led.commandSetTeal());
        tab.addButton("Blink Red").onTrue(led.commandBlinkingState(COColor.kRed, 0.5));
        tab.addButton("Fire").onTrue(led.commandFire());
        tab.addButton("Rainbow").onTrue(led.commandRainbow());
        tab.addButton("ColorflowCO").onTrue(led.commandColorflowCO());
        tab.addButton("Off").onTrue(led.commandOff());
        tab.addButton("Half Orange").onTrue(led.commandPercentageFull(() -> 0.5, COColor.kCOOrangeLed));
        tab.addButton("Partial Orange").onTrue(led.commandSolidColorNumLeds(COColor.kCOOrangeLed, led::getLedsOn));
        tab.addButton("Larson Blue").onTrue(led.commandLarson(COColor.kBlue));
        tab.addButton("Larson Red").onTrue(led.commandLarson(COColor.kRed));
        tab.addButton("Twinkle Off Blue").onTrue(led.commandTwinkle(COColor.kBlue, true));
        tab.addButton("Twinkle Off Red").onTrue(led.commandTwinkle(COColor.kRed, true));
        tab.addButton("Twinkle Blue").onTrue(led.commandTwinkle(COColor.kBlue, false));
        tab.addButton("Twinkle Red").onTrue(led.commandTwinkle(COColor.kRed, false));
    }

    private void buildTestTab() {
        String key = "Test";
        ElasticTab tab = dashboard.addTab(key);

        tab.addButton("Set Box and Hood to Coast (When Pressed)")
                .onTrue(hood.setCoast().alongWith(box.setCoast()));
        tab.addButton("Generate Json").onTrue(dashboard.generateDashboardJsonCommand());
    }
}