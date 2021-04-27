package sample.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import sample.model.DataBaseManager;
import sample.model.Employer;
import sample.model.UserRole;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;

public class EmployeesTabController {

    @FXML
    private TableView<Employer> tableView;

    @FXML
    private TableColumn<Employer, Integer> id;

    @FXML
    private TableColumn<Employer, String> name;

    @FXML
    private TableColumn<Employer, String> lastName;

    @FXML
    private TableColumn<Employer, String> partherName;

    @FXML
    private TableColumn<Employer, String> position;

    @FXML
    private TableColumn<Employer, String> department;

    @FXML
    private TableColumn<Employer, Integer> salary;


    @FXML
    private Button addButton;

    @FXML
    private Button changeButton;

    @FXML
    private Button deleteButton;

    ObservableList<Employer> array = FXCollections.observableArrayList();

    @FXML
    void initialize() {
        if(DataBaseManager.getShared().getUser().getUserRole() != UserRole.admin) {
            addButton.setDisable(true);
            changeButton.setDisable(true);
            deleteButton.setDisable(true);
        }
        array = FXCollections.observableArrayList(getData());
        fillTableView(array);

    }

    @FXML
    void addButtonAction() {
        Stage addEditView = new Stage();
        try {
            Parent addEditViewRoot = FXMLLoader.load(getClass().getResource("../view/AddEditEmployeeView.fxml"));
            addEditView.setScene(new Scene(addEditViewRoot, 465, 374));
            addEditView.setTitle("Добавить запись");
            addEditView.setResizable(false);
            addEditView.setFullScreen(false);
            addEditView.show();
            addEditView.setOnCloseRequest(windowEvent -> {
                array = FXCollections.observableArrayList(getData());
                tableView.setItems(array);
                tableView.refresh();
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void changeButtonAction() {
        if (tableView.getSelectionModel().getSelectedItem() != null) {
            Stage addEditView = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/AddEditEmployeeView.fxml"));
            try {
                Parent addEditViewRoot = loader.load();
                addEditView.setTitle("Редактировать запись");
                addEditView.setScene(new Scene(addEditViewRoot, 465, 374));
                addEditView.setResizable(false);
                addEditView.setFullScreen(false);
                AddEditEmployeeController addEditEmployeeController = loader.getController();
                addEditView.show();
                addEditEmployeeController.setData(tableView.getSelectionModel().getSelectedItem());
                addEditView.setOnCloseRequest(windowEvent -> {
                    array = FXCollections.observableArrayList(getData());
                    tableView.setItems(array);
                    tableView.refresh();
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            showAlert("Внимание!", "Выберите запись для редактирования!", Alert.AlertType.INFORMATION);
        }
    }

    @FXML
    void deleteButtonAction() {
        if (tableView.getSelectionModel().getSelectedItem() != null) {
            if (showQuestionAlert()) {
                try {
                    DataBaseManager.getShared().deleteEmployee(tableView.getSelectionModel().getSelectedItem());
                    array = FXCollections.observableArrayList(getData());
                    tableView.setItems(array);
                    tableView.refresh();
                } catch (SQLException | ClassNotFoundException e) {
                    e.printStackTrace();
                    showAlert("Ошибка запроса!", "Не удалось выполнить запрос!", Alert.AlertType.ERROR);
                }
            }
        } else {
            showAlert("Внимание!", "Выберите запись для удаления!", Alert.AlertType.INFORMATION);
        }
    }

    private void fillTableView(ObservableList<Employer> array) {
        id.setCellValueFactory(new PropertyValueFactory<>("id"));
        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        lastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        partherName.setCellValueFactory(new PropertyValueFactory<>("partherName"));
        position.setCellValueFactory(new PropertyValueFactory<>("position"));
        department.setCellValueFactory(new PropertyValueFactory<>("department"));
        salary.setCellValueFactory(new PropertyValueFactory<>("salary"));
        tableView.setItems(array);
    }

    private ArrayList<Employer> getData() {
        ArrayList<Employer> result = new ArrayList<>();

        try {
            result = DataBaseManager.getShared().getEmployeers();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return result;
    }

    private void showAlert(String title ,String message,  Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(message);
        alert.showAndWait();
    }

    private boolean showQuestionAlert() {
        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.setTitle("Удалить запись?");
        alert.setHeaderText("Вы уверены что хотить удалить запись?");
        ButtonType yes = new ButtonType("Да");
        alert.getButtonTypes().addAll(yes);
        alert.getButtonTypes().addAll(new ButtonType("Нет"));
        Optional<ButtonType> result = alert.showAndWait();
        return result.get() == yes;
    }

}