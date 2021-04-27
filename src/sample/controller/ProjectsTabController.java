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
import sample.model.Project;
import sample.model.UserRole;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Optional;

public class ProjectsTabController {

    @FXML
    private TableView<Project> tableView;

    @FXML
    private TableColumn<Project, Integer> id;

    @FXML
    private TableColumn<Project, String> name;

    @FXML
    private TableColumn<Project, Double> price;

    @FXML
    private TableColumn<Project, String> department;

    @FXML
    private TableColumn<Project, Date> dateBeg;

    @FXML
    private TableColumn<Project, Date> dateEnd;

    @FXML
    private TableColumn<Project, Date> dateEndReal;

    @FXML
    private Button addButton;

    @FXML
    private Button changeButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Button reportButton;

    @FXML
    void addButtonAction() {
        Stage addEditView = new Stage();
        try {
            Parent addEditViewRoot = FXMLLoader.load(getClass().getResource("../view/AddEditProjectView.fxml"));
            addEditView.setScene(new Scene(addEditViewRoot, 573, 364));
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/AddEditProjectView.fxml"));
            try {
                Parent addEditViewRoot = loader.load();
                addEditView.setTitle("Редактировать запись");
                addEditView.setScene(new Scene(addEditViewRoot, 573, 364));
                addEditView.setResizable(false);
                addEditView.setFullScreen(false);
                AddEditProjectController addEditProjectController = loader.getController();
                addEditView.show();
                addEditProjectController.setData(tableView.getSelectionModel().getSelectedItem());
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
                    DataBaseManager.getShared().deleteProject(tableView.getSelectionModel().getSelectedItem());
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
    void reportButtonAction() {
        Stage reportView = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/reportView.fxml"));
        try {
            Parent reportViewRoot = loader.load();
            reportView.setTitle("Отчет");
            reportView.setScene(new Scene(reportViewRoot, 752, 413));
            reportView.setResizable(false);
            reportView.setFullScreen(false);
            reportView.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    ObservableList<Project> array = FXCollections.observableArrayList();

    @FXML
    void initialize() {
        if(DataBaseManager.getShared().getUser().getUserRole() != UserRole.admin) {
            addButton.setDisable(true);
            changeButton.setDisable(true);
            deleteButton.setDisable(true);
            reportButton.setDisable(true);
        }
        array = FXCollections.observableArrayList(getData());
        fillTableView(array);
    }

    private void fillTableView(ObservableList<Project> array) {
        id.setCellValueFactory(new PropertyValueFactory<>("id"));
        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        price.setCellValueFactory(new PropertyValueFactory<>("price"));
        department.setCellValueFactory(new PropertyValueFactory<>("department"));
        dateBeg.setCellValueFactory(new PropertyValueFactory<>("dateBeg"));
        formatDateCell(dateBeg);
        dateEnd.setCellValueFactory(new PropertyValueFactory<>("dateEnd"));
        formatDateCell(dateEnd);
        dateEndReal.setCellValueFactory(new PropertyValueFactory<>("dateEndReal"));
        formatDateCell(dateEndReal);
        tableView.setItems(array);
    }

    private void formatDateCell(TableColumn<Project, Date> colum) {
        colum.setCellFactory(column -> {
            TableCell<Project, Date> cell = new TableCell<>() {
                private final SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");

                @Override
                protected void updateItem(Date item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                    } else {
                        setText(format.format(item));
                    }
                }
            };
            return cell;
        });
    }

    private ArrayList<Project> getData() {
        ArrayList<Project> result = new ArrayList<>();

        try {
            result = DataBaseManager.getShared().getProjects();
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
