package org.example.elizarov_bd;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;

public class SalaryWindow extends Application {
    private final TableView<Salary> table = new TableView<>();
    private final ObservableList<Salary> salaries = FXCollections.observableArrayList();

    public static void main(String[] args) {
        launch(args);

    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Справочник окладов");

        // Создание колонок таблицы
        TableColumn<Salary, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Salary, String> positionColumn = new TableColumn<>("Должность");
        positionColumn.setCellValueFactory(new PropertyValueFactory<>("position"));

        TableColumn<Salary, Double> salaryColumn = new TableColumn<>("Оклад");
        salaryColumn.setCellValueFactory(new PropertyValueFactory<>("salary"));

        Collections.addAll(table.getColumns(), idColumn, positionColumn, salaryColumn);
        table.setItems(salaries);

        // Кнопки для работы со справочником
        Button addButton = new Button("Добавить");
        Button editButton = new Button("Изменить");
        Button deleteButton = new Button("Удалить");

        // Обработчики событий
        addButton.setOnAction(e -> addSalary());
        editButton.setOnAction(e -> editSalary());
        deleteButton.setOnAction(e -> deleteSalary());

        // Панель для кнопок
        HBox buttonPanel = new HBox(10, addButton, editButton, deleteButton);
        buttonPanel.setPadding(new Insets(10));

        // Основной макет
        BorderPane root = new BorderPane();
        root.setCenter(table);
        root.setBottom(buttonPanel);

        // Загрузка данных в таблицу
        loadSalaries();

        // Настройка сцены
        Scene scene = new Scene(root, 500, 400);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/main_style.css")).toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void loadSalaries() {
        try (Connection connection = DBConfig.getConnection()) {
            String query = "CREATE TABLE IF NOT EXISTS Salaries (ID SERIAL PRIMARY KEY,Position VARCHAR(100) NOT NULL, Salary NUMERIC(10, 2) NOT NULL);";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.execute();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        salaries.clear();
        String sql = "SELECT * FROM Salaries"; // Предполагаем, что таблица Salaries уже создана
        try (Connection conn = DBConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                salaries.add(new Salary(
                        rs.getInt("ID"),
                        rs.getString("Position"),
                        rs.getDouble("Salary")
                ));
            }
        } catch (SQLException ex) {
            showError("Ошибка загрузки данных: " + ex.getMessage());
        }
    }

    private void addSalary() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Добавление оклада");
        dialog.setHeaderText("Введите данные:");
        dialog.setContentText("Должность:");

        Optional<String> positionResult = dialog.showAndWait();
        positionResult.ifPresent(position -> {
            TextInputDialog salaryDialog = new TextInputDialog();
            salaryDialog.setTitle("Добавление оклада");
            salaryDialog.setHeaderText("Введите данные:");
            salaryDialog.setContentText("Оклад:");

            Optional<String> salaryResult = salaryDialog.showAndWait();
            salaryResult.ifPresent(salary -> {
                try {
                    double salaryValue = Double.parseDouble(salary);
                    String sql = "INSERT INTO Salaries (Position, Salary) VALUES (?, ?)";
                    try (Connection conn = DBConfig.getConnection();
                         PreparedStatement pstmt = conn.prepareStatement(sql)) {
                        pstmt.setString(1, position);
                        pstmt.setDouble(2, salaryValue);
                        pstmt.executeUpdate();
                        loadSalaries(); // Обновляем таблицу
                    }
                } catch (NumberFormatException ex) {
                    showError("Оклад должен быть числом.");
                } catch (SQLException ex) {
                    showError("Ошибка добавления оклада: " + ex.getMessage());
                }
            });
        });
    }

    private void editSalary() {
        Salary selectedSalary = table.getSelectionModel().getSelectedItem();
        if (selectedSalary != null) {
            TextInputDialog dialog = new TextInputDialog(selectedSalary.getPosition());
            dialog.setTitle("Редактирование оклада");
            dialog.setHeaderText("Введите новую должность:");
            dialog.setContentText("Должность:");

            Optional<String> positionResult = dialog.showAndWait();
            positionResult.ifPresent(position -> {
                TextInputDialog salaryDialog = new TextInputDialog(String.valueOf(selectedSalary.getSalary()));
                salaryDialog.setTitle("Редактирование оклада");
                salaryDialog.setHeaderText("Введите новый оклад:");
                salaryDialog.setContentText("Оклад:");

                Optional<String> salaryResult = salaryDialog.showAndWait();
                salaryResult.ifPresent(salary -> {
                    try {
                        double salaryValue = Double.parseDouble(salary);
                        String sql = "UPDATE Salaries SET Position = ?, Salary = ? WHERE ID = ?";
                        try (Connection conn = DBConfig.getConnection();
                             PreparedStatement pstmt = conn.prepareStatement(sql)) {
                            pstmt.setString(1, position);
                            pstmt.setDouble(2, salaryValue);
                            pstmt.setInt(3, selectedSalary.getId());
                            pstmt.executeUpdate();
                            loadSalaries(); // Обновляем таблицу
                        }
                    } catch (NumberFormatException ex) {
                        showError("Оклад должен быть числом.");
                    } catch (SQLException ex) {
                        showError("Ошибка изменения оклада: " + ex.getMessage());
                    }
                });
            });
        } else {
            showError("Выберите оклад для редактирования.");
        }
    }

    private void deleteSalary() {
        Salary selectedSalary = table.getSelectionModel().getSelectedItem();
        if (selectedSalary != null) {
            Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
            confirmDialog.setTitle("Подтверждение удаления");
            confirmDialog.setHeaderText("Вы уверены, что хотите удалить оклад?");
            confirmDialog.setContentText("Должность: " + selectedSalary.getPosition() + ", Оклад: " + selectedSalary.getSalary());

            Optional<ButtonType> result = confirmDialog.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                String sql = "DELETE FROM Salaries WHERE ID = ?";
                try (Connection conn = DBConfig.getConnection();
                     PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setInt(1, selectedSalary.getId());
                    pstmt.executeUpdate();
                    loadSalaries(); // Обновляем таблицу
                } catch (SQLException ex) {
                    showError("Ошибка удаления оклада: " + ex.getMessage());
                }
            }
        } else {
            showError("Выберите оклад для удаления.");
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Класс для хранения данных об окладе
    public static class Salary {
        private final int id;
        private final String position;
        private final double salary;

        public Salary(int id, String position, double salary) {
            this.id = id;
            this.position = position;
            this.salary = salary;
        }

        public int getId() {
            return id;
        }

        public String getPosition() {
            return position;
        }

        public double getSalary() {
            return salary;
        }
    }
}