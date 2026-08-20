package frc.lib.util.Controls.StreamDeck;

import edu.wpi.first.math.Pair;
import edu.wpi.first.networktables.BooleanPublisher;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import org.littletonrobotics.junction.Logger;
import org.littletonrobotics.junction.networktables.LoggedNetworkBoolean;

public class StreamDeck extends VirtualSubsystem {
    private Map<StreamDeckButton, ButtonRecord> buttonMap = new HashMap<>();
    private NetworkTable deckTable;

    public StreamDeck() {
        super();
        NetworkTable deckTable = NetworkTableInstance.getDefault().getTable("StreamDeck");
    }

    private static class ButtonRecord{
        private LoggedNetworkBoolean pressed;
        private Supplier<Boolean> activeSupplier;
        private BooleanPublisher activePublisher;

        ButtonRecord(LoggedNetworkBoolean pressed, Supplier<Boolean> activeSupplier, BooleanPublisher activePub) {
            this.pressed = pressed;
            this.activeSupplier = activeSupplier;
            this.activePublisher = activePublisher;
        }
    }

    @Override
    public void periodic() {
        buttonMap.values().forEach(buttonRecord -> {
            button.activePublisher.set(button.activeSupplier.getAsBoolean());
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

    public Trigger addButton(StreamDeckButton button) {
        verifyAddedButton(button);
        LoggedNetworkBoolean pressed = new LoggedNetworkBoolean("StreamDeck/" + button.getKey());
        NetworkTable buttonTable = deckTable.getSubTable("Button/" + button.getIndex())
        BooleanPublisher activePublisher = buttonTable.getBooleanTopic(button.getPressedNetworkKey()).publish();
        buttonMap.put(button, new ButtonRecord(pressed, pressed::getAsBoolean, activePublisher));
        buttonTable.getStringTopic(button.getConfigNetworkKey()).publish().set(button.getConfigString());
        return new Trigger(buttonMap.get(button).pressed::get);
    }

    public Trigger addButton(StreamDeckButton button, BooleanSupplier activeSupplier) {
        verifyAddedButton(button);
        LoggedNetworkBoolean pressed = new LoggedNetworkBoolean("StreamDeck/" + button.getKey());
        NetworkTable buttonTable = deckTable.getSubTable("Button/" + button.getIndex())
        BooleanPublisher activePublisher = buttonTable.getBooleanTopic(button.getPressedNetworkKey()).publish();
        buttonMap.put(button, new ButtonRecord(pressed, activeSupplier, activePublisher));
        buttonTable.getStringTopic(button.getConfigNetworkKey()).publish().set(button.getConfigString());
        return new Trigger(buttonMap.get(button).pressed::get);
    }

    public Trigger button(String key) {
        for (StreamDeckButton button : buttonMap.keySet()) {
            if (button.getKey().equals(key)) {
                return this.button(button);
            }
        }
        throw new IllegalArgumentException("Stream Deck button trigger added for invalid button key " + key);
    }
}
