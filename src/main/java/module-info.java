module com.todolist {
    requires javafx.controls;
    requires javafx.fxml;
    requires jdk.jfr;
    requires java.logging;


    opens com.todolist to javafx.fxml;
    exports com.todolist;
    exports com.todolist.controller;
    exports com.todolist.datamodel;
    opens com.todolist.controller to javafx.fxml;
}