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
                new StreamDeckButtonConfig(COColor.kCOOrangePure, COColor.kBlack);
        StreamDeckButtonConfig tealConfig =
                new StreamDeckButtonConfig(COColor.kCOTealPure, COColor.kWhite);
        StreamDeckButtonConfig blueConfig =
                new StreamDeckButtonConfig(COColor.kBlue, COColor.kWhite);
        StreamDeckButtonConfig activeConfig =
                new StreamDeckButtonConfig(COColor.kGreen, COColor.kBlack);

        // Use button directly as a trigger, OR
        streamdeck.addButton(0, 0, "Shoot from Hub")
                .withInactiveConfig(orangeConfig)
                .withActiveConfig(activeConfig)
                .withText("SHT\nHUB")
                .withActiveSupplier(() -> shooterManager.getShooterOverride() == ShooterOverride.HUB)
        .onTrue(Commands.runOnce(() -> {
                        ShooterOverride current = shooterManager.getShooterOverride();
                        if (current == ShooterOverride.HUB) {
                        shooterManager.setShooterOverride(ShooterOverride.NONE);
                        } else {
                        shooterManager.setShooterOverride(ShooterOverride.HUB);
                        }
                }).ignoringDisable(true));

        // Reference button as trigger through `streamdeck`, OR
        streamdeck.addButton(0, 1, "Shoot from Tower")
                .withInactiveConfig(orangeConfig)
                .withActiveConfig(activeConfig)
                .withText("SHT\nTWR")
                .withActiveSupplier(() -> shooterManager.getShooterOverride() == ShooterOverride.TOWER);

        streamdeck.button("Shoot from Tower")
                .onTrue(Commands.runOnce(() -> {
                        ShooterOverride current = shooterManager.getShooterOverride();
                        if (current == ShooterOverride.TOWER) {
                        shooterManager.setShooterOverride(ShooterOverride.NONE);
                        } else {
                        shooterManager.setShooterOverride(ShooterOverride.TOWER);
                        }
                }).ignoringDisable(true));

        // use StreamDeckButton object as a Trigger (it is one)
        StreamDeckButton shootFromTrenchButton = streamdeck.addButton(0, 2, "Shoot from Trench")
                .withInactiveConfig(orangeConfig)
                .withActiveConfig(activeConfig)
                .withText("SHT\nTCH")
                .withActiveSupplier(() -> shooterManager.getShooterOverride() == ShooterOverride.TRENCH);

        shootFromTrenchButton.onTrue(Commands.runOnce(() -> {
                        ShooterOverride current = shooterManager.getShooterOverride();
                        if (current == ShooterOverride.TRENCH) {
                        shooterManager.setShooterOverride(ShooterOverride.NONE);
                        } else {
                        shooterManager.setShooterOverride(ShooterOverride.TRENCH);
                        }
                }).ignoringDisable(true));

        // Stream Deck buttons are just normal triggers, so chain them like any other trigger
        streamdeck.addButton(0, 7, "Reset Gyro 1")
                .withInactiveConfig(tealConfig)
                .withActiveConfig(activeConfig)
                .withText("Gyr")
        .and(
                streamdeck.addButton(1, 7, "Reset Gyro 2")
                        .withInactiveConfig(tealConfig)
                        .withActiveConfig(activeConfig)
                        .withText("Gyr")
        ).onTrue(Commands.runOnce(() -> drive.resetOdometry(
                new Pose2d(RobotState.getGlobalPose().getTranslation(), Rotation2d.kZero)))
        .ignoringDisable(true));

        streamdeck.addButton(3, 2, "Intake Roller FWD")
                .withInactiveConfig(blueConfig)
                .withActiveConfig(activeConfig)
                .withText("Int\nFWD")
        .whileTrue(intakeRoller.intakeREVCommand());
    }
}
