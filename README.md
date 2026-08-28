Driver Station side code for using a Stream Deck XL as an FRC Robot Controller. See https://github.com/karlolson5/OrangeLib/tree/main/2027/src/main/java/first/lib/util/Controls/StreamDeck for the robot side code.

Build with (Windows)
```bash
pyinstaller --onefile --windowed --name="StreamDeck2027" \
--collect-all resvg-py \
--collect-submodules StreamDeck \
--hidden-import ntcore \
--add-data "assets/fonts;assets/fonts" \
--add-data "assets/images;assets/images" \
--add-binary "assets/dlls;assets/dlls" \
src/main.py
```
or (MacOS/Linux)

```bash
pyinstaller --onefile --windowed --name="StreamDeck2027" \
--collect-all resvg-py \
--collect-submodules StreamDeck \
--hidden-import ntcore \
--add-data "assets/fonts:assets/fonts" \
--add-data "assets/images:assets/images" \
--add-binary "assets/dlls:assets/dlls" \
src/main.py
```