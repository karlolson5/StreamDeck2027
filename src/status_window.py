from __future__ import annotations
import tkinter as tk
from typing import Optional, Callable
import constants as c
from app_state import AppState
from sim.sim_stream_deck import SimStreamDeckXL


class StatusWindow:
    """Status display, plus live controls for simulated robot targeting and a simulated deck."""
    def __init__(self, root: tk.Tk, app_state: AppState, set_nt_target: Callable[[bool], None]):
        self.root = root
        self.app_state = app_state
        self.set_nt_target = set_nt_target
        self.root.title("StreamDeck2027")
        self.root.geometry("450x200")
        self.root.resizable(False, False)
        self.last_deck_connected: Optional[bool] = None
        self.last_robot_connected: Optional[bool] = None
        self.last_hid_error: Optional[bool] = None
        self._sim_window: Optional[tk.Toplevel] = None

        self.status_label = tk.Label(
            root, text="Initializing...", fg="white", bg="black",
            font=("Arial", 14, "bold"), height=3
        )
        self.status_label.pack(fill=tk.BOTH, expand=True)

        controls = tk.Frame(root)
        controls.pack(fill=tk.X, padx=10, pady=10)

        self.target_sim_var = tk.BooleanVar(value=self.app_state.target_simulated_robot)
        tk.Checkbutton(
            controls, text="Target simulated robot",
            variable=self.target_sim_var, command=self._on_target_sim_toggle
        ).pack(anchor="w")

        self.sim_deck_button = tk.Button(
            controls, text="Start Simulated Stream Deck",
            command=self.start_simulated_deck
        )
        self.sim_deck_button.pack(anchor="w", pady=(5, 0))

    def _on_target_sim_toggle(self):
        target_simulated = self.target_sim_var.get()
        self.app_state.set_target_simulated_robot(target_simulated)
        self.set_nt_target(target_simulated)

    def start_simulated_deck(self):
        if self.app_state.sim_deck_instance is not None:
            return  # already running

        self._sim_window = tk.Toplevel(self.root)
        sim_deck = SimStreamDeckXL(c.SIM_KEY_LAYOUT, self._sim_window)
        self.app_state.set_sim_deck_instance(sim_deck)

        def on_sim_close():
            sim_deck.close()  # lets the background loop's inner while exit cleanly
            self.app_state.clear_sim_deck_instance()
            self._sim_window.destroy()
            self._sim_window = None
            self._refresh_button_state()

        self._sim_window.protocol("WM_DELETE_WINDOW", on_sim_close)
        self._refresh_button_state()

    def _refresh_button_state(self):
        deck_connected = bool(self.last_deck_connected)
        sim_active = self.app_state.sim_deck_instance is not None
        self.sim_deck_button.config(state=tk.DISABLED if (deck_connected or sim_active) else tk.NORMAL)

    def update_status(self, deck_connected: bool, robot_connected: bool, hid_error: bool = False):
        """Thread-safe update helper via Tkinter's .after loop."""
        if (deck_connected == self.last_deck_connected and
            robot_connected == self.last_robot_connected and
            hid_error == self.last_hid_error):
            return
        self.last_robot_connected = robot_connected
        self.last_deck_connected = deck_connected
        self.last_hid_error = hid_error

        def update():
            if hid_error:
                self.status_label.config(
                    text="⚠️ Can't access USB devices\nInstall hidapi to use a real Stream Deck",
                    bg="#8A6D00", fg="red"
                )
            elif not deck_connected:
                self.status_label.config(text="🔴 No Stream Deck Connected", bg="#7A1D1D", fg="white")
            elif deck_connected and not robot_connected:
                self.status_label.config(text="🟡 Stream Deck Connected, No Robot", bg="#D4A373", fg="black")
            else:
                self.status_label.config(text="🟢 Stream Deck and Robot Connected", bg="#2A6F3F", fg="white")
            self._refresh_button_state()

        self.root.after(0, update)