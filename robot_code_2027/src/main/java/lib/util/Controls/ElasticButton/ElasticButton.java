package frc.lib.util.Controls.ElasticButton;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.button.Trigger;

public class ElasticButton {

    private String key;
    private boolean defaultValue;
    private Trigger trigger;
    private NetworkTableEntry entry;

    public ElasticButton(ElasticTab parent, String key, boolean defaultValue) {
        this.key = key;
        this.defaultValue = defaultValue;
        entry = parent.getTable().getEntry(key);
        entry.setBoolean(defaultValue);
        trigger = new Trigger(() -> entry.getBoolean(defaultValue));
    }

    public String getKey() {
        return key;
    }
    public Trigger getTrigger() {
        return trigger;
    }
}