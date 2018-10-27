package jp.ac.ibaraki.cis.puma;

import java.util.ArrayList;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;

/**
 * スプライン曲線の点を表すクラス
 * 
 * @author shotaro
 *
 */
public class SplineCurvePoint extends Circle {
	private static final double RADIUS = 10.0; // 点の半径
	private double dragBaseX; // クリックされた場所のx座標を保存するために使用する変数
	private double dragBaseY; // クリックされた場所のy座標を保存するために使用する変数
	private int number; // 点の番号

	/**
	 * コンストラクタ
	 * 
	 * @param centerX   点の中心のx座標
	 * @param centerY   点の中心のy座標
	 * @param pointList スプライン曲線用の点をまとめたリスト
	 * @param red       曲線の赤の色の数値
	 * @param green     曲線の緑の色の数値
	 * @param blue      曲線の青の色の数値
	 * @param width     曲線の太さの数値
	 * @param layer     レイヤー
	 */
	public SplineCurvePoint(double centerX, double centerY, ArrayList<SplineCurvePoint> pointList, int red, int green,
			int blue, double width, Pane layer) {

		// 点を描画
		super(centerX, centerY, RADIUS);

		setId("point"); // IDをセット
		setStroke(Color.BLACK);
		setFill(Color.WHITE);
		setClip(new Rectangle(0, 0, layer.getWidth(), layer.getHeight())); // クリッピング

		// 点にマウスが入ったときの処理
		setOnMouseEntered((e) -> {
			setFill(Color.BLACK);
		});

		// 点からマウスが出たときの処理
		setOnMouseExited((e) -> {
			setFill(Color.WHITE);
		});

		// 点をクリックしたときの処理
		setOnMousePressed((e) -> {
			dragBaseX = e.getX() - getCenterX();
			dragBaseY = e.getY() - getCenterY();

			// 点を右クリックされた場合はその点を削除する
			if (e.getButton().equals(MouseButton.SECONDARY)) {
				layer.getChildren().remove(this);
				pointList.remove(number);
				for (int i = 0; i < pointList.size(); i++) {
					pointList.get(i).setNumber(i);
				}

				// 以前描いた曲線をクリア
				layer.getChildren().remove(layer.lookup("#curve"));

				// 点の数が2つ以上なら曲線を描画する
				if (pointList.size() >= 2) {
					SplineCurve curve = new SplineCurve(pointList, red, green, blue, width, layer);
					layer.getChildren().add(curve);
				}
			}
		});

		// 点をドラッグしたときの処理
		setOnMouseDragged((e) -> {
			if (e.getX() >= 0.0 && e.getX() <= layer.getWidth() && e.getY() >= 0.0 && e.getY() <= layer.getHeight()) {
				setCenterX(e.getX() - dragBaseX);
				setCenterY(e.getY() - dragBaseY);
			}

			// 以前描いた曲線をクリア
			layer.getChildren().remove(layer.lookup("#curve"));

			// 点の数が2つ以上なら曲線を描画する
			if (pointList.size() >= 2) {
				SplineCurve curve = new SplineCurve(pointList, red, green, blue, width, layer);
				layer.getChildren().add(curve);
			}
		});
	}

	/**
	 * 曲線の点の番号を設定するメソッド
	 * 
	 * @param number 点の番号
	 */
	public void setNumber(int number) {
		this.number = number;
	}
}
