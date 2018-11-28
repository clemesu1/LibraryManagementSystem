package library.assistant.ui.login;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import library.assistant.database.DatabaseHandler;
import library.assistant.settings.Preferences;
import library.assistant.ui.main.MainController;
import library.assistant.util.LibraryAssistantUtil;
import org.apache.commons.codec.digest.DigestUtils;

public class LoginController implements Initializable {
    DatabaseHandler handler;
    @FXML
    private static JFXTextField username;
    @FXML
    private JFXPasswordField password;
    
    Preferences preference;
    @FXML
    private Label titleLabel;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        preference = Preferences.getPreferences();
        handler = DatabaseHandler.getInstance();
    }    

    @FXML
    private void handleLoginButtonAction(ActionEvent event) throws SQLException {
        titleLabel.setText("Library Assistant Login");
        titleLabel.setStyle("-fx-background-color:black;-fx-text-fill:white"); DatabaseHandler handler = DatabaseHandler.getInstance();
        String st = "SELECT username, password FROM ACCOUNT";
        ResultSet rs = handler.execQuery(st);
        String user = username.getText();
        String pass = DigestUtils.shaHex(password.getText());
            while(rs.next()) {
                String username = rs.getString("username");
                String password = rs.getString("password");
                if(user.equals(username) && pass.equals(DigestUtils.shaHex(password))) {
                    closeStage();
                    loadMain();
                } else {
                    titleLabel.setText("Invalid Credentials");
                    titleLabel.setStyle("-fx-background-color:d32f2f;-fx-text-fill:white");
                }     
                
            }
        
        /*if(user.equals(preference.getUsername()) && pass.equals(preference.getPassword())) {
            closeStage();
            loadMain();
        } else {
            titleLabel.setText("Invalid Credentials");
            titleLabel.setStyle("-fx-background-color:d32f2f;-fx-text-fill:white");
        }*/ 
   }

    @FXML
    private void handleCancelButtonAction(ActionEvent event) {
        System.exit(0);
    }

    private void closeStage() {
        ((Stage)username.getScene().getWindow()).close();
    }
    void loadWindow(String loc, String title)
    {
        try {
            Parent parent = FXMLLoader.load(getClass().getResource(loc));
            Stage stage = new Stage(StageStyle.DECORATED);
            stage.setTitle(title);
            stage.setScene(new Scene(parent));
            stage.show();
            
        } catch (IOException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @FXML
    private void loadRegisterMember(ActionEvent event) {
        loadWindow("/library/assistant/ui/registermember/register_member.fxml", "Register Member");
    }
    
    @FXML
    private void loadForgotPassword(ActionEvent event) {
        loadWindow("/library/assistant/ui/forgotpassword/forgot_password.fxml", "Forgot Password");
    }
    void loadMain()
    {
        try {
            Parent parent = FXMLLoader.load(getClass().getResource("/library/assistant/ui/main/main.fxml"));
            Stage stage = new Stage(StageStyle.DECORATED);
            stage.setTitle("Library Assistant");
            stage.setScene(new Scene(parent));
            stage.show();
            LibraryAssistantUtil.setStageIcon(stage);
        } catch (IOException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
