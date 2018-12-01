package library.assistant.ui.addmember;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.sql.PreparedStatement;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import library.assistant.alert.AlertMaker;
import library.assistant.database.DatabaseHandler;
import library.assistant.ui.listmember.MemberListController;
import library.assistant.ui.listmember.MemberListController.Member;

public class MemberAddController implements Initializable {

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
    private CheckBox adminBox;
    @FXML
    private JFXButton saveButton;
    @FXML
    private JFXButton cancelButton;

    private AnchorPane rootPane;
    
    private Boolean isInEditMode = Boolean.FALSE;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        handler = DatabaseHandler.getInstance();
    }    

    @FXML
    private void cancel(ActionEvent event) {
        Stage stage = (Stage) name.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void addMember(ActionEvent event) {
        try {
            String mUser = username.getText();
            String mPass = password.getText();
            String mName = name.getText();
            String mID = id.getText();
            String mPhone = phone.getText();
            String mEmail = email.getText();
            
            Boolean flag = mUser.isEmpty() || mPass.isEmpty() || mName.isEmpty() || mID.isEmpty() || mPhone.isEmpty() || mEmail.isEmpty();
            if(flag) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText(null);
                alert.setContentText("Please enter in all fields");
                alert.showAndWait();
                return;
            }
            
            if(isInEditMode) {
                handleUpdateMember();
                return;
            }
            
            String sql = "INSERT INTO MEMBER (ID, NAME, PHONE, PASSWORD, USERNAME, PASSWORD, ISADMIN) VALUES (?, ?, ?, ?, ?, ?, ?)"; 
            PreparedStatement st = handler.getConnection().prepareStatement(sql);
            
            st.setString(1, mID);
            st.setString(2, mName);
            st.setString(3, mPhone);
            st.setString(4, mEmail);
            st.setString(5, mUser);
            st.setString(6, mPass);
            if(adminBox.isSelected())
                st.setBoolean(7, true);
            else
                st.setBoolean(7, false);
            System.out.println(sql);
            if(handler.execAction(sql)) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText(null);
                alert.setContentText("Saved");
                alert.showAndWait();
                Stage stage = (Stage) rootPane.getScene().getWindow();
                stage.close();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText(null);
                alert.setContentText("Error Occured");
                alert.showAndWait();
            }
        } catch (Exception ex) {
            Logger.getLogger(MemberAddController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void inflateUI(MemberListController.Member member) {
        name.setText(member.getName());
        id.setText(member.getId());
        id.setEditable(false);
        phone.setText(member.getPhone());
        email.setText(member.getEmail());
        
        isInEditMode = Boolean.TRUE;
    }
    
    private void handleUpdateMember() {
        Member member = new MemberListController.Member(name.getText(), id.getText(), phone.getText(), email.getText());
        if(DatabaseHandler.getInstance().updateMember(member)) {
            AlertMaker.showSimpleAlert("Success", "Member Updated");
        }
        else {
            AlertMaker.showErrorMessage("Error", "Cannot update Member");
        }
    }
}
