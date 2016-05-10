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
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class UriageSyukei {
	public static void main(String[] args) {
		HashMap<String, Long> brasum = new HashMap<String, Long>(); //マップオブジェクト生成
		HashMap<String, Long> comsum = new HashMap<String, Long>(); //マップオブジェクト生成
//1.支店定義ファイル読み込み
		//if(args.length != 1)
		HashMap<String, String> store = new HashMap<String, String>(); //マップオブジェクト生成(名前)
		try {
			File file = new File(args[0],"branch.lst");  //ファイル名を探す
			FileReader filereader = new FileReader(file);  //ファイルから文字列をバッファへ渡す
			BufferedReader br = new BufferedReader(filereader);  //文字列を蓄え、要求に応じて文字列を渡す

			String line;  //変数

			while ((line = br.readLine()) != null) {  //文字列データの受け取り
				String[] items = line.split(",");  //カンマで分ける
//				System.out.println(items[0]);
				store.put(items[0], items[1]);  //items[0]支店コードをキーに, items[1]支店名、を格納
				brasum.put(items[0], 0L);
//				System.out.println(store.get(items[0]));
//				System.out.println(brasum.get(items[0]));

				for(int i =0; i < items.length; i++) {
//					System.out.println(items[0] + "," + items[1]);
				}
				String str = items[0];

				Pattern p = Pattern.compile("^\\d{3}$");  //半角数値3桁にマッチ
		        Matcher m = p.matcher(str);
				if(!m.find()) {
					System.out.println("支店定義ファイルのフォーマットが不正です");
					return;
				}
			}
			br.close();
			filereader.close();
		} catch (FileNotFoundException e) {
			System.out.println("支店定義ファイルが存在しません");
		}
		catch(IOException e) {
			System.out.println(e);
		}

		System.out.println();

//2.商品定義ファイル
		HashMap<String, String> products = new HashMap<String, String>(); //マップオブジェクト生成
		try {
			File file = new File(args[0],"commodity.lst");
			FileReader filereader = new FileReader(file);
			BufferedReader br = new BufferedReader(filereader);

			String line;  //変数

			while ((line = br.readLine()) != null) {  //文字列データの受け取り
				String[] item = line.split(",");  //カンマで分ける

				products.put(item[0], item[1]);  //item[0]商品コード, item[1]商品名、を格納
				comsum.put(item[0], 0L);
//				for(int i =0; i < items.length; i++) {
//					System.out.println(item[0] + "," + item[1]);
//					System.out.println(products.get(item[0]));
//				}
//				String str = item[0];

				Pattern p = Pattern.compile("^\\w{8}$");  //半角英数かつ数値 8桁と一致
		        Matcher m = p.matcher(item[0]);
				if(!m.find()) {
					System.out.println("商品定義ファイルのフォーマットが不正です");
					return;
				}
			}
			br.close();
			filereader.close();
		} catch (FileNotFoundException e) {
			System.out.println("商品定義ファイルが存在しません");
		} catch (IOException e) {
			System.out.println("商品定義ファイルのフォーマットが不正です");
		}

//3.集計
		File file = new File(args[0]);
		File files[] = file.listFiles();  //ファイルの一覧をFile型の配列で返す

		ArrayList<File> sum = new ArrayList<File>();
		for(int i = 0; i < files.length; i++) {
			if(files[i].getName().endsWith(".rcd")) {  //接尾語が.rcdと一致
				String[] item = files[i].getName().toString().split("\\.");  //[C:\Kadai]の表示なし、文字列を.で区切る

				int j = Integer.parseInt(item[0]);  //文字列を数値に変換

				if(j - 1 == i) {
//				System.out.println(j);
				} else {
					System.out.println("売上ファイル名が連番になっていません");
					return;
				}
				sum.add(files[i]);
			}
		}
		try {
			for(int k = 0; k < sum.size(); k++) {
				File String = new File(sum.get(k).toString());
				FileReader filereader = new FileReader(String);
				BufferedReader br = new BufferedReader(filereader);
				String line;

				ArrayList<String> contents = new ArrayList<String>(); //リストに、rcdファイルのみを格納
				while ((line = br.readLine()) != null) {  //文字列データの受け取り
					contents.add(line);
				}
//				System.out.println(contents.get(0)); //支店コード
//				System.out.println(contents.get(1)); //商品コード
//				System.out.println(contents.get(2)); //売上額

				if(!store.containsKey(contents.get(0))) { //storeマップにリスト「contents」と同じキーが含まれているか
					System.out.println("<該当ファイル名>の支店コードが不正です"); //falseなら左文章を表示
					return;
				}

				long sale = Long.parseLong(contents.get(2)); //売上げ額をLong型の数値に
				long shop = brasum.get(contents.get(0)); //支店コードをgetする
				long code = comsum.get(contents.get(1)); //商品コードをgetする
//				System.out.println(contents.get(0));
//				System.out.println(sale + shop);

				brasum.put(contents.get(0), sale + shop); //支店ごとの売上合計をマップにいれる
				comsum.put(contents.get(1), sale + code); //商品ごとの売上合計をマップに入れる

				String str = Long.toString(sale + shop);

				Pattern p = Pattern.compile("^\\d{1,10}$"); //1～10桁までの半角英数にマッチするか
				Matcher m = p.matcher(str);
				if(!m.find()) {
					System.out.println("合計金額が10桁を超えました");
					return;
				}
				if(!(contents.size() == 3)) { //要素数が3と同じではない場合のみ、以下のメッセージを表示
					System.out.println("＜該当ファイル名＞のフォーマットが不正です");
					return;
				}

				br.close();
				filereader.close();
			}
		} catch (FileNotFoundException e) {
				System.out.println("商品定義ファイルが存在しません");
				return;
		} catch (IOException e) {
				System.out.println("商品定義ファイルのフォーマットが不正です");
				return;
		}

//4.集計結果出力
	//支店別集計ファイル
		File branch = new File(args[0],"\\branch.out");
		try {
			FileWriter fw = new FileWriter(branch);
			BufferedWriter bw = new BufferedWriter(fw);

			List<Map.Entry<String, Long>> shopSum =
					new ArrayList<Map.Entry<String, Long>>(brasum.entrySet());// List生成、ソートここから
			Collections.sort(shopSum, new Comparator<Map.Entry<String,Long>>() {

				@Override
				public int compare(Entry<String,Long> entry1, Entry<String,Long> entry2) { //compareを使って比較
					return ((Long)entry2.getValue()).compareTo((Long)entry1.getValue());
				}
			});
			for(Entry<String, Long> s : shopSum) {
				bw.write(s.getKey() + "," + store.get(s.getKey()) + "," + s.getValue());
				bw.newLine();
			}
			bw.close();
			fw.close();
		} catch (IOException e) {
			System.out.println(e);
		}

	//商品別集計ファイル
		File commodity = new File(args[0], "\\commodity.out");
		try {
			FileWriter fw = new FileWriter(commodity);
			BufferedWriter bw = new BufferedWriter(fw);

			List<Map.Entry<String, Long>> codeSum =
					new ArrayList<Map.Entry<String, Long>>(comsum.entrySet());// List生成、ソートここから
			Collections.sort(codeSum, new Comparator<Map.Entry<String,Long>>() {

				@Override
				public int compare(Entry<String,Long> entry1, Entry<String,Long> entry2) { //compareを使って比較
					return ((Long)entry2.getValue()).compareTo((Long)entry1.getValue());
				}
			});
			for(Entry<String, Long> t : codeSum) {
				bw.write(t.getKey() + "," + products.get(t.getKey()) + "," + t.getValue());
				bw.newLine();
			}
			bw.close();
			fw.close();
		} catch(IOException e) {
			System.out.println(e);
		}
	}
}