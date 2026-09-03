from __future__ import annotations
import os
import sys
import time
import constants as c


def resource_path(filename):
    try:
        if getattr(sys, 'frozen', False):
            base_path = sys._MEIPASS
        else:
            base_path = os.path.abspath(os.path.join(os.path.dirname(__file__), "..", ".."))
    except Exception:
        base_path = os.path.abspath(".")
    return os.path.join(base_path, filename)

def asset_path(*paths):
    return resource_path(os.path.join(c.DEFAULT_ASSETS_PATH, *paths))

def get_font_path():
    return asset_path("fonts", c.FONT)

def time_ms() -> int:
    return time.time_ns() // 1_000_000