package jp.ac.ibaraki.cis.puma;

import javafx.geometry.Point2D;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * 矩形を表すクラス
 * 
 * @author shotaro
 *
 */
public class MyRectangle extends Rectangle {

	/**
	 * コンストラクタ
	 * 
	 * @param previous 以前の座標が格納されているPoint2D
	 * @param current  現在の座標が格納されているPoint2D
	 * @param red      矩形の赤の色の数値
	 * @param green    矩形の緑の色の数値
	 * @param blue     矩形の青の色の数値
	 * @param layer    レイヤー
	 */
	public MyRectangle(Point2D previous, Point2D current, int red, int green, int blue, Pane layer) {

		double startX, startY; // 矩形の描画開始位置のx座標、y座標
		double width, height; // 矩形の幅、高さ

		// 矩形の描画開始位置、幅、高さを調整
		if (current.getX() - previous.getX() >= 0.0) {
			startX = previous.getX();
			width = current.getX() - previous.getX();
		} else {
			startX = current.getX();
			width = previous.getX() - current.getX();
		}
		if (current.getY() - previous.getY() >= 0.0) {
			startY = previous.getY();
			height = current.getY() - previous.getY();
		} else {
			startY = current.getY();
			height = previous.getY() - current.getY();
		}

		// 矩形を描画
		setX(startX);
		setY(startY);
		setWidth(width);
		setHeight(height);
		setId("rectangle"); // IDをセット
		setFill(Color.rgb(red, green, blue));
		setClip(new Rectangle(0, 0, layer.getWidth(), layer.getHeight())); // クリッピング

		// 矩形をクリックしたときの処理
		setOnMousePressed((e) -> {

			// 矩形を2回左クリックされた場合はその矩形をぼかす
			if (e.getButton().equals(MouseButton.PRIMARY) && e.getClickCount() == 2) {
				setEffect(new GaussianBlur());
			}

			// 矩形を右クリックされた場合はその矩形を削除する
			if (e.getButton().equals(MouseButton.SECONDARY)) {
				layer.getChildren().remove(this);
			}
		});
	}

}
