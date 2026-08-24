package frc.lib.util.Controls.ElasticButton;

import edu.wpi.first.networktables.NetworkTable;
import java.util.LinkedHashSet;
import java.util.Set;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj2.command.Commands;

public class ElasticTab {
    private Set<String> buttons = new LinkedHashSet<>();
    private NetworkTable table;

    public ElasticTab(ElasticDashboard parent, String key) {
        table = parent.getTable().getSubTable(key);
    }
    public Trigger addPressButton(String key) {
        NetworkTableEntry entry = table.getEntry(key);
        entry.setBoolean(false);
        buttons.add(key);
        return new Trigger(() -> entry.getBoolean(false))
            .onTrue(Commands.runOnce(() -> entry.setBoolean(false)));
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