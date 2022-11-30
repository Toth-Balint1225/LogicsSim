package hu.unipannon.sim.logic;

import java.util.TreeMap;

import hu.unipannon.sim.data.WorkspaceData;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * The lookup table is the core functionallity of a combinational network. This implementation
 * stores the inputs and outputs in separate matrices. The inputs represent all the possible 
 * combinations of the input variables.
 * @author Tóth Bálint 
 */
public class LookupTable {
    
    /**
     * Storage mapping for the labels.
     */
    private Map<String,Integer> inputIndexes;
    private Map<String,Integer> outputIndexes;
    
    private boolean inputTable[][];
    private boolean outputTable[][];


    private int rows;

    private int inN;
    private int outN;

    private LookupTable() {
    }

    /**
     * Constructs a lookup table from the inputs and outputs.
     * The inputs get generated with all combinations, and the
     * outputs are filled up with false values.
     * @param inputLabels the label set of the inputs
     * @param outputLabels label set of the outputs
     */
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

    /**
     * Used for cloning and copying
     * @param inputTable the table we want to copy
     * @param outputTable the table we want to copy
     */
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
        var res = new String[inputIndexes.entrySet().size()];
        for (var it  : inputIndexes.entrySet()) {
            res[it.getValue()] = it.getKey();
        }
        return Arrays.asList(res);
    }

    public List<String> outputs() {
        var res = new String[outputIndexes.entrySet().size()];
        for (var it  : outputIndexes.entrySet()) {
            res[it.getValue()] = it.getKey();
        }
        return Arrays.asList(res);
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
	Optional<Integer> row = fetchRow(inputs);
	if (!row.isPresent())
	    throw new InvalidParamException("Row not found");
        int idx = row.get();
        for (String o : outputs)
            outputTable[idx][outputIndexes.get(o)] = true;
    }

    /**
     * Used to add an entry to the lookup table. An entry is a list of pins (combination of inputs) that the
     * mapping should equate to a logical true.
     * @param inputs combination of inputs.
     * @param outputs outputs to be set to true
     * @throws InvalidParamException
     */
    public void nullEntry(List<String> inputs, List<String> outputs) throws InvalidParamException {
        if (outputs.size() == 0) {
            throw new InvalidParamException("No output parameter specified");
        }
        for (String output : outputs) {
            if (!outputIndexes.containsKey(output)) {
                throw new InvalidParamException(output);
            }
        }
	Optional<Integer> row = fetchRow(inputs);
	if (!row.isPresent())
	    throw new InvalidParamException("Row not found");
        int idx = row.get();
        for (String o : outputs)
            outputTable[idx][outputIndexes.get(o)] = false;
    }

    public Optional<Boolean> evaluate(List<String> inputs, String output) {
        if (output == null) {
            return Optional.empty();
        }
        if (!outputIndexes.containsKey(output)) {
            return Optional.empty();
        }
        Optional<Integer> row = fetchRow(inputs);
	if (!row.isPresent())
	    return Optional.empty();
	else {
	    int idx = row.get();
	    return Optional.of(outputTable[idx][outputIndexes.get(output)]);
	}
    }

    
    private Optional<Integer> fetchRow(List<String> inputs) {

        if (inputs.size() == 0) {
            return Optional.of(0);
        }

	// get the indices
	// then sum the 2 powers on the indices
	// -> that's the row we want

	int[] powers = new int[inputs.size()];
	int idx = 0;
	for (String s : inputs) {
            if (!inputIndexes.containsKey(s)) {
                return Optional.empty();
            }
	    powers[idx] = inputIndexes.get(s);
	    //System.out.println("" + powers[idx]);
	    idx++;
	}

	int row = Arrays.stream(powers)
	    .reduce(0,(x,y) -> x + (int)Math.pow(2,y));

	//System.out.println("row: " + row);
	return Optional.of(row);
	
	/*
	// search for the correct row
	// iterate over all rows
        for (int i=0;i<rows;i++) {
	    // match counter
            int count = 0;

	    // iterate over the row
            for (int j=0;j<inputs.size();j++) {

		// if the table's cell is true, then inc matches
                if (inputTable[i][inputIndexes.get(inputs.get(j))])
                    count++;
            }

	    // if we found all of them, then return
            if (count == inputs.size()) {
                return Optional.of(i);
            }
        }

        return Optional.of(0);
	*/
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

    public WorkspaceData.LUT toData() {
        WorkspaceData.LUT res = new WorkspaceData.LUT();
        // inputs and outputs
        res.inputs = inputIndexes.keySet().stream().toArray(String[]::new);
        res.outputs= outputIndexes.keySet().stream().toArray(String[]::new);
        List<WorkspaceData.LUTEntry> entryBuffer = new LinkedList<>();
        // lut entries
        for (int i=0;i<rows;i++) {
            WorkspaceData.LUTEntry entry = new WorkspaceData.LUTEntry();
            List<String> lhsBuffer = new LinkedList<>();
            List<String> rhsBuffer = new LinkedList<>();
            for (int j=0;j<inN;j++) {
                final int jPrime = j;
                if (inputTable[i][j]) {
                    lhsBuffer.add(inputIndexes.entrySet().stream()
                        .filter(e -> {
                            return e.getValue().equals(jPrime);
                        })
                        .map(e -> e.getKey())
                        .toArray(String[]::new)[0]);
                }
            }
            boolean toAdd = false;
            for (int j=0;j<outN;j++) {
                if (outputTable[i][j]) {
                    final int jPrime = j;
                    rhsBuffer.add(outputIndexes.entrySet().stream()
                        .filter(e -> {
                            return e.getValue().equals(jPrime);
                        })
                        .map(e -> e.getKey())
                        .toArray(String[]::new)[0]);
                    toAdd = true;
                }
            }
            if (toAdd) {
                entryBuffer.add(entry);
                entry.lhs = lhsBuffer.stream().toArray(String[]::new);
                entry.rhs = rhsBuffer.stream().toArray(String[]::new);
            }
        }
        res.entries = entryBuffer.stream().toArray(WorkspaceData.LUTEntry[]::new);
        return res;
    }

    public boolean[][] inputTable() {
        return inputTable;
    }

    public boolean[][] outputTable() {
        return outputTable;
    }

    public int rows() {
        return rows;
    }
}
