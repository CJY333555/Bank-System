module com.example.crystalbank {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.crystalbank to javafx.fxml;
    exports com.crystalbank;

    opens com.crystalbank.controller to javafx.fxml;
    exports com.crystalbank.controller;

    opens com.crystalbank.database to javafx.fxml;
    exports com.crystalbank.database;

    opens com.crystalbank.model to javafx.base, javafx.fxml;
    exports com.crystalbank.model;

    exports com.crystalbank.util;
}