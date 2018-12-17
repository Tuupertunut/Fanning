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

import com.github.tuupertunut.fanning.hwinterface.FanController;
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

    HardwareItem hwRoot;

    public MockHardwareManager(HardwareItem hwRoot) {
        this.hwRoot = hwRoot;

        updateHardwareTree();
    }

    public MockHardwareManager() {
        MockSensor sa = new MockSensor("fake cpu core1 temp", "sct1", "Temperature", "°C");
        MockSensor sb = new MockSensor("fake cpu core2 temp", "sct2", "Temperature", "°C");
        MockSensor sc = new MockSensor("fake cpu voltage", "scv", "Voltage", "V");
        MockSensor sd = new MockSensor("fake cpu fan speed", "scf", "Fan speed", "RPM");
        MockSensor se = new MockSensor("fake cpu fan percent", "scp", "Control", "%");
        MockFanController fa = new MockFanController(se, "fc", 30, 50);
        MockHardwareItem ha = new MockHardwareItem(Arrays.asList(), Arrays.asList(sa, sb, sc, sd, se), Arrays.asList(fa), "fake cpu", "hc");

        MockSensor sf = new MockSensor("fake gpu temp", "sgt", "Temperature", "°C");
        MockSensor sg = new MockSensor("fake gpu fan speed", "sgf", "Fan speed", "RPM");
        MockSensor sh = new MockSensor("fake gpu fan percent", "sgp", "Control", "%");
        MockFanController fb = new MockFanController(sh, "fg", 0, 100);
        MockHardwareItem hb = new MockHardwareItem(Arrays.asList(), Arrays.asList(sf, sg, sh), Arrays.asList(fb), "fake gpu", "hg");

        hwRoot = new MockHardwareItem(Arrays.asList(ha, hb), Arrays.asList(), Arrays.asList(), "computer", "c");

        updateHardwareTree();
    }

    @Override
    public void updateHardwareTree() {
        for (Sensor sensor : getAllSensors()) {
            Optional<FanController> sensorFanController = getAllFanControllers().stream().filter((FanController c) -> c.getSensor().equals(sensor)).findFirst();
            if (sensorFanController.isPresent() && sensorFanController.get().controlledValueProperty().get().isPresent()) {
                ((MockSensor) sensor).value.set(sensorFanController.get().controlledValueProperty().get().getAsDouble());
            } else {
                ((MockSensor) sensor).value.set(ThreadLocalRandom.current().nextInt(30, 50));
            }
        }
    }

    @Override
    public HardwareItem getHardwareRoot() {
        return hwRoot;
    }
}
