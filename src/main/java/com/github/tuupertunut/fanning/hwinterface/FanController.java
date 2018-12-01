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

import java.util.OptionalDouble;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ObservableDoubleValue;

/**
 *
 * @author Tuupertunut
 */
public interface FanController extends HardwareTreeElement {

    Sensor getSensor();

    @Override
    default String getName() {
        return getSensor().getName();
    }

    default String getSensorId() {
        return getSensor().getId();
    }

    default String getSensorType() {
        return getSensor().getSensorType();
    }

    default String getMeasurementUnit() {
        return getSensor().getMeasurementUnit();
    }

    default ObservableDoubleValue measuredValueProperty() {
        return getSensor().valueProperty();
    }

    ObjectProperty<OptionalDouble> controlledValueProperty();

    double getMinControlledValue();

    double getMaxControlledValue();
}