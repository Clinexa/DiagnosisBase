package com.clinexa.basediagnosis;

import com.clinexa.basediagnosis.utils.ICDLanguage;
import org.jetbrains.annotations.NotNull;

/**
 * Interface for objects that have translatable title.
 *
 * @since 0.1-dev.2
 * @author Nikita S.
 */
public interface Titled {
    /**
     * Returns title in given language.
     *
     * @param language language to be used.
     * @return title translated to a given language.
     */
    @NotNull String getTitle(@NotNull ICDLanguage language);

    /**
     * Returns title in English.
     *
     * @deprecated use {@link #getTitle(ICDLanguage)} instead.
     * @return title translated to a given language.
     */
    @Deprecated(since = "1.0-dev.2", forRemoval = true)
    @NotNull String getTitle();
}
