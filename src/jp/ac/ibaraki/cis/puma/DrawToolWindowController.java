package jp.ac.ibaraki.cis.puma;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.canvas.*;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.shape.LineTo;
import javafx.scene.SnapshotParameters;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javax.imageio.ImageIO;

import static jp.ac.ibaraki.cis.puma.ModeConstants.*;

/**
 * お絵かきツールのコントローラとなるクラス
 * 
 * @author shotaro
 *
 */
public class DrawToolWindowController implements Initializable {

	/** 背景のペイン */
	@FXML
	private Pane rootPane;

	/** 背景のキャンバス */
	@FXML
	private Canvas rootCanvas;

	/** 赤、緑、青の色の数値を表すラベル */
	@FXML
	private Label label_RED, label_GREEN, label_BLUE;

	/** 赤、緑、青の色の数値、線の太さの数値を調節するスライダー */
	@FXML
	private Slider slider_RED, slider_GREEN, slider_BLUE, slider_LINE_WIDTH;

	/** レイヤーを選択するためのコンボボックス */
	@FXML
	private ComboBox<Integer> layerCombo;

	/** 自分自身のStage */
	private Stage thisStage;

	/** レイヤーとなるペインを格納する配列 */
	private Pane[] layer;

	/** レイヤーに対応するキャンバスを格納する配列 */
	private Canvas[] canvas;

	/** 描画モードを設定する変数 */
	private int mode;

	/** 選択されているレイヤーを表す変数 */
	private int selectedLayer;

	/** 以前の座標を格納するためのPoint2D */
	private Point2D previous;

	/** 自由線の描画で使用する変数 */
	private MyFreeLine freeline;

	/** 描画したスプライン曲線用の点をまとめたリスト */
	private ArrayList<SplineCurvePoint>[] pointList;

	/** スプライン曲線の描画で使用する変数 */
	private int[] curve_red; // 赤の色の数値
	private int[] curve_green; // 緑の色の数値
	private int[] curve_blue; // 青の色の数値
	private double[] curve_width; // 線の太さの数値

	/** BMPの読み込み時と保存時に使用するファイルチューザー */
	private FileChooser openFileChooser, saveFileChooser;

	/** BMPを読み込んでキャンバスに表示する際に使用するGraphicsContext */
	private GraphicsContext gc;

	/**
	 * 「BMPを開く」メニューを選択したときに行うメソッド
	 * 
	 * @param e ActionEvent
	 */
	@FXML
	private void handleOpen(ActionEvent e) {
		File openFile = openFileChooser.showOpenDialog(thisStage);
		if (openFile != null) {
			// BMPを読み込んで背景のキャンバスに描画
			Image openImage = new Image(openFile.toURI().toString());
			gc.drawImage(openImage, 0, 0, rootCanvas.getWidth(), rootCanvas.getHeight());
		}
	}

	/**
	 * 「BMPで保存」メニューを選択したときに行うメソッド
	 * 
	 * @param e ActionEvent
	 * @throws Exception
	 */
	@FXML
	private void handleSave(ActionEvent e) throws Exception {
		File saveFile = saveFileChooser.showSaveDialog(thisStage);
		if (saveFile != null) {
			// キャンバスに描かれた絵をBMPとして保存
			BufferedImage saveImage = new BufferedImage((int) rootCanvas.getWidth(), (int) rootCanvas.getHeight(),
					BufferedImage.TYPE_INT_RGB);
			Graphics g = saveImage.getGraphics();
			g.drawImage(SwingFXUtils.fromFXImage(rootPane.snapshot(new SnapshotParameters(), null), null), 0, 0, null);
			g.dispose();
			ImageIO.write(saveImage, "bmp", saveFile);
		}
	}

	/**
	 * 「アプリケーションの終了」メニューを選択したときに行うメソッド
	 * 
	 * @param e ActionEvent
	 */
	@FXML
	private void handleExit(ActionEvent e) {
		Platform.exit();
	}

	/**
	 * 「全てのレイヤーを初期化」メニューを選択したときに行うメソッド
	 * 
	 * @param e ActionEvent
	 */
	@FXML
	private void handleReset(ActionEvent e) {
		// 全てのレイヤーのオブジェクトをすべて消す
		for (int i = 0; i < layerCombo.getItems().size(); i++) {
			layer[i].getChildren().removeAll(layer[i].lookupAll("#line"));
			layer[i].getChildren().removeAll(layer[i].lookupAll("#rectangle"));
			layer[i].getChildren().removeAll(layer[i].lookupAll("#point"));
			layer[i].getChildren().removeAll(layer[i].lookupAll("#curve"));
			layer[i].getChildren().removeAll(layer[i].lookupAll("#ellipse"));
			pointList[i].clear();
		}
		gc.clearRect(0, 0, rootPane.getWidth(), rootPane.getHeight()); // 背景をリセット
	}

