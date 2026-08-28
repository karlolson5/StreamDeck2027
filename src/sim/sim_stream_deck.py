from __future__ import annotations
from collections.abc import Callable
import sys

if sys.version_info >= (3, 12):
    from typing import override
else:
    from typing_extensions import override

import tkinter as tk
import io
import constants as c
import PIL
from PIL import Image, ImageTk

class CustomizableGridApp:
    def __init__(self, root: tk.Tk, key_layout: tuple[int, int], key_size: tuple[int, int], key_callbacks: Callable[[int, bool],[]], title: str = "Button Grid"):
        self.root = root
        self.root.title(title)
        
        # Grid parameters
        self.rows = key_layout[0]
        self.cols = key_layout[1]
        
        # Storage dictionary for button objects
        self.buttons: dict[int, tk.Button] = {}
        
        # Setup UI Frames
        self.setup_grid_panel(key_size, key_callbacks)

    def setup_grid_panel(self, key_size: tuple[int, int], key_callbacks: Callable[[int, bool],[]]):
        """Creates the grid matrix layout on the right side."""
        self.grid_frame = tk.Frame(self.root, padx=10, pady=10)
        self.grid_frame.pack(side=tk.RIGHT, fill=tk.BOTH, expand=True, padx=10, pady=10)
        
        for r in range(self.rows):
            for c in range(self.cols):
                # Unique default text for every button
                index = r * self.cols + c
                
                # Instantiate button widget with standard defaults
                btn = tk.Button(
                    self.grid_frame, 
                    text="", 
                    width=key_size[0], 
                    height=key_size[1],
                    bg="black",
                    fg="white",
                )

                btn.bind("<ButtonPress-1>",lambda event, idx=index: key_callbacks(None, idx, True))
                btn.bind("<ButtonRelease-1>",lambda event, idx=index: key_callbacks(None, idx, False))

                # Map standard layout placement metrics
                btn.grid(row=r, column=c, padx=4, pady=4, sticky="nsew")
                
                # Store references locally
                self.buttons[index] = btn
                
        # Make the layout stretch responsively inside its parent container
        for r in range(self.rows):
            self.grid_frame.rowconfigure(r, weight=1)
        for c in range(self.cols):
            self.grid_frame.columnconfigure(c, weight=1)

    def set_text(self, index: int, new_text: str):
        if index in self.buttons:
            self.root.after(0, lambda: self.buttons[index].config(text=new_text))

    def set_bg_color(self, index: int, color: str):
        if index in self.buttons:
            self.root.after(0, lambda: self.buttons[index].config(bg=color))

    def set_fg_color(self, index: int, color: str):
        if index in self.buttons:
            self.root.after(0, lambda: self.buttons[index].config(fg=color))

    def set_button_image(self, index: int, image: bytes):
        def _update():
            img = PIL.ImageTk.PhotoImage(Image.open(io.BytesIO(image)))
            self.buttons[index].config(image=img)
            self.buttons[index].image = img  # keep a reference so it isn't garbage collected
        self.root.after(0, _update)

class SimStreamDeck(CustomizableGridApp):
    def __init__(self, key_layout: tuple[int, int], tk_root: tk.Tk):
        self._init(key_layout, tk_root)

    def _init(self, key_layout: tuple[int, int], tk_root: tk.Tk):
        self.KEY_LAYOUT: tuple[int, int] = key_layout
        self.KEY_COUNT: int = self.KEY_LAYOUT[0] * self.KEY_LAYOUT[1]
        self._open: bool = False
        self._key_callbacks: Callable[[int, bool],[]] = lambda key, selected: None
        self._brightness: int = 100
        self.KEY_PIXEL_WIDTH = c.KEY_PIXEL_WIDTH
        self.KEY_PIXEL_HEIGHT = c.KEY_PIXEL_HEIGHT
        self.KEY_IMAGE_FORMAT = c.KEY_IMAGE_FORMAT
        self.KEY_FLIP = (False, False) # c.KEY_FLIP is for the physical flipping of the internal LED screen
        self.KEY_ROTATION = 0 # c.KEY_ROTATION is for the physical rotation of the internal LED screen
        super().__init__(tk_root, self.KEY_LAYOUT, self.key_image_format()["size"], lambda *args: self._key_callbacks(*args), "Simulated Stream Deck")

    def deck_type(self):
        return "SimStreamDeck"

    def get_serial_number(self):
        return "SimStreamDeck0001"

    def get_firmware_version(self):
        return "SimStreamDeckFW1.0.0"

    def key_layout(self) -> tuple[int, int]:
        return self.KEY_LAYOUT

    def key_count(self) -> int:
        return self.KEY_COUNT

    def key_image_format(self) -> dict[str, tuple[int, int]]:
        return {
            'size': (self.KEY_PIXEL_WIDTH, self.KEY_PIXEL_HEIGHT),
            'format': self.KEY_IMAGE_FORMAT,
            'flip': self.KEY_FLIP,
            'rotation': self.KEY_ROTATION,
        }

    def set_key_image(self, index: int, image: bytes):
        self.set_button_image(index, image)

    def open(self):
        self._open = True

    def close(self):
        self._open = False

    def is_open(self) -> bool:
        return self._open

    def set_brightness(self, brightness: int):
        self._brightness = brightness

    def set_key_callback(self, callback: Callable[[int, bool],[]]):
        self._key_callbacks = callback

    def is_visual(self) -> bool:
        return True

class SimStreamDeckXL(SimStreamDeck):
    @override
    def get_firmware_version(self):
        raise NotImplementedError("Don't call this until the following issue is fixed: https://github.com/abcminiuser/python-elgato-streamdeck/issues/38")
