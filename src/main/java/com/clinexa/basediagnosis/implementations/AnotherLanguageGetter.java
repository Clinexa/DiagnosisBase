package com.clinexa.basediagnosis.implementations;

import com.clinexa.basediagnosis.utils.ICDLanguage;

@FunctionalInterface
public interface AnotherLanguageGetter {

    String getInAnotherLanguage(ICDLanguage language);

}
