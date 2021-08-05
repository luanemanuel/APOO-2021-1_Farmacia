package br.com.apoo2021.farm.screens;

import br.com.apoo2021.farm.FarmApp;
import br.com.apoo2021.farm.util.FarmDialogs;
import br.com.apoo2021.farm.util.ScreenAdjusts;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class LoadScreenController implements Initializable {

    @FXML
    private Text loadMessageTextField;

    @FXML
    private StackPane stackPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadMessageTextField.setText("Carregando...");
        new Thread(() -> {
            boolean loaded = false;
            Thread updateFarm = new Thread(() -> {
                FarmApp.dataManager.getFarmManager().updateFarmData();
            });
            Thread updateProduct = new Thread(() -> {
                FarmApp.dataManager.getProductManager().updateProductList();
            });
            Thread updateCostumer = new Thread(() -> {
                FarmApp.dataManager.getCostumerManager().updateCostumerList();
            });
            updateFarm.start();
            updateProduct.start();
            updateCostumer.start();
            try{
                loadMessageTextField.setText("Carregando dados de usu\u00e1rio...");
                updateFarm.join();
                loadMessageTextField.setText("Carregando produtos...");
                updateProduct.join();
                loadMessageTextField.setText("Carregando clientes...");
                updateCostumer.join();
                loaded = true;
                loadMessageTextField.setText("Finalizando carregamento...");
            }catch(InterruptedException e){
                FarmApp.logger.error("Erro nos Threads no carregamento de dados no login",e);
            }
            boolean finalLoaded = loaded;
            Platform.runLater(() -> {
                if(finalLoaded){
                    try{
                        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("screens/MainScreen.fxml")));
                        Stage stage = (Stage) loadMessageTextField.getScene().getWindow();
                        stage.setScene(new Scene(root));
                        ScreenAdjusts.centerScreen(stage);
                        ScreenAdjusts.setDraggable(root,stage);
                    }catch(IOException e){
                        FarmApp.logger.error("Error ao tentar abrir a tela principal!",e);
                    }
                }else{
                    try{
                        FarmDialogs.showDialog(stackPane, "Error", "Error ao carregar os dados.\nTente novamente mais tarde!");
                        FarmApp.dataManager.getFarmManager().clearFarmData();
                        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("screens/LoginScreen.fxml")));
                        Stage stage = (Stage) loadMessageTextField.getScene().getWindow();
                        stage.setScene(new Scene(root));
                        ScreenAdjusts.centerScreen(stage);
                        ScreenAdjusts.setDraggable(root,stage);
                    }catch(IOException e){
                        FarmApp.logger.error("Error ao tentar abrir a tela de login!",e);
                    }
                }
            });
        }).start();
    }
}
