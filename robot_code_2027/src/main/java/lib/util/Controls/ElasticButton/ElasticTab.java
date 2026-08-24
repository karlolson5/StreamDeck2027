package frc.lib.util.Controls.ElasticButton;

import edu.wpi.first.networktables.NetworkTable;
import java.util.LinkedHashMap;
import java.util.Map;
import edu.wpi.first.wpilibj2.command.button.Trigger;

public class ElasticTab {
    private Map<String, ElasticButton> buttons = new LinkedHashMap<>();
    private NetworkTable table;

    public ElasticTab(ElasticDashboard parent, String key) {
        table = parent.getTable().getSubTable(key);
    }

    public Trigger addButton(String key) {
        ElasticButton button = new ElasticButton(this, key, false);
        buttons.put(key, button);
        return button;
    }

    public NetworkTable getTable() {
        return table;
    }

    public Map<String, ElasticButton> getButtons() {
        return buttons;
    }
}