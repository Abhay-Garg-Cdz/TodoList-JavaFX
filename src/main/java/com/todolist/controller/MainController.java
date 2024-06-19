package com.todolist.controller;

import com.todolist.datamodel.TodoData;
import com.todolist.datamodel.TodoItem;
import javafx.application.Platform;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.util.Callback;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.logging.Logger;


public class MainController {
    private static final Logger logger = Logger.getLogger(MainController.class.getName()) ;
    @FXML
    private ListView<TodoItem> todoListView;
    @FXML
    private TextArea itemDetailsTextArea;
    @FXML
    private BorderPane mainBorderPane;
    @FXML
    private Label deadlineLabel;
    @FXML
    private ContextMenu listContextMenu;
    @FXML
    private ToggleButton filterToggleButton;

    private FilteredList<TodoItem> filteredList;
    private Predicate<TodoItem> allItems;
    private Predicate<TodoItem> todayItem;


    public void initialize() {
        listContextMenu = new ContextMenu();
        MenuItem deleteMenuItem = new MenuItem("Delete");
        deleteMenuItem.setOnAction(event -> {
            TodoItem item = todoListView.getSelectionModel().getSelectedItem();
            deleteItem(item);
        });
        listContextMenu.getItems().add(deleteMenuItem);
        todoListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                TodoItem newItem = todoListView.getSelectionModel().getSelectedItem();
                itemDetailsTextArea.setText(newItem.getDetails());
                deadlineLabel.setText(newItem.getDeadline().toString());
            }
        });
        allItems = todoItem -> true;

        todayItem = todoItem -> (todoItem.getDeadline().equals(LocalDate.now()));

        filteredList = new FilteredList<>(TodoData.getInstance().getTodoItems(), allItems);
        SortedList<TodoItem> sortedList = new SortedList<>(filteredList, Comparator.comparing(TodoItem::getDeadline));
        todoListView.setItems(sortedList);
        todoListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        todoListView.getSelectionModel().selectFirst();

        todoListView.setCellFactory(new Callback<>() {

            @Override
            public ListCell<TodoItem> call(ListView<TodoItem> param) {
                ListCell<TodoItem> cell = new ListCell<>() {
                    @Override
                    protected void updateItem(TodoItem item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setText(null);
                        } else {
                            setText(item.getShortDescription());
                            if (item.getDeadline().isBefore(LocalDate.now().plusDays(1))) {
                                setTextFill(Color.RED);
                            } else if (item.getDeadline().equals(LocalDate.now().plusDays(1))) {
                                setTextFill(Color.BLUE);
                            }
                        }

                    }
                };

                cell.emptyProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue) {
                        cell.setContextMenu(null);
                    } else {
                        cell.setContextMenu(listContextMenu);
                    }
                });

                return cell;
            }
        });

    }

    @FXML
    public void showNewItemDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        dialog.setTitle("New Todo Item");
        dialog.setHeaderText("Create New Todo Item");
        FXMLLoader loader = new FXMLLoader();
        try {
            loader.setLocation(getClass().getResource("/com/todolist/todItemDialog.fxml"));
//        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("com/todolist/todoItemDialog.fxml"));
            dialog.getDialogPane().setContent(loader.load());
        } catch (Exception e) {
            logger.severe("Cannot Load FXML File: " +e.getMessage());

        }
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            TodoItemDialogController dialogController = loader.getController();
            TodoItem newItem = dialogController.processResults();
            todoListView.getSelectionModel().select(newItem);
        }
    }

    @FXML
    public void handleListKeyPressed(KeyEvent keyEvent) {
        TodoItem item = todoListView.getSelectionModel().getSelectedItem();
            if (keyEvent.getCode().equals(KeyCode.DELETE)) {
                deleteItem(item);
            }
    }

    @FXML
    public void handleFilterButton() {
        TodoItem selectedItem = todoListView.getSelectionModel().getSelectedItem();
        if (filterToggleButton.isSelected()) {
            filteredList.setPredicate(todayItem);
            if(filteredList.isEmpty()){
                itemDetailsTextArea.clear();
                deadlineLabel.setText("");
                System.out.println(" ");
            }
            else if(filteredList.contains(selectedItem)){
                todoListView.getSelectionModel().select(selectedItem);
            }
            else{
                todoListView.getSelectionModel().selectFirst();
            }
        } else {
            filteredList.setPredicate(allItems);
            todoListView.getSelectionModel().select(selectedItem);
        }
    }

    @FXML
    public void exitHandler(){
        Platform.exit();
    }

    public void deleteItem(TodoItem item) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Todo  Item");
        alert.setHeaderText("Delete Item: \n" + item.getShortDescription());
        alert.setContentText("Are you sure you want to delete this item?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            TodoData.getInstance().deleteTodoItem(item);
        }
    }


}