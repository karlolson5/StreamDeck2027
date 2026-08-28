from __future__ import annotations
from dataclasses import dataclass
import os
import ntcore

NT_INSTANCE = nt_instance = ntcore.NetworkTableInstance.create()
PRESSED_PUBLISH_OPTIONS = ntcore.PubSubOptions(periodic=0.02, sendAll=True)

FONT = "Roboto-Regular.ttf"
DEFAULT_ASSETS_PATH = "assets"

SERVER_IPS = ("10.34.76.2", "127.0.0.1") # Robot IP (10.TE.AM.2) , Sim IP (127.0.0.1)
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
    NO_CONFIG = MAGENTA
    WARNING = YELLOW

WARNING_BGFGTX = (COLORS.YELLOW, COLORS.RED, "WRN!")

BRIGHTNESS: int = 80 # Stream Deck brightness as a percentage
DEFAULT_BACKGROUND_COLOR = COLORS.BLACK
DEFAULT_FOREGROUND_COLOR = COLORS.WHITE
KEY_SPACING = (36, 36)
BACKGROUND_IMAGE = "sandspit_logo.png"
TEXT_HEIGHT_OFFSET = 5

#StreamDeckXL values
KEY_PIXEL_WIDTH, KEY_PIXEL_HEIGHT = 96, 96
KEY_IMAGE_FORMAT = "JPEG"
KEY_FLIP = (True, True)
KEY_ROTATION = 0

#SimStreamDeck config
SIM_KEY_LAYOUT = (4, 8)