
package library.assistant.ui.listaccount;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import library.assistant.util.LibraryAssistantUtil;

public class AccountListLoader extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/account_list.fxml")); 
        
        Scene scene = new Scene(root);
        
        stage.setScene(scene);
        stage.show();
        LibraryAssistantUtil.setStageIcon(stage);

    }

    public static void main(String[] args) {
        launch(args);
    }
    
}