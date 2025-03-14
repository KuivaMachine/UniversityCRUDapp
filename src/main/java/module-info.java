module org.example.elizarov_bd {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires static lombok;


    opens org.example.elizarov_bd to javafx.fxml;
    exports org.example.elizarov_bd;
}