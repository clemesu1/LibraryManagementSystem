package library.assistant.forgotpassword;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import library.assistant.database.DatabaseHandler;

public class ForgotPasswordController implements Initializable {

    DatabaseHandler handler;

    @FXML
    private JFXTextField username;
    @FXML
    private JFXTextField email;
    @FXML
    private JFXButton submitButton;
    @FXML
    private JFXButton cancelButton;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        handler = DatabaseHandler.getInstance();
    }   

    @FXML
    private void handleSubmitButton(ActionEvent event) {
        String uUser = username.getText();
        String uEmail = email.getText();
        Boolean flag = uUser.isEmpty() || uEmail.isEmpty();

        if(flag) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Please enter in all fields");
            alert.showAndWait();
            return;
        }
        flag = false;
        String qu = "SELECT password FROM ACCOUNT WHERE " + "'" + uUser +"'";
        String pass = "";
        ResultSet rs = handler.execQuery(qu);
        try {
            while(rs.next()) {
                pass = rs.getString("password");  
                
                flag = true;
            }
            if(!flag) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText(null);
                alert.setContentText("No such user exists");
                alert.showAndWait();            
            }
            else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText(null);
                alert.setContentText("Your password is: " + pass);
                alert.showAndWait();   
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(ForgotPasswordController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void handleCancelButton(ActionEvent event) {
        Stage stage = (Stage) username.getScene().getWindow();
        stage.close();
    }
    
    
    
}
