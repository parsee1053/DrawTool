package jp.ac.ibaraki.cis.puma;

import javafx.geometry.Point2D;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;

/**
 * 楕円を表すクラス
 * 
 * @author shotaro
 *
 */
public class MyEllipse extends Ellipse {

	/**
	 * コンストラクタ
	 * 
	 * @param previous 以前の座標が格納されているPoint2D
	 * @param current  現在の座標が格納されているPoint2D
	 * @param red      楕円の赤の色の数値
	 * @param green    楕円の緑の色の数値
	 * @param blue     楕円の青の色の数値
	 * @param layer    レイヤー
	 */
	public MyEllipse(Point2D previous, Point2D current, int red, int green, int blue, Pane layer) {

		double centerX, centerY; // 楕円の中心の水平位置、垂直位置
		double width, height; // 楕円の幅、高さ

		// 楕円の中心、幅、高さを調整
		centerX = (previous.getX() + current.getX()) / 2.0;
		centerY = (previous.getY() + current.getY()) / 2.0;
		if (current.getX() - previous.getX() >= 0.0) {
			width = (current.getX() - previous.getX()) / 2.0;
		} else {
			width = (previous.getX() - current.getX()) / 2.0;
		}
		if (current.getY() - previous.getY() >= 0.0) {
			height = (current.getY() - previous.getY()) / 2.0;
		} else {
			height = (previous.getY() - current.getY()) / 2.0;
		}

		// 楕円を描画
		setCenterX(centerX);
		setCenterY(centerY);
		setRadiusX(width);
		setRadiusY(height);
		setId("ellipse"); // IDをセット
		setFill(Color.rgb(red, green, blue));
		setClip(new Rectangle(0, 0, layer.getWidth(), layer.getHeight())); // クリッピング

		// 楕円をクリックしたときの処理
		setOnMousePressed((e) -> {

			// 楕円を2回左クリックされた場合はその楕円をぼかす
			if (e.getButton().equals(MouseButton.PRIMARY) && e.getClickCount() == 2) {
				setEffect(new GaussianBlur());
			}

			// 楕円を右クリックされた場合はその楕円を削除する
			if (e.getButton().equals(MouseButton.SECONDARY)) {
				layer.getChildren().remove(this);
			}
		});
	}

}
