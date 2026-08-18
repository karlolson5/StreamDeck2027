
from collections.abc import Callable
from typing import override

from ntcore import BooleanSubscriber, StringSubscriber

from controller.stream_deck_button import ButtonConfig
from controller.stream_deck_controller import StreamDeckController
import constants as c


class StreamDeckConfigSubscriber():
    def __init__(self, controller: StreamDeckController):
        self._controller = controller
        self._button_config_sources: dict[int, tuple[StringSubscriber, BooleanSubscriber]] = {}
        self._button_config_callables: dict[int, tuple[Callable[[],ButtonConfig], Callable[[],bool]]] = {}
        self._init_complete = False
        self._ensure_init()

    def _ensure_init(self):
            self._make_intial_topics()
            self._update_button_topics()
            self._build_button_callables()

    def re_init(self):
        self._init_complete = False
        self._ensure_init()
    
    def _make_intial_topics(self):
        if self._init_complete:
            return
        for index in range(self._controller.num_buttons):
             tbl = self._controller.table.getSubTable(f"Button/{index}")
             self._button_config_sources[index] = (
                  tbl.getStringTopic("Appearance").subscribe(""+"$&$"+""+"$&$"+""+"$&$"+""+"$&$"+""+"$&$"+""+"$&$"+""),
                  tbl.getBooleanTopic("Selected").subscribe(False) # TODO: update to "Active", requires robot code changes
             )
        self._init_complete = True
    
    def _update_button_topics(self):
        pass

    def _build_button_callables(self):
         for index, (appearance_source, active_source) in self._button_config_sources.items():
            self._button_config_callables[index] = (
                lambda appearance_source=appearance_source: ButtonConfig(*appearance_source.get().split("$&$")),
                active_source.get
            )

    def get_button_config_callables(self) -> dict[int, tuple[Callable[[],ButtonConfig], Callable[[],bool]]]:
         self._ensure_init()
         return self._button_config_callables

