package com.clinexa.basediagnosis.implementations;

import com.clinexa.basediagnosis.DiagnosisEntity;
import com.clinexa.basediagnosis.ICDVersion;
import com.clinexa.basediagnosis.descriptors.DescriptionHandler;

import java.util.Objects;

public abstract class DiagnosisEntityImplementationICD11<T extends DescriptionHandler> implements DiagnosisEntity {

    String ICD11Code;
    T descriptionHandler;

    public DiagnosisEntityImplementationICD11(String ICD11Code, T descriptionHandler) {
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
    public T getDescriptionHandler() {
        return descriptionHandler;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof DiagnosisEntityImplementationICD11<?> that)) return false;
        return Objects.equals(getICD11Code(), that.getICD11Code());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getICD11Code());
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{" +
                "ICD11Code='" + ICD11Code + '\'' +
                '}';
    }
}
