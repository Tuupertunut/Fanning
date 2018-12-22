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
 * A fan controller for controlling a certain fan in the hardware. A fan
 * controller is always associated with a sensor, which measures the value of
 * the controllable attribute (such as speed percentage) of the fan.
 *
 * @author Tuupertunut
 */
public interface FanController extends HardwareTreeElement {

    /**
     * Returns the associated sensor of this fan controller.
     *
     * @return the associated sensor of this fan controller.
     */
    Sensor getSensor();

    /**
     * Returns the name of the fan controller. The default implementation uses
     * the name of the associated sensor.
     *
     * @return the name of the fan controller.
     */
    @Override
    default String getName() {
        return getSensor().getName();
    }

    /**
     * Returns the id of the associated sensor.
     *
     * @return the id of the associated sensor.
     */
    default String getSensorId() {
        return getSensor().getId();
    }

    /**
     * Returns the type of the associated sensor.
     *
     * @return the type of the associated sensor.
     */
    default String getSensorType() {
        return getSensor().getSensorType();
    }

    /**
     * Returns the measurement unit of the associated sensor.
     *
     * @return the measurement unit of the associated sensor.
     */
    default String getMeasurementUnit() {
        return getSensor().getMeasurementUnit();
    }

    /**
     * The observable box of the value of the associated sensor. This value is
     * different from the controlled value in that this is measured from the
     * hardware. If the fan controller is being controlled, this usually follows
     * the controlled value with a small delay.
     *
     * @return the observable box of the value of the associated sensor.
     */
    default ObservableDoubleValue measuredValueProperty() {
        return getSensor().valueProperty();
    }

    /**
     * The property representing which value the fan controller is currently set
     * to. If the value is empty, it means this fan is not controlled. Setting a
     * value to this property will control the fan.
     *
     * @return the property of the controlled value.
     */
    ObjectProperty<OptionalDouble> controlledValueProperty();

    /**
     * Returns the minimum value this fan controller can be set to.
     *
     * @return the minimum value this fan controller can be set to.
     */
    double getMinControlledValue();

    /**
     * Returns the maximum value this fan controller can be set to.
     *
     * @return the maximum value this fan controller can be set to.
     */
    double getMaxControlledValue();
}
