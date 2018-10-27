package jp.ac.ibaraki.cis.puma;

import javafx.geometry.Point2D;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;

/**
 * 自由線を表すクラス
 * 
 * @author shotaro
 *
 */
public class MyFreeLine extends Path {

	/**
	 * コンストラクタ
	 * 
	 * @param previous 以前の座標が格納されているPoint2D
	 * @param red      線の赤の色の数値
	 * @param green    線の緑の色の数値
	 * @param blue     線の青の色の数値
	 * @param width    線の太さの数値
	 * @param layer    レイヤー
	 */
	public MyFreeLine(Point2D previous, int red, int green, int blue, double width, Pane layer) {

		// 線の描画開始位置、色、太さを設定
		getElements().add(new MoveTo(previous.getX(), previous.getY()));
		setId("line"); // IDをセット
		setStroke(Color.rgb(red, green, blue));
		setStrokeWidth(width);
		setStrokeLineCap(StrokeLineCap.ROUND);
		setStrokeLineJoin(StrokeLineJoin.ROUND);
		setClip(new Rectangle(0, 0, layer.getWidth(), layer.getHeight())); // クリッピング

		// 線をクリックしたときの処理
		setOnMousePressed((e) -> {

			// 線を2回左クリックされた場合はその線をぼかす
			if (e.getButton().equals(MouseButton.PRIMARY) && e.getClickCount() == 2) {
				setEffect(new GaussianBlur());
			}

			// 線を右クリックされた場合はその線を削除する
			if (e.getButton().equals(MouseButton.SECONDARY)) {
				layer.getChildren().remove(this);
			}
		});
	}
}
