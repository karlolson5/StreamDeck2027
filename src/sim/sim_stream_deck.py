from __future__ import annotations
from collections.abc import Callable
import sys

if sys.version_info >= (3, 12):
    from typing import override
else:
    def override(func):
        return func

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
        self._held: dict[int, bool] = {}
        self._raw_images: dict[int, bytes] = {}
        self._tk_images: dict[int, ImageTk.PhotoImage] = {}
        # Nominal button size in pixels (e.g. 96x96), used as a fallback
        # before a button has been drawn on screen for the first time.
        self._key_size = key_size

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
                
                # Instantiate button widget with standard defaults.
                btn = tk.Button(
                    self.grid_frame,
                    text="",
                    bg="black",
                    fg="white",
                    bd=0,
                    highlightthickness=0,
                )

                btn.bind("<ButtonPress-1>",   lambda event, idx=index: self._on_left_press(idx, key_callbacks))
                btn.bind("<ButtonRelease-1>", lambda event, idx=index: self._on_left_release(idx, key_callbacks))

                # Right-click: Button-3 covers Windows/Linux and most mice on Mac.
                # Button-2 covers some Mac trackpad/mouse configs.
                # Control-Button-1 covers Mac trackpads with no right-click button configured.
                btn.bind("<Button-3>",           lambda event, idx=index: self._on_right_click(idx, key_callbacks))
                btn.bind("<Button-2>",           lambda event, idx=index: self._on_right_click(idx, key_callbacks))
                btn.bind("<Control-Button-1>",   lambda event, idx=index: self._on_right_click(idx, key_callbacks))

                btn.bind("<Configure>", lambda event, idx=index: self._on_button_resize(idx, event))

                # Map standard layout placement metrics
                btn.grid(row=r, column=c, padx=4, pady=4, sticky="nsew")
                
                # Store references locally
                self.buttons[index] = btn
                
        # Make the layout stretch responsively inside its parent container
        for r in range(self.rows):
            self.grid_frame.rowconfigure(r, weight=1, minsize=key_size[1])
        for c in range(self.cols):
            self.grid_frame.columnconfigure(c, weight=1, minsize=key_size[0])

        total_w = self.cols * (key_size[0] + 8) + 20
        total_h = self.rows * (key_size[1] + 8) + 20
        self.root.geometry(f"{total_w}x{total_h}")

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
            # Remember the original bytes so we can re-render this image
            # at a different size later if the button gets resized.
            self._raw_images[index] = image

            btn = self.buttons[index]
            width, height = btn.winfo_width(), btn.winfo_height()
            if width <= 1 or height <= 1:
                width, height = self._key_size

            self._render_image(index, image, (width, height))
        self.root.after(0, _update)

    def _on_button_resize(self, index: int, event: tk.Event):
        """Re-scales this button's cached image to match its new size."""
        raw = self._raw_images.get(index)
        if raw is None:
            return
        width, height = max(event.width, 1), max(event.height, 1)
        self._render_image(index, raw, (width, height))

    def _on_left_press(self, index: int, key_callbacks: Callable[[int, bool], []]):
        """Left-click press does nothing if the button is currently held via right-click."""
        if self._held.get(index, False):
            return
        key_callbacks(None, index, True)

    def _on_left_release(self, index: int, key_callbacks: Callable[[int, bool], []]):
        """Left-click release also clears a right-click hold, if one is active."""
        if self._held.get(index, False):
            self._held[index] = False
        key_callbacks(None, index, False)

    def _on_right_click(self, index: int, key_callbacks: Callable[[int, bool], []]):
        """Right-click toggles a button between held-down and released."""
        is_held = self._held.get(index, False)
        if is_held:
            self._held[index] = False
            key_callbacks(None, index, False)   # simulate release
        else:
            self._held[index] = True
            key_callbacks(None, index, True)    # simulate press-and-hold

    def _render_image(self, index: int, image: bytes, size: tuple[int, int]):
        """Decodes `image` and draws it scaled to exactly fill `size`,
        so it always matches the current button dimensions with no
        leftover whitespace and no cropping."""
        pil_image = Image.open(io.BytesIO(image)).convert("RGBA")
        pil_image = pil_image.resize(size, Image.LANCZOS)
        tk_image = ImageTk.PhotoImage(pil_image)
        self.buttons[index].config(image=tk_image)
        self._tk_images[index] = tk_image  # keep a reference so it isn't garbage collected

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