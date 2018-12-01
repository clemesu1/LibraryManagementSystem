package library.assistant.ui.membermain;

import com.jfoenix.controls.JFXTextField;
import com.jfoenix.effects.JFXDepthManager;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import library.assistant.database.DatabaseHandler;
import library.assistant.ui.main.MainController;
import library.assistant.util.LibraryAssistantUtil;

public class MemberMainController implements Initializable {

    DatabaseHandler databaseHandler;
    
    @FXML
    private StackPane rootPane;
    @FXML
    private HBox bookid_info;
    @FXML
    private JFXTextField bookIDInput;
    @FXML
    private Text bookName;
    @FXML
    private Text bookAuthor;
    @FXML
    private Text bookStatus;

    Boolean isReadyForReturn = false;
    @FXML
    private Label welcomeUser;
    @FXML
    private ListView<String> issueDataList;
    @FXML
    private JFXTextField bookIDInput1;
    @FXML
    private JFXTextField memberIDInput;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        JFXDepthManager.setDepth(bookid_info, 1);
        
        databaseHandler = DatabaseHandler.getInstance();
    }    

    void loadWindow(String loc, String title) {
        try {
            Parent parent = FXMLLoader.load(getClass().getResource(loc));
            Stage stage = new Stage(StageStyle.DECORATED);
            stage.setTitle(title);
            stage.setScene(new Scene(parent));
            stage.show();
            LibraryAssistantUtil.setStageIcon(stage);
        } catch (IOException ex) {
            Logger.getLogger(MemberMainController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    void clearBookCache() {
        bookName.setText("");
        bookAuthor.setText("");
        bookStatus.setText("");
    }

    @FXML
    private void handleMenuClose(ActionEvent event) {
        ((Stage)rootPane.getScene().getWindow()).close();
    }

    @FXML
    private void handleMenuViewBooks(ActionEvent event) {
        loadWindow("/library/assistant/ui/listbook/book_list.fxml", "Book List");
    }

    @FXML
    private void handleMenuFullScreen(ActionEvent event) {
        Stage stage = ((Stage)rootPane.getScene().getWindow());
        stage.setFullScreen(!stage.isFullScreen());
    }


    @FXML
    private void loadIssueOperation(ActionEvent event) {
        String memberID = memberIDInput.getText();
        String bookID = bookIDInput.getText();
        String qu = "SELECT * FROM MEMBER WHERE id = '" + memberID + "'";
        ResultSet rs = databaseHandler.execQuery(qu);
        String memberName = "";
        try {
            while(rs.next()) {                
                memberName = rs.getString("name");
            }
        } catch (SQLException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Issue Operation");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to borrow the book " + bookName.getText() + "?");
        
        Optional<ButtonType> response = alert.showAndWait(); 
        if(response.get() == ButtonType.OK) {
            String str = "INSERT INTO ISSUE(memberID,bookID) VALUES ("
                    + "'" + memberID + "',"
                    + "'" + bookID + "')";
            String str2 = "UPDATE BOOK SET isAvail = false WHERE id = '" + bookID + "'";
            System.out.println(str + " and  " + str2);
            if(databaseHandler.execAction(str) && databaseHandler.execAction(str2)) {
                Alert alert1 = new Alert(Alert.AlertType.CONFIRMATION);
                alert1.setTitle("Success");
                alert1.setHeaderText(null);
                alert1.setContentText("Book Borrowed");
                
                alert1.showAndWait();
            } else {
                Alert alert1 = new Alert(Alert.AlertType.ERROR);
                alert1.setTitle("Failed");
                alert1.setHeaderText(null);
                alert1.setContentText("Borrowing Failed");
                
                alert1.showAndWait();
            }
        } else {
            Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
            alert1.setTitle("Cancelled");
            alert1.setHeaderText(null);
            alert1.setContentText("Borrow Operation Failed");
                
            alert1.showAndWait();
        }
    }

    @FXML
    private void loadMemberBookInfo(ActionEvent event) {
        ObservableList<String> issueData = FXCollections.observableArrayList();
        isReadyForReturn = false;

        String id = bookIDInput1.getText();
        String qu = "SELECT * FROM ISSUE WHERE bookID = '" + id + "'";
        ResultSet rs = databaseHandler.execQuery(qu);
        try {
            while(rs.next()) {
                String mBookID = id;
                String mMemberID = rs.getString("memberID");
                Timestamp mIssueTime = rs.getTimestamp("issueTime");
                int mRenewCount = rs.getInt("renew_count");
                
                issueData.add("Issue Date and Time : " + mIssueTime.toGMTString());
                issueData.add("Renew Count : " + mRenewCount);
                
                
                issueData.add("Book Information:-");
                qu = "SELECT * FROM BOOK WHERE ID = '" + mBookID + "'";
                ResultSet r1 = databaseHandler.execQuery(qu);
                
                while(r1.next()) {
                    issueData.add("\tBook Name : " + r1.getString("title"));
                    issueData.add("\tBook ID : " + r1.getString("id"));
                    issueData.add("\tBook Author : " + r1.getString("author"));
                    issueData.add("\tBook Publisher : " + r1.getString("publisher"));
                }
                issueData.add("Member Information:-");
                qu = "SELECT * FROM MEMBER WHERE ID = '" + mMemberID + "'";
                r1 = databaseHandler.execQuery(qu);
                
                while(r1.next()) {
                    issueData.add("\tName : " + r1.getString("name"));
                    issueData.add("\tPhone : " + r1.getString("phone"));
                    issueData.add("\tEmail : " + r1.getString("email"));
                }
                
                isReadyForReturn = true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        issueDataList.getItems().setAll(issueData);
    }

    @FXML
    private void loadRenewOp(ActionEvent event) {
        if(!isReadyForReturn) { 
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Failed");
            alert.setHeaderText(null);
            alert.setContentText("Please select a book to return");
            alert.showAndWait();
            return;
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Renew Operation");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to renew the book?");
                
        Optional<ButtonType> response = alert.showAndWait();
        if (response.get() == ButtonType.OK) {
            String ac = "UPDATE ISSUE SET issueTime = CURRENT_TIMESTAMP, renew_count = renew_count+1 WHERE BOOKID = '" + bookIDInput.getText() + "'";
            System.out.println(ac);
            if(databaseHandler.execAction(ac)) {
                Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
                alert1.setTitle("Success");
                alert1.setHeaderText(null);
                alert1.setContentText("Book Has Been Renewed");
                alert1.showAndWait();
            } else {
                Alert alert1 = new Alert(Alert.AlertType.ERROR);
                alert1.setTitle("Failed");
                alert1.setHeaderText(null);
                alert1.setContentText("Renewal Has Failed");
                alert1.showAndWait();
            }
        } else {
            Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
            alert1.setTitle("Cancelled");
            alert1.setHeaderText(null);
            alert1.setContentText("Renew Operation Cancelled");    
            alert1.showAndWait();
        }
    }

    @FXML
    private void loadReturnOp(ActionEvent event) {
        if(!isReadyForReturn) { 
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Failed");
            alert.setHeaderText(null);
            alert.setContentText("Please select a book to return");
            alert.showAndWait();
            return;
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Return Operation");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to return the book?");
        
        Optional<ButtonType> response = alert.showAndWait(); 
        if(response.get() == ButtonType.OK) {
            String id = bookIDInput.getText();
            String ac1 = "DELETE FROM ISSUE WHERE BOOKID = '" + id + "'";
            String ac2 = "UPDATE BOOK SET ISAVAIL = TRUE WHERE ID = '" + id + "'";

            if(databaseHandler.execAction(ac1) && databaseHandler.execAction(ac2)) {
                Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
                alert1.setTitle("Success");
                alert1.setHeaderText(null);
                alert1.setContentText("Book Has Been Returned");
                alert1.showAndWait();
            } else {
                Alert alert1 = new Alert(Alert.AlertType.ERROR);
                alert1.setTitle("Failed");
                alert1.setHeaderText(null);
                alert1.setContentText("Return Has Failed");
                alert1.showAndWait();
            }
        } else {
            Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
            alert1.setTitle("Cancelled");
            alert1.setHeaderText(null);
            alert1.setContentText("Return Operation Cancelled");             
            alert1.showAndWait();
        }
    }
    
    public void setWelcomeText(String text) {
        welcomeUser.setText("Welcome " + text + "!");
    }

    @FXML
    private void loadBookInfo(ActionEvent event) {
        clearBookCache();
        
        String id = bookIDInput.getText();
        String qu = "SELECT * FROM BOOK WHERE id = '" + id + "'";
        ResultSet rs = databaseHandler.execQuery(qu);
        Boolean flag = false;
        try {
            while(rs.next())
            {                
                String bName = rs.getString("title");
                String bAuthor = rs.getString("author");
                Boolean bStatus = rs.getBoolean("isAvail");
                
                bookName.setText(bName);
                bookAuthor.setText(bAuthor);
                String status = (bStatus)?"Available" : "Not Available";
                bookStatus.setText(status);
                
                flag = true;
            }
            if(!flag) {
                bookName.setText("No Such Book Available");
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}