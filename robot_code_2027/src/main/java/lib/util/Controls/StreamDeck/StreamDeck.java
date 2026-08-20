package frc.lib.util.Controls.StreamDeck;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BooleanSupplier;
import java.util.Objects;
import frc.lib.util.VirtualSubsystem;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;

public class StreamDeck extends VirtualSubsystem {
    // Supports ONLY StreamDeckXL
    public static final int rowCount = 4;
    public static final int colCount = 8;
    public static final int buttonCount = rowCount * colCount;

    static final NetworkTable deckTable = NetworkTableInstance.getDefault().getTable("StreamDeck");

    private Set<StreamDeckButton> buttonSet = new HashSet<>();

    @Override
    public void periodic() {
        buttonSet.forEach(button -> button.update());
    }

    private void verifyAddedButton(int index, String key) {
        if (buttonSet.stream().anyMatch(
                existingButton -> Objects.equals(key, existingButton.getKey())
            )) {
            throw new IllegalArgumentException("Button with that key has already been added");
        }
        if (buttonSet.stream().anyMatch(
                existingButton -> Objects.equals(index, existingButton.getIndex())
            )) {
            throw new IllegalArgumentException("Button at that location (Index) has already been added");
        }
        return;
    }

    public static int calculate_index(int row, int col) {
        validate(row, rowCount, "row");
        validate(col, colCount, "col");
        return validate(row * colCount + col % colCount, buttonCount, "index");
    }

    private static int validate(int index, int max, String name) {
        if (index >= max) {
            throw new IllegalArgumentException("StreamDeck " + name + " " + index + " out of bounds, must be >= 0, <" + max);
        }
        return index;
    }

    public StreamDeckButton addButton(int row, int col, String key) {
        int index = calculate_index(row, col);
        verifyAddedButton(index, key);
        button = new StreamDeckButton(index, key);
        buttonSet.add(button);
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
