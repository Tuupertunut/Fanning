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

import com.github.cliftonlabs.json_simple.JsonException;
import com.github.tuupertunut.fanning.mockhardware.MockFanController;
import com.github.tuupertunut.fanning.mockhardware.MockHardwareItem;
import com.github.tuupertunut.fanning.mockhardware.MockHardwareManager;
import com.github.tuupertunut.fanning.mockhardware.MockSensor;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.OptionalDouble;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Tuupertunut
 */
public class FanningServiceTest {

    MockHardwareManager hwManager;
    MockSensor sct1;
    MockFanController fg;
    FanningService fanningService;

    @Before
    public void setUp() {
        sct1 = new MockSensor("fake cpu core1 temp", "sct1", "Temperature", "°C");
        MockHardwareItem hc = new MockHardwareItem(Arrays.asList(), Arrays.asList(sct1), Arrays.asList(), "fake cpu", "hc");

        MockSensor sgp = new MockSensor("fake gpu fan percent", "sgp", "Control", "%");
        fg = new MockFanController(sgp, "fg", 0, 100);
        MockHardwareItem hg = new MockHardwareItem(Arrays.asList(), Arrays.asList(sgp), Arrays.asList(fg), "fake gpu", "hg");

        MockHardwareItem root = new MockHardwareItem(Arrays.asList(hc, hg), Arrays.asList(), Arrays.asList(), "computer", "c");

        hwManager = new MockHardwareManager(root);

        /* A mock storage. */
        Storage storage = new Storage() {
            @Override
            public List<FanCurve> load() throws IOException, JsonException {
                return Arrays.asList(new FanCurve(sct1, fg, Arrays.asList(new Mapping(5.0, 6.5))));
            }

            @Override
            public void store(List<FanCurve> fanCurves) throws IOException {
            }
        };

        fanningService = new FanningService(hwManager, storage);
    }

    @Test
    public void testLoadFromStorage() throws IOException, JsonException {
        Assert.assertTrue(fanningService.fanCurvesProperty().isEmpty());
        fanningService.loadFromStorage();
        Assert.assertEquals(1, fanningService.fanCurvesProperty().size());
    }

    @Test
    public void testFindCurveOfFan() throws IOException, JsonException {
        fanningService.loadFromStorage();
        Assert.assertEquals(fanningService.fanCurvesProperty().get(0), fanningService.findCurveOfFan(fg).get());
    }

    @Test
    public void testUpdateMakesFanFollowFanCurve() throws IOException, JsonException {
        fanningService.loadFromStorage();
        Assert.assertEquals(OptionalDouble.empty(), fg.controlledValueProperty().get());
        fanningService.update();
        Assert.assertNotEquals(OptionalDouble.empty(), fg.controlledValueProperty().get());
        fanningService.fanCurvesProperty().clear();
        fanningService.update();
        Assert.assertEquals(OptionalDouble.empty(), fg.controlledValueProperty().get());
    }

    @Test
    public void testUpdateDoesNotMakeFanFollowEmptyFanCurve() throws IOException, JsonException {
        fanningService.fanCurvesProperty().add(new FanCurve(sct1, fg, Arrays.asList()));
        fanningService.update();
        Assert.assertEquals(OptionalDouble.empty(), fg.controlledValueProperty().get());
    }
}
