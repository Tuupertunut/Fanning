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

import com.github.tuupertunut.fanning.mockhardware.MockFanController;
import com.github.tuupertunut.fanning.mockhardware.MockHardwareItem;
import com.github.tuupertunut.fanning.mockhardware.MockHardwareManager;
import com.github.tuupertunut.fanning.mockhardware.MockSensor;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Tuupertunut
 */
public class HardwareManagerTest {

    MockSensor sgf;
    MockFanController fg;
    MockHardwareManager hwManager;

    @Before
    public void setUp() {
        MockSensor sct1 = new MockSensor("fake cpu core1 temp", "sct1", "Temperature", "°C");
        MockSensor scp = new MockSensor("fake cpu fan percent", "scp", "Control", "%");
        MockFanController fc = new MockFanController(scp, "fc", 30, 50);
        MockHardwareItem hc = new MockHardwareItem(Arrays.asList(), Arrays.asList(sct1, scp), Arrays.asList(fc), "fake cpu", "hc");

        MockSensor sgt = new MockSensor("fake gpu temp", "sgt", "Temperature", "°C");
        sgf = new MockSensor("fake gpu fan speed", "sgf", "Fan speed", "RPM");
        MockSensor sgp = new MockSensor("fake gpu fan percent", "sgp", "Control", "%");
        fg = new MockFanController(sgp, "fg", 0, 100);
        MockHardwareItem hg = new MockHardwareItem(Arrays.asList(), Arrays.asList(sgt, sgf, sgp), Arrays.asList(fg), "fake gpu", "hg");

        MockHardwareItem root = new MockHardwareItem(Arrays.asList(hc, hg), Arrays.asList(), Arrays.asList(), "computer", "c");

        hwManager = new MockHardwareManager(root);
    }

    @Test
    public void testGetAllHardware() {
        Assert.assertArrayEquals(new String[]{"c", "hc", "hg"}, hwManager.getAllHardware().stream().map(HardwareItem::getId).toArray());
    }

    @Test
    public void testGetAllSensors() {
        Assert.assertArrayEquals(new String[]{"sct1", "scp", "sgt", "sgf", "sgp"}, hwManager.getAllSensors().stream().map(Sensor::getId).toArray());
    }

    @Test
    public void testGetAllFanControllers() {
        Assert.assertArrayEquals(new String[]{"fc", "fg"}, hwManager.getAllFanControllers().stream().map(FanController::getId).toArray());
    }

    @Test
    public void testFindSensorById() {
        Assert.assertEquals(sgf, hwManager.findSensorById("sgf").get());
    }

    @Test
    public void testFindFanControllerById() {
        Assert.assertEquals(fg, hwManager.findFanControllerById("fg").get());
    }
}
