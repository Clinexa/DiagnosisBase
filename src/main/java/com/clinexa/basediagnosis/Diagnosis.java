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

import java.nio.file.ProviderNotFoundException;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

public class Diagnosis extends DiagnosisEntityImplementationICD11 {
    public Diagnosis(DiagnosesSystem system, ICDLanguage language, String ICD11Code, String title) {
        super(system, language, ICD11Code, title);
    }

    public Diagnosis() {
        super();
    }

    @Deprecated(since = "0.1-dev.2", forRemoval = true)
    public Diagnosis(String ICD11Code, String title) {
        super(ICD11Code, title);
    }

    public List<Symptom> getSymptoms() {
        ServiceLoader<SymptomSupplier> loader = ServiceLoader.load(SymptomSupplier.class);
        for (SymptomSupplier symptomSupplier : loader) {
            if (symptomSupplier.canProcess(this))
                return symptomSupplier.process(this);
        }
        throw new ProviderNotFoundException("No symptom provider for diagnosis: " + this);
    }
}
