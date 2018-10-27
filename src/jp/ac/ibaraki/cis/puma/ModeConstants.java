package jp.ac.ibaraki.cis.puma;

/**
 * 描画モードの選択で使用する定数を集めたクラス
 * 
 * @author shotaro
 *
 */
public final class ModeConstants {

	/** コンストラクタ */
	private ModeConstants() {
	}

	/** 描画モードを表す定数 */
	public static final int FREELINE = 0; // 自由線モード
	public static final int RECTANGLE = 1; // 矩形モード
	public static final int CURVE = 2; // 曲線モード
	public static final int ELLIPSE = 3; // 楕円モード

}
