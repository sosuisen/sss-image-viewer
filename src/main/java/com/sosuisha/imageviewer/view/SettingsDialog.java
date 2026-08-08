package com.sosuisha.imageviewer.view;

import com.sosuisha.imageviewer.SettingsService;

import io.github.sosuisen.jfxbuilder.controls.CheckBoxBuilder;

import javafx.geometry.Insets;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;

/**
 * Modal dialog for application settings.
 */
public class SettingsDialog {

    private SettingsDialog() {
    }

    /**
     * Shows the settings dialog and saves the settings when OK is pressed.
     */
    public static void show() {
        var settings = SettingsService.getInstance();
        var openDndCheckBox = CheckBoxBuilder.create("Open a drag and drop window at start-up")
                .selected(settings.isOpenDragAndDropWindowAtStartup())
                .build();

        var dialog = new Dialog<ButtonType>();
        dialog.setTitle("Settings");
        dialog.getDialogPane().setContent(openDndCheckBox);
        dialog.getDialogPane().setPadding(new Insets(10));
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.showAndWait().ifPresent(buttonType -> {
            if (buttonType == ButtonType.OK) {
                settings.setOpenDragAndDropWindowAtStartup(openDndCheckBox.isSelected());
            }
        });
    }
}
