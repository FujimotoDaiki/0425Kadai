package jp.co.iccom.fujimoto_daiki.calculate_sales;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


public class CalculateSystem {
	public static void main(String[] args) {
		if(args.length != 1) {
			System.out.println("予期せぬエラーが発生しました");
			return;
		}

		HashMap<String, Long> branchSumMap = new HashMap<String, Long>(); //マップオブジェクト生成
		HashMap<String, Long> commoditySumMap = new HashMap<String, Long>(); //マップオブジェクト生成
		
		//1.支店定義ファイル読み込み
		HashMap<String, String> branchDetailMap = new HashMap<String, String>(); //マップオブジェクト生成(名前)
		
		//2.商品定義ファイル
		HashMap<String, String> commodityDetailMap = new HashMap<String, String>(); //マップオブジェクト生成
		
		//支店定義ファイルの読み込み
		if(!fileReader(args[0], "branch.lst", "^\\d{3}$", "支店", branchDetailMap, branchSumMap)) {
			return;
		}
		//商品定義ファイルの読み込み
		if(!fileReader(args[0], "commodity.lst", "^\\w{8}$", "商品", commodityDetailMap, commoditySumMap)) {
			return;
		}
		//加算処理
		if(!(saleSum(args[0], branchSumMap, commoditySumMap))) {
			return;
		}
		//支店別ファイル集計 fileWriterメソッド呼び出し
		if(!fileWriter(args[0], "branch.out", branchDetailMap, branchSumMap)) {
			return;
		}
		//商品別ファイル集計 fileWriterメソッド呼び出し
		if(!fileWriter(args[0], "commodity.out", commodityDetailMap, commoditySumMap)) {
			return;
		}
	}

	//支店＆商品定義ファイルの読み込み
	private static boolean fileReader(String path, String fileName, String expretion, String branch, HashMap<String, String> detailMap, HashMap<String, Long> sumMap) {
		FileReader fr = null;
		BufferedReader br = null;
		try {
			File file = new File(path + File.separator + fileName);  //ファイル名を探す
			fr = new FileReader(file);  //ファイルから文字列をバッファへ渡す
			br = new BufferedReader(fr);  //文字列を蓄え、要求に応じて文字列を渡す

			String line;  //変数

			while ((line = br.readLine()) != null) {  //文字列データの受け取り
				String[] items = line.split(",", -1);  //カンマで分ける

				if(!(items.length == 2)) { //要素数が2と同じではない場合のみ、以下のメッセージを表示
					System.out.println(branch + "定義ファイルのフォーマットが不正です");
					return false;
				}

				if(!items[0].matches(expretion)) { //支店コードが三桁ではない場合のみ、メッセージを表示
					System.out.println(branch + "定義ファイルのフォーマットが不正です");
					return false;
				}
				detailMap.put(items[0], items[1]);  //items[0]支店コードをキーに, items[1]支店名、を格納
				sumMap.put(items[0], 0L);
			}
		} catch (FileNotFoundException e) {
			System.out.println(branch + "定義ファイルが存在しません");
			return false;
		} catch(IOException e) {
			System.out.println(branch + "定義ファイルが存在しません");
			return false;
		}
		finally {
			try {
				if(br != null) {
					br.close();
				}
			} catch(IOException e) {
				System.out.println("予期せぬエラーが発生しました");
				return false;
			}
			try {
				if(fr != null) {
					fr.close();
				}
			} catch(IOException e) {
				System.out.println("予期せぬエラーが発生しました");
				return false;
			}
		}
		return true;
	}

