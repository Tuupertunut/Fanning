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

import com.github.tuupertunut.fanning.core.FanCurve;
import com.github.tuupertunut.fanning.core.FanningService;
import com.github.tuupertunut.fanning.hwinterface.FanController;
import com.github.tuupertunut.fanning.hwinterface.HardwareItem;
import com.github.tuupertunut.fanning.hwinterface.HardwareTreeElement;
import com.github.tuupertunut.fanning.hwinterface.Sensor;
import com.github.tuupertunut.fanning.util.ObservableListBinding;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.OptionalDouble;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ListBinding;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
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
    private TreeTableView<HardwareTreeElement> fanTreeTable;
    @FXML
    private StackPane fanCurvePaneContainer;

    /* A reference to the binding must be retained because JavaFX uses weak
     * listeners. This would otherwise be garbage collected. */
    private ListBinding<AnchorPane> containerChildrenBinding;

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

        fanTreeTable.getColumns().setAll(createFanTreeTableColumns());
        fanTreeTable.setRoot(createFanTreeTableModel(fanningService.getHardwareManager().getHardwareRoot()));

        ObservableValue<TreeItem<HardwareTreeElement>> selectedFanProperty = fanTreeTable.getSelectionModel().selectedItemProperty();
        ObservableValue<TreeItem<HardwareTreeElement>> selectedSensorProperty = sensorTreeTable.getSelectionModel().selectedItemProperty();

        NotSelectedPane notSelectedPane = new NotSelectedPane();
        NotControlledPane notControlledPane = new NotControlledPane(selectedFanProperty);
        CreateFanCurvePane createFanCurvePane = new CreateFanCurvePane(fanningService, selectedFanProperty, selectedSensorProperty);
        FanCurvePane fanCurvePane = new FanCurvePane(fanningService, selectedFanProperty, selectedSensorProperty);

        containerChildrenBinding = new ObservableListBinding<>(EasyBind.combine(selectedFanProperty, selectedSensorProperty, fanningService.fanCurvesProperty(), (TreeItem<HardwareTreeElement> selFan, TreeItem<HardwareTreeElement> selSensor, ObservableList<FanCurve> fanCurves) -> {
            if (selFan == null || !(selFan.getValue() instanceof FanController)) {
                return FXCollections.singletonObservableList(notSelectedPane);
            } else if (fanningService.isFanControlled((FanController) selFan.getValue())) {
                return FXCollections.singletonObservableList(fanCurvePane);
            } else if (selSensor == null || !(selSensor.getValue() instanceof Sensor)) {
                return FXCollections.singletonObservableList(notControlledPane);
            } else {
                return FXCollections.singletonObservableList(createFanCurvePane);
            }
        }));
        Bindings.bindContent(fanCurvePaneContainer.getChildren(), containerChildrenBinding);
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

    private static List<TreeTableColumn<HardwareTreeElement, ?>> createFanTreeTableColumns() {

        TreeTableColumn<HardwareTreeElement, String> fanNameColumn = new TreeTableColumn<>("Fan");
        fanNameColumn.setPrefWidth(150);
        fanNameColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<HardwareTreeElement, String> data) -> {

            HardwareTreeElement elem = data.getValue().getValue();
            return new ReadOnlyStringWrapper(elem.getName());
        });

        TreeTableColumn<HardwareTreeElement, String> fanTypeColumn = new TreeTableColumn<>("Type");
        fanTypeColumn.setPrefWidth(100);
        fanTypeColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<HardwareTreeElement, String> data) -> {

            HardwareTreeElement elem = data.getValue().getValue();
            if (elem instanceof FanController) {
                return new ReadOnlyStringWrapper(((FanController) elem).getSensorType());
            } else {
                return new ReadOnlyStringWrapper("");
            }
        });

        TreeTableColumn<HardwareTreeElement, String> fanValueColumn = new TreeTableColumn<>("Controlled value");
        fanValueColumn.setPrefWidth(100);
        fanValueColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<HardwareTreeElement, String> data) -> {

            HardwareTreeElement elem = data.getValue().getValue();
            if (elem instanceof FanController) {
                return EasyBind.map(((FanController) elem).controlledValueProperty(), (OptionalDouble value) -> {
                    if (value.isPresent()) {
                        return Double.toString(value.getAsDouble()) + " " + ((FanController) elem).getMeasurementUnit();
                    } else {
                        return "Not controlled";
                    }
                });
            } else {
                return new ReadOnlyStringWrapper("");
            }
        });

        return Arrays.asList(fanNameColumn, fanTypeColumn, fanValueColumn);
    }

    private static TreeItem<HardwareTreeElement> createFanTreeTableModel(HardwareItem hw) {
        TreeItem<HardwareTreeElement> treeItem = new TreeItem<>(hw);
        treeItem.setExpanded(true);

        for (FanController fan : hw.getFanControllers()) {
            treeItem.getChildren().add(new TreeItem<>(fan));
        }
        for (HardwareItem subHw : hw.getSubHardware()) {
            treeItem.getChildren().add(createFanTreeTableModel(subHw));
        }

        return treeItem;
    }
}
