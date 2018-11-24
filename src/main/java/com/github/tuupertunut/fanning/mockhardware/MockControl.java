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

import com.github.tuupertunut.fanning.hwinterface.Control;
import com.github.tuupertunut.fanning.hwinterface.Sensor;
import java.util.OptionalDouble;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 *
 * @author Tuupertunut
 */
public class MockControl implements Control {

    Sensor sensor;
    String id;
    double minControlledValue;
    double maxControlledValue;
    ObjectProperty<OptionalDouble> controlledValue;

    public MockControl(Sensor sensor, String id, double minControlledValue, double maxControlledValue) {
        this.sensor = sensor;
        this.id = id;
        this.minControlledValue = minControlledValue;
        this.maxControlledValue = maxControlledValue;

        controlledValue = new SimpleObjectProperty<>(OptionalDouble.empty());
    }

    @Override
    public Sensor getSensor() {
        return sensor;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public double getMinControlledValue() {
        return minControlledValue;
    }

    @Override
    public double getMaxControlledValue() {
        return maxControlledValue;
    }

    @Override
    public ObjectProperty<OptionalDouble> controlledValueProperty() {
        return controlledValue;
    }
}
