package frc.robot.humanControls;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import frc.lib.util.COColor;
import frc.lib.util.Controls.StreamDeck.StreamDeck;
import frc.lib.util.Controls.StreamDeck.StreamDeck.CommandType;
import frc.lib.util.Controls.StreamDeck.StreamDeck.StreamDeckButtonType;
import frc.lib.util.Controls.StreamDeck.StreamDeck.StreamDeckCommand;
import frc.lib.util.Controls.StreamDeck.StreamDeckButton;
import frc.lib.util.Controls.StreamDeck.StreamDeckButtonConfig;
import frc.robot.HubStateTracker;
import frc.robot.HubStateTracker.AutoResult;
import frc.robot.RobotContainer;
import frc.robot.RobotState;
import frc.robot.subsystems.box.Box;
import frc.robot.subsystems.box.Box.BoxState;
import frc.robot.subsystems.drive.Drive;
import frc.robot.subsystems.drive.DriveConstants.ClimbSide;
import frc.robot.subsystems.feeder.FeederConstants;
import frc.robot.subsystems.feeder.FeederConstants.FeederState;
import frc.robot.subsystems.feeder.FeederManager;
import frc.robot.subsystems.hopper.Hopper;
import frc.robot.subsystems.hopper.HopperConstants.HopperState;
import frc.robot.subsystems.hopper.HopperManager;
import frc.robot.subsystems.intake.IntakeManager;
import frc.robot.subsystems.intake.IntakeManager.IntakeState;
import frc.robot.subsystems.intake.IntakePivot;
import frc.robot.subsystems.intake.IntakeRoller;
import frc.robot.subsystems.shooter.Hood;
import frc.robot.subsystems.shooter.Shooter;
import frc.robot.subsystems.shooter.ShooterManager;
import frc.robot.subsystems.shooter.ShooterManager.ShooterOverride;
import frc.robot.subsystems.vision.VisionConstants;

public class StreamDeckControls {

    private final RobotContainer container;
    private final StreamDeck streamdeck;
    private final ShooterManager shooterManager;
    private final FeederManager feederManager;
    private final Drive drive;
    private final HopperManager hopperManager;
    private final IntakeManager intakeManager;
    private final Hopper hopper;
    private final Shooter shooter;
    private final IntakePivot intakePivot;
    private final IntakeRoller intakeRoller;
    private final Hood hood;
    private final Box box;

    public StreamdeckControls(RobotContainer container) {
        this.container = container;
        streamdeck = container.getStreamDeck();
        shooterManager = container.getShooterManager();
        feederManager = container.getFeederManager();
        drive = container.getDrive();
        hopperManager = container.getHopperManager();
        intakeManager = container.getIntakeManager();
        shooter = container.getShooter();
        hopper = container.getHopper();
        intakePivot = container.getIntakePivot();
        intakeRoller = container.getIntakeRoller();
        hood = container.getHood();
        box = container.getBox();
        configureStreamdeckBindings();
    }

