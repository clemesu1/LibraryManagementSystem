package library.assistant.ui.registermember;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import library.assistant.database.DatabaseHandler;

public class RegisterMemberController implements Initializable {

    DatabaseHandler handler;
    
    @FXML
    private JFXTextField username;
    @FXML
    private JFXPasswordField password;
    @FXML
    private JFXTextField name;
    @FXML
    private JFXTextField id;
    @FXML
    private JFXTextField phone;
    @FXML
    private JFXTextField email;
    @FXML
    private JFXButton saveButton;
    @FXML
    private JFXButton cancelButton;
    
    private Boolean isInEditMode = Boolean.FALSE;
    @FXML
    private AnchorPane rootPane;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        handler = DatabaseHandler.getInstance();
    }    

    @FXML
    private void registerMember(ActionEvent event) {
        
        String mUser = username.getText();
        String mPass = password.getText();
        String mName = name.getText();
        String mID = id.getText();
        String mPhone = phone.getText();
        String mEmail = email.getText();
        
        int exec = 0;
        
        Boolean flag = mUser.isEmpty() || mPass.isEmpty() || mName.isEmpty() || mID.isEmpty() || mPhone.isEmpty() || mEmail.isEmpty();
        if(flag) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Please enter in all fields");
            alert.showAndWait();
            return;
        }
        PreparedStatement st;
        try {
            st = DatabaseHandler.getConnection().prepareStatement("INSERT INTO MEMBER (ID, NAME, PHONE, EMAIL, USERNAME, PASSWORD, ISADMIN) VALUES (?, ?, ?, ?, ?, ?, ?)");
            st.setString(1, mID);
            st.setString(2, mName);
            st.setString(3, mPhone);
            st.setString(4, mEmail);
            st.setString(5, mUser);
            st.setString(6, mPass);
            st.setBoolean(7, false);
            exec = st.executeUpdate();
              
        } catch (SQLException ex) {
            Logger.getLogger(RegisterMemberController.class.getName()).log(Level.SEVERE, null, ex);
        }  
        if(exec > 0) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText(null);
            alert.setContentText("Account Created");
            alert.showAndWait();
            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.close();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Error Occured");
            alert.showAndWait();
        }        
    }

    @FXML
    private void cancel(ActionEvent event) {
        Stage stage = (Stage) name.getScene().getWindow();
        stage.close();
    }
    
}
