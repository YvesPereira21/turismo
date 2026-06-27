package io.turismo.backend.model.enums;

public enum StateName {
    PARAIBA("PB"),
    PERNAMBUCO("PE"),
    SERGIPE("SE"),
    RIOGRANDEDOSUL("RS"),
    RIODEJANEIRO("RJ"),
    SAOPAULO("SP");

    private final String name;

    StateName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
