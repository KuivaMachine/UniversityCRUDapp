package org.example.elizarov_bd;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
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
import java.time.LocalDate;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;

public class HelloApplication extends Application {
    private final TableView<Employee> table = new TableView<>();
    ObservableList<Employee> employees = loadDataFromDatabase();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        table.setItems(employees);
        table.setPrefSize(700, 800);
        primaryStage.setTitle("Управление персоналом");

        // Заголовок
        Label titleLabel = new Label("Курсовая работа по предмету Базы данных");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label subtitleLabel = new Label("Елизаров А.Е. ЗИИ-331");
        subtitleLabel.setStyle("-fx-font-size: 15px; -fx-font-weight: bold;");

        // Создание колонок таблицы
        TableColumn<Employee, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Employee, String> famColumn = new TableColumn<>("Фамилия");
        famColumn.setCellValueFactory(new PropertyValueFactory<>("fam"));

        TableColumn<Employee, String> imColumn = new TableColumn<>("Имя");
        imColumn.setCellValueFactory(new PropertyValueFactory<>("im"));

        TableColumn<Employee, String> otchColumn = new TableColumn<>("Отчество");
        otchColumn.setCellValueFactory(new PropertyValueFactory<>("otch"));

        TableColumn<Employee, String> dolColumn = new TableColumn<>("Должность");
        dolColumn.setCellValueFactory(new PropertyValueFactory<>("dol"));

        TableColumn<Employee, Double> oklColumn = new TableColumn<>("Оклад");
        oklColumn.setCellValueFactory(new PropertyValueFactory<>("okl"));

        TableColumn<Employee, String> tnColumn = new TableColumn<>("Табельный номер");
        tnColumn.setCellValueFactory(new PropertyValueFactory<>("tn"));

        TableColumn<Employee, String> drogColumn = new TableColumn<>("Дата рождения");
        drogColumn.setCellValueFactory(new PropertyValueFactory<>("drog"));

        TableColumn<Employee, Integer> kolDetColumn = new TableColumn<>("Количество детей");
        kolDetColumn.setCellValueFactory(new PropertyValueFactory<>("kolDet"));

        TableColumn<Employee, String> datUColumn = new TableColumn<>("Дата увольнения");
        datUColumn.setCellValueFactory(new PropertyValueFactory<>("datU"));

        TableColumn<Employee, Boolean> otpColumn = new TableColumn<>("Отпуск");
        otpColumn.setCellValueFactory(new PropertyValueFactory<>("otp"));

        TableColumn<Employee, String> obrColumn = new TableColumn<>("Образование");
        obrColumn.setCellValueFactory(new PropertyValueFactory<>("obr"));

        TableColumn<Employee, String> podrColumn = new TableColumn<>("Подразделение");
        podrColumn.setCellValueFactory(new PropertyValueFactory<>("podr"));

        // Добавление колонок в таблицу
        Collections.addAll(table.getColumns(), idColumn, famColumn, imColumn, otchColumn, dolColumn, oklColumn, tnColumn, drogColumn, kolDetColumn, datUColumn, otpColumn, obrColumn, podrColumn);

        // Кнопки меню
        Button personalCardsButton = new Button("Работа с личными карточками");
        personalCardsButton.setOnAction(e -> showPersonalCardsWindow());

        Button movementInfoButton = new Button("Сбор информации по движению кадров");
        movementInfoButton.setOnAction(e -> showMovementInfoWindow());

        Button referenceBooksButton = new Button("Создание и ведение вспомогательных справочников");
        referenceBooksButton.setOnAction(e -> showReferenceBooksWindow());

        Button timeSheetInfoButton = new Button("Сбор информации и печать справок по табельному учету");
        timeSheetInfoButton.setOnAction(e -> showTimeSheetInfoWindow());

        Button exitButton = new Button("Выход");
        exitButton.setOnAction(e -> primaryStage.close());

        // Панель для кнопок меню
        HBox menuPanel = new HBox(10, exitButton);
        menuPanel.setAlignment(Pos.BOTTOM_RIGHT);

