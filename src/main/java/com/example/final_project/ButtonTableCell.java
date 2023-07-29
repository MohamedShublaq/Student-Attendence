package com.example.final_project;

import javafx.scene.control.Button;
import javafx.scene.control.TableCell;

public class ButtonTableCell<S, T> extends TableCell<S, T> {
    public final Button button;
    public final ButtonCellCallback<S> callback;

    public ButtonTableCell(String buttonText, ButtonCellCallback<S> callback) {
        this.button = new Button(buttonText);
        this.callback = callback;

        button.setOnAction(event -> {
            if (getTableRow() != null) {
                int index = getTableRow().getIndex();
                S item = getTableView().getItems().get(index);
                callback.onButtonClicked(item);
            }
        });

    }
    @Override
    protected void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setGraphic(null);
        } else {
            setGraphic(button);
        }
    }

    interface ButtonCellCallback<S> {
        void onButtonClicked(S item);
    }
}
