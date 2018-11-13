# Requirements specification

## Purpose

The software is a desktop application for automatically controlling computer fan speeds based on values of hardware temperature sensors. It features a GUI for defining mappings from temperatures to fan speeds.

It is NOT a goal to implement a library for reading or writing temperature or fan speed sensors, those already exist.

## Required features

- User can define a mapping from some temperatures to some fan speeds, called a fan curve.

- User can see the fan curve as a chart in the GUI.

- If the hardware has multiple fans and multiple temperature sensors, the user can create multiple fan curves and select which fan is controlled by which temp sensor.

- The fan curves are stored in a file and loaded when the software starts up.

- The software has an interface for attaching sensor libraries in the future by editing the code.

- The software can control fans based on the fan curves if it had an attached sensor library.

## Further development features

- The software can be connected to a real sensor library, such as OpenHardwareMonitorLib for Windows or libsensors for Linux.

- The software can be run in the background, so that it controls the fans, but the GUI is not visible.

- User can define a fan curve that combines data from multiple sensors.

- User can define a delay for reacting to temperature changes, called a hysteresis.

- User can add new sensor libraries as plugins.