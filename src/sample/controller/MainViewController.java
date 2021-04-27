package sample.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import sample.model.DataBaseManager;

import java.io.IOException;

public class MainViewController {

    @FXML
    private Label userNameLabel;

    @FXML
    private Button employeesTabButton;

    @FXML
    private Button departmentsTabButton;

    @FXML
    private Button projectsTabButton;

    @FXML
    private AnchorPane tabs;

    @FXML
    private Label tabLabel;

    private boolean isLogOut = false;

    @FXML
    void pressOnTabButton(ActionEvent event) {
        System.out.println(((Button) event.getSource()).getId());
        System.out.println(employeesTabButton.toString());

        switch (((Button) event.getSource()).getId()) {
            case "employeesTabButton":
                openEmployeesTab();
                break;
            case "departmentsTabButton":
                openDepartmentsTab();
                break;
            case "projectsTabButton":
                openProjectsTab();
                break;
            default:
                break;

        }
    }

    @FXML
    void logOutButton() {
        isLogOut = true;
        projectsTabButton.getScene().getWindow().hide();
    }

    @FXML
    void initialize() {
        tabLabel.setText("Сотрудники");
        loadPage("employeesTab");
        userNameLabel.setText(DataBaseManager.getShared().getUser().getLogin());
    }

    private void openEmployeesTab() {
        employeesTabButton.setStyle("-fx-background-color: white; -fx-text-fill: black");
        departmentsTabButton.setStyle("-fx-background-color:  #06AED5; -fx-text-fill: white");
        projectsTabButton.setStyle("-fx-background-color:  #06AED5; -fx-text-fill: white");
        tabLabel.setText("Сотрудники");
        loadPage("employeesTab");
    }

    private void openDepartmentsTab() {
        employeesTabButton.setStyle("-fx-background-color:  #06AED5; -fx-text-fill: white");
        departmentsTabButton.setStyle("-fx-background-color:  white; -fx-text-fill: black");
        projectsTabButton.setStyle("-fx-background-color:  #06AED5; -fx-text-fill: white");
        tabLabel.setText("Отделы");
        loadPage("departmentsTab");
    }

    private void openProjectsTab() {
        employeesTabButton.setStyle("-fx-background-color:  #06AED5; -fx-text-fill: white");
        departmentsTabButton.setStyle("-fx-background-color:  #06AED5; -fx-text-fill: white");
        projectsTabButton.setStyle("-fx-background-color:  white; -fx-text-fill: black");
        tabLabel.setText("Проекты");
        loadPage("projectsTab");
    }

    private void loadPage(String name) {
        Parent root = null;

        try {
            root = FXMLLoader.load(getClass().getResource("../view/"+name+".fxml"));

        } catch (IOException e) {
            e.printStackTrace();
        }
        Region n = (Region) root;
        tabs.getChildren().add(n);
        n.prefHeightProperty().bind(tabs.heightProperty());
        n.prefWidthProperty().bind(tabs.widthProperty());
    }

    public boolean isLogOut() { return isLogOut; }

}
