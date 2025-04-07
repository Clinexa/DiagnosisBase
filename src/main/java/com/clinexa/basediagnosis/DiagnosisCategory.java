package com.clinexa.basediagnosis;

public class DiagnosisCategory {

    public enum Type {
        DIAGNOSIS,
        SYMPTOM,
        SPECIAL_ENTITY
    }

    private String name;
    private String description;
    private String systemCode;
    private Type type;


    public DiagnosisCategory(String name, String description, String systemCode, Type type) {
        this.name = name;
        this.description = description;
        this.systemCode = systemCode;
        this.type = type;
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

    public Type getType() {
        return type;
    }
}
