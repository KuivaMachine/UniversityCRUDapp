package org.example.elizarov_bd;


import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class HelloApplication extends Application {
    private final TableView<Student> table = new TableView<>();
    ObservableList<Student> students = loadDataFromDatabase();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        table.setItems(students);
        table.setPrefSize(600, 500);
        primaryStage.setTitle("Курсовая работа по предмету Базы данных");

        // Заголовок
        Label titleLabel = new Label("Курсовая работа по предмету Базы данных");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label subtitleLabel = new Label("Елизаров А.Е. ЗИИ-331");
        subtitleLabel.setStyle("-fx-font-size: 15px; -fx-font-weight: bold;");


        // Создание колонок таблицы
        TableColumn<Student, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Student, String> lastNameColumn = new TableColumn<>("Фамилия");
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));

        TableColumn<Student, String> firstNameColumn = new TableColumn<>("Имя");
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));

        TableColumn<Student, String> fath = new TableColumn<>("Отчество");
        fath.setCellValueFactory(new PropertyValueFactory<>("fath"));

        TableColumn<Student, Integer> birthYearColumn = new TableColumn<>("Год рождения");
        birthYearColumn.setCellValueFactory(new PropertyValueFactory<>("birthYear"));

        TableColumn<Student, Integer> mark_1 = new TableColumn<>("12.02");
        mark_1.setCellValueFactory(new PropertyValueFactory<>("mark_1"));

        TableColumn<Student, Integer> mark_2 = new TableColumn<>("13.02");
        mark_2.setCellValueFactory(new PropertyValueFactory<>("mark_2"));

        TableColumn<Student, Integer> mark_3 = new TableColumn<>("14.02");
        mark_3.setCellValueFactory(new PropertyValueFactory<>("mark_3"));

        TableColumn<Student, Integer> mark_4 = new TableColumn<>("15.02");
        mark_4.setCellValueFactory(new PropertyValueFactory<>("mark_4"));

        TableColumn<Student, Integer> teach_surname = new TableColumn<>("Фамилия преподавателя");
        teach_surname.setCellValueFactory(new PropertyValueFactory<>("teach_surname"));

        TableColumn<Student, Integer> teach_name = new TableColumn<>("Имя преподавателя");
        teach_name.setCellValueFactory(new PropertyValueFactory<>("teach_name"));

        TableColumn<Student, Integer> teach_midname = new TableColumn<>("Отчество преподавателя");
        teach_midname.setCellValueFactory(new PropertyValueFactory<>("teach_midname"));

        // Добавление колонок в таблицу
        Collections.addAll(table.getColumns(), idColumn, lastNameColumn, firstNameColumn, fath, birthYearColumn, mark_1, mark_2, mark_3, mark_4, teach_surname, teach_name, teach_midname);


        // Выпадающий список для сортировки
        ComboBox<String> sortComboBox = new ComboBox<>();
        sortComboBox.getItems().addAll("ID", "Фамилия", "Имя", "Год рождения");
        sortComboBox.setValue("ID"); // Значение по умолчанию

        // Кнопка сортировки
        Button sortButton = new Button("Сортировать по");
        sortButton.setOnAction(e -> {
            String selectedAttribute = sortComboBox.getValue();
            switch (selectedAttribute) {
                case "ID":
                    students.sort((s1, s2) -> Integer.compare(s1.getId(), s2.getId()));
                    break;
                case "Фамилия":
                    students.sort((s1, s2) -> s1.getLastName().compareTo(s2.getLastName()));
                    break;
                case "Имя":
                    students.sort((s1, s2) -> s1.getFirstName().compareTo(s2.getFirstName()));
                    break;
                case "Год рождения":
                    students.sort((s1, s2) -> Integer.compare(s1.getBirthYear(), s2.getBirthYear()));
                    break;
            }
        });
        // Кнопка "Добавить студента"
        Button addStudentButton = new Button("Добавить студента");
        addStudentButton.setOnAction(e -> addStudent());
        // Кнопка "Удалить студента"
        Button deleteButton = new Button("Удалить студента");
        deleteButton.setOnAction(e -> deleteStudent());
        // Панель для кнопки и выпадающего списка
        HBox sortPanel = new HBox(10, sortButton, sortComboBox);
        sortPanel.setAlignment(Pos.CENTER);

        // Основной макет
        VBox root = new VBox(10, titleLabel, subtitleLabel, table, sortPanel, addStudentButton, deleteButton);
        root.setPadding(new Insets(10));
        root.setAlignment(Pos.TOP_CENTER);

        Image icon = new Image(Objects.requireNonNull(Objects.requireNonNull(getClass().getResource("/icon.png")).toExternalForm()));
        primaryStage.getIcons().add(icon);

        // Настройка сцены
        Scene scene = new Scene(root, 1200, 700);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/main_style.css")).toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private ObservableList<Student> loadDataFromDatabase() {
        ObservableList<Student> students = FXCollections.observableArrayList();

        try (Connection connection = DBConfig.getConnection()) {
            String query = "SELECT students.id, s_surname, s_name, s_fathland, birth,  mark_1, mark_2, mark_3, mark_4, t_surname, t_name, t_fathland\n" +
                    "FROM students LEFT JOIN teachers ON (students.teach_id=teachers.id);";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String lastName = resultSet.getString("s_surname");
                String firstName = resultSet.getString("s_name");
                String fath = resultSet.getString("s_fathland");
                String teach_name = resultSet.getString("t_name");
                String teach_surname = resultSet.getString("t_surname");
                String teach_midname = resultSet.getString("t_fathland");
                int birthYear = resultSet.getInt("birth");
                int mark_1 = resultSet.getInt("mark_1");
                int mark_2 = resultSet.getInt("mark_2");
                int mark_3 = resultSet.getInt("mark_3");
                int mark_4 = resultSet.getInt("mark_4");

                students.add(new Student(id, lastName, firstName, fath, birthYear, mark_1, mark_2, mark_3, mark_4, teach_surname, teach_name, teach_midname));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return students;
    }

    private void deleteStudent() {
        Stage stage = new Stage();
        Image icon = new Image(Objects.requireNonNull(Objects.requireNonNull(getClass().getResource("/delete.png")).toExternalForm()));
        stage.getIcons().add(icon);
        stage.setTitle("Удалить студента");
        ArrayList<TextField> fields = new ArrayList<>();
        TextField surnameField = new TextField();
        surnameField.setPromptText("Фамилия");
        TextField nameField = new TextField();
        nameField.setPromptText("Имя");
        TextField fathlandField = new TextField();
        fathlandField.setPromptText("Отчество");
        Collections.addAll(fields, surnameField, nameField, fathlandField);

        Button deleteButton = new Button("Удалить");
        deleteButton.setOnAction(e -> {
            boolean isAllFull = true;
            for (TextField field : fields) {
                field.setStyle("-fx-background-color: white; -fx-text-fill: black;");
                if (field.getText().isEmpty()) {
                    field.setPromptText("Окно не должно быть пустым!");
                    field.setStyle("-fx-background-color: #b0ff91; -fx-prompt-text-fill: #000000;-fx-border-color: #2d752f; ");
                    isAllFull = false;
                }
            }
            if (isAllFull) {
                // Получаем данные из полей
                String surname = surnameField.getText().strip();
                String name = nameField.getText().strip();
                String fathland = fathlandField.getText().strip();

                try (Connection connection = DBConfig.getConnection()) {
                    String query = "DELETE FROM students  WHERE s_surname=? AND s_name=? AND s_fathland=?";
                    PreparedStatement statement = connection.prepareStatement(query);
                    statement.setString(1, surname);
                    statement.setString(2, name);
                    statement.setString(3, fathland);
                    int rowsDeleted = statement.executeUpdate();
                    // Проверяем, была ли удалена запись
                    if (rowsDeleted > 0) {
                        // Создаем информационное окно
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Информация");
                        alert.setHeaderText("Студент успешно удален");
                        // Показываем окно и ждем, пока пользователь его закроет
                        students = loadDataFromDatabase();
                        table.setItems(students);
                        alert.showAndWait();
                        stage.close();
                    } else {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Информация");
                        alert.setHeaderText("В базе не существует записи с таким ФИО");
                        // Показываем окно и ждем, пока пользователь его закроет
                        alert.showAndWait();
                    }
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        });
        HBox but = new HBox(20, deleteButton);
        but.setAlignment(Pos.CENTER);
        // Макет для формы
        VBox formLayout = new VBox(4,
                surnameField,
                nameField,
                fathlandField, but
        );
        VBox.setMargin(but, new Insets(20, 0, 0, 0));
        formLayout.setPadding(new Insets(20));

        // Настройка сцены
        Scene scene = new Scene(formLayout, 400, 600);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/delete_style.css")).toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    private void addStudent() {
        Stage addStudentStage = new Stage();
        Image icon = new Image(Objects.requireNonNull(Objects.requireNonNull(getClass().getResource("/add.png")).toExternalForm()));
        addStudentStage.getIcons().add(icon);
        addStudentStage.setTitle("Добавить студента");

        ArrayList<TextField> fields = new ArrayList<>();
        TextField surnameField = new TextField();
        surnameField.setPromptText("Фамилия");
        TextField nameField = new TextField();
        nameField.setPromptText("Имя");
        TextField fathlandField = new TextField();
        fathlandField.setPromptText("Отчество");
        TextField birthField = new TextField();
        birthField.setPromptText("Год рождения");
        TextField mark1Field = new TextField();
        mark1Field.setPromptText("Первая оценка");
        TextField mark2Field = new TextField();
        mark2Field.setPromptText("Вторая оценка");
        TextField mark3Field = new TextField();
        mark3Field.setPromptText("Третья оценка");
        TextField mark4Field = new TextField();
        mark4Field.setPromptText("Четвертая оценка");
        TextField teach_id = new TextField();
        teach_id.setPromptText("ID преподавателя");
        Collections.addAll(fields, surnameField, nameField, fathlandField, birthField, mark1Field, mark2Field, mark3Field, mark4Field, teach_id);


        // Кнопка "Сохранить"
        Button saveButton = new Button("Сохранить");
        saveButton.setOnAction(e -> {
            boolean isAllFull = true;
            for (TextField field : fields) {
                field.setStyle("-fx-background-color: white; -fx-text-fill: black;");
                if (field.getText().isEmpty()) {
                    field.setPromptText("Окно не должно быть пустым!");
                    field.setStyle("-fx-background-color: #b0ff91; -fx-prompt-text-fill: #000000;-fx-border-color: #2d752f; ");
                    isAllFull = false;
                }
            }
            if (isAllFull) {
                // Получаем данные из полей
                String surname = surnameField.getText().strip();
                String name = nameField.getText().strip();
                String fathland = fathlandField.getText().strip();
                int birthYear = Integer.parseInt(birthField.getText());
                int mark1 = Integer.parseInt(mark1Field.getText());
                int mark2 = Integer.parseInt(mark2Field.getText());
                int mark3 = Integer.parseInt(mark3Field.getText());
                int mark4 = Integer.parseInt(mark4Field.getText());
                int teach_id_num = Integer.parseInt(teach_id.getText());


                try (Connection connection = DBConfig.getConnection()) {
                    String query = "INSERT INTO students  (s_surname, s_name, s_fathland, birth,  mark_1, mark_2, mark_3, mark_4,  teach_id) VALUES (?, ?, ?,?, ?, ?, ?, ?, ?)";
                    PreparedStatement statement = connection.prepareStatement(query);
                    statement.setString(1, surname);
                    statement.setString(2, name);
                    statement.setString(3, fathland);
                    statement.setInt(4, birthYear);
                    statement.setInt(5, mark1);
                    statement.setInt(6, mark2);
                    statement.setInt(7, mark3);
                    statement.setInt(8, mark4);
                    statement.setInt(9, teach_id_num);
                    statement.executeUpdate();
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
                // Создаем информационное окно
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Информация");
                alert.setHeaderText("Студент успешно добавлен");

                students = loadDataFromDatabase();
                table.setItems(students);
                // Показываем окно и ждем, пока пользователь его закроет
                alert.showAndWait();

                // Закрываем окно
                addStudentStage.close();
            }
        });
        HBox but = new HBox(10, saveButton);
        but.setAlignment(Pos.CENTER);
        // Макет для формы
        VBox formLayout = new VBox(4,
                surnameField,
                nameField,
                fathlandField,
                birthField,
                mark1Field,
                mark2Field,
                mark3Field,
                mark4Field,
                teach_id, but
        );
        formLayout.setPadding(new Insets(20));
        VBox.setMargin(but, new Insets(20, 0, 0, 0));

        // Настройка сцены
        Scene scene = new Scene(formLayout, 400, 600);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/add_style.css")).toExternalForm());
        addStudentStage.setScene(scene);
        addStudentStage.show();
    }


}

