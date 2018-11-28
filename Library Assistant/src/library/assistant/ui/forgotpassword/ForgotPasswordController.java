package library.assistant.ui.forgotpassword;

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
import library.assistant.util.LibraryAssistantUtil;

public class ForgotPasswordController implements Initializable {

    DatabaseHandler handler;
    
    @FXML
    private JFXButton submitButton;
    @FXML
    private JFXButton cancelButton;
    @FXML
    private JFXTextField email;
    @FXML
    private JFXTextField username;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        LibraryAssistantUtil.setStageIcon(stage);
        handler = DatabaseHandler.getInstance();
    }    

    @FXML
    private void handleSubmitButton(ActionEvent event) {
        String uUser = username.getText();
        String uEmail = email.getText();
        Boolean empty = uUser.isEmpty() || uEmail.isEmpty();
        
        if(empty) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Please enter in all fields");
            alert.showAndWait();
            return;
        }
        boolean flag = false;
        String qu = "SELECT password FROM ACCOUNT WHERE username  = '" + uUser + "'";
        String pass = "";
        ResultSet rs = handler.execQuery(qu);
        
        try {
            while(rs.next()) {
                pass = rs.getString("password");
                flag = true;
            }
            if(flag) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText(null);
                alert.setContentText("Your password is: " + pass);
                alert.showAndWait();
            }
            else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText(null);
                alert.setContentText("User does not exist.");
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
