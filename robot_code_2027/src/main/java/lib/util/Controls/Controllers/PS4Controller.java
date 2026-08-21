package frc.lib.util.Controls;
import org.wpilib.command3.button.CommandGamepad;
import org.wpilib.command3.Scheduler;

public class PS4Controller {
    private final CommandGamepad gamepad;
    
    public PS4Controller(Scheduler scheduler, int port) {
        super(scheduler, port);
        this.gamepad = new CommandGamepad(scheduler, port);
    }
    public PS4Controller(int port) {
        super(port);
        this.gamepad = new CommandGamepad(port);
    }

    // Renaming face buttons
    public Trigger cross() {
        return gamepad.southFace();
    }
    public Trigger cross(EventLoop loop) {
        return gamepad.southFace(loop);
    }
    public Trigger x() {
        return cross();
    }
    public Trigger x(EventLoop loop) {
        return cross(loop);
    }
    public Trigger circle() {
        return gamepad.eastFace();
    }
    public Trigger circle(EventLoop loop) {
        return gamepad.eastFace(loop);
    }
    public Trigger o() {
        return circle();
    }
    public Trigger o(EventLoop loop) {
        return circle(loop);
    }
    public Trigger square() {
        return gamepad.westFace();
    }
    public Trigger square(EventLoop loop) {
        return gamepad.westFace(loop);
    }
    public Trigger triangle() {
        return gamepad.northFace();
    }
    public Trigger triangle(EventLoop loop) {
        return gamepad.northFace(loop);
    }

    // Directly from CommandGamepad, exist on PS Controller
    public Trigger options() {
        return gamepad.start();
    }
    public Trigger options(EventLoop loop) {
        return gamepad.start(loop);
    }
    public Trigger share() {
        return gamepad.back();
    }
    public Trigger share(EventLoop loop) {
        return gamepad.back(loop);
    }
    public Trigger ps() {
        return gamepad.guide();
    }
    public Trigger ps(EventLoop loop) {
        return gamepad.guide(loop);
    }
    public Trigger L1() {
        return gamepad.leftBumper();
    }
    public Trigger L1(EventLoop loop) {
        return gamepad.leftBumper(loop);
    }
    public Trigger R1() {
        return gamepad.rightBumper();
    }
    public Trigger R1(EventLoop loop) {
        return gamepad.rightBumper(loop);
    }
    public Trigger L2() {
        return gamepad.leftTrigger();
    }
    public Trigger L2(double threshold) {
        return gamepad.leftTrigger(threshold);
    }
    public Trigger L2(double threshold, EventLoop loop) {
        return gamepad.leftTrigger(threshold, loop);
    }
    public Trigger R2() {
        return gamepad.rightTrigger();
    }
    public Trigger R2(double threshold) {
        return gamepad.rightTrigger(threshold);
    }
    public Trigger R2(double threshold, EventLoop loop) {
        return gamepad.rightTrigger(threshold, loop);
    }
    public Trigger L3() {
        return gamepad.leftStick();
    }
    public Trigger L3(EventLoop loop) {
        return gamepad.leftStick(loop);
    }
    public Trigger R3() {
        return gamepad.rightStick();
    }
    public Trigger R3(EventLoop loop) {
        return gamepad.rightStick(loop);
    }
    public Trigger dpadDown() {
        return gamepad.dpadDown();
    }
    public Trigger dpadDown(EventLoop loop) {
        return gamepad.dpadDown(loop);
    }
    public Trigger dpadLeft() {
        return gamepad.dpadLeft();
    }
    public Trigger dpadLeft(EventLoop loop) {
        return gamepad.dpadLeft(loop);
    }
    public Trigger dpadRight() {
        return gamepad.dpadRight();
    }
    public Trigger dpadRight(EventLoop loop) {
        return gamepad.dpadRight(loop);
    }
    public Trigger dpadUp() {
        return gamepad.dpadUp();
    }
    public Trigger dpadUp(EventLoop loop) {
        return gamepad.dpadUp(loop);
    }
    public Trigger touchpad() {
        return gamepad.touchpad();
    }
    public Trigger touchpad(EventLoop loop) {
        return gamepad.touchpad(loop);
    }

    // Directly from CommandGenericHID, exist on PS Controller
    public Trigger axisGreaterThan(int axis, double threshold) {
        return gamepad.axisGreaterThan(axis, threshold);
    }
    public Trigger axisGreaterThan(int axis, double threshold, EventLoop loop) {
        return gamepad.axisGreaterThan(axis, threshold, loop);
    }
    public Trigger axisLessThan(int axis, double threshold) {
        return gamepad.axisLessThan(axis, threshold);
    }
    public Trigger axisLessThan(int axis, double threshold, EventLoop loop) {
        return gamepad.axisLessThan(axis, threshold, loop);
    }
    public Trigger axisMagnitudeGreaterThan(int axis, double threshold) {
        return gamepad.axisMagnitudeGreaterThan(axis, threshold);
    }
    public Trigger axisMagnitudeGreaterThan(int axis, double threshold, EventLoop loop) {
        return gamepad.axisMagnitudeGreaterThan(axis, threshold, loop);
    }
    public Trigger button(int button) {
        return gamepad.button(button);
    }
    public Trigger button(int button, EventLoop loop) {
        return gamepad.button(button, loop);
    }
    public GenericHID getHID() {
        return gamepad.getHID();
    }
    public double getRawAxis(int axis) {
        return gamepad.getRawAxis(axis);
    }
    public boolean isConnected() {
        return gamepad.isConnected();
    }
    public Trigger pov(int pov, POVDirection angle, EventLoop loop) {
        return gamepad.pov(pov, angle, loop);
    }
    public Trigger pov(POVDirection angle) {
        return gamepad.pov(angle);
    }
    public Trigger povCenter() {
        return gamepad.povCenter();
    }
    public Trigger povDown() {
        return gamepad.povDown();
    }
    public Trigger povDownLeft() {
        return gamepad.povDownLeft();
    }
    public Trigger povDownRight() {
        return gamepad.povDownRight();
    }
    public Trigger povLeft() {
        return gamepad.povLeft();
    }
    public Trigger povRight() {
        return gamepad.povRight();
    }
    public Trigger povUp() {
        return gamepad.povUp();
    }
    public Trigger povUpLeft() {
        return gamepad.povUpLeft();
    }
    public Trigger povUpRight() {
        return gamepad.povUpRight();
    }
    public void setRumble(RumbleType type, double value) {
        gamepad.setRumble(type, value);
    }

    // rumble command
    public Command rumbleCommand(RumbleType rumbleType, double intensity, double duration) {
        return Command.noRequirements(coroutine -> {
            setRumble(rumbleType, intensity);
            coroutine.waitUntilElapsed(duration);
            setRumble(rumbleType, 0.0);
        }).withName("Controller"+getPort()+"_Rumble");
    }
}




