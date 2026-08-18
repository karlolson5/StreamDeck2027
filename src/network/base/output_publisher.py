
from abc import ABC, abstractmethod

class OutputPublisher(ABC):
    @abstractmethod
    def send_connected(self, connected: bool):
        pass
    @abstractmethod
    def send_heartbeat(self):
        pass
    @abstractmethod
    def send_data(self, *args):
        pass