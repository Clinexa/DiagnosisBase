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

import com.clinexa.basediagnosis.implementations.TitledImplementation;
import com.clinexa.basediagnosis.utils.ICDLanguage;

public class DiagnosisCategory extends TitledImplementation {

    private String systemCode;

    public DiagnosisCategory(String name, String systemCode, ICDLanguage language, DiagnosesSystem system) {
        super(name, language, (var lang) -> system.getTitleByEntityID(systemCode, lang).getTitle(lang));
        this.systemCode = systemCode;
    }

    @Deprecated(since = "0.1-dev.2", forRemoval = true)
    public DiagnosisCategory(String name, String systemCode) {
        this(name, systemCode, ICDLanguage.ENGLISH, DiagnosesSystem.getDefaultDiagnosesSystem());
    }

    @Deprecated(since = "0.1-dev.2", forRemoval = true)
    public String getName() {
        return getTitle(language);
    }

    public String getSystemCode() {
        return systemCode;
    }
}
