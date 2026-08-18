import os
import sys
import time
import constants as c


def resource_path(filename):
    if hasattr(sys, "_MEIPASS"):
        return os.path.join(sys._MEIPASS, filename)
    return filename

def asset_path(*paths):
    return resource_path(os.path.join(c.DEFAULT_ASSETS_PATH, *paths))

def time_ms() -> int:
    return time.time_ns() // 1_000_000