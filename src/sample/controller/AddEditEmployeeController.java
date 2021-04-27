package sample.controller;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.WindowEvent;
import sample.model.DataBaseManager;
import sample.model.Department;
import sample.model.Employer;

public class AddEditEmployeeController {

    @FXML
    private Button addEditButton;

    @FXML
    private TextField nameTextField;

    @FXML
    private TextField lastNameTextField;

    @FXML
    private TextField patherTextField;

    @FXML
    private TextField positionTextField;

    @FXML
    private TextField salaryTextField;

    @FXML
    private ComboBox<Department> departmentComboBox;

    private boolean isEdit = false;

    private Employer employer;

    @FXML
    void initialize() {
        ObservableList<Department> departments = FXCollections.observableArrayList(getDepartments());
        departmentComboBox.setItems(departments);
        salaryTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                salaryTextField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
    }

    @FXML
    void addEditButtonAction() {
        if (!isEdit) {
            addEmployee();
        } else {
            editEmployee();
        }
    }

    private ArrayList<Department> getDepartments() {
        ArrayList<Department> array = new ArrayList<>();
        try {
            array = DataBaseManager.getShared().getDepartments();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return array;
    }

    public void setData(Employer employer) {
        this.employer = employer;
        nameTextField.setText(employer.getName());
        lastNameTextField.setText(employer.getLastName());
        patherTextField.setText(employer.getPartherName());
        salaryTextField.setText(employer.getSalary().toString());
        positionTextField.setText(employer.getPosition());
        if (employer.getDepartment() != null)
            departmentComboBox.getSelectionModel().select(new Department(employer.getDepartment().getId(), employer.getDepartment().getName()));
        addEditButton.setText("Изменить");
        isEdit = true;
    }

    private void addEmployee() {
        if (!nameTextField.getText().isEmpty() && !lastNameTextField.getText().isEmpty() && !patherTextField.getText().isEmpty()) {
            Integer depId;
            String depName;
            Department department = null;
            if (departmentComboBox.getSelectionModel().getSelectedItem() != null) {
                depId = departmentComboBox.getSelectionModel().getSelectedItem().getId();
                depName = departmentComboBox.getSelectionModel().getSelectedItem().getName();
                department = new Department(depId, depName);
            }
            int salary;
            try {
                salary = Integer.parseInt(salaryTextField.getText());
            } catch (NumberFormatException exception) {
                salary = 0;
            }
            employer = new Employer(nameTextField.getText(), lastNameTextField.getText(), patherTextField.getText(),
                    positionTextField.getText(), salary, department);
            try {
                DataBaseManager.getShared().insertEmployee(employer);
                addEditButton.getScene().getWindow().fireEvent(new WindowEvent(addEditButton.getScene().getWindow(), WindowEvent.WINDOW_CLOSE_REQUEST));
            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
                showAlert("Ошибка запроса", "Ошибка во время выполнения запроса!", Alert.AlertType.ERROR);
            }
            nameTextField.clear();
            lastNameTextField.clear();
            patherTextField.clear();
            salaryTextField.clear();
            positionTextField.clear();
        } else {
            showAlert("Некоректные данные", "Поле Имя, Фамилия или Отчество не заполнено.", Alert.AlertType.WARNING);
        }
    }

    private void editEmployee() {
        if (!nameTextField.getText().isEmpty() && !lastNameTextField.getText().isEmpty() && !patherTextField.getText().isEmpty()) {
            Integer depId;
            Integer idEmp = employer.getId();
            System.out.println(idEmp);
            String depName;
            Department department = null;
            if (departmentComboBox.getSelectionModel().getSelectedItem() != null) {
                depId = departmentComboBox.getSelectionModel().getSelectedItem().getId();
                depName = departmentComboBox.getSelectionModel().getSelectedItem().getName();
                department = new Department(depId, depName);
            }
            int salary;
            try {
                salary = Integer.parseInt(salaryTextField.getText());
            } catch (NumberFormatException exception) {
                salary = 0;
            }
            employer = new Employer(idEmp, nameTextField.getText(), lastNameTextField.getText(), patherTextField.getText(),
                    positionTextField.getText(), salary, department);
            try {
                if (showQuestionAlert()) {
                    DataBaseManager.getShared().updateEmployee(employer);
                    addEditButton.getScene().getWindow().fireEvent(new WindowEvent(addEditButton.getScene().getWindow(), WindowEvent.WINDOW_CLOSE_REQUEST));
                }
            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
                showAlert("Ошибка запроса", "Ошибка во время выполнения запроса!", Alert.AlertType.ERROR);
            }
        } else {
            showAlert("Некоректные данные", "Поле Имя, Фамилия или Отчество не заполнено.", Alert.AlertType.WARNING);
        }
    }

    private void showAlert(String title ,String message,  Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(message);
        alert.showAndWait();
    }

    private boolean showQuestionAlert() {
        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.setTitle("Внести изменения?");
        alert.setHeaderText("Вы уверены что хотите внести изменения в запись?");
        ButtonType yes = new ButtonType("Да");
        alert.getButtonTypes().addAll(yes);
        alert.getButtonTypes().addAll(new ButtonType("Нет"));
        Optional<ButtonType> result = alert.showAndWait();
        return result.get() == yes;
    }

}
