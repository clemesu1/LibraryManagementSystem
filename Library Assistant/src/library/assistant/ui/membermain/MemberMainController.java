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
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import library.assistant.database.DatabaseHandler;
import library.assistant.util.LibraryAssistantUtil;

public class MemberMainController implements Initializable {

    DatabaseHandler databaseHandler;
    
    @FXML
    private StackPane rootPane;
    @FXML
    private HBox bookname_info;
    @FXML
    private JFXTextField bookNameInput;
    private Text bookID;
    @FXML
    private HBox bookid_info;
    @FXML
    private JFXTextField bookIDInput;
    @FXML
    private ListView<String> borrowDataList;
    @FXML
    private Text bookNID;
    @FXML
    private Text bookNAuthor;
    @FXML
    private Text bookNStatus;
    @FXML
    private Text bookIDName;
    @FXML
    private Text bookIDAuthor;
    @FXML
    private Text bookIDStatus;

    Boolean isReadyForReturn = false;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        JFXDepthManager.setDepth(bookname_info, 1);
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
    
    void clearBookNameCache() {
        bookNID.setText("");
        bookNAuthor.setText("");
        bookNStatus.setText("");
    }
    
    void clearBookIDCache() {
        bookIDName.setText("");
        bookIDAuthor.setText("");
        bookIDStatus.setText("");
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
        
        String bookN = bookNameInput.getText();
        String bookID = bookIDInput.getText();
        String memberID = "";
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Issue Operation");
        alert.setHeaderText(null);
        if(bookNameInput.getText().equals("")) {
            alert.setContentText("Are you sure you want to borrow the book " + bookIDName.getText() + "?");
        } else {
            alert.setContentText("Are you sure you want to borrow the book " + bookIDName.getText() + "?");
        }
        Optional<ButtonType> response = alert.showAndWait(); 
        if(response.get() == ButtonType.OK) {
            String qu = "SELECT id FROM MEMBER";
            ResultSet rs = databaseHandler.execQuery(qu);
            try {
                while(rs.next()) {
                    memberID = rs.getString("id");
                }
            } catch(Exception e) {
                Logger.getLogger(MemberMainController.class.getName()).log(Level.SEVERE, null, e);
            }
            String str = "INSERT INTO ISSUE(memberID,bookID) VALUES ("
                    + "'" + memberID + "',"
                    + "'" + bookID + "')";
            String str2 = "UPDATE BOOK SET isAvail = false WHERE id = '" + bookID + "'";
            System.out.println(str + " and  " + str2);
            if(databaseHandler.execAction(str) && databaseHandler.execAction(str2)) {
                Alert alert1 = new Alert(Alert.AlertType.CONFIRMATION);
                alert1.setTitle("Success");
                alert1.setHeaderText(null);
                alert1.setContentText("Book Issue Complete");
                
                alert1.showAndWait();
            } else {
                Alert alert1 = new Alert(Alert.AlertType.ERROR);
                alert1.setTitle("Failed");
                alert1.setHeaderText(null);
                alert1.setContentText("Issue Operation Failed");
                
                alert1.showAndWait();
            }
        } else {
            Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
            alert1.setTitle("Cancelled");
            alert1.setHeaderText(null);
            alert1.setContentText("Issue Operation Failed");
                
            alert1.showAndWait();
        }
    }

    @FXML
    private void loadBookInfo2(ActionEvent event) {
        ObservableList<String> borrowData = FXCollections.observableArrayList();
        isReadyForReturn = false;

        String id = bookID.getText();
        String qu = "SELECT * FROM ISSUE WHERE bookID = '" + id + "'";
        ResultSet rs = databaseHandler.execQuery(qu);
        try {
            while(rs.next()) {
                String mBookID = id;
                String mMemberID = rs.getString("memberID");
                Timestamp mIssueTime = rs.getTimestamp("issueTime");
                int mRenewCount = rs.getInt("renew_count");
                
                borrowData.add("Issue Date and Time : " + mIssueTime.toGMTString());
                borrowData.add("Renew Count : " + mRenewCount);
                
                
                borrowData.add("Book Information:-");
                qu = "SELECT * FROM BOOK WHERE ID = '" + mBookID + "'";
                ResultSet r1 = databaseHandler.execQuery(qu);
                
                while(r1.next()) {
                    borrowData.add("\tBook Name : " + r1.getString("title"));
                    borrowData.add("\tBook ID : " + r1.getString("id"));
                    borrowData.add("\tBook Author : " + r1.getString("author"));
                    borrowData.add("\tBook Publisher : " + r1.getString("publisher"));
                }
                borrowData.add("Member Information:-");
                qu = "SELECT * FROM MEMBER WHERE ID = '" + mMemberID + "'";
                r1 = databaseHandler.execQuery(qu);
                
                while(r1.next()) {
                    borrowData.add("\tName : " + r1.getString("name"));
                    borrowData.add("\tPhone : " + r1.getString("phone"));
                    borrowData.add("\tEmail : " + r1.getString("email"));
                }
                
                isReadyForReturn = true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(MemberMainController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        borrowDataList.getItems().setAll(borrowData);
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
        alert.setTitle("Confirm Return Operation");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to return the book?");
        
        Optional<ButtonType> response = alert.showAndWait(); 
        if(response.get() == ButtonType.OK) {
            String id = bookID.getText();
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

    @FXML
    private void loadReturnOp(ActionEvent event) {
    }

    @FXML
    private void loadBookNameInfo(ActionEvent event) {
        clearBookNameCache();
        
        String title = bookNameInput.getText();
        String qu = "SELECT * FROM BOOK WHERE title = '" + title + "'";
        ResultSet rs = databaseHandler.execQuery(qu);
        Boolean flag = false;
        try {
            while(rs.next())
            {                
                String bName = rs.getString("title");
                String bAuthor = rs.getString("author");
                Boolean bStatus = rs.getBoolean("isAvail");
                
                bookNID.setText(bName);
                bookNAuthor.setText(bAuthor);
                String status = (bStatus)?"Available" : "Not Available";
                bookNStatus.setText(status);
                
                flag = true;
            }
            if(!flag) {
                bookID.setText("No Such Book Available");
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(MemberMainController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    @FXML
    private void loadBookIDInfo(ActionEvent event) {
    }
}