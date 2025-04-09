package com.clinexa.basediagnosis.implementations;

import com.clinexa.basediagnosis.Titled;
import com.clinexa.basediagnosis.utils.ICDLanguage;

public class TitledImplementation implements Titled {

    protected String title;
    protected ICDLanguage language;
    protected AnotherLanguageGetter anotherLanguageGetter;

    public TitledImplementation(String title, ICDLanguage language, AnotherLanguageGetter anotherLanguageGetter) {
        this.title = title;
        this.language = language;
        this.anotherLanguageGetter = anotherLanguageGetter;
    }

    public TitledImplementation() {}

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
