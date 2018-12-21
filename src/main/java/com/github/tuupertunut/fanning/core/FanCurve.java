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

import com.github.tuupertunut.fanning.hwinterface.FanController;
import com.github.tuupertunut.fanning.hwinterface.Sensor;
import java.util.List;
import java.util.OptionalDouble;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;

/**
 *
 * @author Tuupertunut
 */
public class FanCurve {

    private final Sensor sensor;
    private final FanController fanController;
    private final ListProperty<Mapping> changePoints;

    public FanCurve(Sensor sensor, FanController fanController, List<Mapping> changePoints) {
        this.sensor = sensor;
        this.fanController = fanController;
        this.changePoints = new SimpleListProperty<>(FXCollections.observableArrayList(changePoints));
    }

    public Sensor getSensor() {
        return sensor;
    }

    public FanController getFanController() {
        return fanController;
    }

    public ListProperty<Mapping> changePointsProperty() {
        return changePoints;
    }

    public OptionalDouble getFanValueAt(double sensorValue) {
        Mapping closestBelow = null;
        Mapping smallest = null;
        for (Mapping m : changePoints) {
            if (m.key <= sensorValue && (closestBelow == null || m.key > closestBelow.key)) {
                closestBelow = m;
            }
            if (smallest == null || m.key < smallest.key) {
                smallest = m;
            }
        }
        if (closestBelow != null) {
            return OptionalDouble.of(closestBelow.value);
        } else if (smallest != null) {
            return OptionalDouble.of(smallest.value);
        } else {
            return OptionalDouble.empty();
        }
    }
}
