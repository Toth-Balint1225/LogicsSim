package hu.unipannon.sim.data;

import java.io.File;
import java.util.Optional;

import hu.unipannon.sim.gui.Controller;
import javafx.scene.control.TreeItem;

public class ComponentLoader {
    
    // this is the temporary stuff
    private final String COM_DIR = "C:/users/tothb/Documents/UNIV/Szakdolgozat/LogicsSimulator/comps";
    
    private ComponentLoader() {
    }
    
    private static ComponentLoader instance = null;
    
    public static ComponentLoader getInstance() {
        if (instance == null)
            instance = new ComponentLoader();
        return instance;
    }

    private String givePathFromUid(String uid) {
        String[] parts = uid.split(":");
        StringBuilder sbr = new StringBuilder(COM_DIR);
        for (String it : parts) {
            sbr.append("/");
            sbr.append(it);
        }
        sbr.append(".json");
        return sbr.toString();
    }

    public Optional<WorkspaceData> locateWorkspace(String uid) {
        return Serializer.readWorkspaceFromFile(givePathFromUid(uid));
    }

    public Optional<TypeData> locateComponent(String uid) {
        return Serializer.readTypeFromFile(givePathFromUid(uid));
    }

    public TreeItem<Controller.ComponentCellItem> componentTree() {
        File rootDir = new File(COM_DIR);
        return treeFromFile(rootDir,"",true);
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
