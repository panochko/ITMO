package language;

import java.util.Locale;
import java.util.ResourceBundle;

public class LocalizationManager {

    public static final Locale RUSSIAN = new Locale.Builder()
            .setLanguage("ru")
            .setRegion("RU")
            .build();
    public static final Locale ICELANDIC = new Locale.Builder()
            .setLanguage("is")
            .setRegion("IS")
            .build();
    public static final Locale FRENCH = new Locale.Builder().
            setLanguage("fr")
            .setRegion("FR")
            .build();
    public static final Locale SPANISH_SALVADOR = new Locale.Builder()
            .setLanguage("es")
            .setRegion("SV")
            .build();
    private static ResourceBundle resources = loadBundle(RUSSIAN);
    private LocalizationManager() {}
    public static ResourceBundle getResources() {
        return resources;
    }
    public static void setLocaleByIndex(int index) {
        Locale currentLocale = switch (index) {
            case 1 -> ICELANDIC;
            case 2 -> FRENCH;
            case 3 -> SPANISH_SALVADOR;
            default -> RUSSIAN;
        };
        resources = loadBundle(currentLocale);
    }
    public static String getString(String key) {
        try {
            return resources.getString(key);
        } catch (Exception e) {
            return "!" + key + "!";
        }
    }
    private static ResourceBundle loadBundle(Locale locale) {
        try {
            return ResourceBundle.getBundle("LanguageBundle", locale);
        } catch (Exception e) {
            return ResourceBundle.getBundle("LanguageBundle", RUSSIAN);
        }
    }
}