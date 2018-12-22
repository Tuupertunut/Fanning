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
package com.github.tuupertunut.fanning.core;

import com.github.tuupertunut.fanning.mockhardware.MockFanController;
import com.github.tuupertunut.fanning.mockhardware.MockHardwareItem;
import com.github.tuupertunut.fanning.mockhardware.MockHardwareManager;
import com.github.tuupertunut.fanning.mockhardware.MockSensor;
import java.util.Arrays;
import java.util.OptionalDouble;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Tuupertunut
 */
public class FanCurveTest {

    MockHardwareManager hwManager;
    FanCurve fanCurve;

    @Before
    public void setUp() {
        MockSensor sct1 = new MockSensor("fake cpu core1 temp", "sct1", "Temperature", "°C");
        MockHardwareItem hc = new MockHardwareItem(Arrays.asList(), Arrays.asList(sct1), Arrays.asList(), "fake cpu", "hc");

        MockSensor sgp = new MockSensor("fake gpu fan percent", "sgp", "Control", "%");
        MockFanController fg = new MockFanController(sgp, "fg", 0, 100);
        MockHardwareItem hg = new MockHardwareItem(Arrays.asList(), Arrays.asList(sgp), Arrays.asList(fg), "fake gpu", "hg");

        MockHardwareItem root = new MockHardwareItem(Arrays.asList(hc, hg), Arrays.asList(), Arrays.asList(), "computer", "c");

        hwManager = new MockHardwareManager(root);

        fanCurve = new FanCurve(hwManager.findSensorById("sct1").get(), hwManager.findFanControllerById("fg").get(), Arrays.asList(new Mapping(5.0, 6.5), new Mapping(8.0, 10)));
    }

    @Test
    public void testGetFanValueAtBeforeFirstChangePoint() {
        Assert.assertEquals(6.5, fanCurve.getFanValueAt(3).getAsDouble(), 0);
    }

    @Test
    public void testGetFanValueAtAfterChangePoint() {
        Assert.assertEquals(6.5, fanCurve.getFanValueAt(6).getAsDouble(), 0);
    }

    @Test
    public void testGetFanValueAtAfterLastChangePoint() {
        Assert.assertEquals(10, fanCurve.getFanValueAt(9).getAsDouble(), 0);
    }

    @Test
    public void testGetFanValueAtOnEmptyCurve() {
        fanCurve.changePointsProperty().clear();
        Assert.assertEquals(OptionalDouble.empty(), fanCurve.getFanValueAt(5));
    }
}