	//加算処理
	private static boolean saleSum(String path, HashMap<String, Long> branchSumMap, HashMap<String, Long> commoditySumMap) {
		File file = new File(path + File.separator);
		File files[] = file.listFiles();  //ファイルの一覧をFile型の配列で返す

		ArrayList<File> rcdList = new ArrayList<File>();
		for(int i = 0; i < files.length; i++) {

			String str = files[i].getName();

			if(str.matches("^\\d{8}.rcd$")) {  //接尾語が.rcdと一致
				if(files[i].isFile()) {
					rcdList.add(files[i]);
				}
			}
		}
		for(int i = 0; i < rcdList.size(); i++) {
			String[] item = rcdList.get(i).getName().toString().split("\\.", -1);  //[C:\Kadai]の表示なし、文字列を.で区切る
			int j = Integer.parseInt(item[0]);  //文字列を数値に変換
			if(j - 1 != i) {
				System.out.println("売上ファイル名が連番になっていません");
				return false;
			}
		}
		FileReader fr = null;
		BufferedReader br = null;
		try {
			for(int i = 0; i < rcdList.size(); i++) {
				File String = new File(rcdList.get(i).toString());
				fr = new FileReader(String);
				br = new BufferedReader(fr);

				String line;

				ArrayList<String> contents = new ArrayList<String>(); //リストに、rcdファイルのみを格納
				while ((line = br.readLine()) != null) {  //文字列データの受け取り
					contents.add(line);
				}

				if(!(contents.size() == 3)) { //要素数が3と同じではない場合のみ、以下のメッセージを表示
					System.out.println(rcdList.get(i).getName() + "のフォーマットが不正です");
					return false;
				}

				if(!branchSumMap.containsKey(contents.get(0))) { //storeマップにリスト「contents」と同じキーが含まれているか
					System.out.println(rcdList.get(i).getName() + "の支店コードが不正です"); //falseなら左文章を表示
					//.getNameを使う
					return false;
				}

				if(!commoditySumMap.containsKey(contents.get(1))) {
					System.out.println(rcdList.get(i).getName() + "の商品コードが不正です");
					return false;
				}

				long sale = Long.parseLong(contents.get(2)); //売上げ額をLong型の数値に
				long branchSale = branchSumMap.get(contents.get(0)); //支店ごとの売上をgetする
				long commoditySale = commoditySumMap.get(contents.get(1)); //商品ごとの売上をgetする

				branchSumMap.put(contents.get(0), sale + branchSale); //支店ごとの売上合計をマップにいれる
				commoditySumMap.put(contents.get(1), sale + commoditySale); //商品ごとの売上合計をマップに入れる

				String strBranch = Long.toString(sale + branchSale);
				String strCommodity = Long.toString(sale + commoditySale);

				if(!(strBranch.length() <= 10)) { //支店別の合計金額が1～10桁ではない場合
					System.out.println("合計金額が10桁を超えました");
					return false;
				}

				if(!(strCommodity.length() <= 10)) { //商品別の合計金額が1～10桁ではない場合
					System.out.println("合計金額が10桁を超えました");
					return false;
				}
			}
		} catch (FileNotFoundException e) {
				System.out.println("商品定義ファイルが存在しません");
				return false;
		} catch (IOException e) {
				System.out.println("商品定義ファイルのフォーマットが不正です");
				return false;
		}
		finally {
			try {
				if(br != null) {
					br.close();
				}
			} catch(IOException e) {
				System.out.println("予期せぬエラーが発生しました");
				return false;
			}
			try {
				if(fr != null) {
					fr.close();
				}
			} catch(IOException e) {
				System.out.println("予期せぬエラーが発生しました");
				return false;
			}
		}
		return true;
	}

	//支店＆商品別ファイル出力
	private static boolean fileWriter(String path, String fileName, HashMap<String, String> detailMap, HashMap<String, Long> sumMap) {
		File file = new File(path + File.separator + fileName);
		BufferedWriter bw = null;
		try {
			FileWriter fw = new FileWriter(file);
			bw = new BufferedWriter(fw);

			List<Map.Entry<String, Long>> listSum =
					new ArrayList<Map.Entry<String, Long>>(sumMap.entrySet());// List生成、ソートここから
			Collections.sort(listSum, new Comparator<Map.Entry<String,Long>>() {

				@Override
				public int compare(Entry<String,Long> entry1, Entry<String,Long> entry2) { //compareを使って比較
					return ((Long)entry2.getValue()).compareTo((Long)entry1.getValue());
				}
			});
			for(Entry<String, Long> s : listSum) {
				bw.write(s.getKey() + "," + detailMap.get(s.getKey()) + "," + s.getValue());
				bw.newLine();
			}
		} catch (IOException e) {
			System.out.println("予期せぬエラーが発生しました");
			return false;
		}
		finally {
			try {
				if(bw != null) {
					bw.close();
				}
			} catch(IOException e) {
				System.out.println("予期せぬエラーが発生しました");
				return false;
			}
		}
		return true;
	}
}