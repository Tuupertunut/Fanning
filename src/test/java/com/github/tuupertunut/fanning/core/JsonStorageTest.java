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

import com.github.cliftonlabs.json_simple.JsonArray;
import com.github.cliftonlabs.json_simple.JsonException;
import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.tuupertunut.fanning.mockhardware.MockFanController;
import com.github.tuupertunut.fanning.mockhardware.MockHardwareItem;
import com.github.tuupertunut.fanning.mockhardware.MockHardwareManager;
import com.github.tuupertunut.fanning.mockhardware.MockSensor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Tuupertunut
 */
public class JsonStorageTest {

    MockHardwareManager hwManager;
    JsonStorage storage;

    @Before
    public void setUp() {
        MockSensor sct1 = new MockSensor("fake cpu core1 temp", "sct1", "Temperature", "°C");
        MockHardwareItem hc = new MockHardwareItem(Arrays.asList(), Arrays.asList(sct1), Arrays.asList(), "fake cpu", "hc");

        MockSensor sgp = new MockSensor("fake gpu fan percent", "sgp", "Control", "%");
        MockFanController fg = new MockFanController(sgp, "fg", 0, 100);
        MockHardwareItem hg = new MockHardwareItem(Arrays.asList(), Arrays.asList(sgp), Arrays.asList(fg), "fake gpu", "hg");

        MockHardwareItem root = new MockHardwareItem(Arrays.asList(hc, hg), Arrays.asList(), Arrays.asList(), "computer", "c");

        hwManager = new MockHardwareManager(root);

        storage = new JsonStorage(hwManager, null);
    }

    @Test
    public void testToJson() {
        List<FanCurve> fanCurves = new ArrayList<>();
        fanCurves.add(new FanCurve(hwManager.findSensorById("sct1").get(), hwManager.findFanControllerById("fg").get(), Arrays.asList(new Mapping(5.0, 6.5))));

        /* Constructing the expected json. */
        JsonArray a = new JsonArray();
        JsonObject b = new JsonObject();
        b.put("sensor", "sct1");
        b.put("fanController", "fg");
        JsonArray c = new JsonArray();
        JsonObject d = new JsonObject();
        d.put("key", 5.0);
        d.put("value", 6.5);
        c.add(d);
        b.put("changePoints", c);
        a.add(b);

        Assert.assertEquals(a.toJson(), storage.toJson(fanCurves));
    }

    @Test
    public void testFromJson() throws JsonException {
        String json = "[{\"sensor\":\"sct1\",\"fanController\":\"fg\",\"changePoints\":[{\"key\":5.0,\"value\":6.5}]}]";
        List<FanCurve> fanCurves = storage.fromJson(json);

        Assert.assertEquals(1, fanCurves.size());
        Assert.assertEquals("sct1", fanCurves.get(0).getSensor().getId());
        Assert.assertEquals("fg", fanCurves.get(0).getFanController().getId());
        Assert.assertEquals(1, fanCurves.get(0).changePointsProperty().size());
        Assert.assertEquals(5.0, fanCurves.get(0).changePointsProperty().get(0).key, 0);
        Assert.assertEquals(6.5, fanCurves.get(0).changePointsProperty().get(0).value, 0);
    }

    @Test
    public void testFromJsonWithInvalidJson() {
        String json = "[";
        try {
            List<FanCurve> fanCurves = storage.fromJson(json);

            /* Fail if there was no exception. */
            Assert.fail();
        } catch (JsonException ex) {
            /* This should happen. */
        }
    }
}
