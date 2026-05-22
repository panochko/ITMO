package language;

import java.util.ListResourceBundle;
import java.util.Locale;

public class LanguageBundle extends ListResourceBundle {

    public static final Locale RUSSIAN = new Locale.Builder()
            .setLanguage("ru")
            .setRegion("RU")
            .build();
    public static final Locale ICELANDIC = new Locale.Builder()
            .setLanguage("is")
            .setRegion("IS")
            .build();
    public static final Locale FRENCH = new Locale.Builder()
            .setLanguage("fr")
            .setRegion("FR")
            .build();
    public static final Locale SPANISH_SALVADOR = new Locale.Builder()
            .setLanguage("es")
            .setRegion("SV")
            .build();
    @Override
    protected Object[][] getContents() {
        return CONTENTS;
    }
    private static final Object[][] CONTENTS = {
            {"app.title", "Управление работниками"},
            {"login.title", "Авторизация"},
            {"login.username", "ISU пользователя:"},
            {"login.password", "Пароль:"},
            {"login.button", "Войти"},
            {"login.error", "Ошибка авторизации"},
            {"user.current", "Текущий пользователь:"},
            {"table.title", "Список работников"},
            {"table.id", "ID"},
            {"table.name", "Имя"},
            {"table.coordinates", "Координаты"},
            {"table.salary", "Зарплата"},
            {"table.position", "Должность"},
            {"table.status", "Статус"},
            {"table.creationDate", "Дата создания"},
            {"table.owner", "Владелец"},
            {"visualization.title", "Визуализация"},
            {"filter.placeholder", "Фильтр..."},
            {"button.add", "Добавить"},
            {"button.update", "Обновить"},
            {"button.delete", "Удалить"},
            {"button.clear", "Очистить"},
            {"button.refresh", "Обновить"},
            {"button.logout", "Выйти"},
            {"command.add", "Добавить работника"},
            {"command.update", "Обновить работника"},
            {"command.delete", "Удалить работника"},
            {"animation.add", "Добавление"},
            {"animation.remove", "Удаление"},
            {"animation.update", "Обновление"}
    };
}