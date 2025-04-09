/*
 * This file is part of Clinexa DiagnosisBase.
 *
 * Clinexa DiagnosisBase is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * Clinexa DiagnosisBase is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License and GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License and GNU General Public
 * License along with Clinexa DiagnosisBase. If not, see <https://www.gnu.org/licenses/>.
 */

package com.clinexa.basediagnosis;

import com.clinexa.basediagnosis.implementations.DiagnosisEntityImplementationICD11;
import com.clinexa.basediagnosis.services.SymptomSupplier;
import com.clinexa.basediagnosis.utils.ICDLanguage;
import org.jetbrains.annotations.NotNull;

import java.nio.file.ProviderNotFoundException;
import java.util.List;
import java.util.ServiceLoader;

/**
 * Stores diagnosis information.
 * <br>
 *
 * Most of the behavior is defined by {@link DiagnosisEntityImplementationICD11}.
 *
 * @since 0.1-dev.1
 * @author Nikita S.
 */
@SuppressWarnings("removal")
public class Diagnosis extends DiagnosisEntityImplementationICD11 {
    /**
     * Creates new object of Diagnosis.
     *
     * @param system system that was used to generate entity. Will be used for translations.
     * @param language language of the given title.
     * @param ICD11Code ICD 11 code of the entity.
     * @param title title of the entity in the given language.
     * @since 0.1-dev.2
     */
    public Diagnosis(@NotNull DiagnosesSystem system, @NotNull ICDLanguage language, @NotNull String ICD11Code, @NotNull String title) {
        super(system, language, ICD11Code, title);
    }

    /**
     * Default constructor for serialization. DO NOT USE IT!
     *
     * @deprecated Do not use besides serialization!
     * @since 0.1-dev.2
     */
    public Diagnosis() {
        super();
    }

    /**
     * Old (from 0.1-dev.1) version of the constructor that uses ENGLISH as
     * default language and default diagnoses system (see {@link DiagnosesSystem#getDefaultDiagnosesSystem()}).
     *
     * @param ICD11Code ICD 11 code of the entity.
     * @param title title of the entity in English.
     * @deprecated use four-variable constructor instead to specify used language.
     * @see #Diagnosis(DiagnosesSystem, ICDLanguage, String, String)
     */
    @Deprecated(since = "0.1-dev.2", forRemoval = true)
    public Diagnosis(@NotNull String ICD11Code, @NotNull String title) {
        super(ICD11Code, title);
    }

    /**
     * Searches for service provider that gives symptoms by
     * diagnosis and returns its data.
     * <br>
     *
     * If several providers give symptoms for this diagnosis, the
     * behavior is undefined.
     *
     * @return list of symptoms of this diagnosis.
     * @throws ProviderNotFoundException if no provider gives symptoms for this diagnosis.
     * @see SymptomSupplier
     */
    @SuppressWarnings("unused")
    public @NotNull List<Symptom> getSymptoms() {
        ServiceLoader<SymptomSupplier> loader = ServiceLoader.load(SymptomSupplier.class);
        for (SymptomSupplier symptomSupplier : loader) {
            if (symptomSupplier.canProcess(this))
                return symptomSupplier.process(this);
        }
        throw new ProviderNotFoundException("No symptom provider for diagnosis: " + this);
    }
}
