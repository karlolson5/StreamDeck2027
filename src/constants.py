from dataclasses import dataclass
import os
from matplotlib import font_manager
import ntcore

NT_INSTANCE = nt_instance = ntcore.NetworkTableInstance.create()
PRESSED_PUBLISH_OPTIONS = ntcore.PubSubOptions(periodic=0.02, sendAll=True)

font = font_manager.FontProperties(family="Arial")
FONT_FILE = font_manager.findfont(font)
DEFAULT_ASSETS_PATH = os.path.join(os.path.dirname(__file__), "../assets")

SERVER_IPS = ("10.34.76.2", "127.0.0.1")
MIN_LOOP_TIME_S = 0.02

@dataclass
class COLORS:
    CO_ORANGE = "#FF7A1C"
    CO_TEAL = "#209299"
    WHITE = "#FFFFFF"
    BLACK = "#000000"
    RED = "#FF0000"
    GREEN = "#00FF00"
    BLUE = "#0000FF"
    CYAN = "#00FFFF"
    MAGENTA = "#FF00FF"
    YELLOW = "#FFFF00"
    DEFAULT_BACKGROUND = BLACK
    DEFAULT_FOREGROUND = WHITE
    NO_CONFIG = YELLOW

BRIGHTNESS: int = 80 # Stream Deck brightness as a percentage
DEFAULT_BACKGROUND_COLOR = COLORS.BLACK
DEFAULT_FOREGROUND_COLOR = COLORS.BLACK
KEY_SPACING = (36, 36)
BACKGROUND_IMAGE = "sandspit_logo.png"
TEXT_HEIGHT_OFFSET = 5
