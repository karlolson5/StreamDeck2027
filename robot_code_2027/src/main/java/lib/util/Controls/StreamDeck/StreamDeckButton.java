package frc.lib.util.Controls.StreamDeck;

import java.util.ArrayList;
import java.util.List;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import java.util.function.BooleanSupplier;
import edu.wpi.first.networktables.BooleanPublisher;
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
    private LoggedNetworkBoolean pressed;
    private BooleanPublisher activePublisher;
    private StringPublisher configPublisher;
    private BooleanSupplier activeSupplier;
    private NetworkTable table;

    private StreamDeckButton(int index, String key, BooleanSupplier activeSupplier) {
        this(index, key, activeSupplier, new LoggedNetworkBoolean("StreamDeck/" + key));
    }

    private StreamDeckButton(int index, String key, BooleanSupplier activeSupplier, LoggedNetworkBoolean pressed) {
        super(pressed::get);
        this.pressed = pressed;
        this.activeSupplier = activeSupplier;
        this.index = index;
        this.key = key;
        this.table = NetworkTableInstance.getDefault().getTable("StreamDeck").getSubTable("Button/" + index);
        this.configPublisher = table.getStringTopic("Appearance").publish();
        this.activePublisher = table.getBooleanTopic("Selected").publish();
    }

    private StreamDeckButton(int index, String key) {
        this(index, key, activeSupplier, new LoggedNetworkBoolean("StreamDeck/" + key));
    }

    private StreamDeckButton(int index, String key, LoggedNetworkBoolean pressed) {
        super(pressed::get);
        this.pressed = pressed;
        this.activeSupplier = pressed;
        this.index = index;
        this.key = key;
        this.table = NetworkTableInstance.getDefault().getTable("StreamDeck").getSubTable("Button/" + index);
        this.configPublisher = table.getStringTopic("Appearance").publish();
        this.activePublisher = table.getBooleanTopic("Selected").publish();
    }

    public StreamDeckButton(int row, int col, String key) {
        this(calculate_index(row, col), key);
    }

    public StreamDeckButton(int row, int col, String key, BooleanSupplier activeSupplier) {
        this(calculate_index(row, col), key, activeSupplier);
    }

    public void update() {
        activePublisher.set(button.activeSupplier.getAsBoolean());
    }

    private int calculate_index(int row, int col) {
        return row * 8 + col % 8;
    }

    private void publishConfig() {
        configPublisher.set(getConfigString());
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
}
