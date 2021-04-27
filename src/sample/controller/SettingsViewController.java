package sample.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.xml.sax.SAXException;
import sample.model.DataBaseManager;
import sample.model.XMLFile;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.util.Map;

public class SettingsViewController {

    @FXML
    private TextField userNameTextField;

    @FXML
    private TextField urlTextField;

    @FXML
    private PasswordField passwordTextField;

    XMLFile file = new XMLFile("src/sample/settings.xml");

    @FXML
    void saveButtonAction() {
        if (!userNameTextField.getText().isEmpty() && !urlTextField.getText().isEmpty() && !passwordTextField.getText().isEmpty()) {
           try {
               file.writeFile(userNameTextField.getText(), passwordTextField.getText(), urlTextField.getText());
               showAlert("Изменения сохранены", "Сохранение прошло успешно", Alert.AlertType.INFORMATION);
           } catch (ParserConfigurationException | TransformerException e) {
               e.printStackTrace();
               showAlert("Ошибка", "Не удалось сохранить конфигурационный файл.", Alert.AlertType.ERROR);
           }
        } else {
            showAlert("Внимание", "Поля пустые. Заполните поля.", Alert.AlertType.WARNING);
        }
    }

    @FXML
    void initialize() {
        try {
            Map<String, String> conf = file.readFile();
            DataBaseManager.getShared().setConfiguration(conf.get("url"), conf.get("username"), conf.get("password"));
            userNameTextField.setText(conf.get("username"));
            urlTextField.setText(conf.get("url"));
            passwordTextField.setText(conf.get("password"));
        } catch (IOException | ParserConfigurationException | SAXException e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось загрузить конфигурационный файл.", Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title ,String message,  Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(message);
        alert.showAndWait();
    }

}
