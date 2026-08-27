@echo off
echo ===================================================
echo Building StreamDeck2027 Standalone Executable...
echo ===================================================

:: 1. Run the optimal PyInstaller command
pyinstaller --onefile --windowed --name="StreamDeck2027" ^
--collect-all resvg-py ^
--collect-submodules StreamDeck ^
--hidden-import ntcore ^
--add-data "assets/fonts;assets/fonts" ^
--add-data "assets/images;assets/images" ^
--add-binary "assets/dlls;assets/dlls" ^
src/main.py

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
