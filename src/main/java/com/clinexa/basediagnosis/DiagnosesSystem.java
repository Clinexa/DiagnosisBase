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
import com.clinexa.basediagnosis.utils.ICDLanguage;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

/**
 * Common interface for all diagnoses and symptoms managers.
 *
 * @since 0.1-dev.1
 * @author Nikita S.
 */
public interface DiagnosesSystem {

    /**
     * Returns instance of diagnoses system used by default.
     *
     * @return instance of diagnoses system used by default
     */
    static @NotNull DiagnosesSystem getDefaultDiagnosesSystem() {
        return ICD11DiagnosesSystem.getInstance();
    }

    /**
     * Sets default language.
     *
     * @param language language to be set as a one used by default.
     * @since 0.1-dev.2
     */
    void setLanguage(@NotNull ICDLanguage language);

    /**
     * Returns {@link Diagnosis}, {@link Symptom}, or {@link DiagnosisCategory} object
     * for a given ICD 11 code in set language.
     *
     * @param icd11Code ICD 11 code
     * @return {@link Diagnosis}, {@link Symptom}, or {@link DiagnosisCategory} object for a given ICD 11 code.
     */
    @NotNull Object getByICD11Code(@NotNull String icd11Code);
    /**
     * Returns main categories in set language.
     *
     * @return list of {@link Diagnosis}, {@link Symptom}, or {@link DiagnosisCategory} objects of main subcategories.
     */
    @NotNull List<Map.Entry<Object, String>> getParentCategoryListing();
    /**
     * Returns elements of the category for the set language.
     *
     * @param category ID of category to check.
     * @return list of {@link Diagnosis}, {@link Symptom}, or {@link DiagnosisCategory} objects form the category.
     */
    @NotNull List<Map.Entry<Object, String>> getCategoryListing(@NotNull String category);
    /**
     * Returns search results for the given input query for set language.
     *
     * @param query query to search.
     * @return list of {@link Diagnosis}, {@link Symptom}, or {@link DiagnosisCategory} objects for the given query.
     */
    @NotNull List<Map.Entry<Object, String>> getSearchResult(@NotNull String query);
    /**
     * Returns untranslatable {@link Titled} object with title of
     * the entity by its ID for set language.
     *
     * @param entity ID of the entity to search.
     * @return {@link Titled} object with title of entity with given ID.
     * @since 0.1-dev.2
     */
    @NotNull Titled getTitleByEntityID(@NotNull String entity);

    /**
     * Returns {@link Diagnosis}, {@link Symptom}, or {@link DiagnosisCategory} object
     * for a given ICD 11 code.
     *
     * @param icd11Code ICD 11 code
     * @param language language to be used as default in returned object.
     * @return {@link Diagnosis}, {@link Symptom}, or {@link DiagnosisCategory} object for a given ICD 11 code.
     * @since 0.1-dev.2
     */
    @NotNull Object getByICD11Code(@NotNull String icd11Code, @NotNull ICDLanguage language);
    /**
     * Returns main categories.
     *
     * @param language default language of the results.
     * @return list of {@link Diagnosis}, {@link Symptom}, or {@link DiagnosisCategory} objects of main subcategories.
     * @since 0.1-dev.2
     */
    @NotNull List<Map.Entry<Object, String>> getParentCategoryListing(@NotNull ICDLanguage language);
    /**
     * Returns elements of the category.
     *
     * @param category ID of category to check.
     * @param language default language of the results.
     * @return list of {@link Diagnosis}, {@link Symptom}, or {@link DiagnosisCategory} objects form the category.
     * @since 0.1-dev.2
     */
    @NotNull List<Map.Entry<Object, String>> getCategoryListing(@NotNull String category, @NotNull ICDLanguage language);
    /**
     * Returns search results for the given input query.
     *
     * @param query query to search.
     * @param language default language of the results.
     * @return list of {@link Diagnosis}, {@link Symptom}, or {@link DiagnosisCategory} objects for the given query.
     * @since 0.1-dev.2
     */
    @NotNull List<Map.Entry<Object, String>> getSearchResult(@NotNull String query, @NotNull ICDLanguage language);
    /**
     * Returns untranslatable {@link Titled} object with title of
     * the entity by its ID.
     *
     * @param entity ID of the entity to search.
     * @param language the only language results will be available in.
     * @return {@link Titled} object with title of entity with given ID.
     * @since 0.1-dev.2
     */
    @NotNull Titled getTitleByEntityID(@NotNull String entity, @NotNull ICDLanguage language);


    /**
     * Sets parameter for system.
     *
     * @param key key.
     * @param value value to be stored.
     * @implNote give user predefined constants with keys that should or might be set.
     */
    void setParameter(@NotNull String key, String value);

    /**
     * Initializes class before usage.
     * <br>
     *
     * Particular implementation may ask for some parameters to be
     * set using {@link #setParameter(String, String)} before calling
     * this method.
     */
    void init();

}
