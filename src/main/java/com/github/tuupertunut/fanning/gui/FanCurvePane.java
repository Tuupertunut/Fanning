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
import com.github.tuupertunut.fanning.core.Mapping;
import com.github.tuupertunut.fanning.util.ObservableListBinding;
import java.io.IOException;
import java.util.Comparator;
import java.util.Optional;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ListBinding;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import org.fxmisc.easybind.EasyBind;

/**
 *
 * @author Tuupertunut
 */
public class FanCurvePane extends AnchorPane {

    private final FanningService fanningService;
    private final ObservableValue<FanCurve> selectedFanCurveProperty;

    private LineChart<Double, Double> chart;
    private FanCurveTableView fanCurveEditor;
    @FXML
    private StackPane chartContainer;
    @FXML
    private StackPane fanCurveEditorContainer;
    @FXML
    private Label infoLabel;
    @FXML
    private Button deleteButton;
    @FXML
    private Button saveButton;

    public FanCurvePane(FanningService fanningService, ObservableValue<FanCurve> selectedFanCurveProperty) {
        this.fanningService = fanningService;
        this.selectedFanCurveProperty = selectedFanCurveProperty;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FanCurvePane.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }
    /* A reference to the binding must be retained because JavaFX uses weak
     * listeners. This would otherwise be garbage collected. */
    private ListBinding<XYChart.Data<Double, Double>> chartSeriesBinding;

    @FXML
    private void initialize() {
        NumberAxis sensorAxis = new NumberAxis();
        sensorAxis.labelProperty().bind(EasyBind.map(selectedFanCurveProperty, (FanCurve selFanCurve) -> {
            if (selFanCurve == null) {
                /* This is never visible */
                return "";
            } else {
                return selFanCurve.getSensor().getName() + " (" + selFanCurve.getSensor().getMeasurementUnit() + ")";
            }
        }));
        NumberAxis fanAxis = new NumberAxis();
        fanAxis.labelProperty().bind(EasyBind.map(selectedFanCurveProperty, (FanCurve selFanCurve) -> {
            if (selFanCurve == null) {
                /* This is never visible */
                return "";
            } else {
                return selFanCurve.getFanController().getName() + " (" + selFanCurve.getFanController().getMeasurementUnit() + ")";
            }
        }));

        XYChart.Series<Double, Double> chartSeries = new XYChart.Series<>();
        chartSeries.setName("Fan curve");

        /* There are many layers of mapping here. First the selected fan curve
         * is mapped to a list of change points, then the change points are
         * sorted, then the change point list is mapped to a list of chart
         * points. */
        chartSeriesBinding = new ObservableListBinding<>(EasyBind.map(selectedFanCurveProperty, (FanCurve selFanCurve) -> {
            if (selFanCurve == null) {
                /* This is never visible */
                return FXCollections.emptyObservableList();
            } else {
                return new ObservableListBinding<>(EasyBind.map(new ReadOnlyListWrapper<>(selFanCurve.changePointsProperty().sorted(Comparator.comparingDouble((Mapping m) -> m.key))), (ObservableList<Mapping> changePoints) -> {

                    ObservableList<XYChart.Data<Double, Double>> chartPoints = FXCollections.observableArrayList();
                    for (int i = 0; i < changePoints.size(); i++) {
                        Mapping m = changePoints.get(i);
                        if (i > 0) {
                            Mapping prev = changePoints.get(i - 1);
                            XYChart.Data<Double, Double> midDataPoint = new XYChart.Data<>(m.key, prev.value);
                            /* Making the node invisible with 0 size rectangle. */
                            midDataPoint.setNode(new Rectangle());
                            chartPoints.add(midDataPoint);
                        }
                        chartPoints.add(new XYChart.Data<>(m.key, m.value));
                    }
                    return chartPoints;
                }));
            }
        }));
        Bindings.bindContent(chartSeries.getData(), chartSeriesBinding);

        chart = new LineChart(sensorAxis, fanAxis);
        chartContainer.getChildren().add(chart);
        chart.getData().add(chartSeries);
        chart.setAnimated(false);

        fanCurveEditor = new FanCurveTableView();
        fanCurveEditorContainer.getChildren().add(fanCurveEditor);

        fanCurveEditor.changePointsProperty().bind(EasyBind.map(selectedFanCurveProperty, (FanCurve selFanCurve) -> {
            if (selFanCurve == null) {
                /* This is never visible */
                return FXCollections.emptyObservableList();
            } else {
                return selFanCurve.changePointsProperty();
            }
        }));

        infoLabel.textProperty().bind(EasyBind.map(selectedFanCurveProperty, (FanCurve selFanCurve) -> {
            if (selFanCurve == null) {
                /* This is never visible */
                return "";
            } else {
                return "Fan '" + selFanCurve.getFanController().getName() + "' controlled by sensor '" + selFanCurve.getSensor().getName() + "'";
            }
        }));

        deleteButton.setOnAction((ActionEvent event) -> {
            Optional<ButtonType> result = new Alert(Alert.AlertType.CONFIRMATION, "Do you really want to delete this fan curve?", ButtonType.YES, ButtonType.CANCEL).showAndWait();
            if (result.isPresent() && result.get() == ButtonType.YES) {
                fanningService.fanCurvesProperty().remove(selectedFanCurveProperty.getValue());
            }
        });

        saveButton.setOnAction((ActionEvent event) -> {
            try {
                fanningService.storeToStorage();
            } catch (IOException ex) {
                new Alert(Alert.AlertType.ERROR, ex.getMessage(), ButtonType.OK).showAndWait();
            }
        });
    }
}
