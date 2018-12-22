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
import com.github.tuupertunut.fanning.hwinterface.FanController;
import com.github.tuupertunut.fanning.hwinterface.HardwareManager;
import java.io.IOException;
import java.time.Duration;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;

/**
 * The entry point to the software. Controls fans based on the fan curves.
 *
 * @author Tuupertunut
 */
public class FanningService {

    private final HardwareManager hardwareManager;
    private final Storage storage;
    private final ListProperty<FanCurve> fanCurves;

    /**
     * Creates a new FanningService.
     *
     * @param hardwareManager the service for using the hardware.
     * @param storage the service for permanently storing the fan curves.
     */
    public FanningService(HardwareManager hardwareManager, Storage storage) {
        this.hardwareManager = hardwareManager;
        this.storage = storage;
        this.fanCurves = new SimpleListProperty<>(FXCollections.observableArrayList());
    }

    /**
     * Loads fan curves from the storage.
     *
     * @throws IOException
     * @throws JsonException
     */
    public void loadFromStorage() throws IOException, JsonException {
        fanCurves.setAll(storage.load());
    }

    /**
     * Stores the fan curves into the storage.
     *
     * @throws IOException
     */
    public void storeToStorage() throws IOException {
        storage.store(fanCurves);
    }

    /**
     * Starts the update loop at the given rate. The update loop will query the
     * hardware for new sensor data and control fans based on the fan curves.
     *
     * @param updateRate how often to update.
     */
    public void initUpdater(Duration updateRate) {
        /* Making a daemon thread, so it will automatically die when the main
         * thread dies. */
        Executors.newSingleThreadScheduledExecutor((Runnable r) -> {
            Thread thread = Executors.defaultThreadFactory().newThread(r);
            thread.setDaemon(true);
            return thread;
        }).scheduleAtFixedRate(this::update, 0, updateRate.toNanos(), TimeUnit.NANOSECONDS);
    }

    void update() {
        hardwareManager.updateHardwareTree();
        for (FanController fan : hardwareManager.getAllFanControllers()) {
            Optional<FanCurve> optFanCurve = findCurveOfFan(fan);
            if (optFanCurve.isPresent()) {
                FanCurve fanCurve = optFanCurve.get();
                double sensorValue = fanCurve.getSensor().valueProperty().get();
                fan.controlledValueProperty().set(fanCurve.getFanValueAt(sensorValue));
            } else {
                fan.controlledValueProperty().set(OptionalDouble.empty());
            }
        }
    }

    public HardwareManager getHardwareManager() {
        return hardwareManager;
    }

    public ListProperty<FanCurve> fanCurvesProperty() {
        return fanCurves;
    }

    /**
     * Returns the fan curve which controls the given fan, if there is one.
     *
     * @param fan
     * @return the fan curve which controls the fan, or empty if there is none.
     */
    public Optional<FanCurve> findCurveOfFan(FanController fan) {
        for (FanCurve curve : fanCurves) {
            if (curve.getFanController().equals(fan)) {
                return Optional.of(curve);
            }
        }
        return Optional.empty();
    }
}
