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

		HashMap<String, Long> branchsum = new HashMap<String, Long>(); //マップオブジェクト生成
		HashMap<String, Long> commoditysum = new HashMap<String, Long>(); //マップオブジェクト生成
		//1.支店定義ファイル読み込み
		HashMap<String, String> branch = new HashMap<String, String>(); //マップオブジェクト生成(名前)
		FileReader fr = null;
		BufferedReader br = null;
		try {
			File file = new File(args[0] + File.separator + "branch.lst");  //ファイル名を探す
			fr = new FileReader(file);  //ファイルから文字列をバッファへ渡す
			br = new BufferedReader(fr);  //文字列を蓄え、要求に応じて文字列を渡す

			String line;  //変数

			while ((line = br.readLine()) != null) {  //文字列データの受け取り
				String[] items = line.split(",", -1);  //カンマで分ける

				if(!(items.length == 2)) { //要素数が2と同じではない場合のみ、以下のメッセージを表示
					System.out.println("支店定義ファイルのフォーマットが不正です");
					return;
				}

				if(!items[0].matches("^\\d{3}$")) { //支店コードが三桁ではない場合のみ、メッセージを表示
					System.out.println("支店定義ファイルのフォーマットが不正です");
					return;
				}
				branch.put(items[0], items[1]);  //items[0]支店コードをキーに, items[1]支店名、を格納
				branchsum.put(items[0], 0L);
			}
		} catch (FileNotFoundException e) {
			System.out.println("支店定義ファイルが存在しません");
			return;
		} catch(IOException e) {
			System.out.println("支店定義ファイルが存在しません");
			return;
		}
		finally {
			try {
				if(br != null) {
					br.close();
				}
			} catch(IOException e) {
				System.out.println("予期せぬエラーが発生しました");
				return;
			}
			try {
				if(fr != null) {
					fr.close();
				}
			} catch(IOException e) {
				System.out.println("予期せぬエラーが発生しました");
				return;
			}
		}

		//2.商品定義ファイル
		HashMap<String, String> commodity = new HashMap<String, String>(); //マップオブジェクト生成
		try {
			File file = new File(args[0] + File.separator + "commodity.lst");
			fr = new FileReader(file);
			br = new BufferedReader(fr);

			String line;  //変数

			while ((line = br.readLine()) != null) {  //文字列データの受け取り
				String[] item = line.split(",", -1);  //カンマで分ける

				for(int i =0; i < item.length; i++) {
					if(!(item.length == 2)) { //要素数が2と同じではない場合のみ、以下のメッセージを表示
						System.out.println("商品定義ファイルのフォーマットが不正です");
						return;
					}
				}

				if(!item[0].matches("^\\w{8}$")) { //商品コードが8桁以外の時、メッセージを表示
					System.out.println("商品定義ファイルのフォーマットが不正です");
					return;
				}
				commodity.put(item[0], item[1]);  //item[0]商品コード, item[1]商品名、を格納
				commoditysum.put(item[0], 0L);
			}
		} catch (FileNotFoundException e) {
			System.out.println("商品定義ファイルが存在しません");
			return;
		} catch (IOException e) {
			System.out.println("商品定義ファイルが存在しません");
			return;
		}
		finally {
			try {
				if(fr != null) {
					fr.close();
				}
			} catch(IOException e) {
				System.out.println("予期せぬエラーが発生しました");
				return;
			}
			try {
				if(br != null) {
					br.close();
				}
			} catch(IOException e) {
				System.out.println("予期せぬエラーが発生しました");
				return;
			}
		}

		//3.集計
		File file = new File(args[0]);
		File files[] = file.listFiles();  //ファイルの一覧をFile型の配列で返す

		ArrayList<File> sum = new ArrayList<File>();
		for(int i = 0; i < files.length; i++) {
			if(files[i].getName().endsWith(".rcd")) {  //接尾語が.rcdと一致
				if(files[i].isFile()) {
					String[] item = files[i].getName().toString().split("\\.", -1);  //[C:\Kadai]の表示なし、文字列を.で区切る
					int j = Integer.parseInt(item[0]);  //文字列を数値に変換

					if(!(j - 1 == i)) {
						System.out.println("売上ファイル名が連番になっていません");
						return;
					}

//				if(!item[0].matches("^\\w{8}$")) { //.rcd前のファイル名が8桁以外の時、メッセージを表示
//					System.out.println("売上ファイル名が連番になっていません");
//					return;
//				}

					sum.add(files[i]);
				}
			}
		}
		try {
			for(int k = 0; k < sum.size(); k++) {
				File String = new File(sum.get(k).toString());
				FileReader filereader = new FileReader(String);
				br = new BufferedReader(filereader);

				String line;

				ArrayList<String> contents = new ArrayList<String>(); //リストに、rcdファイルのみを格納
				while ((line = br.readLine()) != null) {  //文字列データの受け取り
					contents.add(line);
				}

				if(!(contents.size() == 3)) { //要素数が3と同じではない場合のみ、以下のメッセージを表示
					System.out.println(sum.get(k).getName() + "のフォーマットが不正です");
					return;
				}

				if(!branch.containsKey(contents.get(0))) { //storeマップにリスト「contents」と同じキーが含まれているか
					System.out.println(sum.get(k).getName() + "の支店コードが不正です"); //falseなら左文章を表示
					//.getNameを使う
					return;
				}

				if(!commodity.containsKey(contents.get(1))) {
					System.out.println(sum.get(k).getName() + "の商品コードが不正です");
					return;
				}

//				if(!(contents.size() == 3)) { //要素数が3と同じではない場合のみ、以下のメッセージを表示
//					System.out.println(sum.get(k).getName() + "のフォーマットが不正です");
//					return;
//				}

				long sale = Long.parseLong(contents.get(2)); //売上げ額をLong型の数値に
				long shop = branchsum.get(contents.get(0)); //支店コードをgetする
				long code = commoditysum.get(contents.get(1)); //商品コードをgetする

				branchsum.put(contents.get(0), sale + shop); //支店ごとの売上合計をマップにいれる
				commoditysum.put(contents.get(1), sale + code); //商品ごとの売上合計をマップに入れる

				String str = Long.toString(sale + shop);

				if(!str.matches("^\\d{1,10}$")) { //合計金額が1～10桁ではない場合
					System.out.println("合計金額が10桁を超えました");
					return;
				}
			}
		} catch (FileNotFoundException e) {
				System.out.println("商品定義ファイルが存在しません");
				return;
		} catch (IOException e) {
				System.out.println("商品定義ファイルのフォーマットが不正です");
				return;
		}
		finally {
			try {
				if(br != null) {
					br.close();
				}
			} catch(IOException e) {
				System.out.println("予期せぬエラーが発生しました");
				return;
			}
		}

		//4.集計結果出力
		//支店別集計ファイル
		File file1 = new File(args[0] + File.separator + "branch.out");
		BufferedWriter bw = null;
		try {
			FileWriter fw = new FileWriter(file1);
			bw = new BufferedWriter(fw);

			List<Map.Entry<String, Long>> shopSum =
					new ArrayList<Map.Entry<String, Long>>(branchsum.entrySet());// List生成、ソートここから
			Collections.sort(shopSum, new Comparator<Map.Entry<String,Long>>() {

				@Override
				public int compare(Entry<String,Long> entry1, Entry<String,Long> entry2) { //compareを使って比較
					return ((Long)entry2.getValue()).compareTo((Long)entry1.getValue());
				}
			});
			for(Entry<String, Long> s : shopSum) {
				bw.write(s.getKey() + "," + branch.get(s.getKey()) + "," + s.getValue());
				bw.newLine();
			}
		} catch (IOException e) {
			System.out.println("予期せぬエラーが発生しました");
			return;
		}
		finally {
			try {
				if(bw != null) {
					bw.close();
				}
			} catch(IOException e) {
				System.out.println("予期せぬエラーが発生しました");
				return;
			}
		}

		//商品別集計ファイル
		File file2 = new File(args[0] + File.separator + "commodity.out"); //名前をつけファイルを作成
		try {
			FileWriter fw = new FileWriter(file2);
			bw = new BufferedWriter(fw);

			List<Map.Entry<String, Long>> codeSum =
					new ArrayList<Map.Entry<String, Long>>(commoditysum.entrySet());// List生成、ソートここから
			Collections.sort(codeSum, new Comparator<Map.Entry<String,Long>>() {

				@Override
				public int compare(Entry<String,Long> entry1, Entry<String,Long> entry2) { //compareを使って比較
					return ((Long)entry2.getValue()).compareTo((Long)entry1.getValue());
				}
			});
			for(Entry<String, Long> t : codeSum) {
				bw.write(t.getKey() + "," + commodity.get(t.getKey()) + "," + t.getValue()); //ファイルに書き込む
				bw.newLine();
			}
		} catch(IOException e) {
			System.out.println("予期せぬエラーが発生しました");
			return;
		}
		finally {
			try {
				if(bw != null) {
					bw.close();
				}
			} catch(IOException e) {
				System.out.println("予期せぬエラーが発生しました");
				return;
			}
		}
	}
}