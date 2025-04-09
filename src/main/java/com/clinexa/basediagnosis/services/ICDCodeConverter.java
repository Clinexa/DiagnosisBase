package com.clinexa.basediagnosis.services;

import com.clinexa.basediagnosis.ICDVersion;

public interface ICDCodeConverter {

    ICDVersion getFromVersion();
    ICDVersion getToVersion();

    String convert(String code);

}
