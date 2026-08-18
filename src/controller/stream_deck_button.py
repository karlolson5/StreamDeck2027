

from collections.abc import Callable
from typing import Optional

from controller.stream_deck_controller import StreamDeckController
import constants as c
from StreamDeck.ImageHelpers import PILHelper
from PIL import ImageDraw, ImageFont

class ButtonConfig:
    def __init__(self,
                 active_background: Optional[str] = None,
                 inactive_background: Optional[str] = None,
                 active_foreground: Optional[str] = None,
                 inactive_foreground: Optional[str] = None,
                 active_text: Optional[str] = None,
                 inactive_text: Optional[str] = None
                 ):
        self.active_background = active_background if active_background is not None else c.DEFAULT_BACKGROUND_COLOR
        self.inactive_background = inactive_background if inactive_background is not None else c.DEFAULT_BACKGROUND_COLOR
        self.active_foreground = active_foreground if active_foreground is not None else c.DEFAULT_FOREGROUND_COLOR
        self.inactive_foreground = inactive_foreground if inactive_foreground is not None else c.DEFAULT_FOREGROUND_COLOR
        self.active_text = active_text if active_text is not None else ""
        self.inactive_text = inactive_text if inactive_text is not None else ""
        self.cache: int = 0

    def hash(self):
        return hash(
            self.active_background+"$&$"+
            self.inactive_background+"$&$"+
            self.active_foreground+"$&$"+
            self.inactive_foreground+"$&$"+
            self.active_text+"$&$"+
            self.inactive_text
        )

    def __eq__(self, other):
        return self.hash() == other.hash()

class StreamDeckButton:
    def __init__(self, controller: StreamDeckController, index: int, key: str, config_supplier: Callable[[], ButtonConfig], active_supplier: Callable[[], bool]):
        self.controller: StreamDeckController = controller
        self.row: int = index // self.controller.num_cols
        self.col: int = index % self.controller.num_cols
        self.index: int = index
        self.key: str = key
        self.config_supplier: Callable[[], ButtonConfig] = config_supplier
        self.config: ButtonConfig = self.config_supplier() 
        self.active_supplier: Callable[[], bool] = active_supplier
        self.active: bool = self.active_supplier()

    def update(self):
        new_active = self.active_supplier()
        new_config = self.config_supplier()
        if new_active == self.active and new_config == self.config:
            return
        self.active = new_active
        self.config = new_config
        self.render_key()
        return

    def render_key_image(self, image: bytes):
        self.controller._deck.set_key_image(self.index, image)

    def render_key(self):
        if self.active:
            bg = self.config.active_background
            fg = self.config.active_foreground
            tx = self.config.active_text
        else:
            bg = self.config.inactive_background
            fg = self.config.inactive_foreground
            tx = self.config.inactive_text
        cache_key = (bg, fg, tx)
        if cache_key in self.controller._icon_cache:
            return PILHelper.to_native_key_format(self.controller._deck, self._icon_cache[cache_key])
        
        image = PILHelper.create_key_image(self._deck, background=bg)

        # Draw text, fitting the font size to the key
        if tx != "" and fg != bg:
            font_fraction = 0.8
            fontsize = 1
            draw = ImageDraw.Draw(image)
            font = ImageFont.truetype(c.FONT_FILE, fontsize)
            l, t, r, b = draw.multiline_textbbox((0,0), tx, font)
            while r-l < font_fraction*image.size[0] and b-t < font_fraction*image.size[1]:
                fontsize += 1
                font = ImageFont.truetype(c.FONT_FILE, fontsize)
                l, t, r, b = draw.multiline_textbbox((0,0), tx, font)

            draw.multiline_text((image.width/2, image.height/2), tx, fill=fg, font=font, anchor="mm", align="center")

        self._icon_cache[cache_key] = image
        return PILHelper.to_native_key_formt(self.controller._deck, image)