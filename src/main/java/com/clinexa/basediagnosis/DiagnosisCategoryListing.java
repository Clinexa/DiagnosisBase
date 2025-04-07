package com.clinexa.basediagnosis;

import java.util.List;

public class DiagnosisCategoryListing {

    public enum ListingValue {
        CATEGORIES,
        DIAGNOSES,
        SYMPTOMS
    }

    private ListingValue type;
    private List<DiagnosisCategoryListing> categories;
    private List<Diagnosis> diagnoses;
    private List<Symptom> symptoms;

    private DiagnosisCategoryListing(ListingValue type, List<DiagnosisCategoryListing> categories, List<Diagnosis> diagnoses, List<Symptom> symptoms) {
        this.type = type;
        this.categories = categories;
        this.diagnoses = diagnoses;
        this.symptoms = symptoms;
    }

    public static DiagnosisCategoryListing makeCategoriesList(List<DiagnosisCategoryListing> categories) {
        return new DiagnosisCategoryListing(ListingValue.CATEGORIES, categories, null, null);
    }

    public static DiagnosisCategoryListing makeDiagnosesList(List<Diagnosis> diagnoses) {
        return new DiagnosisCategoryListing(ListingValue.DIAGNOSES, null, diagnoses, null);
    }

    public static DiagnosisCategoryListing makeSymptomsList(List<Symptom> symptoms) {
        return new DiagnosisCategoryListing(ListingValue.SYMPTOMS, null, null, symptoms);
    }

    public ListingValue getType() {
        return type;
    }

    public List<DiagnosisCategoryListing> getCategories() {
        return categories;
    }

    public List<Diagnosis> getDiagnoses() {
        return diagnoses;
    }

    public List<Symptom> getSymptoms() {
        return symptoms;
    }
}
