package library.assistant.ui.listaccount;

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
import library.assistant.ui.main.MainController;
import library.assistant.ui.registermember.RegisterMemberController;
import library.assistant.util.LibraryAssistantUtil;

public class AccountListController implements Initializable {
    ObservableList<AccountListController.Account> list = FXCollections.observableArrayList();
    
    @FXML
    private TableView<Account> tableView;
    @FXML
    private TableColumn<Account, String> userCol;
    @FXML
    private TableColumn<Account, String> idCol;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initCol();
        loadData();
    }
    private void initCol() {
        userCol.setCellValueFactory(new PropertyValueFactory<>("user"));
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
    }
    private void loadData() {
        list.clear();
        
        DatabaseHandler handler = DatabaseHandler.getInstance();
        String qu = "SELECT * FROM ACCOUNT";
        ResultSet rs = handler.execQuery(qu);
        try {
            while(rs.next()) {
                String user = rs.getString("username");
                String id = rs.getString("id");
                
                list.add(new Account(user, id));
            }
        } catch (SQLException ex) {
            Logger.getLogger(RegisterMemberController.class.getName()).log(Level.SEVERE, null, ex);
        }
        tableView.setItems(list);       
    }

    @FXML
    private void handleRefresh(ActionEvent event) {
        loadData();
    }

    @FXML
    private void handleAccountEdit(ActionEvent event) {
    //Fetch the selected row
        AccountListController.Account selectedForEdit = tableView.getSelectionModel().getSelectedItem();
        if(selectedForEdit == null) {
            AlertMaker.showErrorMessage("No account selected", "Please select an account to edit.");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/library/assistant/ui/registermember/register_member.fxml"));
            Parent parent = loader.load();
            
            RegisterMemberController controller = (RegisterMemberController) loader.getController();
            controller.inflateUI(selectedForEdit);
            
            Stage stage = new Stage(StageStyle.DECORATED);
            stage.setTitle("Edit Account");
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
    private void handleAccountDelete(ActionEvent event) {
        //Fetch the selected row
        AccountListController.Account selectedForDeletion = tableView.getSelectionModel().getSelectedItem();
        if(selectedForDeletion == null) {
            AlertMaker.showErrorMessage("No account selected", "Please select an account for deletion.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Deleting account");
        alert.setContentText("Are you sure you want to delete the account " + selectedForDeletion.getUser() + "?");
        Optional<ButtonType> answer = alert.showAndWait();
        if(answer.get() == ButtonType.OK) {
            boolean result = DatabaseHandler.getInstance().deleteAccount(selectedForDeletion);
            if(result) {
                AlertMaker.showSimpleAlert("Account deleted", selectedForDeletion.getUser() + " was deleted successfully.");
                list.remove(selectedForDeletion);
            }
            else {
                AlertMaker.showSimpleAlert("Failed", selectedForDeletion.getUser() + " could not be deleted");
            }
        }
        else {
            AlertMaker.showSimpleAlert("Deletion cancelled", "Deletion process cancelled");
        }
    }
    
    public static class Account {
        private final SimpleStringProperty user;
        private final SimpleStringProperty id;
        
        public Account(String user, String id) {
            this.user = new SimpleStringProperty(user);
            this.id = new SimpleStringProperty(id);
        }
        
        public String getUser() {
            return user.get();
        }

        public String getId() {
            return id.get();
        }
    }
}