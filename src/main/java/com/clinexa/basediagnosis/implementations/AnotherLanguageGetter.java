package com.clinexa.basediagnosis.implementations;

import com.clinexa.basediagnosis.utils.ICDLanguage;
import org.jetbrains.annotations.NotNull;

/**
 * Functional interfaces which is used to store a function
 * which is used to get data (usually title of {@link com.clinexa.basediagnosis.Titled})
 * of the object in another language.
 *
 * @since 1.0-dev.2
 * @author Nikita S.
 * @version 1
 */
@FunctionalInterface
public interface AnotherLanguageGetter {

    /**
     * Method that returns data in a given language.
     *
     * @param language language which is needed in the returned value.
     * @return data retriven in (or translated to) given language.
     * @implSpec Method should return a non-null String in a given language
     *           or throw {@link UnsupportedOperationException} if language
     *           isn't supported.
     */
    @NotNull String getInAnotherLanguage(@NotNull ICDLanguage language);

    /**
     * Standard implementation to throw an exception.
     *
     * @param ignoredLanguage language which is needed in the returned value.
     * @return nothing.
     * @throws UnsupportedOperationException always.
     */
    static String throwException(@NotNull ICDLanguage ignoredLanguage) {
        throw new UnsupportedOperationException("Result may be asked only in original language");
    }

}
