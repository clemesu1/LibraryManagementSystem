package library.assistant.ui.addbook;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import library.assistant.util.LibraryAssistantUtil;

public class BookAddLoader extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/add_book.fxml"));
        
        Scene scene = new Scene(root);
        
        stage.setScene(scene);
        stage.show();
        LibraryAssistantUtil.setStageIcon(stage);

    }

    public static void main(String[] args) {
        launch(args);
    }
    
}
