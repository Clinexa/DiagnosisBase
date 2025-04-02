package com.clinexa.basediagnosis.implementations;

import com.clinexa.basediagnosis.DiagnosisEntity;
import com.clinexa.basediagnosis.ICDVersion;
import com.clinexa.basediagnosis.descriptors.DiagnosisDescriptionHandler;

public abstract class DiagnosisEntityImplementationICD11 implements DiagnosisEntity {

    String ICD11Code;
    DiagnosisDescriptionHandler descriptionHandler;

    public DiagnosisEntityImplementationICD11(String ICD11Code, DiagnosisDescriptionHandler descriptionHandler) {
        this.ICD11Code = ICD11Code;
        this.descriptionHandler = descriptionHandler;
    }

    public String getICD11Code() {
        return ICD11Code;
    }

    @Override
    public String getICDCode(ICDVersion version) {
        if (version == ICDVersion.ICD11)
            return ICD11Code;
        else
            throw new UnsupportedOperationException("Unsupported ICD version: " + version.toString());
    }

    @Override
    public DiagnosisDescriptionHandler getDescriptionHandler() {
        return descriptionHandler;
    }
}
