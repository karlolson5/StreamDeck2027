
from collections.abc import Callable

from StreamDeck.Devices.StreamDeck import StreamDeck
from StreamDeck.Transport.Transport import TransportError
from ntcore import NetworkTable
import constants as c
from StreamDeck.ImageHelpers import PILHelper
from controller.stream_deck_button import ButtonConfig, StreamDeckButton
from PIL import Image, ImageOps

class StreamDeckController:
    def __init__(self, deck: StreamDeck, button_suppliers: dict[int, tuple[Callable[[],ButtonConfig], Callable[[],bool]]]):
        self._deck: StreamDeck = deck
        self.num_rows, self.num_cols = self._deck.key_layout
        self.num_buttons = self._deck.key_count
        self._default_background = self.generate_key_images_from_deck_sized_image(c.BACKGROUND_IMAGE)
        self.buttons: dict[int, StreamDeckButton] = {}
        for index, suppliers in button_suppliers:
            self.buttons[index] = StreamDeckButton(self, index, str(index), suppliers[0], suppliers[1])
        self._remote_connected = False
        self.table: NetworkTable = c.NT_INSTANCE.getTable("StreamDeck")

    def __enter__(self):
        self.open()

    def __exit__(self, *_):
        try:
            self.close()
        except TransportError:
            pass

    def __repr__(self):
        return f'{self._deck.deck_type()} (sn: {self._deck.get_serial_number()}, fw: {self._deck.get_firmware_version()})'

    def open(self):
        self._deck.open()
        print(f'Opened {self}')
        self._deck.set_brightness(c.BRIGHTNESS)
        self._deck.set_key_callback(self.on_key_change)
        self.update()

    def close(self):
        self.close_deck()

    def close_deck(self):
        if self._deck.is_open():
            self.render_default_background()
            self._deck.close()
            print(f'Closed {self}')

    def generate_key_images_from_deck_sized_image(self, image_filename: str) -> dict[int, bytes]:
        image = self.create_full_deck_sized_image(image_filename)
        images = dict()
        for k in range(self._deck.key_count()):
            images[k] = self.crop_key_image_from_deck_sized_image(image, k)
        return images

    def create_full_deck_sized_image(self, image_filename: str) -> bytes:
        """Generates an image that is correctly sized to fit across all keys"""
        key_rows, key_cols = self._deck.key_layout()
        key_width, key_height = self._deck.key_image_format()["size"]
        spacing_x, spacing_y = c.KEY_SPACING

        key_width *= key_cols
        key_height *= key_rows

        spacing_x *= key_cols - 1
        spacing_y *= key_rows - 1

        full_deck_image_size = (key_width + spacing_x, key_height + spacing_y)

        # Create a filled version of the image in the correct aspect ratio and then resize it to fit the full deck
        foreground = Image.open(os.path.join(self._assets_path, image_filename)).convert("RGBA")
        image = Image.new(
            "RGBA",
            (
                foreground.height * full_deck_image_size[0] // full_deck_image_size[1],
                foreground.height,
            ),
            color=c.COLORS.DEFAULT_BACKGROUND,
        )
        image.paste(
            foreground,
            ((image.width - foreground.width) // 2, 0),
            foreground,
        )

        return ImageOps.fit(
            image,
            full_deck_image_size,
            Image.Resampling.LANCZOS,
        )
    
    def crop_key_image_from_deck_sized_image(self, image: bytes, index: int) -> bytes:
        """Crops out a key-sized image from a larger deck-sized image"""
        _, key_cols = self._deck.key_layout()
        key_width, key_height = self._deck.key_image_format()["size"]
        spacing_x, spacing_y = c.KEY_SPACING

        row = index // key_cols
        col = index % key_cols

        start_x = col * (key_width + spacing_x)
        start_y = row * (key_height + spacing_y)

        region = (start_x, start_y, start_x + key_width, start_y + key_height)
        segment = image.crop(region)

        key_image = PILHelper.create_key_image(self._deck)
        key_image.paste(segment)

        return PILHelper.to_native_key_format(self._deck, key_image)

    def render_multi_key_image(self, images: dict[int, bytes]):
        for index, image in images:
            self.buttons[index].render_key_image(image)

    def render_default_background(self):
        self.render_multi_key_image(self._default_background)

    def update(self):
        self._remote_connected = c.NT_INSTANCE.isConnected()
        
        if not self._remote_connected:
            self.render_default_background()
            return

        for b in self.buttons.values():
            b.update()
            