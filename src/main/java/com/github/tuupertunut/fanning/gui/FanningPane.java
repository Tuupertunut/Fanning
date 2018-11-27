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
import com.github.tuupertunut.fanning.hwinterface.Control;
import com.github.tuupertunut.fanning.hwinterface.HardwareItem;
import com.github.tuupertunut.fanning.hwinterface.HardwareTreeElement;
import com.github.tuupertunut.fanning.hwinterface.Sensor;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.OptionalDouble;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.layout.AnchorPane;
import org.fxmisc.easybind.EasyBind;

/**
 *
 * @author Tuupertunut
 */
public class FanningPane extends AnchorPane {

    private final FanningService fanningService;

    @FXML
    private TreeTableView<HardwareTreeElement> sensorTreeTable;
    @FXML
    private TreeTableView<HardwareTreeElement> controlTreeTable;

    public FanningPane(FanningService fanningService) {
        this.fanningService = fanningService;

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
        sensorTreeTable.getColumns().setAll(createSensorTreeTableColumns());
        sensorTreeTable.setRoot(createSensorTreeTableModel(fanningService.getHardwareManager().getHardwareRoot()));

        controlTreeTable.getColumns().setAll(createControlTreeTableColumns());
        controlTreeTable.setRoot(createControlTreeTableModel(fanningService.getHardwareManager().getHardwareRoot()));
    }

    private static List<TreeTableColumn<HardwareTreeElement, ?>> createSensorTreeTableColumns() {

        TreeTableColumn<HardwareTreeElement, String> sensorNameColumn = new TreeTableColumn<>("Sensor");
        sensorNameColumn.setPrefWidth(150);
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
                return EasyBind.map(((Sensor) elem).valueProperty(), (Number value) -> value.toString() + " " + ((Sensor) elem).getMeasurementUnit());
            } else {
                return new ReadOnlyStringWrapper("");
            }
        });

        return Arrays.asList(sensorNameColumn, sensorTypeColumn, sensorValueColumn);
    }

    private static TreeItem<HardwareTreeElement> createSensorTreeTableModel(HardwareItem hw) {
        TreeItem<HardwareTreeElement> treeItem = new TreeItem<>(hw);
        treeItem.setExpanded(true);

        for (Sensor sensor : hw.getSensors()) {
            treeItem.getChildren().add(new TreeItem<>(sensor));
        }
        for (HardwareItem subHw : hw.getSubHardware()) {
            treeItem.getChildren().add(createSensorTreeTableModel(subHw));
        }

        return treeItem;
    }

    private static List<TreeTableColumn<HardwareTreeElement, ?>> createControlTreeTableColumns() {

        TreeTableColumn<HardwareTreeElement, String> controlNameColumn = new TreeTableColumn<>("Control");
        controlNameColumn.setPrefWidth(150);
        controlNameColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<HardwareTreeElement, String> data) -> {

            HardwareTreeElement elem = data.getValue().getValue();
            return new ReadOnlyStringWrapper(elem.getName());
        });

        TreeTableColumn<HardwareTreeElement, String> controlTypeColumn = new TreeTableColumn<>("Type");
        controlTypeColumn.setPrefWidth(100);
        controlTypeColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<HardwareTreeElement, String> data) -> {

            HardwareTreeElement elem = data.getValue().getValue();
            if (elem instanceof Control) {
                return new ReadOnlyStringWrapper(((Control) elem).getSensorType());
            } else {
                return new ReadOnlyStringWrapper("");
            }
        });

        TreeTableColumn<HardwareTreeElement, String> controlValueColumn = new TreeTableColumn<>("Controlled value");
        controlValueColumn.setPrefWidth(100);
        controlValueColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<HardwareTreeElement, String> data) -> {

            HardwareTreeElement elem = data.getValue().getValue();
            if (elem instanceof Control) {
                return EasyBind.map(((Control) elem).controlledValueProperty(), (OptionalDouble value) -> {
                    if (value.isPresent()) {
                        return Double.toString(value.getAsDouble()) + " " + ((Control) elem).getMeasurementUnit();
                    } else {
                        return "Not controlled";
                    }
                });
            } else {
                return new ReadOnlyStringWrapper("");
            }
        });

        return Arrays.asList(controlNameColumn, controlTypeColumn, controlValueColumn);
    }

    private static TreeItem<HardwareTreeElement> createControlTreeTableModel(HardwareItem hw) {
        TreeItem<HardwareTreeElement> treeItem = new TreeItem<>(hw);
        treeItem.setExpanded(true);

        for (Control control : hw.getControls()) {
            treeItem.getChildren().add(new TreeItem<>(control));
        }
        for (HardwareItem subHw : hw.getSubHardware()) {
            treeItem.getChildren().add(createControlTreeTableModel(subHw));
        }

        return treeItem;
    }
}
