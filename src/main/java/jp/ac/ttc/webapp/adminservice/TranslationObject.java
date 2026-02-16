package jp.ac.ttc.webapp.adminservice;

import java.util.List;

/**
 * 複数の翻訳結果が配列で格納されるクラス
 * @author suzuki-takumi23
 * @since 2026-01-28
 */
public class TranslationObject {
    private List<Translation> translations;

    // Getter & Setter
    public List<Translation> getTranslations() {
        return translations;
    }

    public void setTranslations(List<Translation> translations) {
        this.translations = translations;
    }
}
