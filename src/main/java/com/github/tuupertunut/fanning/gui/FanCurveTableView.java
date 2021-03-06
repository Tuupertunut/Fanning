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

import com.github.tuupertunut.fanning.core.Mapping;
import java.io.IOException;
import java.util.Comparator;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

/**
 *
 * @author Tuupertunut
 */
public class FanCurveTableView extends AnchorPane {

    private final ListProperty<Mapping> changePoints;

    @FXML
    private TableView<Mapping> mapTable;
    @FXML
    private Button addButton;
    @FXML
    private TextField addKeyField;
    @FXML
    private TextField addValueField;

    public FanCurveTableView() {
        changePoints = new SimpleListProperty<>(FXCollections.observableArrayList());

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FanCurveTableView.fxml"));
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
        TableColumn<Mapping, Double> keyColumn = new TableColumn<>("Sensor");
        keyColumn.setCellFactory((TableColumn<Mapping, Double> column) -> {

            /* Key mutation handling. */
            BiConsumer<Mapping, Double> editAction = (Mapping rowValue, Double newCellValue) -> {
                Double oldValue = rowValue.value;
                changePoints.remove(rowValue);
                changePoints.removeIf((Mapping m) -> m.key == newCellValue);
                changePoints.add(new Mapping(newCellValue, oldValue));
            };
            return new DoubleTextFieldTableCell(editAction);
        });
        keyColumn.setCellValueFactory((TableColumn.CellDataFeatures<Mapping, Double> data) -> {

            return new ReadOnlyObjectWrapper<>(data.getValue().key);
        });

        TableColumn<Mapping, Double> valueColumn = new TableColumn<>("Fan");
        valueColumn.setCellFactory((TableColumn<Mapping, Double> column) -> {

            /* Value mutation handling. */
            BiConsumer<Mapping, Double> editAction = (Mapping rowValue, Double newCellValue) -> {
                Double oldKey = rowValue.key;
                changePoints.remove(rowValue);
                changePoints.add(new Mapping(oldKey, newCellValue));
            };
            return new DoubleTextFieldTableCell(editAction);
        });
        valueColumn.setCellValueFactory((TableColumn.CellDataFeatures<Mapping, Double> data) -> {

            return new ReadOnlyObjectWrapper<>(data.getValue().value);
        });

        TableColumn<Mapping, Object> deleteButtonColumn = new TableColumn<>();
        deleteButtonColumn.setCellFactory((TableColumn<Mapping, Object> column) -> {

            /* Deletion handling. */
            Consumer<Mapping> buttonAction = (Mapping rowValue) -> {
                changePoints.remove(rowValue);
            };
            return new ButtonTableCell(buttonAction);
        });
        deleteButtonColumn.setCellValueFactory((TableColumn.CellDataFeatures<Mapping, Object> data) -> {

            /* There are no values in the delete button column, so returning
             * empty wrapper. */
            return new ReadOnlyObjectWrapper<>();
        });

        mapTable.getColumns().setAll(keyColumn, valueColumn, deleteButtonColumn);
        mapTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        mapTable.setItems(changePoints.sorted(Comparator.comparingDouble((Mapping m) -> m.key)));

        /* Insertion handling. */
        addButton.setOnAction((ActionEvent event) -> {
            if (validateDoubleString(addKeyField.getText()) && validateDoubleString(addValueField.getText())) {

                double newKey = Double.parseDouble(addKeyField.getText());
                double newValue = Double.parseDouble(addValueField.getText());
                changePoints.removeIf((Mapping m) -> m.key == newKey);
                changePoints.add(new Mapping(newKey, newValue));

                addKeyField.setText("");
                addValueField.setText("");
            } else {
                if (!validateDoubleString(addKeyField.getText())) {
                    addKeyField.setText("");
                }
                if (!validateDoubleString(addValueField.getText())) {
                    addValueField.setText("");
                }
            }
        });
    }

    public ListProperty<Mapping> changePointsProperty() {
        return changePoints;
    }

    private static boolean validateDoubleString(String s) {
        try {
            return Double.isFinite(Double.parseDouble(s));
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    private static class ButtonTableCell extends TableCell<Mapping, Object> {

        private final Button button;

        ButtonTableCell(Consumer<Mapping> buttonAction) {
            button = new Button("del");
            button.setOnAction((ActionEvent event) -> buttonAction.accept(getRowValue()));
        }

        @Override
        protected void updateItem(Object item, boolean empty) {
            super.updateItem(item, empty);

            if (empty) {
                this.setGraphic(null);
            } else {
                this.setGraphic(button);
            }
        }

        private Mapping getRowValue() {
            return this.getTableView().getItems().get(this.getIndex());
        }
    }

    private static class DoubleTextFieldTableCell extends TableCell<Mapping, Double> {

        private final BiConsumer<Mapping, Double> editAction;
        private final TextField tf;

        DoubleTextFieldTableCell(BiConsumer<Mapping, Double> editAction) {
            this.editAction = editAction;
            tf = new TextField();

            /* Commit edit when pressing enter or when focus is lost. */
            tf.setOnAction((ActionEvent event) -> tryEdit());
            tf.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                if (!newValue) {
                    tryEdit();
                }
            });
        }

        @Override
        protected void updateItem(Double item, boolean empty) {
            super.updateItem(item, empty);

            if (empty) {
                this.setGraphic(null);
            } else {
                if (item == null) {
                    tf.setText(null);
                } else {
                    tf.setText(Double.toString(item));
                }
                this.setGraphic(tf);
            }
        }

        private void tryEdit() {
            if (validateDoubleString(tf.getText())) {
                double newCellValue = Double.parseDouble(tf.getText());
                if (newCellValue != this.getItem()) {
                    editAction.accept(getRowValue(), newCellValue);
                }
            } else {
                /* If text is invalid, reset the field back to previous value. */
                tf.setText(Double.toString(this.getItem()));
            }
        }

        private Mapping getRowValue() {
            return this.getTableView().getItems().get(this.getIndex());
        }
    }
}
