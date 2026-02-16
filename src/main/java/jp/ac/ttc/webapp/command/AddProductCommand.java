package jp.ac.ttc.webapp.command;

import java.util.ArrayList;
import java.util.List;

import jp.ac.ttc.webapp.adminservice.DataFormatter;
import jp.ac.ttc.webapp.adminservice.ItemWrapper;
import jp.ac.ttc.webapp.adminservice.ProductLoader;
import jp.ac.ttc.webapp.adminservice.Root;
import jp.ac.ttc.webapp.bean.ProductBean;
import jp.ac.ttc.webapp.connector.MySqlConnectionManager;
import jp.ac.ttc.webapp.context.RequestContext;
import jp.ac.ttc.webapp.context.ResponseContext;
import jp.ac.ttc.webapp.dao.AdminProductDao;

/**
 * 商品追加機能を実現するexecuteを持つCommandクラス
 * @author suzuki-takumi23
 * @since 2026-01-16
 */
public class AddProductCommand extends AbstractCommand {
    //楽天商品APIのURLのちにプロパティファイル化予定
    private static final String RAKUTENAPIURL = "https://app.rakuten.co.jp/services/api/IchibaItem/Search/20220601?applicationId=1079487733172988420&genreId=201268&elements=itemCode,itemName,itemPrice,mediumImageUrls,smallImageUrls,itemCaption";

    @Override
    public ResponseContext execute(ResponseContext resc) {
        RequestContext reqc = getRequestContext();
        
        //必要なパラメータをカンマ区切りで取得
        String[] spiceNameAndIds = reqc.getParameter("spiceNameAndId");
        String spiceNameAndId = spiceNameAndIds[0];
        //それぞれ変数に格納
        String spiceName = spiceNameAndId.split(",")[1];
        String spiceId = spiceNameAndId.split(",")[0];

        //容量を持つ予定の変数。ここで宣言して後でぶち込みます。
        int capacity = 0;
       
        //楽天商品で検索するためのベースURL。まずはキーワードのパラメータのみ設定する
        String baseUrl = RAKUTENAPIURL+"&keyword="+spiceName;
        //楽天APIの機能を持つクラスでbaseUrlをベースに検索
        //検索結果のJsonをJavaオブジェクトにパース。
        Root root = ProductLoader.fetchProducts(baseUrl);
        
        //新規商品を表示させるための結果セット用
        List<ProductBean> productList = new ArrayList<>();

        ///トランザクション開始・今回使用するDaoクラスをインスタンス化
        MySqlConnectionManager.getInstance().beginTransaction();
        AdminProductDao dao = new AdminProductDao();

        //検索結果が格納されたリストから一件分に分解
        for (ItemWrapper itemwWrapper : root.getItems()) {
            //データ成型用クラスで商品説明から原産国名を取得。なければ"表記なし"という固有テキストに
            String originCountry = DataFormatter.getOriginCountry(itemwWrapper.getItem().getItemCaption());

            //apiItemIdをもとに商品がすでに登録されているか確認。
            //登録されていれば何もしない
            if(dao.hasProductAsApi(itemwWrapper.getItem().getItemCode())) {
               System.out.println("この商品は存在します:" + itemwWrapper.getItem().getItemCode()); 
            } else {
                //商品説明から内容量を取得。なければ0のまま
                capacity = DataFormatter.getCapacityFromCaption(itemwWrapper.getItem().getItemCaption());
               
                //内容量を取得できなかった場合はDB登録をあきらめる。
                if(capacity == 0) {
                    System.out.println("容量不明のため登録しません:" + itemwWrapper.getItem().getItemCode());
                } else {
                    System.out.println("新規登録します:" + itemwWrapper.getItem().getItemCode());

                    //商品一件を表すBean
                    ProductBean product = new ProductBean();

                    //変換後のJavaオブジェクトに入っているデータをBeanに詰め替える。
                    product.setApiItemId(itemwWrapper.getItem().getItemCode());
                    product.setProductName(itemwWrapper.getItem().getItemName());
                    product.setPrice(itemwWrapper.getItem().getItemPrice());
                    product.setCapacity(capacity);
                    product.setOriginCountry(originCountry);
                    product.setImageUrl(itemwWrapper.getItem().getMediumImageUrls().get(0).getImageUrl());
                    product.setProductDescription(itemwWrapper.getItem().getItemCaption());
                    product.setSpiceId(Integer.parseInt(spiceId));
                    product.setSalesVolume(0);

                    //まずはproducts表に追加
                    int productId = dao.addProduct(product);
                    //次にstatusを初期化
                    dao.initProductStatus(productId);
                    //結果セットに格納
                    productList.add(product);
                }                
            }
            
        }

        //すべてが終わってからコミット・コネクション閉じる
        MySqlConnectionManager.getInstance().commit();
        MySqlConnectionManager.getInstance().closeConnection();

        if(productList.size() == 0) {
            reqc.setSessionAttribute("noAddedContents", "新規商品が登録されませんでした。");
        }

        //ここもJ2EEに依存
        reqc.setSessionAttribute("productList", productList);
        resc.setRedirect(true);
        resc.setTarget("/spiceEC/admin/selectSpices");
        return resc;
    }
}
