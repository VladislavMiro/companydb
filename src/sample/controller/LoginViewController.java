package sample.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.xml.sax.SAXException;
import sample.model.DataBaseManager;
import sample.model.XMLFile;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

public class LoginViewController {

    @FXML
    private PasswordField passwordTextField;

    @FXML
    private Button logInButton;

    @FXML
    private TextField logInTextField;

    @FXML
    void initialize() {
        try {
            XMLFile file = new XMLFile("src/sample/settings.xml");
            Map<String, String> conf = file.readFile();
            DataBaseManager.getShared().setConfiguration(conf.get("url"), conf.get("username"), conf.get("password"));
        } catch (IOException | ParserConfigurationException | SAXException e) {
            e.printStackTrace();
            showAlert("Ошибка", "Ошибка при загрузке файла конфигурации!", Alert.AlertType.ERROR);
        }
    }

    @FXML
    void logInButton() {
        try {
            DataBaseManager.getShared().connect();
            try {
                if (!logInTextField.getText().isEmpty() && !passwordTextField.getText().isEmpty()) {
                    if (DataBaseManager.getShared().auth(logInTextField.getText(), passwordTextField.getText())) {
                        DataBaseManager.getShared().loadUser(logInTextField.getText());
                        logInButton.getScene().getWindow().hide();
                    } else {
                        showAlert("Ошибка!", "Неверный логин или пароль!", Alert.AlertType.ERROR);
                    }
                } else {
                    showAlert("Внимание!", "Не все поля заполнены!", Alert.AlertType.INFORMATION);
                }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Ошибка", "Ошибка при запросе!", Alert.AlertType.ERROR);
        }
    } catch (SQLException | ClassNotFoundException er) {
            showAlert("Ошибка", "Ошибка при подключении к БД. Проверьте настройки соединения.", Alert.AlertType.ERROR);
        }

    }

    @FXML
    void settingsButtonAction() {
        Stage settingsView = new Stage();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/SettingsView.fxml"));
            Parent settingsViewRoot = loader.load();
            settingsView.setTitle("Настройки");
            settingsView.setScene(new Scene(settingsViewRoot, 492, 260));
            settingsView.setResizable(false);
            settingsView.setFullScreen(false);
            settingsView.show();
            settingsView.setOnHidden(windowEvent -> {
                try {
                    XMLFile file = new XMLFile("src/sample/settings.xml");
                    Map<String, String> conf = file.readFile();
                    DataBaseManager.getShared().setConfiguration(conf.get("url"), conf.get("username"), conf.get("password"));
                } catch (IOException | ParserConfigurationException | SAXException e) {
                    e.printStackTrace();
                    showAlert("Ошибка", "Ошибка при загрузке файла конфигурации!", Alert.AlertType.ERROR);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title ,String message,  Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(message);
        alert.showAndWait();
    }

}
