package hu.uni_pannon.sim.data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Optional;

public class Serializer {
    private static Optional<String> readSource(String filename) {
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
        return Optional.of(stb.toString());
    }

    private static void writeSource(String src, String filename) {
        File f = new File(filename);
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(f));
            writer.write(src);
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

    public static Optional<WorkspaceData> readWorkspaceFromFile(String filename) {
        return readSource(filename).flatMap(src -> {
            return Optional.of(JsonParser.getInstance().toClass(src, WorkspaceData.class));
        });
    }

    public static void writeWorkspaceToFile(WorkspaceData data, String filename) {
        writeSource(JsonParser.getInstance().toJson(data), filename);
    }

    public static Optional<TypeData> readTypeFromFile(String filename) {
        return readSource(filename).flatMap(src -> {
            return Optional.of(JsonParser.getInstance().toClass(src, TypeData.class));
        });
    }

    public static void writeTypeToFile(WorkspaceData data, String filename) {
        writeSource(JsonParser.getInstance().toJson(data), filename);
    }
}
