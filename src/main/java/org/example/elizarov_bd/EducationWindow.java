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

public class EducationWindow extends Application {
    private TableView<EducationLevel> table = new TableView<>();
    private ObservableList<EducationLevel> educationLevels = FXCollections.observableArrayList();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Справочник образования");

        // Создание колонок таблицы
        TableColumn<EducationLevel, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<EducationLevel, String> levelColumn = new TableColumn<>("Уровень образования");
        levelColumn.setCellValueFactory(new PropertyValueFactory<>("level"));

        Collections.addAll(table.getColumns(),idColumn, levelColumn);
        table.setItems(educationLevels);

        // Кнопки для работы со справочником
        Button addButton = new Button("Добавить");
        Button editButton = new Button("Изменить");
        Button deleteButton = new Button("Удалить");

        // Обработчики событий
        addButton.setOnAction(e -> addEducationLevel());
        editButton.setOnAction(e -> editEducationLevel());
        deleteButton.setOnAction(e -> deleteEducationLevel());

        // Панель для кнопок
        HBox buttonPanel = new HBox(10, addButton, editButton, deleteButton);
        buttonPanel.setPadding(new Insets(10));

        // Основной макет
        BorderPane root = new BorderPane();
        root.setCenter(table);
        root.setBottom(buttonPanel);

        // Загрузка данных в таблицу
        loadEducationLevels();

        // Настройка сцены
        Scene scene = new Scene(root, 500, 400);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/main_style.css")).toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void loadEducationLevels() {
        try (Connection connection = DBConfig.getConnection()) {
            String query = "CREATE TABLE IF NOT EXISTS EducationLevels (ID SERIAL PRIMARY KEY,Level VARCHAR(100) NOT NULL);";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.execute();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        educationLevels.clear();
        String sql = "SELECT * FROM EducationLevels"; // Предполагаем, что таблица EducationLevels уже создана
        try (Connection conn = DBConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                educationLevels.add(new EducationLevel(
                        rs.getInt("ID"),
                        rs.getString("Level")
                ));
            }
        } catch (SQLException ex) {
            showError("Ошибка загрузки данных: " + ex.getMessage());
        }
    }

    private void addEducationLevel() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Добавление уровня образования");
        dialog.setHeaderText("Введите уровень образования:");
        dialog.setContentText("Уровень:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(level -> {
            if (!level.isEmpty()) {
                String sql = "INSERT INTO EducationLevels (Level) VALUES (?)";
                try (Connection conn = DBConfig.getConnection();
                     PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, level);
                    pstmt.executeUpdate();
                    loadEducationLevels(); // Обновляем таблицу
                } catch (SQLException ex) {
                    showError("Ошибка добавления уровня образования: " + ex.getMessage());
                }
            } else {
                showError("Уровень образования не может быть пустым.");
            }
        });
    }

    private void editEducationLevel() {
        EducationLevel selectedLevel = table.getSelectionModel().getSelectedItem();
        if (selectedLevel != null) {
            TextInputDialog dialog = new TextInputDialog(selectedLevel.getLevel());
            dialog.setTitle("Редактирование уровня образования");
            dialog.setHeaderText("Введите новый уровень образования:");
            dialog.setContentText("Уровень:");

            Optional<String> result = dialog.showAndWait();
            result.ifPresent(level -> {
                if (!level.isEmpty()) {
                    String sql = "UPDATE EducationLevels SET Level = ? WHERE ID = ?";
                    try (Connection conn = DBConfig.getConnection();
                         PreparedStatement pstmt = conn.prepareStatement(sql)) {
                        pstmt.setString(1, level);
                        pstmt.setInt(2, selectedLevel.getId());
                        pstmt.executeUpdate();
                        loadEducationLevels(); // Обновляем таблицу
                    } catch (SQLException ex) {
                        showError("Ошибка изменения уровня образования: " + ex.getMessage());
                    }
                } else {
                    showError("Уровень образования не может быть пустым.");
                }
            });
        } else {
            showError("Выберите уровень образования для редактирования.");
        }
    }

    private void deleteEducationLevel() {
        EducationLevel selectedLevel = table.getSelectionModel().getSelectedItem();
        if (selectedLevel != null) {
            Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
            confirmDialog.setTitle("Подтверждение удаления");
            confirmDialog.setHeaderText("Вы уверены, что хотите удалить уровень образования?");
            confirmDialog.setContentText("Уровень: " + selectedLevel.getLevel());

            Optional<ButtonType> result = confirmDialog.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                String sql = "DELETE FROM EducationLevels WHERE ID = ?";
                try (Connection conn = DBConfig.getConnection();
                     PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setInt(1, selectedLevel.getId());
                    pstmt.executeUpdate();
                    loadEducationLevels(); // Обновляем таблицу
                } catch (SQLException ex) {
                    showError("Ошибка удаления уровня образования: " + ex.getMessage());
                }
            }
        } else {
            showError("Выберите уровень образования для удаления.");
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Класс для хранения данных об уровне образования
    public static class EducationLevel {
        private final int id;
        private final String level;

        public EducationLevel(int id, String level) {
            this.id = id;
            this.level = level;
        }

        public int getId() {
            return id;
        }

        public String getLevel() {
            return level;
        }
    }
}