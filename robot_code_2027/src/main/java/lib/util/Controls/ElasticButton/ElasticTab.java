package frc.lib.util.Controls.ElasticButton;

import edu.wpi.first.networktables.NetworkTable;
import java.util.LinkedHashSet;
import java.util.Set;
import edu.wpi.first.networktables.NetworkTableEntry;
import org.wpilib.command3.Command;
import org.wpilib.command3.Trigger;

public class ElasticTab {
    private Set<String> buttons = new LinkedHashSet<>();
    private NetworkTable table;
    private String tabKey;

    public ElasticTab(ElasticDashboard parent, String key) {
        tabKey = key;
        table = parent.getTable().getSubTable(tabKey);
    }
    public Trigger addPressButton(String key) {
        NetworkTableEntry entry = table.getEntry(key);
        entry.setBoolean(false);
        buttons.add(key);
        return new Trigger(() -> entry.getBoolean(false))
            .onTrue(
                Command.noRequirements(coroutine -> entry.setBoolean(false))
                .withName("ElasticButtonReset_"+tabKey+"-"+key)
            );
    }

    public Trigger addHoldButton(String key) {
        NetworkTableEntry entry = table.getEntry(key);
        entry.setBoolean(false);
        buttons.add(key);
        return new Trigger(() -> entry.getBoolean(false));
    }

    public Set<String> getButtons() {
        return buttons;
    }
}