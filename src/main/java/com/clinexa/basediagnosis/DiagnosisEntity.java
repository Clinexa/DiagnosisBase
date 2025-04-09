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

import org.jetbrains.annotations.NotNull;

/**
 * Common interface for a diagnosis and a symptom.
 *
 * @since 0.1-dev.1
 * @author Nikita S.
 */
public interface DiagnosisEntity extends Titled {

    /**
     * Returns ICD code of this entity,
     *
     * @param version version of ICD
     * @return ICD code for given version.
     * @implNote throw exception if version is not supported.
     */
    @NotNull String getICDCode(@NotNull ICDVersion version);

    /**
     * Returns ICD 11 code of this entity.
     *
     * @return ICD 11 code.
     */
    default @NotNull String getICD11Code() {
        return getICDCode(ICDVersion.ICD11);
    }
}
