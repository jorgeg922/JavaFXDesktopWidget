package application;
	
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;


public class Main extends Application {
	Stage window;
	Parent root;
	private double x,y;
	
	@Override
	public void start(Stage primaryStage) throws Exception{
		window = primaryStage;
		//remove stage borders
		window.initStyle(StageStyle.TRANSPARENT);
		//load fxml
		root = FXMLLoader.load(getClass().getResource("/ui/widgetUI.fxml"));
		
		//make ui draggable
		root.setOnMousePressed(e -> {
			x = e.getSceneX();
			y = e.getSceneY();
		});
		root.setOnMouseDragged(e -> {
			primaryStage.setX(e.getScreenX() -x);
			primaryStage.setY(e.getScreenY() -y);
		});
		
		window.setScene(new Scene(root, Color.TRANSPARENT));
		window.setResizable(false);
		window.show();
	}
	public static void main(String[] args) {
		launch(args);
	}
}