        // Основной макет
        VBox root = new VBox(10, titleLabel, subtitleLabel, table, personalCardsButton, movementInfoButton, referenceBooksButton, timeSheetInfoButton, menuPanel);
        VBox.setMargin(personalCardsButton, new Insets(20, 0, 0, 0));
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

    private ObservableList<Employee> loadDataFromDatabase() {
        ObservableList<Employee> employees = FXCollections.observableArrayList();

        try (Connection connection = DBConfig.getConnection()) {
            String query = "SELECT * FROM Employees";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String fam = resultSet.getString("FAM");
                String im = resultSet.getString("IM");
                String otch = resultSet.getString("OTCH");
                String dol = resultSet.getString("DOL");
                double okl = resultSet.getDouble("OKL");
                String tn = resultSet.getString("TN");
                String drog = resultSet.getString("DROG");
                int kolDet = resultSet.getInt("KOL_DET");
                String datU = resultSet.getString("DAT_U");
                boolean otp = resultSet.getBoolean("OTP");
                String obr = resultSet.getString("OBR");
                String podr = resultSet.getString("PODR");

                employees.add(new Employee(id, fam, im, otch, dol, okl, tn, drog, kolDet, datU, otp, obr, podr));

            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return employees;
    }

    private void showPersonalCardsWindow() {
        Stage stage = new Stage();
        stage.setTitle("Работа с личными карточками");

        Button addEmployeeButton = new Button("Добавить сотрудника");
        addEmployeeButton.setOnAction(e -> addEmployee());

        Button editEmployeeButton = new Button("Корректировка");
        editEmployeeButton.setOnAction(e -> editEmployee());

        Button deleteEmployeeButton = new Button("Удаление");
        deleteEmployeeButton.setOnAction(e -> deleteEmployee());

        Button returnButton = new Button("Возврат в меню");
        returnButton.setOnAction(e -> stage.close());

        VBox layout = new VBox(10, addEmployeeButton, editEmployeeButton, deleteEmployeeButton, returnButton);
        layout.setPadding(new Insets(10));
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout, 400, 500);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/main_style.css")).toExternalForm());

        stage.setScene(scene);
        stage.show();
    }

    private void showMovementInfoWindow() {
        Stage stage = new Stage();
        stage.setTitle("Сбор информации по движению кадров");

        Button hireButton = new Button("Прием");
        hireButton.setOnAction(e -> hireEmployee());

        Button transferButton = new Button("Перевод");
        transferButton.setOnAction(e -> transferEmployee());

        Button dismissButton = new Button("Увольнение");
        dismissButton.setOnAction(e -> dismissEmployee());

        Button vacationButton = new Button("Отпуск");
        vacationButton.setOnAction(e -> manageVacation());

        Button returnButton = new Button("Возврат в меню");
        returnButton.setOnAction(e -> stage.close());

        VBox layout = new VBox(10, hireButton, transferButton, dismissButton, vacationButton, returnButton);
        layout.setPadding(new Insets(10));
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout, 400, 500);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/main_style.css")).toExternalForm());

