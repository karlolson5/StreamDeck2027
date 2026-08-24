package frc.lib.util.Controls.ElasticButton;

import edu.wpi.first.networktables.NetworkTable;
import java.util.HashSet;
import java.util.Set;
import edu.wpi.first.wpilibj2.command.button.Trigger;

public class ElasticTab {
    private Set<String> buttons = new HashSet<>();
    private NetworkTable table;

    public ElasticTab(ElasticDashboard parent, String key) {
        table = parent.getTable().getSubTable(key);
    }

    public Trigger addButton(String key) {
        entry = table.getEntry(key);
        entry.setBoolean(false);
        buttons.add(key);
        return new Trigger(() -> entry.getBoolean(defaultValue));
    }

    public Set<String> getButtons() {
        return buttons;
    }
}