package jp.ac.ttc.webapp.adminservice;


import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 特定のデータをフォーマットする機能を揃えたユーティリティクラス
 * @author suzuki-takumi23
 * @since 2026-01-16
 */
public class DataFormatter {
    //国名辞典のように機能する
    //原産国名を抜き出すために使うので、新規で見つかり次第更新予定
    public static final Set<String> COUNTRIES = Set.of(
        "日本", "中国", "韓国", "台湾", "小笠原父島",
        "アメリカ", "USA", "フランス", "イタリア",
        "スペイン", "ドイツ", "イギリス",
        "タイ", "ベトナム", "インド"
    );

    //原産国を抜き出すためのパターン
    private static final Pattern ORIGIN_PATTERN = Pattern.compile(
        "(?:原産国|原産地|生産国)\\s*[：:]*\\s*([\\p{IsHan}ァ-ヶーA-Za-z]{1,5})"
        + "|([\\p{IsHan}ァ-ヶーA-Za-z]{1,5})\\s*(?:産)",
        Pattern.UNICODE_CHARACTER_CLASS
    );

    /**
     * 商品説明から内容量を取得する
     * @param caption この値限定の処理内容 {@code Item.itemCaption}
     * @return 整数　グラム表記前提の容量
     */
    public static int getCapacityFromCaption(String caption) {
        //抜き出すための正規表現パターン。
        Pattern pattern = Pattern.compile("([0-9０-９]+(?:[\\.．][0-9０-９]+)?)\\s*(mg|g|kg|ｍｇ|ｇ|ｋｇ)");
        
        //戻り値用の変数。0初期化
        int gramCapa = 0;

        //引数で渡された商品説明から上記正規表現で抜き出す
        Matcher matcher = pattern.matcher(caption);
        if (matcher.find()) {
            //抜き出した文字列を単位と数値に分離
            String capStr =  matcher.group(1);
            String unit = matcher.group(2);
            //それぞれを次のメソッドに渡し、グラム表記の内容量を取得
            gramCapa = (int)toGram(Double.parseDouble(capStr), unit);
        }
        //グラム表記の内容量
        return gramCapa;
    }

    /**
     * このクラスのみで使うよう設計された単位変換メソッド
     * @param value 容量の数値部分　{@code ex: "100", "2", "30"}
     * @param unit 容量の単位部分 {@code ex: "kg", "mg", "g"}
     * @return {@code "g"に統一されたときの数値部分の値}
     */
    static double toGram(double value, String unit) {
        switch (unit.toLowerCase()) {
            case "mg":
                value = value * 1000;
                break;
            case "kg":
                value = value * 1000;
                break;
            default:
                break; // g
        }

        return value;
    }

    /**
     * 商品説明から原産国名を取得する
     * @param text 商品説明{@code Item.itemCaption}
     * @return {@code 抜き出した原産国名 } または {@code "表記なし"}
     */
   public static String getOriginCountry(String text) {
        //nullチェックを一応挟む
        if (text == null || text.isEmpty()) {
            return null;
        }

        //原産国の正規表現からある程度抜きだす
        Matcher matcher = ORIGIN_PATTERN.matcher(text);

        while (matcher.find()) {
            //抜き出せた国名がどのグループに属するかで、「原産国名」だけを抽出
            String candidate =
                matcher.group(1) != null ? matcher.group(1) : matcher.group(2);

            //どこにもマッチしなかったらこのwhileは抜けます。
            if (candidate == null) {
                continue;
            }

            // 国名辞書に存在するものだけ返す
            if (COUNTRIES.contains(candidate)) {
                return candidate;
            }
        }
        return "表記なし";
   }
}
