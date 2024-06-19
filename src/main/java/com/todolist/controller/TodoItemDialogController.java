package com.todolist.controller;

import com.todolist.datamodel.TodoData;
import com.todolist.datamodel.TodoItem;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.time.LocalDate;

public class TodoItemDialogController {

    @FXML
    private TextField shortDescription;
    @FXML
    private TextArea details;
    @FXML
    private DatePicker deadline;

    @FXML
    public void initialize() {
        deadline.setValue(LocalDate.now());
    }
    @FXML
    public TodoItem processResults(){
        String shortDescriptionText = shortDescription.getText().trim();
        String detailsText = details.getText().trim();
        LocalDate deadlineDate = deadline.getValue();
        TodoItem newItem  = new TodoItem(shortDescriptionText, detailsText, deadlineDate);
        TodoData.getInstance().addTodoItem(newItem);
        return newItem;
    }

}
