/*
 * Copyright (C) 2010-2020, Danilo Pianini and contributors
 * listed in the main project's alchemist/build.gradle.kts file.
 *
 * This file is part of Alchemist, and is distributed under the terms of the
 * GNU General Public License, with a linking exception,
 * as described in the file LICENSE in the Alchemist distribution's top directory.
 */

package it.unibo.alchemist.boundary.gui.controller;

import com.jfoenix.controls.JFXButton;
import it.unibo.alchemist.boundary.gui.utility.FXResourceLoader;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import jiconfont.icons.google_material_design_icons.GoogleMaterialDesignIcons;
import jiconfont.javafx.IconFontFX;
import org.jetbrains.annotations.Nullable;

/**
 * This class models a JavaFX controller for ControlTypePopoverLayout.fxml.
 */
public class ControlTypePopoverController implements Initializable {
    /**
     * Layout path.
     */
    public static final String CONTROL_TYPE_POPOVER_LAYOUT = "ControlTypePopoverLayout";

    // FXML components
    @FXML
    @Nullable
    private JFXButton panButton; // Value injected by FXMLLoader
    @FXML
    @Nullable
    private JFXButton selectButton; // Value injected by FXMLLoader

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        IconFontFX.register(GoogleMaterialDesignIcons.getIconFont());
        Objects.requireNonNull(
                panButton,
                FXResourceLoader.getInjectionErrorMessage("panButton", CONTROL_TYPE_POPOVER_LAYOUT)
        );
        Objects.requireNonNull(
                selectButton,
                FXResourceLoader.getInjectionErrorMessage("selectButton", CONTROL_TYPE_POPOVER_LAYOUT)
        );
        panButton.setText("");
        panButton.setGraphic(FXResourceLoader.getWhiteIcon(GoogleMaterialDesignIcons.PAN_TOOL));
        selectButton.setText("");
        selectButton.setGraphic(FXResourceLoader.getWhiteIcon(GoogleMaterialDesignIcons.TAB_UNSELECTED));
    }

    /**
     * Getter method for the pan button.
     *
     * @return the pan button
     */
    @Nullable
    public JFXButton getPanButton() {
        return this.panButton;
    }

    /**
     * Getter method for the select button.
     *
     * @return the select button
     */
    @Nullable
    public JFXButton getSelectButton() {
        return this.selectButton;
    }
}
