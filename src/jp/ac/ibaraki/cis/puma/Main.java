package jp.ac.ibaraki.cis.puma;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * お絵かきツールのメインとなるクラス
 * 
 * @author shotaro
 *
 */
public class Main extends Application {

	/**
	 * startメソッド
	 * 
	 * @param stage
	 * @throws Exception
	 */
	@Override
	public void start(Stage stage) throws Exception {
		stage.setTitle("お絵かきツール Ver1.4ぐらい");
		FXMLLoader loader = new FXMLLoader(getClass().getResource("DrawToolWindow.fxml"));
		Parent root = loader.load();

		DrawToolWindowController controller = (DrawToolWindowController) loader.getController();
		controller.setThisStage(stage);

		stage.setScene(new Scene(root));
		stage.show();
	}

	/**
	 * mainメソッド
	 * 
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		launch(args);
	}

}
