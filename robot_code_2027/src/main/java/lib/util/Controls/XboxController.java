package frc.lib.util.Controls;
import org.wpilib.command3.button.CommandGamepad;
import org.wpilib.command3.Scheduler;

public class XboxController extends CommandGenericHID {
    private final CommandGamepad gamepad;
    
    public XboxController(Scheduler scheduler, int port) {
        super(scheduler, port);
        this.gamepad = new CommandGamepad(scheduler, port);
    }
    public XboxController(int port) {
        super(port);
        this.gamepad = new CommandGamepad(port);
    }

    public Trigger a() {
        return gamepad.southFace();
    }
    public Trigger a(EventLoop loop) {
        return gamepad.southFace(loop);
    }
    public Trigger b() {
        return gamepad.eastFace();
    }
    public Trigger b(EventLoop loop) {
        return gamepad.eastFace(loop);
    }
    public Trigger x() {
        return gamepad.westFace();
    }
    public Trigger x(EventLoop loop) {
        return gamepad.westFace(loop);
    }
    public Trigger y() {
        return gamepad.northFace();
    }
    public Trigger y(EventLoop loop) {
        return gamepad.northFace(loop);
    }

    // Directly from CommandGamepad, exist on Xbox Controller
    public Trigger start() {
        return gamepad.start();
    }
    public Trigger start(EventLoop loop) {
        return gamepad.start(loop);
    }
    public Trigger back() {
        return gamepad.back();
    }
    public Trigger back(EventLoop loop) {
        return gamepad.back(loop);
    }
    public Trigger leftBumper() {
        return gamepad.leftBumper();
    }
    public Trigger leftBumper(EventLoop loop) {
        return gamepad.leftBumper(loop);
    }
    public Trigger rightBumper() {
        return gamepad.rightBumper();
    }
    public Trigger rightBumper(EventLoop loop) {
        return gamepad.rightBumper(loop);
    }
    public Trigger leftTrigger() {
        return gamepad.leftTrigger();
    }
    public Trigger leftTrigger(double threshold) {
        return gamepad.leftTrigger(threshold);
    }
    public Trigger leftTrigger(double threshold, EventLoop loop) {
        return gamepad.leftTrigger(threshold, loop);
    }
    public Trigger rightTrigger() {
        return gamepad.rightTrigger();
    }
    public Trigger rightTrigger(double threshold) {
        return gamepad.rightTrigger(threshold);
    }
    public Trigger rightTrigger(double threshold, EventLoop loop) {
        return gamepad.rightTrigger(threshold, loop);
    }
    public Trigger leftStick() {
        return gamepad.leftStick();
    }
    public Trigger leftStick(EventLoop loop) {
        return gamepad.leftStick(loop);
    }
    public Trigger rightStick() {
        return gamepad.rightStick();
    }
    public Trigger rightStick(EventLoop loop) {
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

    public Command rumbleCommand(RumbleType rumbleType, double intensity, double duration) {
        return Command.noRequirements(coroutine -> {
            this.setRumble(rumbleType, intensity);
            coroutine.waitUntilElapsed(duration);
            this.setRumble(rumbleType, 0.0);
        }).withName("Controller"+getPort()+"Rumble");
    }
}




