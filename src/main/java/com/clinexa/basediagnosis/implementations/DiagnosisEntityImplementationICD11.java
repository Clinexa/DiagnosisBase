package com.clinexa.basediagnosis.implementations;

import com.clinexa.basediagnosis.DiagnosesSystem;
import com.clinexa.basediagnosis.DiagnosisEntity;
import com.clinexa.basediagnosis.ICDVersion;
import com.clinexa.basediagnosis.Titled;
import com.clinexa.basediagnosis.exceptions.DiagnosesSystemException;
import com.clinexa.basediagnosis.utils.ICDLanguage;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public abstract class DiagnosisEntityImplementationICD11 extends TitledImplementation implements DiagnosisEntity, Serializable {

    DiagnosesSystem system;
    String ICD11Code;

    public DiagnosisEntityImplementationICD11(DiagnosesSystem system, ICDLanguage language, String ICD11Code, String title) {
        super(title, language, (var lang) -> ((Titled) system.getByICD11Code(ICD11Code, lang)).getTitle(lang));
        this.system = system;
        this.ICD11Code = ICD11Code;
    }

    public DiagnosisEntityImplementationICD11() {
        super();
    }

    @Deprecated(since = "0.1-dev.2", forRemoval = true)
    public DiagnosisEntityImplementationICD11(String ICD11Code, String title) {
        this(DiagnosesSystem.getDefaultDiagnosesSystem(), ICDLanguage.ENGLISH, ICD11Code, title);
    }

    @Override
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
    public boolean equals(Object o) {
        if (!(o instanceof DiagnosisEntityImplementationICD11 that)) return false;
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

    @Serial
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeObject(ICD11Code);
    }

    @Serial
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException, NoSuchMethodException,
            InvocationTargetException, InstantiationException, IllegalAccessException {
        ICD11Code = (String) in.readObject();

        DiagnosesSystem defaultSystem = DiagnosesSystem.getDefaultDiagnosesSystem();
        var object = (DiagnosisEntityImplementationICD11) defaultSystem.getByICD11Code(ICD11Code);

        system = defaultSystem;
        language = object.language;
        title = object.title;
        anotherLanguageGetter = object.anotherLanguageGetter;
    }
}
