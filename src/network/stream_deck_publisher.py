from __future__ import annotations

from typing import Optional, override
import ntcore
from controller.stream_deck_controller import StreamDeckController
from network.base.output_publisher import OutputPublisher
import util.utilities as u
import constants as c

class StreamDeckPublisher(OutputPublisher):
    def __init__(self, controller: StreamDeckController):
        self._controller = controller
        self._init_complete = False
        self._connected_publisher: ntcore.BooleanPublisher
        self._heartbeat_publisher: ntcore.IntegerPublisher
        self._start_time_ms = u.time_ms()
        self._button_publishers: dict[str, Optional[ntcore.BooleanPublisher]] = {}
        self._ensure_init()

    def __enter__(self):
        return self

    def __exit__(self, *_):
        self.cleanup()

    def _ensure_init(self):
        self._make_intial_topics()
        self._update_button_topics()

    def re_init(self):
        self.cleanup()
        self._init_complete = False
        self._ensure_init()

    def _make_intial_topics(self):
        if self._init_complete:
            return
        tbl = self._controller.table
        self._connected_publisher = tbl.getBooleanTopic("Connected").publish()
        self._heartbeat_publisher = tbl.getIntegerTopic("Heartbeat").publish()
        self._init_complete = True
    
    def _update_button_topics(self):
        current_keys = {b.key for b in self._controller.buttons.values() if b.key}
        tbl = self._controller.table

        for key in list(self._button_publishers.keys()):
            if key not in current_keys:
                self._button_publishers[key].close()
                del self._button_publishers[key]

        for key in current_keys:
            if key not in self._button_publishers:
                self._button_publishers[key] = tbl.getBooleanTopic(key).publish(c.PRESSED_PUBLISH_OPTIONS)

    def _publish_buttons(self):
        for key in self._button_publishers.keys():
            to_publish = self._controller.get_button_by_key(key).get_publish_list()
            for val in to_publish:
                self._button_publishers[key].set(val)
            to_publish = self._controller.get_button_by_key(key).clear_publish_list()


    def update(self):
        self._ensure_init()
        self._publish_buttons()
                
    @override
    def send_connected(self, connected: bool):
        self._ensure_init()
        self._connected_publisher.set(connected)

    @override
    def send_heartbeat(self):
        self._ensure_init()
        self._heartbeat_publisher.set(u.time_ms() - self._start_time_ms)

    def cleanup(self):
        if not self._init_complete:
            return

        try:
            if self._connected_publisher:
                self._connected_publisher.close()
            if self._heartbeat_publisher:
                self._heartbeat_publisher.close()
            for pub in self._button_publishers.values():
                if pub:
                    pub.close()
        except Exception as e:
            print(f"Error during StreamDeckPublisher cleanup: {e}")
