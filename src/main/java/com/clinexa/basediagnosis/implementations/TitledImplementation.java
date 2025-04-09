package com.clinexa.basediagnosis.implementations;

import com.clinexa.basediagnosis.Titled;
import com.clinexa.basediagnosis.utils.ICDLanguage;

public class TitledImplementation implements Titled {

    final protected String title;
    final protected ICDLanguage language;
    final private AnotherLanguageGetter anotherLanguageGetter;

    public TitledImplementation(String title, ICDLanguage language, AnotherLanguageGetter anotherLanguageGetter) {
        this.title = title;
        this.language = language;
        this.anotherLanguageGetter = anotherLanguageGetter;
    }

    @Override
    public final String getTitle(ICDLanguage language) {
        if (this.language == language)
            return title;
        else
            return anotherLanguageGetter.getInAnotherLanguage(language);
    }

    @Override
    public String getTitle() {
        return getTitle(ICDLanguage.ENGLISH);
    }
}
