package library.assistant.ui.listbook;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import library.assistant.util.LibraryAssistantUtil;

public class BookListLoader extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("book_list.fxml"));
        Scene scene = new Scene(root);
        
        stage.setScene(scene);
        stage.show();
        LibraryAssistantUtil.setStageIcon(stage);

    }
    
    public static void main(String[] args) {
        launch(args);
    }
    
}
