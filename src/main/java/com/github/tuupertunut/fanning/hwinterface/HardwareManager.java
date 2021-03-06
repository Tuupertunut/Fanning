/*
 * The MIT License
 *
 * Copyright 2018 Tuupertunut.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.github.tuupertunut.fanning.hwinterface;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Optional;

/**
 * A service for using the hardware.
 *
 * @author Tuupertunut
 */
public interface HardwareManager {

    /**
     * Fetches and updates new values to all sensors.
     */
    void updateHardwareTree();

    /**
     * Returns the root of the hardware tree.
     *
     * @return the root of the hardware tree.
     */
    HardwareItem getHardwareRoot();

    /**
     * Returns a list of all sensors in the hardware tree. The sensors are in
     * depth-first order.
     *
     * @return all sensors in the hardware tree.
     */
    default List<Sensor> getAllSensors() {
        List<Sensor> sensors = new ArrayList<>();

        /* Depth first search for tree using a stack. */
        Deque<HardwareItem> depthFirstStack = new ArrayDeque<>();
        depthFirstStack.push(getHardwareRoot());

        while (!depthFirstStack.isEmpty()) {

            /* Pop a node from the stack and process it. */
            HardwareItem hw = depthFirstStack.pop();
            sensors.addAll(hw.getSensors());

            /* Add children of the node to the stack. */
            for (int i = hw.getSubHardware().size() - 1; i >= 0; i--) {
                HardwareItem subHw = hw.getSubHardware().get(i);
                depthFirstStack.push(subHw);
            }
        }

        return sensors;
    }

    /**
     * Returns a list of all fan controllers in the hardware tree. The fan
     * controllers are in depth-first order.
     *
     * @return all fan controllers in the hardware tree.
     */
    default List<FanController> getAllFanControllers() {
        List<FanController> fans = new ArrayList<>();

        /* Depth first search for tree using a stack. */
        Deque<HardwareItem> depthFirstStack = new ArrayDeque<>();
        depthFirstStack.push(getHardwareRoot());

        while (!depthFirstStack.isEmpty()) {

            /* Pop a node from the stack and process it. */
            HardwareItem hw = depthFirstStack.pop();
            fans.addAll(hw.getFanControllers());

            /* Add children of the node to the stack. */
            for (int i = hw.getSubHardware().size() - 1; i >= 0; i--) {
                HardwareItem subHw = hw.getSubHardware().get(i);
                depthFirstStack.push(subHw);
            }
        }

        return fans;
    }

    /**
     * Returns a list of all hardware in the hardware tree. The hardware are in
     * depth-first order.
     *
     * @return all hardware in the hardware tree.
     */
    default List<HardwareItem> getAllHardware() {
        List<HardwareItem> hardware = new ArrayList<>();

        /* Depth first search for tree using a stack. */
        Deque<HardwareItem> depthFirstStack = new ArrayDeque<>();
        depthFirstStack.push(getHardwareRoot());

        while (!depthFirstStack.isEmpty()) {

            /* Pop a node from the stack and process it. */
            HardwareItem hw = depthFirstStack.pop();
            hardware.add(hw);

            /* Add children of the node to the stack. */
            for (int i = hw.getSubHardware().size() - 1; i >= 0; i--) {
                HardwareItem subHw = hw.getSubHardware().get(i);
                depthFirstStack.push(subHw);
            }
        }

        return hardware;
    }

    /**
     * Returns the sensor that has the given id, if there is one.
     *
     * @param sensorId
     * @return the sensor with the id, or empty if there is none.
     */
    default Optional<Sensor> findSensorById(String sensorId) {
        for (Sensor sensor : getAllSensors()) {
            if (sensor.getId().equals(sensorId)) {
                return Optional.of(sensor);
            }
        }
        return Optional.empty();
    }

    /**
     * Returns the fan controller that has the given id, if there is one.
     *
     * @param fanControllerId
     * @return the fan controller with the id, or empty if there is none.
     */
    default Optional<FanController> findFanControllerById(String fanControllerId) {
        for (FanController fan : getAllFanControllers()) {
            if (fan.getId().equals(fanControllerId)) {
                return Optional.of(fan);
            }
        }
        return Optional.empty();
    }
}
