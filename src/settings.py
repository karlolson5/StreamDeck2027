from __future__ import annotations
import json
import os
from dataclasses import dataclass, asdict

# Lives in the user's home folder, not in the app bundle, so it's writable
# even when the app itself is installed somewhere read-only.
SETTINGS_DIR = os.path.join(os.path.expanduser("~"), ".streamdeck2027")
SETTINGS_FILE = os.path.join(SETTINGS_DIR, "settings.json")


@dataclass
class NetworkSettings:
    robot_ip: str
    sim_ip: str


def load_settings(default_robot_ip: str, default_sim_ip: str) -> NetworkSettings:
    """Load saved IPs, falling back to the given defaults if nothing's saved yet
    or the file is missing/corrupt."""
    try:
        with open(SETTINGS_FILE, "r") as f:
            data = json.load(f)
        return NetworkSettings(
            robot_ip=data.get("robot_ip", default_robot_ip),
            sim_ip=data.get("sim_ip", default_sim_ip),
        )
    except (FileNotFoundError, json.JSONDecodeError, OSError):
        return NetworkSettings(robot_ip=default_robot_ip, sim_ip=default_sim_ip)


def save_settings(settings: NetworkSettings) -> None:
    os.makedirs(SETTINGS_DIR, exist_ok=True)
    with open(SETTINGS_FILE, "w") as f:
        json.dump(asdict(settings), f, indent=2)