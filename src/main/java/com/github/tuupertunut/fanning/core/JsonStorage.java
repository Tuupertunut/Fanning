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
import com.github.cliftonlabs.json_simple.Jsoner;
import com.github.tuupertunut.fanning.hwinterface.FanController;
import com.github.tuupertunut.fanning.hwinterface.HardwareManager;
import com.github.tuupertunut.fanning.hwinterface.Sensor;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Tuupertunut
 */
public class JsonStorage implements Storage {

    private final HardwareManager hwManager;
    private final Path filePath;

    public JsonStorage(HardwareManager hwManager, Path filePath) {
        this.hwManager = hwManager;
        this.filePath = filePath;
    }

    @Override
    public List<FanCurve> load() throws IOException, JsonException {
        if (Files.notExists(filePath)) {
            return Arrays.asList();
        }

        JsonArray jsonFanCurves;
        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            jsonFanCurves = (JsonArray) Jsoner.deserialize(reader);
        }

        List<FanCurve> fanCurves = new ArrayList<>();
        for (Object jsonFanCurveObj : jsonFanCurves) {
            JsonObject jsonFanCurve = (JsonObject) jsonFanCurveObj;

            Sensor sensor = hwManager.findSensorById((String) jsonFanCurve.get("sensor")).get();
            FanController fanController = hwManager.findFanControllerById((String) jsonFanCurve.get("fanController")).get();

            List<Mapping> changePoints = new ArrayList<>();
            for (Object jsonMappingObj : (JsonArray) jsonFanCurve.get("changePoints")) {
                JsonObject jsonMapping = (JsonObject) jsonMappingObj;

                double key = (double) jsonMapping.get("key");
                double value = (double) jsonMapping.get("value");

                changePoints.add(new Mapping(key, value));
            }

            fanCurves.add(new FanCurve(sensor, fanController, changePoints));
        }
        return fanCurves;
    }

    @Override
    public void store(List<FanCurve> fanCurves) throws IOException {
        JsonArray jsonFanCurves = new JsonArray();
        for (FanCurve fanCurve : fanCurves) {
            JsonObject jsonFanCurve = new JsonObject();

            jsonFanCurve.put("sensor", fanCurve.getSensor().getId());
            jsonFanCurve.put("fanController", fanCurve.getFanController().getId());

            JsonArray jsonChangePoints = new JsonArray();
            for (Mapping mapping : fanCurve.changePointsProperty()) {
                JsonObject jsonMapping = new JsonObject();

                jsonMapping.put("key", mapping.key);
                jsonMapping.put("value", mapping.value);

                jsonChangePoints.add(jsonMapping);
            }
            jsonFanCurve.put("changePoints", jsonChangePoints);

            jsonFanCurves.add(jsonFanCurve);
        }

        try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
            Jsoner.serialize(jsonFanCurves, writer);
        }
    }
}
