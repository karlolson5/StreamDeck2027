#!/bin/bash

echo "==================================================="
echo "Getting hidapi library..."
echo "==================================================="

brew install hidapi
mkdir -p assets/dlls
cp "$(brew --prefix hidapi)/lib/libhidapi.dylib" assets/dlls/

echo "==================================================="
echo "Building StreamDeck2027 Standalone Executable..."
echo "==================================================="

# 1. Run the optimal PyInstaller command
pyinstaller --onefile --windowed --name="StreamDeck2027-Mac" \
--paths=src \
--collect-all resvg-py --collect-submodules StreamDeck --collect-all ntcore \
--hidden-import controller.stream_deck_controller \
--hidden-import controller.stream_deck_button \
--hidden-import network.stream_deck_config_subscriber \
--hidden-import network.stream_deck_publisher \
--hidden-import network.base.output_publisher \
--hidden-import sim.sim_stream_deck \
--hidden-import util.utilities \
--add-data "assets/fonts:assets/fonts" \
--add-data "assets/images:assets/images" \
--add-binary "assets/dlls/libhidapi.dylib:assets/dlls" src/main.py

# 2. Check if compilation was successful before clearing build files
if [ $? -eq 0 ]; then
    echo ""
    echo "==================================================="
    echo "Cleaning up temporary build artifacts..."
    echo "==================================================="
    
    # Remove temporary build directory and specs
    rm -rf build
    rm -f StreamDeck2027.spec
    
    echo ""
    echo "SUCCESS! Your executable is ready in: dist/StreamDeck2027"
else
    echo ""
    echo "ERROR: PyInstaller compilation failed. Keeping build logs for debugging."
fi
