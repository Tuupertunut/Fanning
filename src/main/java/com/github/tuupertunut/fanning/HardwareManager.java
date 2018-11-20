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
package com.github.tuupertunut.fanning;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author Tuupertunut
 */
public interface HardwareManager {

    void updateHardwareTree();

    Optional<HardwareItem> getHardwareRoot();

    default List<Sensor> getAllSensors() {
        List<Sensor> sensors = new ArrayList<>();

        if (getHardwareRoot().isPresent()) {
            HardwareItem hardwareRoot = getHardwareRoot().get();

            /* Depth first search for tree using a stack. */
            Deque<HardwareItem> depthFirstStack = new ArrayDeque<>();
            depthFirstStack.push(hardwareRoot);

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
        }

        return sensors;
    }

    default List<Control> getAllControls() {
        List<Control> controls = new ArrayList<>();

        if (getHardwareRoot().isPresent()) {
            HardwareItem hardwareRoot = getHardwareRoot().get();

            /* Depth first search for tree using a stack. */
            Deque<HardwareItem> depthFirstStack = new ArrayDeque<>();
            depthFirstStack.push(hardwareRoot);

            while (!depthFirstStack.isEmpty()) {

                /* Pop a node from the stack and process it. */
                HardwareItem hw = depthFirstStack.pop();
                controls.addAll(hw.getControls());

                /* Add children of the node to the stack. */
                for (int i = hw.getSubHardware().size() - 1; i >= 0; i--) {
                    HardwareItem subHw = hw.getSubHardware().get(i);
                    depthFirstStack.push(subHw);
                }
            }
        }

        return controls;
    }

    default List<HardwareItem> getAllHardware() {
        List<HardwareItem> hardware = new ArrayList<>();

        if (getHardwareRoot().isPresent()) {
            HardwareItem hardwareRoot = getHardwareRoot().get();

            /* Depth first search for tree using a stack. */
            Deque<HardwareItem> depthFirstStack = new ArrayDeque<>();
            depthFirstStack.push(hardwareRoot);

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
        }

        return hardware;
    }
}
