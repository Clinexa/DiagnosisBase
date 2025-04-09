package com.clinexa.basediagnosis;

import com.clinexa.basediagnosis.utils.ICDLanguage;

public interface Titled {
    String getTitle(ICDLanguage language);
    @Deprecated(since = "1.0-dev.2", forRemoval = true)
    String getTitle();
}
