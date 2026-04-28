package br.com.getronics.utils.enums;

public enum E_Project {
    PROJECT_NAME("BatchProcessor");
    private final String value;

    E_Project(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
