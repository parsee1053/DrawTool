package jp.ac.ibaraki.cis.puma;

import java.util.ArrayList;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;

/**
 * スプライン曲線を表すクラス
 * 
 * @author shotaro
 *
 */
public class SplineCurve extends Path {
	private final double[] x, y; // 点の中心のx座標、y座標を格納する配列
	private final Spline splineX = new Spline(); // 点の中心のx座標の配列から計算したスプラインのデータ
	private final Spline splineY = new Spline(); // 点の中心のy座標の配列から計算したスプラインのデータ
	private final double curveNum; // 点と点の間の曲線の番号をdouble型にしたもの

	/**
	 * コンストラクタ
	 * 
	 * @param pointList スプライン曲線用の点をまとめたリスト
	 * @param red       曲線の赤の色の数値
	 * @param green     曲線の緑の色の数値
	 * @param blue      曲線の青の色の数値
	 * @param width     曲線の太さの数値
	 * @param layer     レイヤー
	 */
	public SplineCurve(ArrayList<SplineCurvePoint> pointList, int red, int green, int blue, double width, Pane layer) {
		x = new double[pointList.size()];
		y = new double[pointList.size()];
		for (int i = 0; i < pointList.size(); i++) {
			x[i] = pointList.get(i).getCenterX();
			y[i] = pointList.get(i).getCenterY();
		}
		splineX.initData(x, pointList.size());
		splineY.initData(y, pointList.size());
		curveNum = (double) (pointList.size() - 1);

		// スプライン曲線を描画
		getElements().add(new MoveTo(x[0], y[0]));
		for (double t = 0.0; t <= curveNum; t += 0.01) {
			getElements().add(new LineTo(splineX.calculate(t), splineY.calculate(t)));
		}
		setId("curve"); // IDをセット
		setStroke(Color.rgb(red, green, blue));
		setStrokeWidth(width);
		setStrokeLineCap(StrokeLineCap.ROUND);
		setStrokeLineJoin(StrokeLineJoin.ROUND);

		// クリッピング
		Shape clipShape = new Rectangle(0, 0, layer.getWidth(), layer.getHeight());
		for (int i = 0; i < pointList.size(); i++) {
			clipShape = subtract(clipShape, new Circle(pointList.get(i).getCenterX(), pointList.get(i).getCenterY(),
					pointList.get(i).getRadius()));
		}
		setClip(clipShape);

		// 曲線をクリックしたときの処理
		setOnMousePressed((e) -> {

			// 曲線を2回左クリックされた場合はその曲線をぼかす
			if (e.getButton().equals(MouseButton.PRIMARY) && e.getClickCount() == 2) {
				setEffect(new GaussianBlur());
			}
		});
	}

}