    private void configureStreamdeckBindings() {
        StreamDeckButtonConfig orangeConfig =
                new StreamDeckButtonConfig(COColor.kCOOrangePure.toString(), COColor.kBlack.toString(), "");
        StreamDeckButtonConfig blackConfig =
                new StreamDeckButtonConfig(COColor.kBlack.toString(), COColor.kWhite.toString(), "");
        StreamDeckButtonConfig tealConfig =
                new StreamDeckButtonConfig(COColor.kCOTealPure.toString(), COColor.kWhite.toString(), "");
        StreamDeckButtonConfig tealOnWhiteConfig =
                new StreamDeckButtonConfig(COColor.kWhite.toString(), COColor.kCOTealPure.toString(), "");
        StreamDeckButtonConfig orangeOnWhiteConfig =
                new StreamDeckButtonConfig(COColor.kWhite.toString(), COColor.kCOOrangePure.toString(), "");
        StreamDeckButtonConfig redConfig =
                new StreamDeckButtonConfig(COColor.kRed.toString(), COColor.kBlack.toString(), "");
        StreamDeckButtonConfig blueConfig =
                new StreamDeckButtonConfig(COColor.kBlue.toString(), COColor.kWhite.toString(), "");
        StreamDeckButtonConfig yellowConfig =
                new StreamDeckButtonConfig(COColor.kYellow.toString(), COColor.kBlack.toString(), "");
        StreamDeckButtonConfig yellowOnBlackConfig =
                new StreamDeckButtonConfig(COColor.kBlack.toString(), COColor.kYellow.toString(), "");
        StreamDeckButtonConfig activeConfig =
                new StreamDeckButtonConfig(COColor.kGreen.toString(), COColor.kBlack.toString(), "");

        streamdeck.addButton(
                StreamDeckButtonType.CUSTOM,
                new StreamDeckButton(0, 0, "Shoot from Hub")
                        .withInactiveConfig(orangeConfig)
                        .withActiveConfig(activeConfig)
                        .withText("SHT\nHUB"),
                () -> shooterManager.getShooterOverride() == ShooterOverride.HUB,
                new StreamDeckCommand(
                        CommandType.ON_TRUE,
                        Commands.runOnce(() -> {
                                    ShooterOverride current = shooterManager.getShooterOverride();
                                    if (current == ShooterOverride.HUB) {
                                        shooterManager.setShooterOverride(ShooterOverride.NONE);
                                    } else {
                                        shooterManager.setShooterOverride(ShooterOverride.HUB);
                                    }
                                })
                                .ignoringDisable(true)));
        // 2027 below for comparison
        // streamdeck.addButton(0, 0, "Shoot from Hub")
        //         .withInactiveConfig(orangeConfig)
        //         .withActiveConfig(activeConfig)
        //         .withText("SHT\nHUB")
        //         .withActiveSupplier(() -> shooterManager.getShooterOverride() == ShooterOverride.HUB)
        // .onTrue(Commands.runOnce(() -> {
        //                 ShooterOverride current = shooterManager.getShooterOverride();
        //                 if (current == ShooterOverride.HUB) {
        //                 shooterManager.setShooterOverride(ShooterOverride.NONE);
        //                 } else {
        //                 shooterManager.setShooterOverride(ShooterOverride.HUB);
        //                 }
        //         }).ignoringDisable(true));

        streamdeck.addButton(
                StreamDeckButtonType.CUSTOM,
                new StreamDeckButton(0, 1, "Shoot from Tower")
                        .withInactiveConfig(orangeConfig)
                        .withActiveConfig(activeConfig)
                        .withText("SHT\nTWR"),
                () -> shooterManager.getShooterOverride() == ShooterOverride.TOWER,
                new StreamDeckCommand(
                        CommandType.ON_TRUE,
                        Commands.runOnce(() -> {
                                    ShooterOverride current = shooterManager.getShooterOverride();
                                    if (current == ShooterOverride.TOWER) {
                                        shooterManager.setShooterOverride(ShooterOverride.NONE);
                                    } else {
                                        shooterManager.setShooterOverride(ShooterOverride.TOWER);
                                    }
                                })
                                .ignoringDisable(true)));

        streamdeck.addButton(
                StreamDeckButtonType.CUSTOM,
                new StreamDeckButton(2, 7, "Shoot from Pit")
                        .withInactiveConfig(orangeConfig)
                        .withActiveConfig(activeConfig)
                        .withText("SHT\nPIT"),
                () -> shooterManager.getShooterOverride() == ShooterOverride.PIT,
                new StreamDeckCommand(
                        CommandType.ON_TRUE,
                        Commands.runOnce(() -> {
                                    ShooterOverride current = shooterManager.getShooterOverride();
                                    if (current == ShooterOverride.PIT) {
                                        shooterManager.setShooterOverride(ShooterOverride.NONE);
                                    } else {
                                        shooterManager.setShooterOverride(ShooterOverride.PIT);
                                    }
                                })
                                .ignoringDisable(true)));

        streamdeck.addButton(
                StreamDeckButtonType.CUSTOM,
                new StreamDeckButton(0, 2, "Shoot from Trench")
                        .withInactiveConfig(orangeConfig)
                        .withActiveConfig(activeConfig)
                        .withText("SHT\nTCH"),
                () -> shooterManager.getShooterOverride() == ShooterOverride.TRENCH,
                new StreamDeckCommand(
                        CommandType.ON_TRUE,
                        Commands.runOnce(() -> {
                                    ShooterOverride current = shooterManager.getShooterOverride();
                                    if (current == ShooterOverride.TRENCH) {
                                        shooterManager.setShooterOverride(ShooterOverride.NONE);
                                    } else {
                                        shooterManager.setShooterOverride(ShooterOverride.TRENCH);
                                    }
                                })
                                .ignoringDisable(true)));

        streamdeck.addButton(
                StreamDeckButtonType.CUSTOM,
                new StreamDeckButton(3, 4, "Box Stow")
                        .withInactiveConfig(orangeConfig)
                        .withActiveConfig(activeConfig)
                        .withText("BOX\nSTW"),
                () -> box.getBoxState() == BoxState.STOWED,
                new StreamDeckCommand(
                        CommandType.ON_TRUE,
                        Commands.runOnce(() -> {
                                    box.setBoxState(BoxState.STOWED);
                                })
                                .ignoringDisable(false)));
        streamdeck.addButton(
                StreamDeckButtonType.CUSTOM,
                new StreamDeckButton(2, 4, "Box Extend")
                        .withInactiveConfig(orangeConfig)
                        .withActiveConfig(activeConfig)
                        .withText("BOX\nEXT"),
                () -> box.getBoxState() == BoxState.EXTEND,
                new StreamDeckCommand(
                        CommandType.ON_TRUE,
                        Commands.runOnce(() -> {
                                    box.setBoxState(BoxState.EXTEND);
                                })
                                .ignoringDisable(true)));
        streamdeck.addButton(
                StreamDeckButtonType.CUSTOM,
                new StreamDeckButton(3, 7, "Box Climb")
                        .withInactiveConfig(orangeConfig)
                        .withActiveConfig(activeConfig)
                        .withText("BOX\nCLMB"),
                () -> box.getBoxState() == BoxState.CLIMB,
                new StreamDeckCommand(
                        CommandType.ON_TRUE,
                        Commands.runOnce(() -> {
                                    box.setBoxState(BoxState.CLIMB);
                                })
                                .ignoringDisable(true)));
        streamdeck.addButton(
                StreamDeckButtonType.CUSTOM,
                new StreamDeckButton(2, 5, "Box Zero")
                        .withInactiveConfig(redConfig)
                        .withActiveConfig(activeConfig)
                        .withText("BOX\nZRO"),
                () -> box.getBoxState() == BoxState.EXTEND,
                new StreamDeckCommand(CommandType.ON_TRUE, box.zeroBoxCommand().ignoringDisable(true)));

        streamdeck.addButton(
                StreamDeckButtonType.CUSTOM,
                new StreamDeckButton(0, 3, "Shoot from Corner")
                        .withInactiveConfig(orangeConfig)
                        .withActiveConfig(activeConfig)
                        .withText("SHT\nCNR"),
                () -> shooterManager.getShooterOverride() == ShooterOverride.CORNER,
                new StreamDeckCommand(
                        CommandType.ON_TRUE,
                        Commands.runOnce(() -> {
                                    ShooterOverride current = shooterManager.getShooterOverride();
                                    if (current == ShooterOverride.CORNER) {
                                        shooterManager.setShooterOverride(ShooterOverride.NONE);
                                    } else {
                                        shooterManager.setShooterOverride(ShooterOverride.CORNER);
                                    }
                                })
                                .ignoringDisable(true)));

        streamdeck.addButton(
                StreamDeckButtonType.CUSTOM,
                new StreamDeckButton(1, 3, "Reverse Feeder Pulse")
                        .withInactiveConfig(redConfig)
                        .withActiveConfig(activeConfig)
                        .withText("FdREV\nPulse"),
                () -> feederManager.getFeederState() == FeederState.REVERSE,
                new StreamDeckCommand(
                        CommandType.ON_TRUE,
                        Commands.sequence(
                                feederManager.revFeederShoot(),
                                hopperManager.reverseHopper(),
                                new WaitCommand(FeederConstants.revTime),
                                feederManager.stopFeederShoot(),
                                hopperManager.stopHopper())));

        streamdeck.addButton(
                StreamDeckButtonType.PRESS,
                new StreamDeckButton(0, 6, "Reset Vision 1")
                        .withInactiveConfig(tealConfig)
                        .withActiveConfig(activeConfig)
                        .withText("Viz"));
        streamdeck.addButton(
                StreamDeckButtonType.PRESS,
                new StreamDeckButton(1, 6, "Reset Vision 2")
                        .withInactiveConfig(tealConfig)
                        .withActiveConfig(activeConfig)
                        .withText("Viz"));
        streamdeck
                .button("Reset Vision 1")
                .and(streamdeck.button("Reset Vision 2"))
                .onTrue(Commands.runOnce(() -> {
                            RobotState.getVisionPose(VisionConstants.visionPoseThresholdSeconds)
                                    .ifPresent(drive::resetOdometry);
                        })
                        .ignoringDisable(true));

        streamdeck.addButton(
                StreamDeckButtonType.PRESS,
                new StreamDeckButton(0, 7, "Reset Gyro 1")
                        .withInactiveConfig(tealConfig)
                        .withActiveConfig(activeConfig)
                        .withText("Gyr"));
        streamdeck.addButton(
                StreamDeckButtonType.PRESS,
                new StreamDeckButton(1, 7, "Reset Gyro 2")
                        .withInactiveConfig(tealConfig)
                        .withActiveConfig(activeConfig)
                        .withText("Gyr"));
        streamdeck
                .button("Reset Gyro 1")
                .and(streamdeck.button("Reset Gyro 2"))
                .onTrue(Commands.runOnce(() -> drive.resetOdometry(
                                new Pose2d(RobotState.getGlobalPose().getTranslation(), Rotation2d.kZero)))
                        .ignoringDisable(true));
        streamdeck.addButton(
                StreamDeckButtonType.PRESS,
                new StreamDeckButton(1, 0, "Hopper fwd")
                        .withInactiveConfig(tealConfig)
                        .withActiveConfig(activeConfig)
                        .withText("Hop\nFWD"),
                new StreamDeckCommand(CommandType.WHILE_TRUE, hopper.hopperSTARTCommand()));
        streamdeck.addButton(
                StreamDeckButtonType.CUSTOM,
                new StreamDeckButton(1, 1, "Hopper stop")
                        .withInactiveConfig(tealConfig)
                        .withActiveConfig(activeConfig)
                        .withText("Hop\nSTOP"),
                () -> hopperManager.getHopperState() == HopperState.IDLE,
                new StreamDeckCommand(CommandType.ON_TRUE, hopperManager.stopHopper()));
        streamdeck.addButton(
                StreamDeckButtonType.PRESS,
                new StreamDeckButton(1, 2, "Hopper Rev")
                        .withInactiveConfig(tealConfig)
                        .withActiveConfig(activeConfig)
                        .withText("Hop\nREV"),
                new StreamDeckCommand(CommandType.WHILE_TRUE, hopper.hopperREVCommand()));

        streamdeck.addButton(
                StreamDeckButtonType.PRESS,
                new StreamDeckButton(3, 0, "Intake UP")
                        .withInactiveConfig(blueConfig)
                        .withActiveConfig(activeConfig)
                        .withText("Int\nUP"),
                new StreamDeckCommand(CommandType.WHILE_TRUE, intakePivot.manualStowCommand()));
        streamdeck.addButton(
                StreamDeckButtonType.CUSTOM,
                new StreamDeckButton(3, 1, "Intake Down")
                        .withInactiveConfig(blueConfig)
                        .withActiveConfig(activeConfig)
                        .withText("Int\nDWN"),
                () -> intakeManager.getIntakeState() == IntakeState.DOWNSTOPPED,
                new StreamDeckCommand(CommandType.ON_TRUE, Commands.runOnce(() -> {
                    intakeManager.setIntakeState(IntakeState.DOWNSTOPPED);
                })));
        streamdeck.addButton(
                StreamDeckButtonType.PRESS,
                new StreamDeckButton(3, 2, "Intake Roller FWD")
                        .withInactiveConfig(blueConfig)
                        .withActiveConfig(activeConfig)
                        .withText("Int\nFWD"),
                new StreamDeckCommand(CommandType.WHILE_TRUE, intakeRoller.intakeREVCommand()));

        streamdeck.addButton(
                StreamDeckButtonType.PRESS,
                new StreamDeckButton(3, 3, "Intake Roller REV")
                        .withInactiveConfig(blueConfig)
                        .withActiveConfig(activeConfig)
                        .withText("Int\nREV"),
                new StreamDeckCommand(CommandType.WHILE_TRUE, intakeRoller.intakeREVCommand()));
        streamdeck.addButton(
                StreamDeckButtonType.PRESS,
                new StreamDeckButton(2, 6, "Hood Zero")
                        .withInactiveConfig(redConfig)
                        .withActiveConfig(activeConfig)
                        .withText("HOOD\nZRO"),
                new StreamDeckCommand(
                        CommandType.ON_TRUE, hood.zeroHoodCommand().ignoringDisable(true)));

        streamdeck.addButton(
                StreamDeckButtonType.CUSTOM,
                new StreamDeckButton(2, 3, "Override Box Extend")
                        .withInactiveConfig(orangeConfig)
                        .withActiveConfig(activeConfig)
                        .withText("BOX\nOVR"),
                () -> box.getBoxOverride() == BoxState.EXTEND,
                new StreamDeckCommand(CommandType.ON_TRUE, Commands.runOnce(() -> {
                    if (box.getBoxOverride() == BoxState.EXTEND) {
                        box.setBoxOverride(BoxState.NONE);
                    } else {
                        box.setBoxOverride(BoxState.EXTEND);
                    }
                })));

        streamdeck.addButton(
                StreamDeckButtonType.CUSTOM,
                new StreamDeckButton(2, 2, "DisableSpinUp")
                        .withInactiveConfig(blackConfig)
                        .withActiveConfig(activeConfig)
                        .withText("NO\nSPIN"),
                () -> RobotState.spinUpDisabled,
                new StreamDeckCommand(CommandType.ON_TRUE, Commands.runOnce(() -> RobotState.toggleSpinUpDisabled())));

        streamdeck.addButton(
                StreamDeckButtonType.PRESS,
                new StreamDeckButton(0, 5, "X Lock")
                        .withInactiveConfig(tealConfig)
                        .withActiveConfig(activeConfig)
                        .withText("XLK"),
                new StreamDeckCommand(CommandType.WHILE_TRUE, drive.XStop().withName("XLock")));

        streamdeck.addButton(
                StreamDeckButtonType.CUSTOM,
                new StreamDeckButton(3, 5, "We Win Auto")
                        .withInactiveConfig(blackConfig)
                        .withActiveConfig(tealConfig)
                        .withText("W"),
                () -> HubStateTracker.whoWonAuto() == AutoResult.WIN,
                new StreamDeckCommand(
                        CommandType.ON_TRUE,
                        Commands.runOnce(() -> {
                                    HubStateTracker.setAutoWinner(AutoResult.WIN);
                                })
                                .ignoringDisable(true)));
        streamdeck.addButton(
                StreamDeckButtonType.CUSTOM,
                new StreamDeckButton(3, 6, "We Lose Auto")
                        .withInactiveConfig(blackConfig)
                        .withActiveConfig(tealConfig)
                        .withText("L"),
                () -> HubStateTracker.whoWonAuto() == AutoResult.LOSE,
                new StreamDeckCommand(
                        CommandType.ON_TRUE,
                        Commands.runOnce(() -> {
                                    HubStateTracker.setAutoWinner(AutoResult.LOSE);
                                })
                                .ignoringDisable(true)));

        streamdeck.addButton(
                StreamDeckButtonType.PRESS,
                new StreamDeckButton(2, 0, "Left Climb On Held")
                        .withInactiveConfig(tealConfig)
                        .withActiveConfig(activeConfig)
                        .withText("LCLB\nALGN"),
                new StreamDeckCommand(
                        CommandType.WHILE_TRUE, box.setExtend().andThen(drive.autoClimb(ClimbSide.LEFT))));
        streamdeck.addButton(
                StreamDeckButtonType.PRESS,
                new StreamDeckButton(2, 1, "Right Climb On Held")
                        .withInactiveConfig(tealConfig)
                        .withActiveConfig(activeConfig)
                        .withText("RCLB\nALGN"),
                new StreamDeckCommand(
                        CommandType.WHILE_TRUE, box.setExtend().andThen(drive.autoClimb(ClimbSide.RIGHT))));
    }
}
