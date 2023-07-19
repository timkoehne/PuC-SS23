from pynput.keyboard import Controller, Key
import os, time

time.sleep(10)

with open(os.path.join(os.path.dirname(__file__), 'PuC-SS23/compiler/adt.puc'), 'r') as file:
    lines = file.read().splitlines()
    print(lines)

    keyboard = Controller()
    for i, line in enumerate(lines):
        for char in line:
            keyboard.type(char)
            time.sleep(0.05)
        if i < len(lines)-1:
            keyboard.press(Key.enter)
            keyboard.release(Key.enter)
            time.sleep(0.05)
