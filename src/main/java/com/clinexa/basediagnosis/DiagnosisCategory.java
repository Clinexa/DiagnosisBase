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
import org.jetbrains.annotations.NotNull;

/**
 * Stores data about diagnosis category.
 *
 * @since 0.1-dev.1
 * @author Nikita S.
 */
public class DiagnosisCategory extends TitledImplementation {

    private final String systemCode;

    /**
     * Creates new diagnosis category.
     *
     * @param system system that was used to generate entity. Will be used for translations.
     * @param language language of the given title.
     * @param systemCode ID of the entity.
     * @param name title of the entity in the given language.
     * @since 0.1-dev.2
     */
    public DiagnosisCategory(@NotNull String name, String systemCode, @NotNull ICDLanguage language,
                             @NotNull DiagnosesSystem system) {
        super(name, language, (var lang) -> system.getTitleByEntityID(systemCode, lang).getTitle(lang));
        this.systemCode = systemCode;
    }

    /**
     * Old (from 0.1-dev.1) version of the constructor that uses ENGLISH as
     * default language and default diagnoses system (see {@link DiagnosesSystem#getDefaultDiagnosesSystem()}).
     *
     * @param systemCode ID of the entity.
     * @param name title of the entity in English.
     * @deprecated use four-variable constructor instead to specify used language.
     * @see #DiagnosisCategory(String, String, ICDLanguage, DiagnosesSystem)
     */
    @Deprecated(since = "0.1-dev.2", forRemoval = true)
    public DiagnosisCategory(@NotNull String name, String systemCode) {
        this(name, systemCode, ICDLanguage.ENGLISH, DiagnosesSystem.getDefaultDiagnosesSystem());
    }

    /**
     * Returns the title of the category.
     *
     * @deprecated use {@link Titled#getTitle(ICDLanguage)} instead.
     * @return title of the category.
     */
    @Deprecated(since = "0.1-dev.2", forRemoval = true)
    public @NotNull String getName() {
        return getTitle(language);
    }

    /**
     * Returns entity ID of this category.
     *
     * @return entity ID.
     */
    @SuppressWarnings("unused")
    public String getSystemCode() {
        return systemCode;
    }
}
