package jp.ac.ttc.webapp.adminservice;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.google.gson.Gson;

/**
 * 楽天商品APIに検索する機能関連のクラス
 * @author suzuki-takumi23
 * @since 2026-01-16
 */
public class ProductLoader {
    

    // public static Root fetchProducts(String url) {
    //     Root root = null;
    //     try {
    //         String serchUrl = url + "&keyword=セレクトスパイス&genreId=201268&elements=itemCode,itemName,itemCaption,itemPrice,imageUrl";

    //         HttpClient client = HttpClient.newHttpClient();
    //         HttpRequest request = HttpRequest.newBuilder()
    //             .uri(URI.create(url))
    //             .build();
    //         HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
    //         String jsonResponse = response.body();

    //         Gson gson = new Gson();
    //         root = gson.fromJson(jsonResponse, Root.class);
        
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }
    //     return root;
    // }

    /**
     * ベースURLをもとに検索するメソッド
     * @param baseUrl
     * @return {@code Rootクラス}
     */
    public static Root fetchProducts(String baseUrl) {
        Root root = null;
        try {

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl))
                .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String jsonResponse = response.body();

            Gson gson = new Gson();
            root = gson.fromJson(jsonResponse, Root.class);
        
        } catch (Exception e) {
            e.printStackTrace();    
        }
        return root;
    }

    //  public static void main(String[] args) {
    //      Root root = fetchProductsBySpiceName("クミン");
    //      if (root != null && root.getItems() != null) {
    //          root.getItems().forEach(item -> {
    //              System.out.println("Item Code: " + item.getItem().getItemCode());
    //              System.out.println("Item Name: " + item.getItem().getItemName());
    //              System.out.println("Item Caption: " + item.getItem().getItemCaption());
    //              System.out.println("Item Price: " + item.getItem().getItemPrice());
    //              System.out.println("Image URL: " + item.getItem().getMediumImageUrls().getLast().getImageUrl());
    //          System.out.println("-----");
    //         });
    //     } else {
    //      System.out.println("No products found or error in fetching products.");
    //     }
    // }
}
