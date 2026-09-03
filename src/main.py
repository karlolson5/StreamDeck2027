from __future__ import annotations
import threading
import sys
import os
import tkinter as tk
import ctypes
import argparse
import time
import signal
from collections.abc import Callable
from typing import Optional
import constants as c
import util.utilities as u
from StreamDeck.DeviceManager import DeviceManager
from StreamDeck.Transport.Transport import TransportError
from network.stream_deck_config_subscriber import StreamDeckConfigSubscriber
from network.stream_deck_publisher import StreamDeckPublisher
from controller.stream_deck_controller import StreamDeckController
from status_window import StatusWindow
from app_state import AppState

if sys.platform == "win32":
    ctypes.CDLL(u.asset_path("dlls", "hidapi.dll"))
elif sys.platform == "darwin":
    lib_dir = u.asset_path("dlls")
    os.environ["DYLD_LIBRARY_PATH"] = lib_dir + os.pathsep + os.environ.get("DYLD_LIBRARY_PATH", "")
elif sys.platform.startswith("linux"):
    lib_dir = u.asset_path("dlls")
    os.environ["LD_LIBRARY_PATH"] = lib_dir + os.pathsep + os.environ.get("LD_LIBRARY_PATH", "")
else:
    print(f"Running on {sys.platform}. Relying on system-installed libhidapi.")

_running: bool = True


def exit_gracefully(*_):
    global _running
    _running = False


def set_nt_target(target_simulated: bool, app_state: AppState):
    """Point the NT client at the real robot or the simulated one, live."""
    target_ip = app_state.sim_ip if target_simulated else app_state.robot_ip
    try:
        c.NT_INSTANCE.stopClient()
    except Exception:
        pass
    c.NT_INSTANCE.setServer(target_ip)
    c.NT_INSTANCE.startClient4("StreamDeck")


def main(running: Callable[[], bool], app_state: AppState, status_win: Optional[StatusWindow] = None):
    set_nt_target(app_state.target_simulated_robot, app_state)

    controller = StreamDeckController()

    with StreamDeckConfigSubscriber(controller) as sub, StreamDeckPublisher(controller) as pub:
        try:
            sent_search_message = False
            while running():
                if not sent_search_message:
                    print("Searching for Stream Deck...")
                    sent_search_message = True

                sim_instance = app_state.sim_deck_instance
                if sim_instance is not None:
                    decks = []
                    hid_error = False
                    decks = [sim_instance]
                else:
                    try:
                        decks = DeviceManager().enumerate()
                        hid_error = False
                    except Exception as e:
                        print(f"Error probing for Stream Decks: {e}")
                        decks = []
                        hid_error = True

                if not decks:
                    if status_win:
                        status_win.update_status(deck_connected=False, robot_connected=False, hid_error=hid_error)
                    pub.send_connected(False)
                    time.sleep(1)
                    continue

                if len(decks) > 1:
                    print("Error: Multiple Stream Decks Detected. Disconnect one of them!")
                    time.sleep(2)
                    continue

                deck = decks[0]
                if not deck.is_visual():
                    continue

                controller.re_init(deck, sub.get_button_config_callables())
                sub.re_init()
                controller.re_init(deck, sub.get_button_config_callables())
                pub.re_init()
                controller.re_init(deck, sub.get_button_config_callables())

                with controller:
                    pub.send_connected(True)
                    last_time = time.time()

                    while running() and controller.is_open():
                        try:
                            controller.update()
                        except TransportError:
                            pass

                        robot_online = c.NT_INSTANCE.isConnected()

                        if status_win:
                            status_win.update_status(deck_connected=True, robot_connected=robot_online)

                        pub.send_heartbeat()
                        pub.update()

                        new_time = time.time()
                        d_time = new_time - last_time
                        if d_time < c.MIN_LOOP_TIME_S:
                            time.sleep(c.MIN_LOOP_TIME_S - d_time)
                        last_time = new_time

                    if status_win:
                        status_win.update_status(deck_connected=False, robot_connected=False)
                    pub.send_connected(False)
                sent_search_message = False

        finally:
            print("Stopping NetworkTables clients...")
            try:
                c.NT_INSTANCE.stopClient()
            except Exception as e:
                print(f"Error stopping NT instance: {e}")
            print("Cleanup complete.")


if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("--sim-deck", action='store_true', help="Start with the simulated Stream Deck window already open.")
    parser.add_argument("--sim-code", action='store_true', help="Start with 'Target simulated robot' already checked.")
    parser.add_argument("-s", "--sim", action='store_true', help="Equivalent to both --sim-code and --sim-deck.")
    args = parser.parse_args()

    start_sim_deck = args.sim or args.sim_deck
    start_sim_robot = args.sim or args.sim_code

    signal.signal(signal.SIGINT, exit_gracefully)

    app_state = AppState()
    app_state.set_target_simulated_robot(start_sim_robot)

    root = tk.Tk()
    icon = tk.PhotoImage(file=u.asset_path("icons", "app_icon.png")) # not inline on next line to prevent garbage collection of the icon
    root.iconphoto(True, icon)
    status_app = StatusWindow(
        root, app_state, lambda sim: set_nt_target(sim, app_state)
    )

    if start_sim_deck:
        status_app.start_simulated_deck()

    def on_close():
        exit_gracefully()
        root.destroy()
    root.protocol("WM_DELETE_WINDOW", on_close)

    worker = threading.Thread(target=main, args=(lambda: _running, app_state, status_app), daemon=True)
    worker.start()

    root.mainloop()
    _running = False