package frc.lib.util.Controls.StreamDeck;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BooleanSupplier;
import java.util.Objects;
import frc.lib.util.VirtualSubsystem;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;

public class StreamDeck extends VirtualSubsystem {
    static final NetworkTable deckTable = NetworkTableInstance.getDefault().getTable("StreamDeck");

    private Set<StreamDeckButton> buttonSet = new HashSet<>();

    @Override
    public void periodic() {
        buttonSet.forEach(button -> button.update());
    }

    private void verifyAddedButton(StreamDeckButton button) {
        if (buttonSet.stream().anyMatch(
                existingButton -> Objects.equals(button.getKey(), existingButton.getKey())
            )) {
            throw new IllegalArgumentException("Button with that key has already been added");
        }
        if (buttonSet.stream().anyMatch(
                existingButton -> Objects.equals(button.getIndex(), existingButton.getIndex())
            )) {
            throw new IllegalArgumentException("Button at that location (Index) has already been added");
        }
        return;
    }

    public StreamDeckButton addButton(StreamDeckButton button) {
        try {
            verifyAddedButton(button);
        } catch (IllegalArgumentException e) {
            button.closePublishers();
            throw e;
        }
        buttonSet.add(button);
        button.publishConfig();
        return button;
    }

    public StreamDeckButton button(String key) {
        for (StreamDeckButton button : buttonSet) {
            if (button.getKey().equals(key)) {
                return button;
            }
        }
        throw new IllegalArgumentException("No button added for key " + key);
    }
}
