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
import java.io.IOException;
import java.util.Optional;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import org.fxmisc.easybind.EasyBind;

/**
 *
 * @author Tuupertunut
 */
public class FanCurvePane extends AnchorPane {

    private final FanningService fanningService;
    private final ObservableValue<FanCurve> selectedFanCurveProperty;

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

    @FXML
    private void initialize() {
        infoLabel.textProperty().bind(EasyBind.map(selectedFanCurveProperty, (FanCurve selFanCurve) -> {
            if (selFanCurve == null) {
                /* This is never visible */
                return "";
            } else {
                return "Control " + selFanCurve.getTarget().getName() + " controlled by sensor " + selFanCurve.getSource().getName();
            }
        }));

        deleteButton.setOnAction((ActionEvent event) -> {
            Optional<ButtonType> result = new Alert(Alert.AlertType.CONFIRMATION, "Do you really want to delete this fan curve?", ButtonType.YES, ButtonType.CANCEL).showAndWait();
            if (result.isPresent() && result.get() == ButtonType.YES) {
                fanningService.fanCurvesProperty().remove(selectedFanCurveProperty.getValue());
            }
        });
    }
}
