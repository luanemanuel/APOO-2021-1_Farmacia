package br.com.apoo2021.farm.screens;

import br.com.apoo2021.farm.FarmApp;
import br.com.apoo2021.farm.database.SQLRunner;
import br.com.apoo2021.farm.util.MD5Cripto;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import javax.print.attribute.standard.JobOriginatingUserName;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

public class LoginScreenController implements Initializable {

    @FXML
    private JFXTextField usernameTextField;

    @FXML
    private JFXPasswordField passwordTextField;

    @FXML
    private JFXButton loginButton;

    @FXML
    private JFXButton closeButton;

    @FXML
    private JFXButton signUpButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    void loginPressed(ActionEvent event) {
        String hash = MD5Cripto.MD5Converter(usernameTextField.getText().toLowerCase()+passwordTextField.getText());
        if (hash != null) {
            List<Object> crf = SQLRunner.executeSQLScript.SQLSelect("GetFarmCRF", hash);
            if(crf != null && !crf.isEmpty()) {
                List<Object> name = SQLRunner.executeSQLScript.SQLSelect("GetFarmName", hash);
                if (name != null && !name.isEmpty()) {
                    FarmApp.userManager.setFarmData((int) crf.get(0), (String) name.get(0));
                }
            } else {
                // login/senha não encontrados
                System.out.println("?");
            }
        }
    }

    @FXML
    void signUpPressed(ActionEvent event) {
        try{
            Stage stage = (Stage) closeButton.getScene().getWindow();
            stage.setScene(new Scene(FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("screens/RegisterScreen.fxml")))));
            Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
            stage.setX((primScreenBounds.getWidth() - stage.getWidth()) / 2);
            stage.setY((primScreenBounds.getHeight() - stage.getHeight()) / 2);
        }catch(IOException e){
            FarmApp.logger.error("Erro ao clicar em registrar usuário, tela LoginScreen",e);
        }
    }

}
