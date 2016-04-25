import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UriageSyukei {
	public static void main(String[] args) {
//支店定義ファイル読み込み
		
		//if(args.length != 1)
		HashMap<String, String> map = new HashMap<String, String>(); //マップオブジェクト生成
		try {
			File file = new File(args[0],"branch.lst");  //ファイル名を探す
			FileReader filereader = new FileReader(file);  //ファイルから文字列をバッファへ渡す
			BufferedReader br = new BufferedReader(filereader);  //文字列を蓄え、要求に応じて文字列を渡す

			String line;  //変数
			
			while ((line = br.readLine()) != null) {  //文字列データの受け取り
				String[] items = line.split(",");
				
				map.put(items[0], items[1]);  //items[0]支店コード, items[1]支店名
//				for(int i =0; i < items.length; i++) {
					System.out.println(items[0] + "," + items[1]);
//				}
				String str = items[0];
				
				Pattern p = Pattern.compile("^\\d{3}$");  //半角数値3桁にマッチ
		        Matcher m = p.matcher(str);
			}
			br.close();
			filereader.close();
		} catch (FileNotFoundException e) {
			System.out.println("支店定義ファイルが存在しません");
		} catch (IOException e) {
			System.out.println("支店定義ファイルのフォーマットが不正です");
		}
		

		System.out.println();
		
//商品定義ファイル
		HashMap<String, String> map1 = new HashMap<String, String>(); //マップオブジェクト生成
		try {
			File file = new File(args[0],"commodity.txt");
			FileReader filereader = new FileReader(file);
			BufferedReader br = new BufferedReader(filereader);

			String line;  //変数
			
			while ((line = br.readLine()) != null) {  //文字列データの受け取り
				String[] item = line.split(",");
				
				map1.put(item[0], item[1]);
//				for(int i =0; i < items.length; i++) {
					System.out.println(item[0] + "," + item[1]);
//				}
			}
			br.close();
			filereader.close();
		} catch (FileNotFoundException e) {
			System.out.println("商品定義ファイルが存在しません");
		} catch (IOException e) {
			System.out.println("商品定義ファイルのフォーマットが不正です");
		}
	}
}