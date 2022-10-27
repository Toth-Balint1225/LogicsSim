package hu.unipannon.sim.data;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import hu.unipannon.sim.Settings;
import hu.unipannon.sim.gui.Controller;
import javafx.scene.control.TreeItem;

public class ComponentLoader {
    
    // this is the temporary stuff
    private List<String> comDirs;
    
    private ComponentLoader() {
        comDirs = Arrays.asList(Settings.getInstance().getData().folders);
    }
   
    private static ComponentLoader instance = null;
    
    public static ComponentLoader getInstance() {
        if (instance == null)
            instance = new ComponentLoader();
        return instance;
    }

    private String givePathFromUid(String dir,String uid) {
        String[] parts = uid.split(":");
        StringBuilder sbr = new StringBuilder(dir);
        for (String it : parts) {
            sbr.append("/");
            sbr.append(it);
        }
        sbr.append(".json");
        return sbr.toString();
    }

    public Optional<WorkspaceData> locateWorkspace(String uid) {
        for (var dir : comDirs) {
            var loaded = Serializer.readWorkspaceFromFile(givePathFromUid(dir,uid));
            if (loaded.isPresent()) {
                if (loaded.get().type.equals("WORKSPACE"))
                    return loaded;
            }
        }
        return Optional.empty();
    }

    public Optional<TypeData> locateComponent(String uid) {
        for (var dir : comDirs) {
            var loaded = Serializer.readTypeFromFile(givePathFromUid(dir,uid));
            if (loaded.isPresent()) {
                if (loaded.get().type.equals("TYPE"))
                    return loaded;
            }
        }
        return Optional.empty();    }

    public TreeItem<Controller.ComponentCellItem> componentTree() {
        TreeItem<Controller.ComponentCellItem> res = new TreeItem<>();
        for (var dir : comDirs) {
            File rootDir = new File(dir);
            
            res.getChildren().add(treeFromFile(rootDir,"",true));
        }
        return res;
    }

    private TreeItem<Controller.ComponentCellItem> treeFromFile(File f, String base, boolean root) {
        if (f.isDirectory()) {
            TreeItem<Controller.ComponentCellItem> res = new TreeItem<>(
                new Controller.ComponentCellItem(f.getName(),""));
            for (File it : f.listFiles()) {
                res.getChildren().add(treeFromFile(it,(root ? "" : base + f.getName() + ":"), false));
            }
            return res;
        } else {
            String name = f.getName().split("\\.")[0];
            return new TreeItem<Controller.ComponentCellItem>(
                new Controller.ComponentCellItem(name,base + name));
        }
    }

}
