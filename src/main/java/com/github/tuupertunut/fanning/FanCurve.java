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

import com.github.tuupertunut.fanning.hwinterface.Control;
import com.github.tuupertunut.fanning.hwinterface.Sensor;
import java.util.NavigableMap;
import java.util.TreeMap;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

/**
 *
 * @author Tuupertunut
 */
public class FanCurve {

    /* In order to use NavigableMap specific methods, the internal
     * implementation must be available. */
    private final NavigableMap<Double, Double> internalChangePointsMap;

    private final Sensor source;
    private final Control target;
    private final ObservableMap<Double, Double> changePoints;

    public FanCurve(Sensor source, Control target) {
        this.source = source;
        this.target = target;

        internalChangePointsMap = new TreeMap<>();
        changePoints = FXCollections.observableMap(internalChangePointsMap);
    }

    public Sensor getSource() {
        return source;
    }

    public Control getTarget() {
        return target;
    }

    public ObservableMap<Double, Double> changePointsProperty() {
        return changePoints;
    }

    public double getTargetValueAt(double sensorValue) {
        Double key = internalChangePointsMap.floorKey(sensorValue);
        if (key == null) {
            key = internalChangePointsMap.firstKey();
        }
        return changePoints.get(key);
    }
}
