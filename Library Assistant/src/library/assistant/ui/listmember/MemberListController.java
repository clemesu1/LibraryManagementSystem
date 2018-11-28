
package library.assistant.ui.listmember;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import library.assistant.alert.AlertMaker;
import library.assistant.database.DatabaseHandler;
import library.assistant.ui.addmember.MemberAddController;
import library.assistant.ui.main.MainController;
import library.assistant.util.LibraryAssistantUtil;

public class MemberListController implements Initializable {

    ObservableList<MemberListController.Member> list = FXCollections.observableArrayList();
    
    @FXML
    private TableView<Member> tableView;
    @FXML
    private TableColumn<Member, String> nameCol;
    @FXML
    private TableColumn<Member, String> idCol;
    @FXML
    private TableColumn<Member, String> phoneCol;
    @FXML
    private TableColumn<Member, String> emailCol;


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initCol();
        loadData();
    }    
    
    private void initCol() {
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
    }
    
    private void loadData() {
        list.clear();
        
        DatabaseHandler handler = DatabaseHandler.getInstance();
        String qu = "SELECT * FROM MEMBER";
        ResultSet rs = handler.execQuery(qu);
        try {
            while(rs.next()) {
                String name = rs.getString("name");
                String phone = rs.getString("phone");
                String id = rs.getString("id");
                String email = rs.getString("email");
                
                list.add(new Member(name, id, phone, email));
                
            }
        } catch (SQLException ex) {
            Logger.getLogger(MemberAddController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        tableView.setItems(list);       
    }

    @FXML
    private void handleRefresh(ActionEvent event) {
        loadData();
    }

    @FXML
    private void handleMemberEdit(ActionEvent event) {
        //Fetch the selected row
        MemberListController.Member selectedForEdit = tableView.getSelectionModel().getSelectedItem();
        if(selectedForEdit == null) {
            AlertMaker.showErrorMessage("No member selected", "Please select a member to edit.");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/library/assistant/ui/addmember/member_add.fxml"));
            Parent parent = loader.load();
            
            MemberAddController controller = (MemberAddController) loader.getController();
            controller.inflateUI(selectedForEdit);
            
            Stage stage = new Stage(StageStyle.DECORATED);
            stage.setTitle("Edit Member");
            stage.setScene(new Scene(parent));
            stage.show();
            LibraryAssistantUtil.setStageIcon(stage);
            
            stage.setOnCloseRequest((e) -> {
                handleRefresh(new ActionEvent());
            });
            
        } catch (IOException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void handleMemberDelete(ActionEvent event) {
        //Fetch the selected row
        MemberListController.Member selectedForDeletion = tableView.getSelectionModel().getSelectedItem();
        if(selectedForDeletion == null) {
            AlertMaker.showErrorMessage("No member selected", "Please select a member for deletion.");
            return;
        }
        if(DatabaseHandler.getInstance().ifMemberHasAnyBooks(selectedForDeletion)) {
            AlertMaker.showErrorMessage("Cannot be deleted", "This member has borrowed books");
            return;
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Deleting member");
        alert.setContentText("Are you sure you want to delete the member " + selectedForDeletion.getName() + "?");
        Optional<ButtonType> answer = alert.showAndWait();
        if(answer.get() == ButtonType.OK) {
            boolean result = DatabaseHandler.getInstance().deleteMember(selectedForDeletion);
            if(result) {
                AlertMaker.showSimpleAlert("Member deleted", selectedForDeletion.getName() + " was deleted successfully.");
                list.remove(selectedForDeletion);
            }
            else {
                AlertMaker.showSimpleAlert("Failed", selectedForDeletion.getName() + " could not be deleted");
            }
        }
        else {
            AlertMaker.showSimpleAlert("Deletion cancelled", "Deletion process cancelled");
        }
    }
    
    public static class Member {
        
        private final SimpleStringProperty name;
        private final SimpleStringProperty id;
        private final SimpleStringProperty phone;
        private final SimpleStringProperty email;
        
        public Member(String name, String id, String phone, String email) {
            this.name = new SimpleStringProperty(name);
            this.id = new SimpleStringProperty(id);
            this.phone = new SimpleStringProperty(phone);
            this.email = new SimpleStringProperty(email);
        }

        public String getName() {
            return name.get();
        }

        public String getId() {
            return id.get();
        }

        public String getPhone() {
            return phone.get();
        }

        public String getEmail() {
            return email.get();
        }
    }
}
