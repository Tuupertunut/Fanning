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
package com.github.tuupertunut.fanning.gui;

import com.github.tuupertunut.fanning.core.FanningService;
import com.github.tuupertunut.fanning.core.JsonStorage;
import com.github.tuupertunut.fanning.core.Storage;
import com.github.tuupertunut.fanning.hwinterface.HardwareManager;
import com.github.tuupertunut.fanning.mockhardware.MockHardwareManager;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author Tuupertunut
 */
public class Fanning extends Application {

    private FanningService fanningService;

    @Override
    public void init() throws Exception {
        HardwareManager hwManager = new MockHardwareManager();
        Storage jsonStorage = new JsonStorage(hwManager, getPlatformSpecificConfigDir().resolve("Fanning/fanCurves.json"));
        fanningService = new FanningService(hwManager, jsonStorage);
        fanningService.loadFromStorage();
        fanningService.initUpdater(Duration.ofSeconds(1));
    }

    /* Apache commons says this is a valid way to detect the operating system.
     * https://github.com/apache/commons-lang/blob/LANG_3_7/src/main/java/org/apache/commons/lang3/SystemUtils.java */
    private Path getPlatformSpecificConfigDir() {
        if (System.getProperty("os.name").startsWith("Windows")) {
            return Paths.get(System.getProperty("user.home")).resolve("AppData/Local");
        } else if (System.getProperty("os.name").startsWith("Mac OS X")) {
            return Paths.get(System.getProperty("user.home")).resolve("Library/Application Support");
        } else {
            return Paths.get(System.getProperty("user.home")).resolve(".config");
        }
    }

    @Override
    public void start(Stage stage) throws Exception {
        FanningPane root = new FanningPane(fanningService);

        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
