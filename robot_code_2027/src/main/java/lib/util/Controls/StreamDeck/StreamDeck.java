package frc.lib.util.Controls.StreamDeck;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BooleanSupplier;

public class StreamDeck extends VirtualSubsystem {
    private Map<StreamDeckButton, ButtonRecord> buttonMap = new HashMap<>();

    @Override
    public void periodic() {
        buttonMap.values().forEach(buttonRecord -> {
            button.update();
        });
    }

    private void verifyAddedButton(StreamDeckButton button) {
        if (map.keySet().stream().anyMatch(
                existingButton -> Objects.equals(button.getKey(), existingButton.getKey())
            )) {
            throw new IllegalArgumentException("Button with that key has already been added");
        }
        if (map.keySet().stream().anyMatch(
                existingButton -> Objects.equals(button.getIndex(), existingButton.getIndex())
            )) {
            throw new IllegalArgumentException("Button at that location (Index) has already been added");
        }
        return;
    }

    public StreamDeckButton addButton(StreamDeckButton button) {
        verifyAddedButton(button);
        buttonMap.put(button, new ButtonRecord(pressed, pressed::getAsBoolean, activePublisher));
        return button;
    }

    public StreamDeckButton addButton(StreamDeckButton button, BooleanSupplier activeSupplier) {
        verifyAddedButton(button);
        buttonMap.put(button, new ButtonRecord(pressed, activeSupplier, activePublisher));
        return button;
    }

    public StreamDeckButton button(String key) {
        for (StreamDeckButton button : buttonMap.keySet()) {
            if (button.getKey().equals(key)) {
                return button;
            }
        }
        throw new IllegalArgumentException("No button added for key " + key);
    }
}
