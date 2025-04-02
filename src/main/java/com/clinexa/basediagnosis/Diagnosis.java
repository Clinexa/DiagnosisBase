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

import com.clinexa.basediagnosis.descriptors.DiagnosisDescriptionHandler;
import com.clinexa.basediagnosis.implementations.DiagnosisEntityImplementationICD11;

import java.util.List;

public class Diagnosis extends DiagnosisEntityImplementationICD11 {
    public Diagnosis(String ICD11Code, DiagnosisDescriptionHandler descriptionHandler) {
        super(ICD11Code, descriptionHandler);
    }

    public List<Symptom> getSymptoms() {
        throw new UnsupportedOperationException("Not supported yet with ICD 11 as the only database");
        // TODO: Loader where modules can save symptomps per diagnosis
    }
}
