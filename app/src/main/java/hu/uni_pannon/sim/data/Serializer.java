package hu.uni_pannon.sim.data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Optional;

public class Serializer {
    public static Optional<WorkspaceData> readFromFile(String filename) {
        File f = new File(filename);
        BufferedReader reader = null;
        StringBuilder stb = new StringBuilder();
        String line = null;
        try {
            reader = new BufferedReader(new FileReader(f));
            while ((line = reader.readLine()) != null) 
                stb.append(line);
        } catch (IOException e) {
            e.printStackTrace();
            return Optional.empty();
        } finally {
            try {
                reader.close();
            } catch (Exception e) {
                System.err.println("UNREACHABLE");
                e.printStackTrace();
            }
        }
        return Optional.of(JsonParser.getInstance().toClass(stb.toString(), WorkspaceData.class));
    }

    public static void writeToFile(WorkspaceData data, String filename) {
        File f = new File(filename);
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(f));
            writer.write(JsonParser.getInstance().toJson(data));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
            writer.close();    
            } catch (Exception e) {
                System.err.println("UNREACHABLE");
                e.printStackTrace();
            }
        }
    }
}
