import tkinter as tk
from typing import Optional

class StatusWindow:
    """Lightweight GUI to track and display Stream Deck & Robot connection health."""
    def __init__(self, root: tk.Tk):
        self.root = root
        self.root.title("StreamDeck2027 Status")
        self.root.geometry("450x120")
        self.root.resizable(False, False)
        self.last_deck_connected: Optional[bool] = None
        self.last_robot_connected: Optional[bool] = None

        self.status_label = tk.Label(
            root, 
            text="Initializing...", 
            fg="white", 
            bg="black", 
            font=("Arial", 14, "bold"),
            height=3
        )
        self.status_label.pack(fill=tk.BOTH, expand=True)

    def update_status(self, deck_connected: bool, robot_connected: bool):
        """Thread-safe update helper via Tkinter's .after loop."""
        if (deck_connected == self.last_deck_connected and
            robot_connected == self.last_robot_connected):
            return
        self.last_robot_connected = robot_connected
        self.last_deck_connected = deck_connected
        def update():
            if not deck_connected:
                self.status_label.config(
                    text="🔴 No Stream Deck Connected", 
                    bg="#7A1D1D", fg="white"
                )
            elif deck_connected and not robot_connected:
                self.status_label.config(
                    text="🟡 Stream Deck Connected, No Robot", 
                    bg="#D4A373", fg="black"
                )
            else:
                self.status_label.config(
                    text="🟢 Stream Deck and Robot Connected", 
                    bg="#2A6F3F", fg="white"
                )
        self.root.after(0, update)
