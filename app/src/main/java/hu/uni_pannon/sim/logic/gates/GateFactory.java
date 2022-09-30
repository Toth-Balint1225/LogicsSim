package hu.uni_pannon.sim.logic.gates;

import java.util.Optional;

import hu.uni_pannon.sim.logic.Component;

public class GateFactory {
    public static Optional<Component> fromString(String id, int ins) {
        switch (id) {
            case "AND":
                return Optional.of(new AndGate(ins));
            case "OR":
                return Optional.of(new OrGate(ins));
            case "NOT":
                return Optional.of(new NotGate());
            case "XOR":
                return Optional.of(new XorGate(ins));
            case "NAND":
                return Optional.of(new NandGate(ins));
            case "NOR":
                return Optional.of(new NorGate(ins));
            case "XNOR":
                return Optional.of(new XnorGate(ins));
            case "BUFFER":
                return Optional.of(new BufferGate());
            default:
                return Optional.empty();
        }
    }
}
