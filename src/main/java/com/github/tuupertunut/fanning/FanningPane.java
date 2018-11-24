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

import com.github.tuupertunut.fanning.hwinterface.HardwareItem;
import com.github.tuupertunut.fanning.hwinterface.HardwareManager;
import com.github.tuupertunut.fanning.hwinterface.HardwareTreeElement;
import com.github.tuupertunut.fanning.hwinterface.Sensor;
import com.github.tuupertunut.fanning.mockhardware.MockHardwareManager;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.layout.AnchorPane;

/**
 *
 * @author Tuupertunut
 */
public class FanningPane extends AnchorPane {

    @FXML
    private TreeTableView<HardwareTreeElement> sensorTreeTable;

    public FanningPane() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FanningPane.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    @FXML
    private void initialize() {
        TreeTableColumn<HardwareTreeElement, String> sensorNameColumn = new TreeTableColumn<>("Sensor");
        sensorNameColumn.setPrefWidth(200);
        sensorNameColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<HardwareTreeElement, String> data) -> {

            HardwareTreeElement elem = data.getValue().getValue();
            return new ReadOnlyStringWrapper(elem.getName());
        });

        TreeTableColumn<HardwareTreeElement, String> sensorTypeColumn = new TreeTableColumn<>("Type");
        sensorTypeColumn.setPrefWidth(100);
        sensorTypeColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<HardwareTreeElement, String> data) -> {

            HardwareTreeElement elem = data.getValue().getValue();
            if (elem instanceof Sensor) {
                return new ReadOnlyStringWrapper(((Sensor) elem).getSensorType());
            } else {
                return new ReadOnlyStringWrapper("");
            }
        });

        TreeTableColumn<HardwareTreeElement, String> sensorValueColumn = new TreeTableColumn<>("Value");
        sensorValueColumn.setPrefWidth(100);
        sensorValueColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<HardwareTreeElement, String> data) -> {

            HardwareTreeElement elem = data.getValue().getValue();
            if (elem instanceof Sensor) {
                return Bindings.convert(((Sensor) elem).valueProperty()).concat(" " + ((Sensor) elem).getMeasurementUnit());
            } else {
                return new ReadOnlyStringWrapper("");
            }
        });

        sensorTreeTable.getColumns().setAll(sensorNameColumn, sensorTypeColumn, sensorValueColumn);


        HardwareManager hwManager = new MockHardwareManager();
        TreeItem<HardwareTreeElement> root = createTreeTableModel(hwManager.getHardwareRoot().get());

        sensorTreeTable.setRoot(root);

        Executors.newSingleThreadScheduledExecutor((Runnable r) -> {
            Thread thread = Executors.defaultThreadFactory().newThread(r);
            thread.setDaemon(true);
            return thread;
        }).scheduleAtFixedRate(() -> {
            hwManager.updateHardwareTree();
        }, 1, 1, TimeUnit.SECONDS);
    }

    private TreeItem<HardwareTreeElement> createTreeTableModel(HardwareItem hw) {
        TreeItem<HardwareTreeElement> treeItem = new TreeItem<>(hw);

        for (Sensor sensor : hw.getSensors()) {
            treeItem.getChildren().add(new TreeItem<>(sensor));
        }
        for (HardwareItem subHw : hw.getSubHardware()) {
            treeItem.getChildren().add(createTreeTableModel(subHw));
        }

        return treeItem;
    }
}
