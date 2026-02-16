package jp.ac.ttc.webapp.adminservice;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.google.gson.Gson;

/**
 * 翻訳APIを使用するクラス
 * @author suzuki-takumi23
 * @since 2026-01-28
 */
public class Translator {
    public String  translateString(String txt) {
        String translationString = "";
        try{
                String apiKey = "2b465d98-8bf0-4d44-8929-edf83ffb7182:fx";  // ← DeepL APIキー
                String text = txt;
                String targetLang = "JA";

                // DeepL Free API endpoint

                String params ="text=" + URLEncoder.encode(text, StandardCharsets.UTF_8) +
                                "&target_lang=" + targetLang;

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("https://api-free.deepl.com/v2/translate"))
                        .header("Authorization", "DeepL-Auth-Key " + apiKey)
                        .header("Content-Type", "application/x-www-form-urlencoded")
                        .POST(HttpRequest.BodyPublishers.ofString(params))
                        .build();


                HttpClient client = HttpClient.newHttpClient();
                HttpResponse<String> response =
                        client.send(request, HttpResponse.BodyHandlers.ofString());
                String json = response.body();
                Gson gson = new Gson();
                System.out.print(json);
                TranslationObject translationObject = gson.fromJson(json, TranslationObject.class);
                System.err.println("TransrationObject.getTranslations " + translationObject.getTranslations());
                List<Translation> list = translationObject.getTranslations();
                translationString = list.get(list.size()-1).getText();
        } catch(Exception e) {
                e.printStackTrace();
        }
        return translationString;
    }
}
