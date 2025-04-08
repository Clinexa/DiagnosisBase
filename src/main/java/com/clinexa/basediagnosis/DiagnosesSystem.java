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

import com.clinexa.basediagnosis.systems.ICD11DiagnosesSystem;

import java.util.List;
import java.util.Map;

public interface DiagnosesSystem {

    static DiagnosesSystem getDefaultDiagnosesSystem() {
        return new ICD11DiagnosesSystem();
    }

    DiagnosisEntity getByICD11Code(String icd11Code);

    List<Map.Entry<Object, String>> getParentCategoryListing();
    List<Map.Entry<Object, String>> getCategoryListing(String category);
    List<Map.Entry<String, String>> getSearchResult(String query);

    void setParameter(String key, String value);

    void init();

}
