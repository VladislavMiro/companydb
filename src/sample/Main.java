package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sample.controller.MainViewController;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Stage loginView = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("view/LoginView.fxml"));
        Parent loginViewRoot = loader.load();
        loginView.setTitle("Авторизация");
        loginView.setScene(new Scene(loginViewRoot, 466, 232));
        loginView.setResizable(false);
        loginView.setFullScreen(false);
        loginView.show();
        loginView.setOnHidden(windowEvent -> {
            Stage mainView = new Stage();
            try {
                FXMLLoader loader1 = new FXMLLoader(getClass().getResource("view/MainView.fxml"));
                Parent mainViewRoot = loader1.load();
                mainView.setScene(new Scene(mainViewRoot, 954, 468));
                MainViewController mainViewController = loader1.getController();
                mainView.show();
                mainView.setOnHidden(winEvent -> {
                    if (mainViewController.isLogOut()) {
                        loginView.show();
                    }
                });
                mainView.setOnCloseRequest(winEvent -> System.exit(0));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        loginView.setOnCloseRequest(windowEvent -> System.exit(0));

    }


    public static void main(String[] args) {
        launch(args);
    }
}
