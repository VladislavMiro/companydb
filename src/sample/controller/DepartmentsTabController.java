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
import sample.model.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;

public class DepartmentsTabController {

    @FXML
    private TableView<Department> tableView;

    @FXML
    private TableColumn<Department, Integer> id;

    @FXML
    private TableColumn<Department, String> name;

    @FXML
    private Button addButton;

    @FXML
    private Button editButton;

    @FXML
    private Button deleteButton;

    ObservableList<Department> array = FXCollections.observableArrayList();

    @FXML
    void initialize() {
        if(DataBaseManager.getShared().getUser().getUserRole() != UserRole.admin) {
            addButton.setDisable(true);
            editButton.setDisable(true);
            deleteButton.setDisable(true);
        }
        array = FXCollections.observableArrayList(getData());
        fillTableView(array);

    }

    @FXML
    void addButtonAction() {
        Stage addEditView = new Stage();
        System.out.println(getClass().getResource("../view/AddEditDepartmentsView.fxml"));
        try {
            Parent addEditViewRoot = FXMLLoader.load(getClass().getResource("../view/AddEditDepartmentsView.fxml"));
            addEditView.setScene(new Scene(addEditViewRoot, 442, 153));
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
    void deleteButtonAction() {
        if (tableView.getSelectionModel().getSelectedItem() != null) {
            if (showQuestionAlert()) {
                try {
                    DataBaseManager.getShared().deleteFromDepartments(tableView.getSelectionModel().getSelectedItem());
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

    @FXML
    void editButtonAction() {
        if (tableView.getSelectionModel().getSelectedItem() != null) {
            Stage addEditView = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/AddEditDepartmentsView.fxml"));
            try {
                Parent addEditViewRoot = loader.load();
                addEditView.setTitle("Редактировать запись");
                addEditView.setScene(new Scene(addEditViewRoot, 442, 153));
                addEditView.setResizable(false);
                addEditView.setFullScreen(false);
                AddEditDepartmentsController addEditDepartmentsController = loader.getController();
                addEditView.show();
                addEditDepartmentsController.setData(tableView.getSelectionModel().getSelectedItem());
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

    private void fillTableView(ObservableList<Department> array) {
        id.setCellValueFactory(new PropertyValueFactory<>("id"));
        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        tableView.setItems(array);
    }

    private ArrayList<Department> getData() {
        ArrayList<Department> result = new ArrayList<>();

        try {
            result = DataBaseManager.getShared().getDepartments();
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
