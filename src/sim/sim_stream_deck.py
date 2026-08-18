from collections.abc import Callable

import tkinter as tk
from tkinter import colorchooser, messagebox

class SimStreamDeck(CustomizableGridApp):
    def __init__(self, key_layout: tuple[int, int], tk_root: tk.Tk):
        self._init()

    def _init(self, key_layout: tuple[int, int], tk_root: tk.Tk):
        self._key_layout: tuple[int, int] = (8, 4)
        self._key_count: int = self.key_layout[0] * self.key_layout[1]
        self._open: bool = False
        self._key_callbacks: Callable[[int, bool],[]] = lambda key, selected: None
        self._brightness: int = 100
        self._key_image_format: dict[str, tuple[int, int]] = {"size": (32, 32)}
        super().__init__(self, tk_root, self._key_layout, self._key_image_format()["size"], self._key_callbacks, "Simulated Stream Deck")

    def deck_type(self):
        return "SimStreamDeck"

    def get_serial_number(self):
        return "SimStreamDeck0001"

    def get_firmware_version(self):
        return "SimStreamDeckFW1.0.0"

    def key_layout(self) -> tuple[int, int]:
        return self._key_layout

    def key_count(self) -> int:
        return self._key_count

    def key_image_format(self) -> dict[str, tuple[int, int]]:
        return self.key_image_format

    def open(self):
        self._open = True
        self.start_gui()

    def close(self):
        self._open = False
        self._init()

    def is_open(self) -> bool:
        return self._open

    def set_brightness(self, brightness: int):
        self._brightness = brightness

    def set_key_callback(self, callback: Callable[[int, bool],[]]):
        self._key_callbacks = callback

    def start_gui(self):
        pass

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
        self.grid_frame = tk.Frame(self.root, padding=10)
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

                btn.bind("<ButtonPress-1>",lambda event, idx=index: key_callbacks(idx, True))
                btn.bind("<ButtonRelease-1>",lambda event, idx=index: key_callbacks(idx, False))

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
        """Applies configuration string to the targeted element."""
        if index in self.buttons:
            self.buttons[index].config(text=new_text)

    def set_bg_color(self, index: int, color: str):
        """Applies configuration string to the targeted element."""
        if index in self.buttons:
            self.buttons[index].config(bg=color)

    def set_fg_color(self, index: int, color: str):
        """Applies configuration string to the targeted element."""
        if index in self.buttons:
            self.buttons[index].config(fg=color)