package jp.ac.ibaraki.cis.puma;

/**
 * スプライン曲線のデータを表すクラス
 * 
 * @author shotaro
 *
 */
public class Spline {
	private double[] a, b, c, d; // 3次多項式の係数a, b, c, d
	private int curveNum; // 点と点の間の曲線の番号

	/** コンストラクタ */
	public Spline() {
		curveNum = 0;
	}

	/**
	 * スプラインのデータを初期化するメソッド
	 * 
	 * @param sp            スプラインのデータの計算に使用する座標(x座標またはy座標)が格納されている配列
	 * @param pointlistSize 点の個数
	 */
	public void initData(double sp[], int pointlistSize) {
		double tmp;
		double[] w;

		curveNum = pointlistSize - 1;

		a = new double[pointlistSize];
		b = new double[pointlistSize];
		c = new double[pointlistSize];
		d = new double[pointlistSize];
		w = new double[pointlistSize];

		// 0次係数aを設定
		for (int i = 0; i <= curveNum; i++) {
			a[i] = sp[i];
		}

		// 2次係数cを計算
		c[0] = 0.0;
		c[curveNum] = 0.0;
		for (int i = 1; i < curveNum; i++) {
			c[i] = 3.0 * (a[i - 1] - 2.0 * a[i] + a[i + 1]);
		}
		w[0] = 0.0;
		for (int i = 1; i < curveNum; i++) {
			tmp = 4.0 - w[i - 1];
			c[i] = (c[i] - c[i - 1]) / tmp;
			w[i] = 1.0 / tmp;
		}
		for (int i = curveNum - 1; i > 0; i--) {
			c[i] = c[i] - c[i + 1] * w[i];
		}

		// 1次係数b, 3次係数dを計算
		b[curveNum] = 0.0;
		d[curveNum] = 0.0;
		for (int i = 0; i < curveNum; i++) {
			d[i] = (c[i + 1] - c[i]) / 3.0;
			b[i] = a[i + 1] - a[i] - c[i] - d[i];
		}
	}

	/**
	 * 媒介変数tに対する補間値を計算するメソッド
	 * 
	 * @param t 媒介変数
	 * @return tに対応する補間値
	 */
	public double calculate(double t) {
		int i;
		double dt;

		i = (int) Math.floor(t); // 小数点以下切り捨て
		if (i < 0) {
			i = 0;
		} else if (i >= curveNum) {
			i = curveNum - 1;
		}
		dt = t - (double) i;
		return a[i] + (b[i] + (c[i] + d[i] * dt) * dt) * dt;
	}
}
