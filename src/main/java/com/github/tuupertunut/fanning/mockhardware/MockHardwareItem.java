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

import com.github.tuupertunut.fanning.hwinterface.FanController;
import com.github.tuupertunut.fanning.hwinterface.HardwareItem;
import com.github.tuupertunut.fanning.hwinterface.Sensor;
import java.util.List;

/**
 *
 * @author Tuupertunut
 */
public class MockHardwareItem implements HardwareItem {

    List<HardwareItem> subHardware;
    List<Sensor> sensors;
    List<FanController> fans;
    String name;
    String id;

    public MockHardwareItem(List<HardwareItem> subHardware, List<Sensor> sensors, List<FanController> fans, String name, String id) {
        this.subHardware = subHardware;
        this.sensors = sensors;
        this.fans = fans;
        this.name = name;
        this.id = id;
    }

    @Override
    public List<HardwareItem> getSubHardware() {
        return subHardware;
    }

    @Override
    public List<Sensor> getSensors() {
        return sensors;
    }

    @Override
    public List<FanController> getFanControllers() {
        return fans;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getId() {
        return id;
    }
}
