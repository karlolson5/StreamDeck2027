from collections.abc import Callable

import constants as c
import ctypes
import time
import signal
import util.utilities as u
from StreamDeck.DeviceManager import DeviceManager
from StreamDeck.Devices import StreamDeck
from StreamDeck.Transport.Transport import TransportError
from network.stream_deck_config_subscriber import StreamDeckConfigSubscriber
from network.stream_deck_publisher import StreamDeckPublisher
from controller.stream_deck_controller import StreamDeckController

ctypes.CDLL(u.asset_path("dlls", "hidapi.dll"))

_running: bool = True

def exit_gracefully(*_):
    global _running  # pylint: disable=global-statement
    _running = False

def main(running: Callable[[], bool]):
    controller = StreamDeckController()
    sub = StreamDeckConfigSubscriber(controller)
    pub = StreamDeckPublisher(controller)
    try:
        sent_search_message = False
        while running():
            if not sent_search_message:
                print("Searching for Stream Deck...")
                sent_search_message = True
            
            decks: list[StreamDeck.StreamDeck] = DeviceManager().enumerate()

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
                    # update config sub, this is part of controller.update() now I think
                    pub.send_heartbeat()
                    pub.update()
                    try:
                        controller.update()
                    except TransportError:
                        pass
                    
                    new_time = time.time()
                    d_time = new_time - last_time
                    if d_time < c.MIN_LOOP_TIME_S:
                        time.sleep(c.MIN_LOOP_TIME_S - d_time)
                    last_time = new_time
            
            pub.send_connected(False)

    finally:
        # Clean up resources to prevent connection leaks
        print("Cleaning up NetworkTables resources...")

        
        # Stop NetworkTables clients
        print("Stopping NetworkTables clients...")
        try:
            c.NT_INSTANCE.stopClient()
        except Exception as e:
            print(f"Error stopping NT instance: {e}")
        
        
        print("Cleanup complete.")

if __name__ == "__main__":
    signal.signal(signal.SIGINT, exit_gracefully)
    main(lambda: _running)

    # For simulated streamdeck only
    # one of these things needs to run in a background thread...
    # root = tk.Tk()
    # app = CustomizableGridApp(root)
    # root.geometry("650x400")
    # root.mainloop()