package hu.uni_pannon.sim.logic;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class LookupTableTests {

    @Test
    public void rowCalculate() {
	List<String> ins  = Arrays.asList("a","b","c");
	List<String> outs = Arrays.asList("r");
	LookupTable lut = new LookupTable(ins,outs);
	try {
	    lut.addEntry(ins,outs);
	} catch(Exception e) {
	    e.printStackTrace();
	}

	lut.print();

	boolean res = lut.evaluate(Arrays.asList("a","b","c"),"r")
	    .orElse(false);
	
	assertEquals(true,res);
    }

}
