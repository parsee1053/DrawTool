package jp.ac.ibaraki.cis.puma;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;

/**
 * バージョン情報のウィンドウのコントローラとなるクラス
 * 
 * @author shotaro
 *
 */
public class VersionWindowController {

	/**
	 * 「OK」ボタンを押したときに行うメソッド
	 * 
	 * @param e ActionEvent
	 */
	@FXML
	private void handleClose(ActionEvent e) {
		((Node) e.getSource()).getScene().getWindow().hide(); // ウィンドウを閉じる
	}
}
