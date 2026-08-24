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
        ElasticTab tab = dashboard.addTab(key, container.getIntakeRoller(), container.getIntakePivot());

        // Configure the while-held behavior
        tab.addButton("Roller Forward (While Held)")
                .setupWhileHeldCommand(intakeRoller.intakeFWDCommand(), intakeRoller.intakeSTOPCommand());
        tab.addButton("Roller Reverse (While Held)")
                .setupWhileHeldCommand(intakeRoller.intakeREVCommand(), intakeRoller.intakeSTOPCommand());

        tab.addButton("Pivot Voltage Up (While Held)")
                .setupWhileHeldCommand(intakePivot.pivotVoltageUp(), intakePivot.pivotStopCommand());
        tab.addButton("Pivot Voltage Down (While Held)")
                .setupWhileHeldCommand(intakePivot.pivotVoltageDown(), intakePivot.pivotStopCommand());
        tab.addButton("Pivot Stow (When Pressed)").setupOnPressCommand(intakePivot.manualStowCommand());
        tab.addButton("Pivot Deployed Position (When Pressed)").setupOnPressCommand(intakePivot.manualDeployCommand());
        tab.addButton("Pivot Position Command (When Pressed)").setupOnPressCommand(intakePivot.pivotPositionControl());

        tab.addButton("STOW (When Pressed)").setupOnPressCommand(intakeManager.stowIntake());
        tab.addButton("STOW SLOWLY (When Pressed)").setupOnPressCommand(intakeManager.stowIntakeSlowly());
        tab.addButton("INTAKE (When Pressed)").setupOnPressCommand(intakeManager.deployIntake());
        tab.addButton("DEPLOY (When Pressed)").setupOnPressCommand(intakeManager.deployIntake());

        tab.addButton("Print Intake Absolute Rotations")
                .setupOnPressCommandIgnoringDisabled(intakePivot.printAbsoluteRotations());
    }

    private void buildHopperTab() {
        String key = "Hopper";
        ElasticTab tab = dashboard.addTab(key, container.getHopper());

        tab.addButton("Hopper Voltage FWD (While Held)")
                .setupWhileHeldCommand(hopper.hopperSTARTCommand(), hopper.hopperSTOPCommand());
        tab.addButton("Hopper Voltage REV (While Held)")
                .setupWhileHeldCommand(hopper.hopperREVCommand(), hopper.hopperSTOPCommand());
    }

    private void buildShooterTab() {
        String key = "Shooter";
        ElasticTab tab = dashboard.addTab(key, container.getHood(), container.getShooter());

        tab.addButton("Hood Voltage UP Command (While Held)")
                .setupWhileHeldCommand(hood.hoodVoltageUP(), hood.hoodVoltageSTOP());
        tab.addButton("Hood Voltage DOWN Command (While Held)")
                .setupWhileHeldCommand(hood.hoodVoltageDOWN(), hood.hoodVoltageSTOP());
        tab.addButton("Hood Max Up (When Pressed)")
                .setupOnPressCommand(hood.positionSetpointCommand(() -> HoodConstants.kForwardLimitRads));
        tab.addButton("Hood Max Down (When Pressed)")
                .setupOnPressCommand(hood.positionSetpointCommand(() -> HoodConstants.kHoodStowedAngleRadians));
        tab.addButton("Move Hood Position (When Pressed)").setupOnPressCommand(hood.hoodPositionControl());
        tab.addButton("Set Current Hood Position as Zero (WhenPressed)")
                .setupOnPressCommand(hood.setHoodZeroCommand().ignoringDisable(true));
        tab.addButton("Hood Zero Command (When Pressed)").setupOnPressCommand(hood.zeroHoodCommand());

        tab.addButton("Set Hood To Zero")
                .setupOnPressCommand(Commands.runOnce(() -> hood.setCurrentPosition(HoodConstants.kReverseLimitRads)));

        tab.addButton("Set Shooter Torque FOC (While Held)")
                .setupWhileHeldCommand(shooter.shooterTorqueON(), shooter.shooterSTOP());

        tab.addButton("Set Shooter Torque FoC Velocity (While Held)")
                .setupWhileHeldCommand(shooter.shooterTorqueFoCVelocityON(), shooter.shooterSTOP());

        tab.addButton("AutoTune Flywheel (While Held)")
                .setupWhileHeldCommand(
                        new FlywheelAutoTuneCommand(
                                shooter,
                                Robot.isSimulation()
                                        ? FlywheelAutoTuneConfig.forShooterSim()
                                        : FlywheelAutoTuneConfig.forShooter()),
                        shooter.shooterSTOP());

        // tab.addButton("Shooter Max Speed (While Held)")
        //         .setupWhileHeldCommand(shooter.shooterON(), shooter.shooterSTOP());
        tab.addButton("Shooter Voltage Command (While Held)")
                .setupWhileHeldCommand(shooter.shooterVoltageON(), shooter.shooterSTOP());
        // tab.addButton("Shooting with Lookup Table (While
        // Held)").setupWhileHeldCommand(shooterManager.startShooting())

        tab.addButton("LUT Tuning (While Held)")
                .setupWhileHeldCommand(
                        new ShooterTuningCommand(shooter, hood, shooterManager, feederManager, hopperManager, feeder),
                        Commands.parallel(
                                shooter.shooterSTOP(),
                                hood.positionSetpointCommand(() -> HoodConstants.kReverseLimitRads)));
    }

    private void buildFeederTab() {
        String key = "Feeder";
        ElasticTab tab = dashboard.addTab(key, container.getFeeder());

        tab.addButton("Feeder Up Velocity torque (While Held)")
                .setupWhileHeldCommand(feeder.feederVelocityControlUP(), feeder.feederSTOPCommand());
        tab.addButton("Feeder Down Velocity torque (While Held)")
                .setupWhileHeldCommand(feeder.feederVelocityControlDOWN(), feeder.feederSTOPCommand());
        tab.addButton("Feeder Up Voltage (While Held)")
                .setupWhileHeldCommand(feeder.feederUPCommand(), feeder.feederSTOPCommand());
        tab.addButton("Feeder Down Voltage (While Held)")
                .setupWhileHeldCommand(feeder.feederDOWNCommand(), feeder.feederSTOPCommand());
        tab.addButton("Feeder UP Current (While Held)")
                .setupWhileHeldCommand(feeder.feederCurrentUPCommand(), feeder.feederSTOPCommand());
        tab.addButton("Feeder DOWN Current (While Held)")
                .setupWhileHeldCommand(feeder.feederCurrentDOWNCommand(), feeder.feederSTOPCommand());

        tab.addButton("AutoTune Feeder (While Held)")
                .setupWhileHeldCommand(
                        new FlywheelAutoTuneCommand(
                                feeder,
                                Robot.isSimulation()
                                        ? FlywheelAutoTuneConfig.forShooterSim()
                                        : FlywheelAutoTuneConfig.forFeeder()),
                        feeder.feederSTOPCommand());
    }

    private void buildBoxTab() {
        String key = "Box";
        ElasticTab tab = dashboard.addTab(key, container.getBox());

        tab.addButton("Box Extend Command (While Held)").setupOnPressCommand(box.setExtend());
        tab.addButton("Box Stow Command (While Held)").setupOnPressCommand(box.setPushingStow());
        tab.addButton("Box Climb Command (When Pressed)").setupOnPressCommand(box.setClimb());
        tab.addButton("Box Zero Command (When Pressed)").setupOnPressCommand(box.zeroBoxCommand());
        tab.addButton("Set Current Box Position To Zero (When Pressed)")
                .setupOnPressCommand(Commands.runOnce(() -> box.setCurrentPosition(0.0)));
        tab.addButton("Box Voltage UP Command (While Held)").setupWhileHeldCommand(box.boxUPCommand());
        tab.addButton("Box Voltage DOWN Command (While Held)").setupWhileHeldCommand(box.boxDOWNCommand());
    }

    private void buildDriveTab() {
        String key = "Drive";
        ElasticTab tab = dashboard.addTab(key);
        SwerveRequest.FieldCentric fieldCentricReq =
                new SwerveRequest.FieldCentric().withDriveRequestType(SwerveModule.DriveRequestType.Velocity);
        // tab.addButton("Characterize
        // Feedforward").setupWhileHeldCommand(DriveCommands.feedforwardCharacterization(drive));
        // tab.addButton("Characterize Slip Current").setupWhileHeldCommand(
        //     Commands.print("running slip current test")
        //     .andThen(DriveCommands.slipCurrentCharacterization(drive)));
        // tab.addButton("Characterize Wheel Radius")
        //     .setupWhileHeldCommand(DriveCommands.wheelRadiusCharacterization(drive));
        // tab.addButton("Drive Stop X").setupOnPressCommand(
        //     Commands.runOnce(
        //         drive.setControl(
        //             fieldCentricReq
        //             .withVelocityX(0)
        //         )
        //     )

        //     );
        tab.addButton("Drive Forward Half Speed (While Held)")
                .setupWhileHeldCommand(
                        Commands.run(
                                () -> drive.setControl(fieldCentricReq
                                        .withVelocityX(0.5 * DriveConstants.kDriveMaxSpeed)
                                        .withVelocityY(0)
                                        .withRotationalRate(0)),
                                drive),
                        Commands.run(
                                () -> drive.setControl(fieldCentricReq
                                        .withVelocityX(0)
                                        .withVelocityY(0)
                                        .withRotationalRate(0)),
                                drive));

        tab.addButton("Drive Turn Clockwise (While Held)")
                .setupWhileHeldCommand(
                        Commands.run(
                                () -> drive.setControl(fieldCentricReq
                                        .withVelocityX(0)
                                        .withVelocityY(0)
                                        .withRotationalRate(0.5 * DriveConstants.kDriveMaxAngularRate)),
                                drive),
                        Commands.run(
                                () -> drive.setControl(fieldCentricReq
                                        .withVelocityX(0)
                                        .withVelocityY(0)
                                        .withRotationalRate(0)),
                                drive));

        tab.addButton("Drive Robot Relative")
                .setupWhileHeldCommand(drive.joystickDriveRobotRelative(
                        () -> -container.getController().getLeftY(),
                        () -> -container.getController().getLeftX(),
                        () -> -container.getController().getRightX()));

        // tab.addButton("Align to hub");

        tab.addButton("Reset Pose To Vision")
                .setupOnPressCommand(Commands.runOnce(
                        () -> {
                            var visionPose = RobotState.getVisionPose(VisionConstants.visionPoseThresholdSeconds);
                            if (visionPose.isPresent()) {
                                drive.resetOdometry(visionPose.get());
                            }
                        },
                        drive));

        tab.addButton("Test Drive To Pose").setupWhileHeldCommand(new DriveToFuelCommand(container));
        tab.addButton("Toggle rotate in movement direction").setupOnPressCommand(Commands.runOnce(() -> {
            drive.toggleRotateHeading();
        }));
        tab.addButton("Left Climb").setupWhileHeldCommand(box.setExtend().andThen(drive.autoClimb(ClimbSide.LEFT)));
        tab.addButton("Right Climb").setupWhileHeldCommand(box.setExtend().andThen(drive.autoClimb(ClimbSide.RIGHT)));
    }

    private void buildLedTab() {
        String key = "Led";
        ElasticTab tab = dashboard.addTab(key);

        tab.addButton("Solid Red").setupOnPressCommandIgnoringDisabled(led.commandSolidColor(COColor.kRed));
        tab.addButton("Right Red")
                .setupOnPressCommandIgnoringDisabled(led.commandSolidColor(COColor.kRed, LedStrip.RIGHT));
        tab.addButton("Left Yellow")
                .setupOnPressCommandIgnoringDisabled(led.commandSolidColor(COColor.kYellow, LedStrip.LEFT));
        tab.addButton("Right Red Left Yellow")
                .setupOnPressCommandIgnoringDisabled(led.commandSolidColor(COColor.kRed, LedStrip.RIGHT)
                        .andThen(led.commandSolidColor(COColor.kYellow, LedStrip.LEFT)));
        tab.addButton("Solid Orange").setupOnPressCommandIgnoringDisabled(led.commandSetOrange());
        tab.addButton("Solid Teal").setupOnPressCommandIgnoringDisabled(led.commandSetTeal());
        tab.addButton("Blink Red").setupOnPressCommandIgnoringDisabled(led.commandBlinkingState(COColor.kRed, 0.5));
        tab.addButton("Fire").setupOnPressCommandIgnoringDisabled(led.commandFire());
        tab.addButton("Rainbow").setupOnPressCommandIgnoringDisabled(led.commandRainbow());
        tab.addButton("ColorflowCO").setupOnPressCommandIgnoringDisabled(led.commandColorflowCO());
        tab.addButton("Off").setupOnPressCommandIgnoringDisabled(led.commandOff());
        tab.addButton("Half Orange")
                .setupOnPressCommandIgnoringDisabled(led.commandPercentageFull(() -> 0.5, COColor.kCOOrangeLed));
        tab.addButton("Partial Orange")
                .setupOnPressCommandIgnoringDisabled(
                        led.commandSolidColorNumLeds(COColor.kCOOrangeLed, led::getLedsOn));
        tab.addButton("Larson Blue").setupOnPressCommandIgnoringDisabled(led.commandLarson(COColor.kBlue));
        tab.addButton("Larson Red").setupOnPressCommandIgnoringDisabled(led.commandLarson(COColor.kRed));
        tab.addButton("Twinkle Off Blue").setupOnPressCommandIgnoringDisabled(led.commandTwinkle(COColor.kBlue, true));
        tab.addButton("Twinkle Off Red").setupOnPressCommandIgnoringDisabled(led.commandTwinkle(COColor.kRed, true));
        tab.addButton("Twinkle Blue").setupOnPressCommandIgnoringDisabled(led.commandTwinkle(COColor.kBlue, false));
        tab.addButton("Twinkle Red").setupOnPressCommandIgnoringDisabled(led.commandTwinkle(COColor.kRed, false));
    }

    private void buildTestTab() {
        String key = "Test";
        ElasticTab tab = dashboard.addTab(key);

        tab.addButton("Set Box and Hood to Coast (When Pressed)")
                .setupOnPressCommand(hood.setCoast().alongWith(box.setCoast()).ignoringDisable(true));
        tab.addButton("Generate Json")
                .setupOnPressCommandIgnoringDisabled(Commands.runOnce(dashboard::generateDashboardJson));
    }
}