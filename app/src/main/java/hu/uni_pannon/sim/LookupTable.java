package hu.uni_pannon.sim;

import java.util.TreeMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class LookupTable {
    
    private Map<String,Integer> inputIndexes;
    private Map<String,Integer> outputIndexes;
    
    private boolean inputTable[][];
    private boolean outputTable[][];


    private int rows;

    private int inN;
    private int outN;

    private LookupTable() {
    }

    public LookupTable(List<String> inputLabels, List<String> outputLabels) {
        this.inN = inputLabels.size();
        this.outN = outputLabels.size();
        inputIndexes = new TreeMap<String,Integer>();
        outputIndexes = new TreeMap<String,Integer>();

        int index = 0;
        for (String s : inputLabels) {
            inputIndexes.put(s,index++);
        }
        index = 0;
        for (String s : outputLabels) {
            outputIndexes.put(s, index++);
        }

        rows = (int)Math.pow(2,inN);
        inputTable = new boolean[rows][inN];
        outputTable = new boolean[rows][outN];

        // generate lookup table
        genTables();
    }

    private void setTable(boolean[][] inputTable, boolean[][] outputTable) {
        this.inputTable = inputTable;
        this.outputTable = outputTable;
        
        this.inN = inputTable[0].length;
        this.outN = outputTable[0].length;

        rows = (int)Math.pow(2,inN);
    }

    private void setIndexes(Map<String,Integer> inputIntexes, Map<String,Integer> outputIndexes) {
        this.inputIndexes = inputIntexes;
        this.outputIndexes = outputIndexes;
    }

    public boolean isInput(String key) {
        return inputIndexes.keySet().contains(key);
    }

    public boolean isOutput(String key) {
        return outputIndexes.keySet().contains(key);
    }

    public List<String> inputs() {
        return new LinkedList<String>(inputIndexes.keySet());
    }

    public List<String> outputs() {
        return new LinkedList<String>(outputIndexes.keySet());
    }

    private void genTables() {
        for (int i=0;i<rows;i++) {
            int acc = i;
            for (int j=0;j<this.inN;j++) {
                int diff = (int)Math.pow(2,this.inN - (j+1));
                if (acc >= diff) {
                    inputTable[i][j] = true;
                    acc -= diff;
                } else {
                    inputTable[i][j] = false;
                }
            } 
            for (int j=0;j<this.outN;j++) {
                    outputTable[i][j] = false;
            }
        }
    }

    public void print() {
        for (String s : inputIndexes.keySet()) {
            System.out.println("Input param: " + s + " -> " + inputIndexes.get(s));
        }
        for (String s : outputIndexes.keySet()) {
            System.out.println("Output param: " + s + " -> " + outputIndexes.get(s));
        }
        for (int i=0;i<rows;i++) {
            for (int j=0;j<inN;j++) {
                System.out.print(" " + (inputTable[i][j] ? 1 : 0));
            }
            System.out.print("\t");
            for (int j=0;j<outN;j++) {
                System.out.print(" " + (outputTable[i][j] ? 1 : 0));
            }
            System.out.println();
        }
    }

    public void addEntry(List<String> inputs, List<String> outputs) throws InvalidParamException {  
        if (outputs.size() == 0) {
            throw new InvalidParamException("No output parameter specified");
        }

        for (String output : outputs) {
            if (!outputIndexes.containsKey(output)) {
                throw new InvalidParamException(output);
            }
        }

        int idx = 0;
        idx = fetchRow(inputs);
        for (String o : outputs)
            outputTable[idx][outputIndexes.get(o)] = true;
    }

    public void nullEntry(List<String> inputs, List<String> outputs) throws InvalidParamException {
        if (outputs.size() == 0) {
            throw new InvalidParamException("No output parameter specified");
        }
        for (String output : outputs) {
            if (!outputIndexes.containsKey(output)) {
                throw new InvalidParamException(output);
            }
        }
        int idx = 0;
        idx = fetchRow(inputs);
        for (String o : outputs)
            outputTable[idx][outputIndexes.get(o)] = false;
    }

    public boolean evaluate(List<String> inputs, String output) throws InvalidParamException {
        if (output == null) {
            throw new InvalidParamException("No output parameter specified");
        }
        if (!outputIndexes.containsKey(output)) {
            throw new InvalidParamException(output);
        }
        int idx = fetchRow(inputs);
        return outputTable[idx][outputIndexes.get(output)];
    }

    private int fetchRow(List<String> inputs) throws InvalidParamException {
        for (String s : inputs) {
            if (!inputIndexes.containsKey(s)) {
                throw new InvalidParamException(s);
            }
        }

        if (inputs.size() == 0) {
            return 0;
        }

        for (int i=0;i<rows;i++) {
            int count = 0;
            for (int j=0;j<inputs.size();j++) {
                if (inputTable[i][inputIndexes.get(inputs.get(j))])
                    count++;
            }
            if (count == inputs.size()) {
                return i;
            }
        }
        return 0;
    }

    public void invert() {
        for (int i=0;i<rows;i++) {
            for (int j=0;j<outN;j++) {
                outputTable[i][j] = true;
            }
        }
    }

    public LookupTable clone() {
        LookupTable lut = new LookupTable();
        lut.setTable(inputTable.clone(),outputTable.clone());
        Map<String,Integer> in = new TreeMap<>();
        Map<String,Integer> out = new TreeMap<>();

        in.putAll(inputIndexes);
        out.putAll(outputIndexes);
        lut.setIndexes(in,out);
        return lut;
    }
}
