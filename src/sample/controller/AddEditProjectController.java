package sample.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.WindowEvent;
import sample.model.DataBaseManager;
import sample.model.Department;
import sample.model.Project;

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

public class AddEditProjectController {

    @FXML
    private TextField nameTextField;

    @FXML
    private TextField priceTextField;

    @FXML
    private ComboBox<Department> departmentComboBox;

    @FXML
    private DatePicker beginDatePicker;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    private Button addEditButton;

    @FXML
    private Label realEndDateLabel;

    @FXML
    private DatePicker realEndDatePicker;

    private boolean isEdit = false;

    private Project project;

    @FXML
    void initialize() {
        ObservableList<Department> departments = FXCollections.observableArrayList(getDepartments());
        departmentComboBox.setItems(departments);
        priceTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                priceTextField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
        priceTextField.setText("0");
        beginDatePicker.setValue(LocalDate.now());
        endDatePicker.setValue(LocalDate.now().plusDays(1));
        realEndDatePicker.setValue(LocalDate.now().plusDays(1));
    }

    @FXML
    void addEditButtonAction() {
        if (!isEdit) {
            addProject();
        } else {
            editProject();
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

    public void setData(Project project) {
        this.project = project;
        nameTextField.setText(project.getName());
        priceTextField.setText(project.getPrice().toString());
        if (project.getDepartment() != null)
            departmentComboBox.getSelectionModel().select(project.getDepartment());
        if (project.getDateBeg() != null)
            beginDatePicker.setValue(project.getDateBeg().toLocalDate());
        if (project.getDateEnd() != null)
            endDatePicker.setValue(project.getDateEnd().toLocalDate());
        if (project.getDateEndReal() != null)
            realEndDatePicker.setValue(project.getDateEndReal().toLocalDate());
        addEditButton.setText("Изменить");
        realEndDatePicker.setVisible(true);
        realEndDateLabel.setVisible(true);
        isEdit = true;
    }

    private void addProject() {
        if (!nameTextField.getText().isEmpty()) {
            Department department = null;
            if (departmentComboBox.getSelectionModel().getSelectedItem() != null) {
                department = new Department(departmentComboBox.getSelectionModel().getSelectedItem().getId(), departmentComboBox.getSelectionModel().getSelectedItem().getName());
            }
            int cost;
            try {
                cost = Integer.parseInt(priceTextField.getText());
            } catch (NumberFormatException exception) {
                cost = 0;
            }
            Date begDate = null;
            Date endDate = null;
            if (!beginDatePicker.getEditor().getText().isEmpty())
                begDate = Date.valueOf(beginDatePicker.getValue());
            if (!endDatePicker.getEditor().getText().isEmpty())
                endDate = Date.valueOf(endDatePicker.getValue());
            project = new Project(nameTextField.getText(), cost, department, begDate, endDate);
            try {
                DataBaseManager.getShared().insertProject(project);
                addEditButton.getScene().getWindow().fireEvent(new WindowEvent(addEditButton.getScene().getWindow(), WindowEvent.WINDOW_CLOSE_REQUEST));
            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
                showAlert("Ошибка", "Ошибка при попытке сделать запрос!", Alert.AlertType.ERROR);
            }
        } else  {
            showAlert("Поля пустые", "Поле название пустое!", Alert.AlertType.WARNING);
        }
    }

    private void editProject() {
        if (!nameTextField.getText().isEmpty()) {
            Department department = null;
            Integer projectId = project.getId();
            if (departmentComboBox.getSelectionModel().getSelectedItem() != null) {
                department = new Department(departmentComboBox.getSelectionModel().getSelectedItem().getId(), departmentComboBox.getSelectionModel().getSelectedItem().getName());
            }
            int cost;
            try {
                cost = Integer.parseInt(priceTextField.getText());
            } catch (NumberFormatException exception) {
                cost = 0;
            }
            Date begDate = null;
            Date endDate = null;
            Date realEndDate = null;
            if (!beginDatePicker.getEditor().getText().isEmpty())
                begDate = Date.valueOf(beginDatePicker.getValue());
            if (!endDatePicker.getEditor().getText().isEmpty())
                endDate = Date.valueOf(endDatePicker.getValue());
            if (!realEndDatePicker.getEditor().getText().isEmpty())
                realEndDate = Date.valueOf(realEndDatePicker.getValue());
            project = new Project(projectId, nameTextField.getText(), cost, department, begDate, endDate, realEndDate);
            try {
                if (showQuestionAlert()) {
                    DataBaseManager.getShared().updateProject(project);
                    addEditButton.getScene().getWindow().fireEvent(new WindowEvent(addEditButton.getScene().getWindow(), WindowEvent.WINDOW_CLOSE_REQUEST));
                }
            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
                showAlert("Ошибка", "Ошибка при попытке сделать запрос!", Alert.AlertType.ERROR);
            }
        } else  {
            showAlert("Поля пустые", "Поле название пустое!", Alert.AlertType.WARNING);
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
