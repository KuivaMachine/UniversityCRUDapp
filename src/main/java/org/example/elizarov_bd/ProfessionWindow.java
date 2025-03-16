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

public class ProfessionWindow extends Application {
    private final TableView<Profession> table = new TableView<>();
    private final ObservableList<Profession> professions = FXCollections.observableArrayList();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Справочник профессий");

        // Создание колонок таблицы
        TableColumn<Profession, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Profession, String> nameColumn = new TableColumn<>("Название профессии");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        // Добавление колонок в таблицу
        Collections.addAll(table.getColumns(), idColumn, nameColumn);
        table.setItems(professions);

        // Кнопки для работы со справочником
        Button addButton = new Button("Добавить");
        Button editButton = new Button("Изменить");
        Button deleteButton = new Button("Удалить");

        // Обработчики событий
        addButton.setOnAction(e -> addProfession());
        editButton.setOnAction(e -> editProfession());
        deleteButton.setOnAction(e -> deleteProfession());

        // Панель для кнопок
        HBox buttonPanel = new HBox(10, addButton, editButton, deleteButton);
        buttonPanel.setPadding(new Insets(10));

        // Основной макет
        BorderPane root = new BorderPane();
        root.setCenter(table);
        root.setBottom(buttonPanel);

        // Загрузка данных в таблицу
        loadProfessions();

        // Настройка сцены
        Scene scene = new Scene(root, 500, 400);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/main_style.css")).toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void loadProfessions() {
        professions.clear();
        createTableIfNotExists(); // Создаем таблицу, если она не существует

        String sql = "SELECT * FROM Professions";
        try (Connection conn = DBConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                professions.add(new Profession(
                        rs.getInt("ID"),
                        rs.getString("Name")
                ));
            }
        } catch (SQLException ex) {
            showError("Ошибка загрузки данных: " + ex.getMessage());
        }
    }

    private void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS Professions (" +
                "ID SERIAL PRIMARY KEY, " +
                "Name VARCHAR(100) NOT NULL)";

        try (Connection conn = DBConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.executeUpdate();
        } catch (SQLException ex) {
            showError("Ошибка создания таблицы Professions: " + ex.getMessage());
        }
    }

    private void addProfession() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Добавление профессии");
        dialog.setHeaderText("Введите название профессии:");
        dialog.setContentText("Название:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> {
            if (!name.isEmpty()) {
                String sql = "INSERT INTO Professions (Name) VALUES (?)";
                try (Connection conn = DBConfig.getConnection();
                     PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, name);
                    pstmt.executeUpdate();
                    loadProfessions(); // Обновляем таблицу
                } catch (SQLException ex) {
                    showError("Ошибка добавления профессии: " + ex.getMessage());
                }
            } else {
                showError("Название профессии не может быть пустым.");
            }
        });
    }

    private void editProfession() {
        Profession selectedProfession = table.getSelectionModel().getSelectedItem();
        if (selectedProfession != null) {
            TextInputDialog dialog = new TextInputDialog(selectedProfession.getName());
            dialog.setTitle("Редактирование профессии");
            dialog.setHeaderText("Введите новое название профессии:");
            dialog.setContentText("Название:");

            Optional<String> result = dialog.showAndWait();
            result.ifPresent(name -> {
                if (!name.isEmpty()) {
                    String sql = "UPDATE Professions SET Name = ? WHERE ID = ?";
                    try (Connection conn = DBConfig.getConnection();
                         PreparedStatement pstmt = conn.prepareStatement(sql)) {
                        pstmt.setString(1, name);
                        pstmt.setInt(2, selectedProfession.getId());
                        pstmt.executeUpdate();
                        loadProfessions(); // Обновляем таблицу
                    } catch (SQLException ex) {
                        showError("Ошибка изменения профессии: " + ex.getMessage());
                    }
                } else {
                    showError("Название профессии не может быть пустым.");
                }
            });
        } else {
            showError("Выберите профессию для редактирования.");
        }
    }

    private void deleteProfession() {
        Profession selectedProfession = table.getSelectionModel().getSelectedItem();
        if (selectedProfession != null) {
            Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
            confirmDialog.setTitle("Подтверждение удаления");
            confirmDialog.setHeaderText("Вы уверены, что хотите удалить профессию?");
            confirmDialog.setContentText("Профессия: " + selectedProfession.getName());

            Optional<ButtonType> result = confirmDialog.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                String sql = "DELETE FROM Professions WHERE ID = ?";
                try (Connection conn = DBConfig.getConnection();
                     PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setInt(1, selectedProfession.getId());
                    pstmt.executeUpdate();
                    loadProfessions(); // Обновляем таблицу
                } catch (SQLException ex) {
                    showError("Ошибка удаления профессии: " + ex.getMessage());
                }
            }
        } else {
            showError("Выберите профессию для удаления.");
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Класс для хранения данных о профессии
    public static class Profession {
        private final int id;
        private final String name;

        public Profession(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }
}