package hu.uni_pannon.sim.data;

import java.util.Optional;

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
}