	/**
	 * 「全消し」ボタンを押したときに行うメソッド
	 * 
	 * @param e ActionEvent
	 */
	@FXML
	private void handleClear(ActionEvent e) {
		// 選択されているレイヤーのオブジェクトをすべて消す
		layer[selectedLayer].getChildren().removeAll(layer[selectedLayer].lookupAll("#line"));
		layer[selectedLayer].getChildren().removeAll(layer[selectedLayer].lookupAll("#rectangle"));
		layer[selectedLayer].getChildren().removeAll(layer[selectedLayer].lookupAll("#point"));
		layer[selectedLayer].getChildren().removeAll(layer[selectedLayer].lookupAll("#curve"));
		layer[selectedLayer].getChildren().removeAll(layer[selectedLayer].lookupAll("#ellipse"));
		pointList[selectedLayer].clear();
	}

	/**
	 * 「バージョン情報」メニューを選択したときに行うメソッド
	 * 
	 * @param e ActionEvent
	 * @throws Exception
	 */
	@FXML
	private void handleVersion(ActionEvent e) throws Exception {
		// バージョン情報を表示するウィンドウを生成して表示
		Stage newStage = new Stage();
		newStage.setTitle("バージョン情報");
		newStage.setResizable(false);
		FXMLLoader loader = new FXMLLoader(getClass().getResource("VersionWindow.fxml"));
		Parent root = loader.load();
		newStage.setScene(new Scene(root));
		newStage.initModality(Modality.APPLICATION_MODAL);
		newStage.initOwner(thisStage);
		newStage.show();
	}

	/**
	 * コンボボックスでレイヤーを選択したときに行うメソッド
	 * 
	 * @param e ActionEvent
	 */
	@FXML
	private void handleLayer(ActionEvent e) {
		selectedLayer = layerCombo.getValue() - 1;
		layer[selectedLayer].toFront(); // 選択されたレイヤーを最前面に移動
	}

	/**
	 * 「自由線」ラジオボタンを押したときに行うメソッド
	 * 
	 * @param e ActionEvent
	 */
	@FXML
	private void setFreeLineMode(ActionEvent e) {
		mode = FREELINE;
	}

	/**
	 * 「矩形」ラジオボタンを押したときに行うメソッド
	 * 
	 * @param e ActionEvent
	 */
	@FXML
	private void setRectangleMode(ActionEvent e) {
		mode = RECTANGLE;
	}

	/**
	 * 「曲線」ラジオボタンを押したときに行うメソッド
	 * 
	 * @param e ActionEvent
	 */
	@FXML
	private void setCurveMode(ActionEvent e) {
		mode = CURVE;
	}

	/**
	 * 「楕円」ラジオボタンを押したときに行うメソッド
	 * 
	 * @param e ActionEvent
	 */
	@FXML
	private void setEllipseMode(ActionEvent e) {
		mode = ELLIPSE;
	}

	/**
	 * 初期化を行うメソッド
	 * 
	 * @param url
	 * @param rb
	 */
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		mode = FREELINE; // 自由線モードに設定
		gc = rootCanvas.getGraphicsContext2D(); // GraphicsContextを取得

		// レイヤー、キャンバスを格納する配列の初期化
		layer = new Pane[layerCombo.getItems().size()];
		canvas = new Canvas[layerCombo.getItems().size()];

		pointList = new ArrayList[layerCombo.getItems().size()]; // スプライン曲線用の点をまとめるリストの初期化

		// スプライン曲線の描画で使用する変数の初期化
		curve_red = new int[layerCombo.getItems().size()];
		curve_green = new int[layerCombo.getItems().size()];
		curve_blue = new int[layerCombo.getItems().size()];
		curve_width = new double[layerCombo.getItems().size()];

