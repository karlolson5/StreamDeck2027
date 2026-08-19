import threading
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
from StreamDeck.Devices import StreamDeck
from StreamDeck.Transport.Transport import TransportError
from network.stream_deck_config_subscriber import StreamDeckConfigSubscriber
from network.stream_deck_publisher import StreamDeckPublisher
from controller.stream_deck_controller import StreamDeckController
from sim.sim_stream_deck import SimStreamDeck

ctypes.CDLL(u.asset_path("dlls", "hidapi.dll"))

_running: bool = True
use_sim_deck: bool = False
use_sim_network: bool = False

def exit_gracefully(*_):
    global _running  # pylint: disable=global-statement
    _running = False

def main(running: Callable[[], bool], tk_root: Optional[tk.Tk] = None):
    target_ip = c.SERVER_IPS[0 if not use_sim_network else 1]
    c.NT_INSTANCE.setServer(target_ip)
    c.NT_INSTANCE.startClient4("StreamDeck")
    controller = StreamDeckController()
    with StreamDeckConfigSubscriber(controller) as sub, StreamDeckPublisher(controller) as pub:
        try:
            sent_search_message = False
            while running():
                if not sent_search_message:
                    print("Searching for Stream Deck...")
                    sent_search_message = True
                
                decks: list[StreamDeck.StreamDeck | SimStreamDeck] = (
                    DeviceManager().enumerate() if not use_sim_deck
                    else [SimStreamDeck(c.SIM_KEY_LAYOUT, tk_root)]
                )

                if not decks:
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

                print(f"Creating controller for {deck.deck_type()}")
                controller.re_init(deck, sub.get_button_config_callables())
                sub.re_init()
                pub.re_init()

                with controller:
                    pub.send_connected(True)
                    last_time = time.time()

                    while running() and controller.is_open():
                        try:
                            controller.update()
                        except TransportError:
                            pass

                        pub.send_heartbeat()
                        pub.update()

                        new_time = time.time()
                        d_time = new_time - last_time
                        if d_time < c.MIN_LOOP_TIME_S:
                            time.sleep(c.MIN_LOOP_TIME_S - d_time)
                        last_time = new_time
                
                pub.send_connected(False)

        finally:
            # Stop NetworkTables clients
            print("Stopping NetworkTables clients...")
            try:
                c.NT_INSTANCE.stopClient()
            except Exception as e:
                print(f"Error stopping NT instance: {e}")
        
        
        print("Cleanup complete.")

if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("--sim-deck", action='store_true', help="Use simulated Stream Deck GUI.")
    parser.add_argument("--sim-code", action='store_true', help="Use when running simulated robot code.")
    parser.add_argument("-s", "--sim", action='store_true', help="Simulation setup, equivalent to both --sim-code and --sim-deck.")
    args = parser.parse_args()

    if args.sim or args.sim_deck:
        use_sim_deck = True # simulate deck
    if args.sim or args.sim_code:
        use_sim_network = True # simulated robot code expected


    signal.signal(signal.SIGINT, exit_gracefully)

    if use_sim_deck:
        # Tkinter owns the main thread; the old polling loop moves to a
        # background thread that starts before the window loop takes over.
        root = tk.Tk()

        def on_close():
            exit_gracefully()
            root.destroy()

        root.protocol("WM_DELETE_WINDOW", on_close)  # closing the window stops everything cleanly

        worker = threading.Thread(target=main, args=(lambda: _running, root), daemon=True)
        worker.start()

        root.mainloop()   # blocks here until the window closes
        _running = False
    else:
        main(lambda: _running)

    # For simulated streamdeck only
    # one of these things needs to run in a background thread...
    # root = tk.Tk()
    # app = CustomizableGridApp(root)
    # root.geometry("650x400")
    # root.mainloop()