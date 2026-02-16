// listまだ

package jp.ac.ttc.webapp.bean;

public class SpiceBean implements java.io.Serializable {
    private int spiceId;
    private String spiceNameJp;
    private String spiceNameEn;
    private String imageUrl;
    private String originCountry;
    private String priceRange;
    private String effect;
    private String exampleDishes;
    private String overview;

    public SpiceBean() {}

    public int getSpiceId() {
        return spiceId;
    }

    public void setSpiceId(int spiceId) {
        this.spiceId = spiceId;
    }

    public String getSpiceNameJp() {
        return spiceNameJp;
    }

    public void setSpiceNameJp(String spiceNameJp) {
        this.spiceNameJp = spiceNameJp;
    }

    public String getSpiceNameEn() {
        return spiceNameEn;
    }

    public void setSpiceNameEn(String spiceNameEn) {
        this.spiceNameEn = spiceNameEn;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getOriginCountry() {
        return originCountry;
    }

    public void setOriginCountry(String originCountry) {
        this.originCountry = originCountry;
    }

    public String getPriceRange() {
        return priceRange;
    }

    public void setPriceRange(String priceRange) {
        this.priceRange = priceRange;
    }

    public String getEffect() {
        return effect;
    }

    public void setEffect(String effect) {
        this.effect = effect;
    }

    public String getExampleDishes() {
        return exampleDishes;
    }

    public void setExampleDishes(String exampleDishes) {
        this.exampleDishes = exampleDishes;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }
}