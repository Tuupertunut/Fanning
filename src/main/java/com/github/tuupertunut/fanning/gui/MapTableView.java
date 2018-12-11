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

import java.io.IOException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import javafx.beans.property.MapProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleMapProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
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
public class MapTableView extends AnchorPane {

    private final MapProperty<Double, Double> map;

    @FXML
    private TableView<Mapping> mapTable;
    @FXML
    private Button addButton;
    @FXML
    private TextField addKeyField;
    @FXML
    private TextField addValueField;

    public MapTableView() {
        map = new SimpleMapProperty<>(FXCollections.observableHashMap());

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MapTableView.fxml"));
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
                Double oldValue = rowValue.value.get();
                map.remove(rowValue.key.get());
                map.put(newCellValue, oldValue);
            };
            return new DoubleTextFieldTableCell(editAction);
        });
        keyColumn.setCellValueFactory((TableColumn.CellDataFeatures<Mapping, Double> data) -> {

            return data.getValue().key;
        });

        TableColumn<Mapping, Double> valueColumn = new TableColumn<>("Fan");
        valueColumn.setCellFactory((TableColumn<Mapping, Double> column) -> {

            /* Value mutation handling. */
            BiConsumer<Mapping, Double> editAction = (Mapping rowValue, Double newCellValue) -> {
                map.put(rowValue.key.get(), newCellValue);
            };
            return new DoubleTextFieldTableCell(editAction);
        });
        valueColumn.setCellValueFactory((TableColumn.CellDataFeatures<Mapping, Double> data) -> {

            return data.getValue().value;
        });

        TableColumn<Mapping, Object> deleteButtonColumn = new TableColumn<>();
        deleteButtonColumn.setCellFactory((TableColumn<Mapping, Object> column) -> {

            /* Deletion handling. */
            Consumer<Mapping> buttonAction = (Mapping rowValue) -> {
                map.remove(rowValue.key.get());
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

        ObservableList<Mapping> tableItems = mapTable.getItems();

        /* Insertion handling. */
        addButton.setOnAction((ActionEvent event) -> {
            if (validateDoubleString(addKeyField.getText()) && validateDoubleString(addValueField.getText())) {

                map.put(Double.parseDouble(addKeyField.getText()), Double.parseDouble(addValueField.getText()));

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

        /* Adding map listener that updates the table items list. */
        map.addListener((MapChangeListener.Change<? extends Double, ? extends Double> change) -> {
            if (change.wasRemoved() && change.wasAdded()) {

                for (Mapping m : tableItems) {
                    if (m.key.get().equals(change.getKey())) {
                        m.value.set(change.getValueAdded());
                        break;
                    }
                }
            } else if (change.wasAdded()) {

                Mapping m = new Mapping();
                m.key.set(change.getKey());
                m.value.set(change.getValueAdded());
                boolean found = false;
                for (int i = 0; i < tableItems.size(); i++) {
                    if (tableItems.get(i).key.get() >= change.getKey()) {
                        tableItems.add(i, m);
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    tableItems.add(m);
                }
            } else {

                for (Mapping m : tableItems) {
                    if (m.key.get().equals(change.getKey())) {
                        tableItems.remove(m);
                        break;
                    }
                }
            }
        });
    }

    public MapProperty<Double, Double> mapProperty() {
        return map;
    }

    private static boolean validateDoubleString(String s) {
        try {
            return Double.isFinite(Double.parseDouble(s));
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    private static class Mapping {

        ObjectProperty<Double> key = new SimpleObjectProperty<>();
        ObjectProperty<Double> value = new SimpleObjectProperty<>();
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
            tf.focusedProperty().addListener(this::textFieldFocusListener);
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

        private void textFieldFocusListener(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {

            /* Commit edit on focus lost. If text is invalid, reset the field
             * back to previous value. */
            if (!newValue) {
                if (validateDoubleString(tf.getText())) {
                    editAction.accept(getRowValue(), Double.parseDouble(tf.getText()));
                } else {
                    tf.setText(Double.toString(this.getItem()));
                }
            }
        }

        private Mapping getRowValue() {
            return this.getTableView().getItems().get(this.getIndex());
        }
    }
}
