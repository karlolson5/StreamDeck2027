from __future__ import annotations
import threading
from typing import Optional
import constants as c
from settings import NetworkSettings, load_settings, save_settings

class AppState:
    """Shared state between the GUI (main thread) and the background worker thread."""
    def __init__(self):
        self._lock = threading.Lock()
        self._sim_deck_instance = None
        self._target_simulated_robot = False
        self._network_settings: NetworkSettings = load_settings(
            c.DEFAULT_ROBOT_IP, c.DEFAULT_SIM_IP
        )

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

    @property
    def robot_ip(self) -> str:
        with self._lock:
            return self._network_settings.robot_ip

    @property
    def sim_ip(self) -> str:
        with self._lock:
            return self._network_settings.sim_ip

    def set_network_ips(self, robot_ip: str, sim_ip: str) -> None:
        with self._lock:
            self._network_settings = NetworkSettings(robot_ip=robot_ip, sim_ip=sim_ip)
        save_settings(self._network_settings)