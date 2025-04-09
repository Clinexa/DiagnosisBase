package com.clinexa.basediagnosis.implementations;

import com.clinexa.basediagnosis.DiagnosesSystem;
import com.clinexa.basediagnosis.DiagnosisEntity;
import com.clinexa.basediagnosis.ICDVersion;
import com.clinexa.basediagnosis.Titled;
import com.clinexa.basediagnosis.services.ICDCodeConverter;
import com.clinexa.basediagnosis.utils.ICDLanguage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.ProviderNotFoundException;
import java.util.Objects;
import java.util.ServiceLoader;

/**
 * Default implementation of {@link DiagnosisEntity} interface which
 * implements common characterizations of diagnoses and symptoms.
 * <br>
 *
 * Uses ICD 11 as a base, so every object is defined by its ICD 11 code.
 *
 * @since 1.0-dev.1
 * @author Nikita S.
 */
public abstract class DiagnosisEntityImplementationICD11 extends TitledImplementation implements DiagnosisEntity, Serializable {

    DiagnosesSystem system;
    String ICD11Code;

    /**
     * Creates new entity of DiagnosisEntityImplementationICD11. Should only be used in
     * constructors of child classes.
     *
     * @param system system that was used to generate entity. Will be used for translations.
     * @param language language of the given title.
     * @param ICD11Code ICD 11 code of the entity.
     * @param title title of the entity in the given language.
     * @since 0.1-dev.2
     */
    public DiagnosisEntityImplementationICD11(DiagnosesSystem system, ICDLanguage language, String ICD11Code, String title) {
        super(title, language, (var lang) -> ((Titled) system.getByICD11Code(ICD11Code, lang)).getTitle(lang));
        this.system = system;
        this.ICD11Code = ICD11Code;
    }

    /**
     * Default constructor for serialization. DO NOT USE IT!
     *
     * @deprecated Do not use besides serialization!
     * @since 0.1-dev.2
     */
    public DiagnosisEntityImplementationICD11() {
        super();
    }

    /**
     * Old (from 0.1-dev.1) version of the constructor that uses ENGLISH as
     * default language and default diagnoses system (see {@link DiagnosesSystem#getDefaultDiagnosesSystem()}).
     *
     * @param ICD11Code ICD 11 code of the entity.
     * @param title title of the entity in English.
     * @deprecated use four-variable constructor instead to specify used language.
     * @see #DiagnosisEntityImplementationICD11(DiagnosesSystem, ICDLanguage, String, String)
     */
    @Deprecated(since = "0.1-dev.2", forRemoval = true)
    public DiagnosisEntityImplementationICD11(String ICD11Code, String title) {
        this(DiagnosesSystem.getDefaultDiagnosesSystem(), ICDLanguage.ENGLISH, ICD11Code, title);
    }

    /**
     * Get ICD 11 code stored within entity.
     *
     * @return ICD 11 code of the entity.
     */
    @Override
    public @NotNull String getICD11Code() {
        return ICD11Code;
    }

    /**
     * Get ICD code in given version.
     * <br>
     *
     * If ICD version is different from 11, then {@link ServiceLoader} is used to search
     * for converter. If no converter is found, exception is raised.
     *
     * @param version ICD version for which you need the code. May not be supported.
     * @return code in the given version of the ICD.
     * @throws ProviderNotFoundException if no suitable converter for ICD code is found.
     * @see ICDCodeConverter
     */
    @Override
    public @NotNull String getICDCode(@NotNull ICDVersion version) {
        if (version == ICDVersion.ICD11) {
            return ICD11Code;
        } else {
            ServiceLoader<ICDCodeConverter> loader = ServiceLoader.load(ICDCodeConverter.class);
            for (ICDCodeConverter converter : loader) {
                if (converter.getFromVersion() == ICDVersion.ICD11 && converter.getToVersion() == version) {
                    return converter.convert(ICD11Code);
                }
            }
        }
        throw new ProviderNotFoundException("Unsupported ICD version: " + version);
    }

    /**
     * Compared different diagnosis entities by ICD 11 code.
     *
     * @param o object to compare to.
     * @return true if entities share the same ICD 11 code, false otherwise.
     */
    @Override
    public boolean equals(@Nullable Object o) {
        if (!(o instanceof DiagnosisEntityImplementationICD11 that)) return false;
        return Objects.equals(getICD11Code(), that.getICD11Code());
    }

    /**
     * Calculates hashCode based on ICD 11 code alone.
     * @return hash code for the entity.
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(getICD11Code());
    }

    /**
     * Returns a String describing class (with name) and basic information
     * about the object which allows to identify it.
     *
     * @return String description of the entity.
     */
    @Override
    public @NotNull String toString() {
        return this.getClass().getSimpleName() + "{" +
                "ICD11Code='" + ICD11Code + '\'' +
                '}';
    }

    /**
     * Used to store information about entity using serialization.
     * For simplicity, only ICD 11 code is saved.
     *
     * @see #readObject(ObjectInputStream)
     */
    @Serial
    private void writeObject(@NotNull ObjectOutputStream out) throws IOException {
        out.writeObject(ICD11Code);
    }

    /**
     * Used to get information about entity using serialization.
     * ICD 11 code is read and then used to get other data from
     * ICD 11 API.
     *
     * @see #writeObject(ObjectOutputStream)
     */
    @Serial
    private void readObject(@NotNull ObjectInputStream in) throws IOException, ClassNotFoundException, NoSuchMethodException,
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
