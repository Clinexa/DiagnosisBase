package com.clinexa.basediagnosis;

public class DiagnosisCategory {

    private String name;
    private String description;
    private String systemCode;

    public DiagnosisCategory(String name, String description, String systemCode) {
        this.name = name;
        this.description = description;
        this.systemCode = systemCode;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getSystemCode() {
        return systemCode;
    }
}
