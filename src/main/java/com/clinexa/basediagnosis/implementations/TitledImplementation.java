package com.clinexa.basediagnosis.implementations;

import com.clinexa.basediagnosis.Titled;
import com.clinexa.basediagnosis.utils.ICDLanguage;
import org.jetbrains.annotations.NotNull;

/**
 * Default implementation for class that gives its title in
 * different languages.
 *
 * @since 0.1-dev.2
 * @author Nikita S.
 * @see Titled
 */
@SuppressWarnings("removal")
public class TitledImplementation implements Titled {

    // protected is used for serialization purposes only
    protected String title;
    protected ICDLanguage language;
    protected AnotherLanguageGetter anotherLanguageGetter;

    /**
     * Create new TitledImplementation using title, its language and method
     * for translating. Should be called from child classes, but may rarely
     * be used for getting a simple {@link Titled} object.
     *
     * @param title title in the given language.
     * @param language language of the title.
     * @param anotherLanguageGetter method saved in a {@link AnotherLanguageGetter} interface which
     *                              is used when one needs a title in a different language. Lambda
     *                              expression with single parameter of {@link ICDLanguage} type
     *                              which returns {@link String} may be used.
     */
    public TitledImplementation(String title, ICDLanguage language, AnotherLanguageGetter anotherLanguageGetter) {
        this.title = title;
        this.language = language;
        this.anotherLanguageGetter = anotherLanguageGetter;
    }

    /**
     * Default constructor for serialization. DO NOT USE IT!
     *
     * @deprecated Do not use besides serialization!
     */
    public TitledImplementation() {}

    /**
     * Returns title of the object in the given language.
     *
     * @param language language to get title in.
     * @return title in the given language.
     * @throws UnsupportedOperationException if language isn't supported.
     */
    @Override
    public final @NotNull String getTitle(@NotNull ICDLanguage language) {
        if (this.language == language)
            return title;
        else
            return anotherLanguageGetter.getInAnotherLanguage(language);
    }

    /**
     * Returns title of the object in English.
     *
     * @return title in English.
     */
    @Override
    public @NotNull String getTitle() {
        return getTitle(ICDLanguage.ENGLISH);
    }
}