        stage.setScene(scene);
        stage.show();
    }

    private void showReferenceBooksWindow() {
        Stage stage = new Stage();
        stage.setTitle("Создание и ведение вспомогательных справочников");

        Button professionButton = new Button("По профессии");
        professionButton.setOnAction(e -> manageProfession());

        Button educationButton = new Button("По образованию (ученой степени)");
        educationButton.setOnAction(e -> manageEducation());

        Button salaryButton = new Button("По зарплате");
        salaryButton.setOnAction(e -> manageSalary());

        Button returnButton = new Button("Возврат в меню");
        returnButton.setOnAction(e -> stage.close());

        VBox layout = new VBox(10, professionButton, educationButton, salaryButton, returnButton);
        layout.setPadding(new Insets(10));
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout, 400, 500);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/main_style.css")).toExternalForm());

        stage.setScene(scene);
        stage.show();
    }

    private void showTimeSheetInfoWindow() {
        Stage stage = new Stage();
        stage.setTitle("Сбор информации и печать справок по табельному учету");

        Button dismissedButton = new Button("Количество уволенных");
        dismissedButton.setOnAction(e -> showDismissedCount());

        Button laidOffButton = new Button("Количество сокращенных");
        laidOffButton.setOnAction(e -> showLaidOffCount());

        Button sickLeaveButton = new Button("Количество б/л");
        sickLeaveButton.setOnAction(e -> showSickLeaveCount());

        Button returnButton = new Button("Возврат в меню");
        returnButton.setOnAction(e -> stage.close());

        VBox layout = new VBox(10, dismissedButton, laidOffButton, sickLeaveButton, returnButton);
        layout.setPadding(new Insets(10));
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout, 400, 500);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/main_style.css")).toExternalForm());

        stage.setScene(scene);
        stage.show();
    }

    private void highlightEmptyField(TextField field, String promptText) {
        field.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
        field.setPromptText("Поле не должно быть пустым");
    }

    private void resetFieldStyle(TextField field, String promptText) {
        field.setStyle(""); // Сброс стиля
        field.setPromptText(promptText); // Возврат оригинальной подсказки
    }

    private void addEmployee() {
        Stage stage = new Stage();
        stage.setTitle("Добавление сотрудника");

        // Поля для ввода данных с подсказками
        TextField famField = new TextField();
        famField.setPromptText("Фамилия");

        TextField imField = new TextField();
        imField.setPromptText("Имя");

        TextField otchField = new TextField();
        otchField.setPromptText("Отчество");

        TextField dolField = new TextField();
        dolField.setPromptText("Должность");

        TextField oklField = new TextField();
        oklField.setPromptText("Оклад");

        TextField tnField = new TextField();
        tnField.setPromptText("Табельный номер");

        DatePicker drogPicker = new DatePicker();
        drogPicker.setPromptText("Дата рождения");

        TextField kolDetField = new TextField();
        kolDetField.setPromptText("Количество детей");

        DatePicker datUPicker = new DatePicker();
        datUPicker.setPromptText("Дата увольнения");

        CheckBox otpCheckBox = new CheckBox("В отпуске");

        TextField obrField = new TextField();
        obrField.setPromptText("Образование");

        TextField podrField = new TextField();
        podrField.setPromptText("Подразделение");

        Button addButton = new Button("Добавить");
        addButton.setOnAction(e -> {
            // Сброс стилей и подсказок
            resetFieldStyle(famField, "Фамилия");
            resetFieldStyle(imField, "Имя");
            resetFieldStyle(otchField, "Отчество");
            resetFieldStyle(dolField, "Должность");
            resetFieldStyle(oklField, "Оклад");
            resetFieldStyle(tnField, "Табельный номер");
            resetFieldStyle(kolDetField, "Количество детей");
            resetFieldStyle(obrField, "Образование");
            resetFieldStyle(podrField, "Подразделение");

            // Проверка заполнения полей
            boolean isValid = true;

            if (famField.getText().isEmpty()) {
                highlightEmptyField(famField, "Фамилия");
                isValid = false;
            }
            if (imField.getText().isEmpty()) {
                highlightEmptyField(imField, "Имя");
                isValid = false;
            }
            if (otchField.getText().isEmpty()) {
                highlightEmptyField(otchField, "Отчество");
                isValid = false;
            }
            if (dolField.getText().isEmpty()) {
                highlightEmptyField(dolField, "Должность");
                isValid = false;
            }
            if (oklField.getText().isEmpty()) {
                highlightEmptyField(oklField, "Оклад");
                isValid = false;
            }
            if (tnField.getText().isEmpty()) {
                highlightEmptyField(tnField, "Табельный номер");
                isValid = false;
            }
            if (kolDetField.getText().isEmpty()) {
                highlightEmptyField(kolDetField, "Количество детей");
                isValid = false;
            }
            if (obrField.getText().isEmpty()) {
                highlightEmptyField(obrField, "Образование");
                isValid = false;
            }
            if (podrField.getText().isEmpty()) {
                highlightEmptyField(podrField, "Подразделение");
                isValid = false;
            }

            // Если все поля заполнены, добавляем сотрудника
            if (isValid) {
                try {
                    String fam = famField.getText();
                    String im = imField.getText();
                    String otch = otchField.getText();
                    String dol = dolField.getText();
                    double okl = Double.parseDouble(oklField.getText());
                    String tn = tnField.getText();
                    LocalDate drog = drogPicker.getValue();
                    int kolDet = Integer.parseInt(kolDetField.getText());
                    LocalDate datU = datUPicker.getValue();
                    boolean otp = otpCheckBox.isSelected();
                    String obr = obrField.getText();
                    String podr = podrField.getText();

                    // Добавление в базу данных
                    try (Connection connection = DBConfig.getConnection()) {
                        String query = "INSERT INTO Employees (FAM, IM, OTCH, DOL, OKL, TN, DROG, KOL_DET, DAT_U, OTP, OBR, PODR) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                        PreparedStatement statement = connection.prepareStatement(query);
                        statement.setString(1, fam);
                        statement.setString(2, im);
                        statement.setString(3, otch);
                        statement.setString(4, dol);
                        statement.setDouble(5, okl);
                        statement.setString(6, tn);
                        statement.setDate(7, java.sql.Date.valueOf(drog));
                        statement.setInt(8, kolDet);
                        statement.setDate(9, datU != null ? java.sql.Date.valueOf(datU) : null);
                        statement.setBoolean(10, otp);
                        statement.setString(11, obr);
                        statement.setString(12, podr);
                        statement.executeUpdate();
                    }

                    // Обновление таблицы
                    employees.setAll(loadDataFromDatabase());
                    stage.close();
                } catch (Exception ex) {
                    System.out.println("Ошибка: " + ex.getMessage());
                }
            }
        });

        VBox layout = new VBox(10,
                famField, imField, otchField, dolField, oklField, tnField,
                drogPicker, kolDetField, datUPicker, otpCheckBox, obrField, podrField,
                addButton);
        layout.setPadding(new Insets(10));

        Scene scene = new Scene(layout, 400, 600);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/main_style.css")).toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    private void editEmployee() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Редактирование сотрудника");
        dialog.setHeaderText("Введите ID сотрудника:");
        dialog.setContentText("ID:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(id -> {
            try {
                int employeeId = Integer.parseInt(id);
                Employee employee = employees.stream()
                        .filter(e -> e.getId() == employeeId)
                        .findFirst()
                        .orElse(null);

                if (employee != null) {
                    Stage stage = new Stage();
                    stage.setTitle("Редактирование сотрудника");

                    // Поля для ввода данных с подсказками
                    TextField famField = new TextField(employee.getFam());
                    famField.setPromptText("Фамилия");

                    TextField imField = new TextField(employee.getIm());
                    imField.setPromptText("Имя");

                    TextField otchField = new TextField(employee.getOtch());
                    otchField.setPromptText("Отчество");

                    TextField dolField = new TextField(employee.getDol());
                    dolField.setPromptText("Должность");

                    TextField oklField = new TextField(String.valueOf(employee.getOkl()));
                    oklField.setPromptText("Оклад");

                    TextField tnField = new TextField(employee.getTn());
                    tnField.setPromptText("Табельный номер");

                    DatePicker drogPicker = new DatePicker(LocalDate.parse(employee.getDrog()));
                    drogPicker.setPromptText("Дата рождения");

                    TextField kolDetField = new TextField(String.valueOf(employee.getKolDet()));
                    kolDetField.setPromptText("Количество детей");

                    DatePicker datUPicker = new DatePicker(employee.getDatU() != null ? LocalDate.parse(employee.getDatU()) : null);
                    datUPicker.setPromptText("Дата увольнения");

                    CheckBox otpCheckBox = new CheckBox("В отпуске");
                    otpCheckBox.setSelected(employee.isOtp());

                    TextField obrField = new TextField(employee.getObr());
                    obrField.setPromptText("Образование");

                    TextField podrField = new TextField(employee.getPodr());
                    podrField.setPromptText("Подразделение");

                    Button saveButton = new Button("Сохранить");
                    saveButton.setOnAction(e -> {
                        // Сброс стилей и подсказок
                        resetFieldStyle(famField, "Фамилия");
                        resetFieldStyle(imField, "Имя");
                        resetFieldStyle(otchField, "Отчество");
                        resetFieldStyle(dolField, "Должность");
                        resetFieldStyle(oklField, "Оклад");
                        resetFieldStyle(tnField, "Табельный номер");
                        resetFieldStyle(kolDetField, "Количество детей");
                        resetFieldStyle(obrField, "Образование");
                        resetFieldStyle(podrField, "Подразделение");

                        // Проверка заполнения полей
                        boolean isValid = true;

                        if (famField.getText().isEmpty()) {
                            highlightEmptyField(famField, "Фамилия");
                            isValid = false;
                        }
                        if (imField.getText().isEmpty()) {
                            highlightEmptyField(imField, "Имя");
                            isValid = false;
                        }
                        if (otchField.getText().isEmpty()) {
                            highlightEmptyField(otchField, "Отчество");
                            isValid = false;
                        }
                        if (dolField.getText().isEmpty()) {
                            highlightEmptyField(dolField, "Должность");
                            isValid = false;
                        }
                        if (oklField.getText().isEmpty()) {
                            highlightEmptyField(oklField, "Оклад");
                            isValid = false;
                        }
                        if (tnField.getText().isEmpty()) {
                            highlightEmptyField(tnField, "Табельный номер");
                            isValid = false;
                        }
                        if (kolDetField.getText().isEmpty()) {
                            highlightEmptyField(kolDetField, "Количество детей");
                            isValid = false;
                        }
                        if (obrField.getText().isEmpty()) {
                            highlightEmptyField(obrField, "Образование");
                            isValid = false;
                        }
                        if (podrField.getText().isEmpty()) {
                            highlightEmptyField(podrField, "Подразделение");
                            isValid = false;
                        }

                        // Если все поля заполнены, сохраняем изменения
                        if (isValid) {
                            try {
                                String fam = famField.getText();
                                String im = imField.getText();
                                String otch = otchField.getText();
                                String dol = dolField.getText();
                                double okl = Double.parseDouble(oklField.getText());
                                String tn = tnField.getText();
                                LocalDate drog = drogPicker.getValue();
                                int kolDet = Integer.parseInt(kolDetField.getText());
                                LocalDate datU = datUPicker.getValue();
                                boolean otp = otpCheckBox.isSelected();
                                String obr = obrField.getText();
                                String podr = podrField.getText();

                                // Обновление в базе данных
                                try (Connection connection = DBConfig.getConnection()) {
                                    String query = "UPDATE Employees SET FAM=?, IM=?, OTCH=?, DOL=?, OKL=?, TN=?, DROG=?, KOL_DET=?, DAT_U=?, OTP=?, OBR=?, PODR=? WHERE id=?";
                                    PreparedStatement statement = connection.prepareStatement(query);
                                    statement.setString(1, fam);
                                    statement.setString(2, im);
                                    statement.setString(3, otch);
                                    statement.setString(4, dol);
                                    statement.setDouble(5, okl);
                                    statement.setString(6, tn);
                                    statement.setDate(7, java.sql.Date.valueOf(drog));
                                    statement.setInt(8, kolDet);
                                    statement.setDate(9, datU != null ? java.sql.Date.valueOf(datU) : null);
                                    statement.setBoolean(10, otp);
                                    statement.setString(11, obr);
                                    statement.setString(12, podr);
                                    statement.setInt(13, employeeId);
                                    statement.executeUpdate();
                                }

                                // Обновление таблицы
                                employees.setAll(loadDataFromDatabase());
                                stage.close();
                            } catch (Exception ex) {
                                System.out.println("Ошибка: " + ex.getMessage());
                            }
                        }
                    });

                    VBox layout = new VBox(10,
                            famField, imField, otchField, dolField, oklField, tnField,
                            drogPicker, kolDetField, datUPicker, otpCheckBox, obrField, podrField,
                            saveButton);
                    layout.setPadding(new Insets(10));

                    Scene scene = new Scene(layout, 400, 600);
                    scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/main_style.css")).toExternalForm());
                    stage.setScene(scene);
                    stage.show();
                } else {
                    System.out.println("Сотрудник с ID " + employeeId + " не найден.");
                }
            } catch (NumberFormatException ex) {
                System.out.println("Некорректный ID.");
            }
        });
    }

    private void deleteEmployee() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Удаление сотрудника");
        dialog.setHeaderText("Введите ID сотрудника:");
        dialog.setContentText("ID:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(id -> {
            try {
                int employeeId = Integer.parseInt(id);

                // Проверка, существует ли сотрудник с таким ID
                Employee employee = employees.stream()
                        .filter(e -> e.getId() == employeeId)
                        .findFirst()
                        .orElse(null);

                if (employee != null) {
                    // Удаление из базы данных
                    try (Connection connection = DBConfig.getConnection()) {
                        String query = "DELETE FROM Employees WHERE id=?";
                        PreparedStatement statement = connection.prepareStatement(query);
                        statement.setInt(1, employeeId);
                        statement.executeUpdate();
                    }

                    // Обновление таблицы
                    employees.setAll(loadDataFromDatabase());

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Удаление сотрудника");
                    alert.setHeaderText(null);
                    alert.setContentText("Сотрудник с ID " + employeeId + " успешно удален.");
                    alert.showAndWait();
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Ошибка");
                    alert.setHeaderText(null);
                    alert.setContentText("Сотрудник с ID " + employeeId + " не найден.");
                    alert.showAndWait();
                }
            } catch (NumberFormatException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Ошибка");
                alert.setHeaderText(null);
                alert.setContentText("Некорректный ID. Введите число.");
                alert.showAndWait();
            } catch (SQLException ex) {
                System.out.println("Ошибка: " + ex.getMessage());
            }
        });
    }

    private void hireEmployee() {
        addEmployee();
    }

    private void transferEmployee() {
        Stage stage = new Stage();
        stage.setTitle("Перевод сотрудника");
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Перевод сотрудника");
        dialog.setHeaderText("Введите ID сотрудника:");
        dialog.setContentText("ID:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(id -> {
            try {
                int employeeId = Integer.parseInt(id);

                // Поле для ввода нового подразделения
                TextField podrField = new TextField();
                podrField.setPromptText("Новое подразделение");

                Button saveButton = new Button("Сохранить");
                saveButton.setOnAction(e -> {
                    // Сброс стиля и подсказки
                    resetFieldStyle(podrField, "Новое подразделение");

                    // Проверка заполнения поля
                    if (podrField.getText().isEmpty()) {
                        highlightEmptyField(podrField, "Новое подразделение");
                    } else {
                        try (Connection connection = DBConfig.getConnection()) {
                            String query = "UPDATE Employees SET PODR=? WHERE id=?";
                            PreparedStatement statement = connection.prepareStatement(query);
                            statement.setString(1, podrField.getText());
                            statement.setInt(2, employeeId);
                            statement.executeUpdate();
                            stage.close();
                        } catch (SQLException ex) {
                            System.out.println("Ошибка: " + ex.getMessage());
                        }

                        // Обновление таблицы
                        employees.setAll(loadDataFromDatabase());
                    }
                });

                VBox layout = new VBox(10, podrField, saveButton);
                layout.setPadding(new Insets(10));


                Scene scene = new Scene(layout, 300, 150);
                scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/main_style.css")).toExternalForm());
                stage.setScene(scene);
                stage.show();
            } catch (NumberFormatException ex) {
                System.out.println("Некорректный ID.");
            }
        });
    }

    private void dismissEmployee() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Увольнение сотрудника");
        dialog.setHeaderText("Введите ID сотрудника:");
        dialog.setContentText("ID:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(id -> {
            try {
                int employeeId = Integer.parseInt(id);

                try (Connection connection = DBConfig.getConnection()) {
                    String query = "UPDATE Employees SET DAT_U=? WHERE id=?";
                    PreparedStatement statement = connection.prepareStatement(query);
                    statement.setDate(1, java.sql.Date.valueOf(LocalDate.now()));
                    statement.setInt(2, employeeId);
                    statement.executeUpdate();
                }

                // Обновление таблицы
                employees.setAll(loadDataFromDatabase());
            } catch (Exception ex) {
                System.out.println("Ошибка: " + ex.getMessage());
            }
        });
    }

    private void manageVacation() {
        Stage stage = new Stage();
        stage.setTitle("Управление отпусками");
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Управление отпусками");
        dialog.setHeaderText("Введите ID сотрудника:");
        dialog.setContentText("ID:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(id -> {
            try {
                int employeeId = Integer.parseInt(id);

                Employee employee = employees.stream()
                        .filter(e -> e.getId() == employeeId)
                        .findFirst()
                        .orElse(null);

                if (employee != null) {
                    CheckBox otpCheckBox = new CheckBox("В отпуске");
                    otpCheckBox.setSelected(employee.isOtp());

                    Button saveButton = new Button("Сохранить");
                    saveButton.setOnAction(e -> {
                        try (Connection connection = DBConfig.getConnection()) {
                            String query = "UPDATE Employees SET OTP=? WHERE id=?";
                            PreparedStatement statement = connection.prepareStatement(query);
                            statement.setBoolean(1, otpCheckBox.isSelected());
                            statement.setInt(2, employeeId);
                            statement.executeUpdate();
                            stage.close();
                        } catch (SQLException ex) {
                            System.out.println("Ошибка: " + ex.getMessage());
                        }

                        // Обновление таблицы
                        employees.setAll(loadDataFromDatabase());
                    });

                    VBox layout = new VBox(10, otpCheckBox, saveButton);
                    layout.setPadding(new Insets(10));


                    Scene scene = new Scene(layout, 300, 150);
                    scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/main_style.css")).toExternalForm());
                    stage.setScene(scene);
                    stage.show();
                } else {
                    System.out.println("Сотрудник с ID " + employeeId + " не найден.");
                }
            } catch (NumberFormatException ex) {
                System.out.println("Некорректный ID.");
            }
        });
    }

    private void manageProfession() {
        Stage stage = new Stage();
        stage.setTitle("Справочник по профессиям");
        ProfessionWindow professionWindow= new ProfessionWindow();
        professionWindow.start(stage);
    }

    private void manageEducation() {
        Stage stage = new Stage();
        stage.setTitle("Справочник по образованию");
        EducationWindow educationWindow = new EducationWindow();
        educationWindow.start(stage);
    }

    private void manageSalary() {
        Stage stage = new Stage();
        stage.setTitle("Справочник по зарплате");
        SalaryWindow salaryWindow = new SalaryWindow();
        salaryWindow.start(stage);
    }

    private void showDismissedCount() {
        try (Connection connection = DBConfig.getConnection()) {
            String query = "SELECT COUNT(*) AS dismissed_count FROM Employees WHERE DAT_U IS NOT NULL";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int dismissedCount = resultSet.getInt("dismissed_count");
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Количество уволенных");
                alert.setHeaderText(null);
                alert.setContentText("Количество уволенных сотрудников: " + dismissedCount);
                alert.showAndWait();
            }
        } catch (SQLException ex) {
            System.out.println("Ошибка: " + ex.getMessage());
        }
    }

    private void showLaidOffCount() {
        try (Connection connection = DBConfig.getConnection()) {
            String query = "SELECT COUNT(*) AS lim_count FROM Employees WHERE dol='Сокращен';";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int limCount = resultSet.getInt("lim_count");
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Количество сокращенных");
                alert.setHeaderText(null);
                alert.setContentText("Количество сокращенных сотрудников: " + limCount);
                alert.showAndWait();
            }
        } catch (SQLException ex) {
            System.out.println("Ошибка: " + ex.getMessage());
        }
    }

    private void showSickLeaveCount() {
        try (Connection connection = DBConfig.getConnection()) {
            String query = "SELECT COUNT(*) AS lim_count FROM Employees WHERE otp=true;";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int limCount = resultSet.getInt("lim_count");
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Количество больничных");
                alert.setHeaderText(null);
                alert.setContentText("Количество больничных: " + limCount);
                alert.showAndWait();
            }
        } catch (SQLException ex) {
            System.out.println("Ошибка: " + ex.getMessage());
        }
    }
}