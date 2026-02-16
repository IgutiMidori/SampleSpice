package jp.ac.ttc.webapp.adminservice;

/**
 * 翻訳APIのJsonパース用クラス
 * 単一の翻訳結果を格納するオブジェクト
 * @author suzuki-takumi23
 * @since 2026-01-28
 */
public class Translation {
    private String detected_source_language;
    private String text;

    // Getter & Setter
    public String getDetected_source_language() {
        return detected_source_language;
    }

    public void setDetected_source_language(String detected_source_language) {
        this.detected_source_language = detected_source_language;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
