package hu.unipannon.sim;

import java.util.Map;
import java.util.TreeMap;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

/*
    dark theme:
    gui background: [37,37,38]
    menu background: [60,60,60]
    toolbar background: [51,51,51]
    tree view background = gui background
    selection color: [56,56,61]
 */

public class Settings {

    // theming information for the workspace
    public static class ThemeDescriptor {
        public String foreground;
        public String background;
        public String active;
        public String name;
    };

    public static class Theme {
        public Paint foreground;
        public Paint background;
        public Paint active;

        public Theme(Paint foreground, Paint background, Paint active) {
            this.foreground = foreground;
            this.background = background;
            this.active = active;
        }
    }

    public static class SettingsData {
        public String theme;

        public String[] folders;
        public ThemeDescriptor[] themes;
    }

    private static Settings instance = null;
    
    private Settings() {
        this.data = new SettingsData();
        this.data.theme = "default";
        this.data.folders = new String[] { "comps" };

        this.themes = new TreeMap<>();
        this.themes.put("default",new Theme(Color.BLACK, Color.WHITE, Color.BLUE));

    }

    public static Settings getInstance() {
        if (instance == null) 
            instance = new Settings();
        return instance;
    }


    private SettingsData data;
    private Map<String, Theme> themes;
    public void setData(SettingsData data) {
        this.data = data;
        for (var t : data.themes) {
            this.themes.put(t.name,new Theme(Color.web(t.foreground), Color.web(t.background), Color.web(t.active)));
        }
    }

    public SettingsData getData() {
        return data;
    }

    public Theme getTheme() {
        if (themes.containsKey(data.theme)) {
            return themes.get(data.theme);
        } else {
            return themes.get("default");
        }
    }
}
