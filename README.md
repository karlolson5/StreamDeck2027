# FRC Stream Deck Controller
Driver Station side code for using a Stream Deck XL as an FRC Robot Controller via NetworkTables4 communication. See https://github.com/karlolson5/OrangeLib/tree/main/2027/src/main/java/first/lib/util/Controls/StreamDeck *[currently private]* for the robot side code.

## Install
On Windows, simply download StreamDeck2027-Windows.exe from the [latest release](https://github.com/karlolson5/StreamDeck2027/releases).

On MacOS, download StreamDeck2027-Mac.tar.gz from the [latest release](https://github.com/karlolson5/StreamDeck2027/releases), then extract the file, then run `xattr -d com.apple.quarantine StreamDeck2027-Mac.app` in the folder containing the app, or right click, press Open, click Done when you get a security warning, then go to System Settings > Privacy & Security and find the notification and press Open Anyway. From then on, you can run it as normal.

On MacOS, download StreamDeck2027-Linux.tar.gz from the [latest release](https://github.com/karlolson5/StreamDeck2027/releases), then extract the file.

## Building the Code
To build the executable from source, see the `scripts` folder.

## Operation
### Connecting a Stream Deck
On launch, the program will continuously search for a physical Stream Deck device connected by USB. Once connected, it will stop searching. This program is only compatible with one Stream Deck connected, and will not find or use a second connected Stream Deck (until the first is disconnected).

### Simulated Stream Deck
If you press the "Start Simulated Stream Deck" button, a second window that acts as a simulated Stream Deck will open, and the program will use that, even if you plug in a physical Stream Deck after. If you close the Simulated Stream Deck window, the program will return to searching for a physical Stream Deck.

### Network Target
By default, the network target is 10.34.76.2, the robot controller IP address for FRC Team 3476 Code Orange. If you check the "Target simulated robot" checkbox, the network target will change to 127.0.0.1, the default local network for simulated FRC robots using WPILib simulation. Use the "Settings..." button to change any of these. Typically, you will want to change the Robot IP address to the [10.TE.AM.2 format](https://docs.wpilib.org/en/stable/docs/networking/networking-introduction/ip-configurations.html).
