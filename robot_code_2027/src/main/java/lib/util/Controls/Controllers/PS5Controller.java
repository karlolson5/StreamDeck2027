package frc.lib.util.Controls;
import org.wpilib.command3.button.CommandGamepad;
import org.wpilib.command3.Scheduler;
import org.wpilib.command3.Trigger;
import org.wpilib.command3.Command;
import org.wpilib.event.EventLoop;
import org.wpilib.driverstation.GenericHID.RumbleType;
import org.wpilib.driverstation.POVDirection;
import org.wpilib.driverstation.Gamepad;

public class PS5Controller extends PS4Controller {
    
    public PS5Controller(Scheduler scheduler, int port) {
        super(scheduler, port);
    }
    public PS5Controller(int port) {
        super(port);
    }
    
    public Trigger create() {
        return share();
    }
    public Trigger create(EventLoop loop) {
        return share(loop);
    }
}




