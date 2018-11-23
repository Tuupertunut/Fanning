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
package com.github.tuupertunut.fanning.mockhardware;

import com.github.tuupertunut.fanning.hwinterface.HardwareItem;
import com.github.tuupertunut.fanning.hwinterface.HardwareManager;
import com.github.tuupertunut.fanning.hwinterface.Sensor;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

/**
 *
 * @author Tuupertunut
 */
public class MockHardwareManager implements HardwareManager {

    Optional<HardwareItem> hwRoot;

    public MockHardwareManager(Optional<HardwareItem> hwRoot) {
        this.hwRoot = hwRoot;

        updateHardwareTree();
    }

    public MockHardwareManager() {
        MockSensor sa = new MockSensor("sensor a", "sa", "Temperature", "°C");
        MockSensor sb = new MockSensor("sensor b", "sb", "Temperature", "°C");
        MockSensor sc = new MockSensor("sensor c", "sc", "Voltage", "V");
        MockHardwareItem ha = new MockHardwareItem(Arrays.asList(), Arrays.asList(sa, sb, sc), Arrays.asList(), "hardware a", "ha");

        MockSensor sd = new MockSensor("sensor d", "sd", "Fan speed", "RPM");
        MockSensor se = new MockSensor("sensor e", "se", "Voltage", "V");
        MockHardwareItem hb = new MockHardwareItem(Arrays.asList(), Arrays.asList(sd, se), Arrays.asList(), "hardware b", "hb");

        HardwareItem root = new MockHardwareItem(Arrays.asList(ha, hb), Arrays.asList(), Arrays.asList(), "computer", "c");

        hwRoot = Optional.of(root);

        updateHardwareTree();
    }

    @Override
    public void updateHardwareTree() {
        for (Sensor sensor : getAllSensors()) {
            ((MockSensor) sensor).value.set(ThreadLocalRandom.current().nextInt(30, 50));
        }
    }

    @Override
    public Optional<HardwareItem> getHardwareRoot() {
        return hwRoot;
    }
}
