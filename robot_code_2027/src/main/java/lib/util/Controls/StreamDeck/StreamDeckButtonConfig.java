package frc.lib.util.Controls.StreamDeck;
import frc.lib.util.COColor;

public class StreamDeckButtonConfig {
    private String foreground;
    private String background;
    private String text;

    public StreamDeckButtonConfig(String background, String foreground, String text) {
        this.background = background;
        this.foreground = foreground;
        this.text = text;
    }

    public StreamDeckButtonConfig(String background, String foreground) {
        this(background, foreground, "");
    }
    
    public StreamDeckButtonConfg(COColor background, COColor foreground, String text) {
        this(background.toString(), foreground.toString(), text);
    }

    public StreamDeckButtonConfg(COColor background, COColor foreground, String text) {
        this(background, foreground, "");
    }

    public String getForeground() {
        return foreground;
    }

    public String getBackground() {
        return background;
    }

    public String getText() {
        return text;
    }
}
