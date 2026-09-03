@echo off
echo ===================================================
echo Building StreamDeck2027 Standalone Executable...
echo ===================================================

:: 1. Run the optimal PyInstaller command
pyinstaller --onedir --windowed --name="StreamDeck2027-Windows" --paths=src --icon=assets/icons/app_icon.ico --hidden-import resvg_py --collect-submodules StreamDeck --collect-all ntcore --collect-all wpiutil --collect-all wpinet --collect-all native.ntcore --collect-all native.wpinet --collect-all native.wpiutil --hidden-import controller.stream_deck_controller --hidden-import controller.stream_deck_button --hidden-import network.stream_deck_config_subscriber --hidden-import network.stream_deck_publisher --hidden-import network.base.output_publisher --hidden-import sim.sim_stream_deck --hidden-import util.utilities --add-data "assets/fonts;assets/fonts" --add-data "assets/images;assets/images" --add-data "assets/icons;assets/icons" --add-binary "assets/dlls;assets/dlls" src/main.py

:: 2. Check if compilation was successful before clearing build files
if %ERRORLEVEL% EQU 0 (
    echo.
    echo ===================================================
    echo Cleaning up temporary build artifacts...
    echo ===================================================
    
    :: Remove the temporary build directory
    if exist build rmdir /s /q build
    
    :: Remove the generated specification file
    if exist StreamDeck2027.spec del /q StreamDeck2027.spec
    
    echo.
    echo SUCCESS! Your executable is ready in: dist/StreamDeck2027.exe
) else (
    echo.
    echo ERROR: PyInstaller compilation failed. Keeping build logs for debugging.
)

pause
