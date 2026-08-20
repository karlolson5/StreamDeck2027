package frc.lib.util.Controls.StreamDeck;

import java.util.ArrayList;
import java.util.List;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import java.util.function.BooleanSupplier;
import edu.wpi.first.networktables.BooleanPublisher;
import edu.wpi.first.networktables.BooleanSubscriber;
import edu.wpi.first.networktables.StringPublisher;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;

public class StreamDeckButton extends Trigger {

    private final int index;
    private final String key;
    private String active_background = "#000000";
    private String inactive_background = "#000000";
    private String active_foreground = "#FFFFFF";
    private String inactive_foreground = "#FFFFFF";
    private String active_text = "";
    private String inactive_text = "";
    private boolean active_set = false;
    private boolean inactive_set = false;
    private BooleanSubscriber pressedSubscriber;
    private BooleanPublisher activePublisher;
    private StringPublisher configPublisher;
    private BooleanSupplier activeSupplier;
    private NetworkTable table;

    private static final BooleanSubscriber connectedSubscriber = StreamDeck.deckTable.getBooleanTopic("Connected").subscribe(false);

    private StreamDeckButton(int index, String key) {
        if (index >= StreamDeck.buttonCount) {
            throw new IllegalArgumentException("StreamDeckButton index " + index + " out of bounds, must be <=" + StreamDeck.buttonCount);
        }
        this(index, key, NetworkTableInstance.getDefault()
            .getBooleanTopic("StreamDeck/" + key)
            .subscribe(false, PubSubOption.sendAll(true), PubSubOption.pollStorage(20)));
    }

    private StreamDeckButton(int index, String key, BooleanSubscriber pressedSubscriber) {
        super(connectedAndPressed(pressedSubscriber));
        this.pressedSubscriber = pressedSubscriber;
        this.activeSupplier = pressedSubscriber::get;
        this.index = index;
        this.key = key;
        this.table = StreamDeck.deckTable.getSubTable("Button/" + index);
        this.configPublisher = table.getStringTopic("Appearance").publish();
        this.activePublisher = table.getBooleanTopic("Active").publish();
    }

    private static BooleanSupplier connectedAndPressed(BooleanSubscriber pressedSubscriber) {
        return () -> {
            boolean sawPress = false;
            for (boolean pressed : pressedSubscriber.readQueueValues()) {
                if (pressed) {
                    sawPress = true;
                    break;
                }
            }

            // Needed for holding to work, since readQueueValues will no longer be true
            boolean currentlyHeld = pressedSubscriber.get();

            return connectedSubscriber.get() && (sawPress || currentlyHeld);
        };
    }

    public StreamDeckButton(int row, int col, String key) {
        if (col >= StreamDeck.buttonCount) {
            throw new IllegalArgumentException("StreamDeckButton index " + col + " out of bounds, must be <=" + StreamDeck.colCount);
        }
        if (row >= StreamDeck.rowCount) {
            throw new IllegalArgumentException("StreamDeckButton row " + row + " out of bounds, must be <=" + StreamDeck.rowCount);
        }
        this(calculate_index(row, col), key);
    }

    public void update() {
        activePublisher.set(activeSupplier.getAsBoolean());
    }

    private static int calculate_index(int row, int col) {
        return row * 8 + col % 8;
    }

    public void publishConfig() {
        configPublisher.set(getConfigString());
    }

    public StreamDeckButton withActiveSupplier(BooleanSupplier activeSupplier) {
        this.activeSupplier = activeSupplier;
        return this;
    }

    private void setInactiveIfUnconfigured() {
        if (!this.inactive_set) {
            this.inactive_background = this.active_background;
            this.inactive_foreground = this.active_foreground;
            this.inactive_text = this.active_text;
        }
    }

    private void setActiveIfUnconfigured() {
        if (!this.active_set) {
            this.active_background = this.inactive_background;
            this.active_foreground = this.inactive_foreground;
            this.active_text = this.inactive_text;
        }
    }

    public StreamDeckButton withActiveConfig(String active_background, String active_foreground, String active_text) {
        this.active_background = active_background;
        this.active_foreground = active_foreground;
        this.active_text = active_text;
        this.active_set = true;
        setInactiveIfUnconfigured();
        return this;
    }

    public StreamDeckButton withActiveConfig(StreamDeckButtonConfig streamDeckText) {
        this.active_background = streamDeckText.getBackground();
        this.active_foreground = streamDeckText.getForeground();
        this.active_text = streamDeckText.getText();
        this.active_set = true;
        setInactiveIfUnconfigured();
        return this;
    }

    public StreamDeckButton withInactiveConfig(
            String inactive_background, String inactive_foreground, String inactive_text) {
        this.inactive_background = inactive_background;
        this.inactive_foreground = inactive_foreground;
        this.inactive_text = inactive_text;
        this.inactive_set = true;
        setActiveIfUnconfigured();
        return this;
    }

    public StreamDeckButton withInactiveConfig(StreamDeckButtonConfig streamDeckText) {
        this.inactive_background = streamDeckText.getBackground();
        this.inactive_foreground = streamDeckText.getForeground();
        this.inactive_text = streamDeckText.getText();
        this.inactive_set = true;
        setActiveIfUnconfigured();
        return this;
    }

    public StreamDeckButton withActiveBackground(String background) {
        this.active_background = background;
        return this;
    }

    public StreamDeckButton withInactiveBackground(String background) {
        this.inactive_background = background;
        return this;
    }

    public StreamDeckButton withActiveForeground(String foreground) {
        this.active_foreground = foreground;
        return this;
    }

    public StreamDeckButton withInactiveForeground(String foreground) {
        this.inactive_foreground = foreground;
        return this;
    }

    public StreamDeckButton withActiveText(String text) {
        this.active_text = text;
        return this;
    }

    public StreamDeckButton withInactiveText(String text) {
        this.inactive_text = text;
        return this;
    }

    public StreamDeckButton withSVG(String text) {
        this.active_text = "SVG:"+text;
        this.inactive_text = "SVG:"+text;
        return this;
    }

    public StreamDeckButton withActiveSVG(String text) {
        this.active_text = "SVG:"+text;
        return this;
    }

    public StreamDeckButton withInactiveSVG(String text) {
        this.inactive_text = "SVG:"+text;
        return this;
    }

    public StreamDeckButton withImage(String text) {
        this.active_text = "assets/"+text;
        this.inactive_text = "assets/"+text;
        return this;
    }


    public StreamDeckButton withActiveImage(String text) {
        this.active_text = "assets/"+text;
        return this;
    }

    public StreamDeckButton withInactiveImage(String text) {
        this.inactive_text = "assets/"+text;
        return this;
    }

    public StreamDeckButton withText(String text) {
        this.active_text = text;
        this.inactive_text = text;
        return this;
    }

    public int getIndex() {
        return index;
    }

    public String getKey() {
        return key;
    }

    private String getConfigString() {
        StringBuilder sb = new StringBuilder();
        sb.append(key);
        sb.append("$&$");
        sb.append(active_background);
        sb.append("$&$");
        sb.append(inactive_background);
        sb.append("$&$");
        sb.append(active_foreground);
        sb.append("$&$");
        sb.append(inactive_foreground);
        sb.append("$&$");
        sb.append(active_text);
        sb.append("$&$");
        sb.append(inactive_text);
        return sb.toString();
    }

    void closePublishers() {
        if (configPublisher != null) configPublisher.close();
        if (activePublisher != null) activePublisher.close();
    }
}
