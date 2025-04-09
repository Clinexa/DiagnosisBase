package com.clinexa.basediagnosis.services;

import com.clinexa.basediagnosis.Diagnosis;
import com.clinexa.basediagnosis.Symptom;

import java.util.List;

public interface SymptomSupplier {

    boolean canProcess(Diagnosis diagnosis);
    List<Symptom> process(Diagnosis diagnosis);

}
