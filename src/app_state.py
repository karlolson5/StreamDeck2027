from __future__ import annotations
import threading
from typing import Optional


class AppState:
    """Shared state between the GUI (main thread) and the background worker thread."""
    def __init__(self):
        self._lock = threading.Lock()
        self._sim_deck_instance = None
        self._target_simulated_robot = False

    @property
    def sim_deck_instance(self):
        with self._lock:
            return self._sim_deck_instance

    def set_sim_deck_instance(self, instance):
        with self._lock:
            self._sim_deck_instance = instance

    def clear_sim_deck_instance(self):
        with self._lock:
            self._sim_deck_instance = None

    @property
    def target_simulated_robot(self) -> bool:
        with self._lock:
            return self._target_simulated_robot

    def set_target_simulated_robot(self, value: bool):
        with self._lock:
            self._target_simulated_robot = value