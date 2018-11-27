package library.assistant.ui.listaccount;


import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import library.assistant.database.DatabaseHandler;
import library.assistant.ui.registermember.RegisterMemberController;


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
