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
import com.github.tuupertunut.fanning.hwinterface.HardwareManager;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;

/**
 *
 * @author Tuupertunut
 */
public class FanningService {

    private final HardwareManager hardwareManager;
    private final ListProperty<FanCurve> fanCurves;

    public FanningService(HardwareManager hardwareManager, List<FanCurve> fanCurves) {
        this.hardwareManager = hardwareManager;
        this.fanCurves = new SimpleListProperty<>(FXCollections.observableArrayList(fanCurves));

        initUpdater();
    }

    private void initUpdater() {
        Executors.newSingleThreadScheduledExecutor((Runnable r) -> {
            Thread thread = Executors.defaultThreadFactory().newThread(r);
            thread.setDaemon(true);
            return thread;
        }).scheduleAtFixedRate(this::update, 1, 1, TimeUnit.SECONDS);
    }

    private void update() {
        hardwareManager.updateHardwareTree();
    }

    public HardwareManager getHardwareManager() {
        return hardwareManager;
    }

    public ListProperty<FanCurve> fanCurvesProperty() {
        return fanCurves;
    }

    public Optional<FanCurve> findCurveOfFan(FanController fan) {
        for (FanCurve curve : fanCurves) {
            if (curve.getTarget().equals(fan)) {
                return Optional.of(curve);
            }
        }
        return Optional.empty();
    }
}