		// 各レイヤーの初期化を行う
		for (int i = 0; i < layerCombo.getItems().size(); i++) {
			layer[i] = new Pane();
			layer[i].setPrefSize(rootCanvas.getWidth(), rootCanvas.getHeight());
			canvas[i] = new Canvas(rootCanvas.getWidth(), rootCanvas.getHeight());
			layer[i].getChildren().add(canvas[i]);
			rootPane.getChildren().add(layer[i]);
			pointList[i] = new ArrayList<>();

			int tmp = i;

			// レイヤーのキャンバス上でクリックされたとき
			canvas[tmp].setOnMousePressed((e) -> {
				previous = new Point2D(e.getX(), e.getY());
				switch (mode) {
				case FREELINE: // 自由線モード
					// 自由線を初期化
					freeline = new MyFreeLine(previous, (int) slider_RED.getValue(), (int) slider_GREEN.getValue(),
							(int) slider_BLUE.getValue(), slider_LINE_WIDTH.getValue(), layer[tmp]);
					layer[tmp].getChildren().add(freeline);
					break;

				case RECTANGLE:
					break; // 矩形モード

				case CURVE: // 曲線モード
					// 点の数が0個なら曲線の色、太さを設定する
					if (pointList[tmp].isEmpty()) {
						curve_red[tmp] = (int) slider_RED.getValue();
						curve_green[tmp] = (int) slider_GREEN.getValue();
						curve_blue[tmp] = (int) slider_BLUE.getValue();
						curve_width[tmp] = slider_LINE_WIDTH.getValue();
					}
					// 点を描画
					SplineCurvePoint point = new SplineCurvePoint(e.getX(), e.getY(), pointList[tmp], curve_red[tmp],
							curve_green[tmp], curve_blue[tmp], curve_width[tmp], layer[tmp]);
					point.setNumber(pointList[tmp].size());
					pointList[tmp].add(point);
					layer[tmp].getChildren().add(point);
					// 以前描いた曲線をクリア
					layer[tmp].getChildren().remove(layer[tmp].lookup("#curve"));
					// 点の数が2つ以上なら曲線を描画する
					if (pointList[tmp].size() >= 2) {
						SplineCurve curve = new SplineCurve(pointList[tmp], curve_red[tmp], curve_green[tmp],
								curve_blue[tmp], curve_width[tmp], layer[tmp]);
						layer[tmp].getChildren().add(curve);
					}
					break;

				case ELLIPSE:
					break; // 楕円モード
				}
			});

			// レイヤーのキャンバス上でドラッグされたとき
			canvas[tmp].setOnMouseDragged((e) -> {
				Point2D current = new Point2D(e.getX(), e.getY());
				switch (mode) {
				case FREELINE: // 自由線モード
					// 自由線を延長
					freeline.getElements().add(new LineTo(current.getX(), current.getY()));
					break;

				case RECTANGLE:
					break; // 矩形モード

				case CURVE:
					break; // 曲線モード

				case ELLIPSE:
					break; // 楕円モード
				}
			});

			// レイヤーのキャンバス上でマウスボタンを離したとき
			canvas[tmp].setOnMouseReleased((e) -> {
				Point2D current = new Point2D(e.getX(), e.getY());
				switch (mode) {
				case FREELINE: // 自由線モード
					// 自由線を延長
					freeline.getElements().add(new LineTo(current.getX(), current.getY()));
					break;

				case RECTANGLE: // 矩形モード
					// 矩形を描画
					MyRectangle rect = new MyRectangle(previous, current, (int) slider_RED.getValue(),
							(int) slider_GREEN.getValue(), (int) slider_BLUE.getValue(), layer[tmp]);
					previous = current;
					layer[tmp].getChildren().add(rect);
					break;

				case CURVE:
					break; // 曲線モード

				case ELLIPSE: // 楕円モード
					// 楕円を描画
					MyEllipse ellipse = new MyEllipse(previous, current, (int) slider_RED.getValue(),
							(int) slider_GREEN.getValue(), (int) slider_BLUE.getValue(), layer[tmp]);
					previous = current;
					layer[tmp].getChildren().add(ellipse);
					break;
				}
			});
		}

		// レイヤー1が選択されている状態に初期化
		selectedLayer = 0;
		layer[selectedLayer].toFront();
		layerCombo.getSelectionModel().selectFirst();

		// ファイルチューザーを初期化
		openFileChooser = new FileChooser();
		saveFileChooser = new FileChooser();
		FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("ビットマップ", "*.bmp");
		openFileChooser.getExtensionFilters().add(filter);
		saveFileChooser.getExtensionFilters().add(filter);

		// スライダーの赤の色の数値を表示
		slider_RED.valueProperty()
				.addListener((ObservableValue<? extends Number> observ, Number oldval, Number newVal) -> {
					label_RED.setText("　：" + newVal.intValue());
				});

		// スライダーの緑の色の数値を表示
		slider_GREEN.valueProperty()
				.addListener((ObservableValue<? extends Number> observ, Number oldval, Number newVal) -> {
					label_GREEN.setText("　：" + newVal.intValue());
				});

		// スライダーの青の色の数値を表示
		slider_BLUE.valueProperty()
				.addListener((ObservableValue<? extends Number> observ, Number oldval, Number newVal) -> {
					label_BLUE.setText("　：" + newVal.intValue());
				});
	}

	/**
	 * 自分自身のStageを設定するメソッド
	 * 
	 * @param stage
	 */
	public void setThisStage(Stage stage) {
		thisStage = stage;
	}

}