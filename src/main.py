from collections.abc import Callable

import constants as c
import ctypes
import util.utilities as u

ctypes.CDLL(u.asset_path("dlls", "hidapi.dll"))

_running: bool = True

def exit_gracefully(*_):
    global _running  # pylint: disable=global-statement
    _running = False

def main(running: Callable[[], bool]):
    