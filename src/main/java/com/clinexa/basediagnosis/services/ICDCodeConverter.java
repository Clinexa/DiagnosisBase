package com.clinexa.basediagnosis.services;

import com.clinexa.basediagnosis.ICDVersion;
import org.jetbrains.annotations.NotNull;

/**
 * Service interface for classes that convert ICD codes between versions.
 *
 * @since 0.1-dev.2
 * @author Nikita S.
 * @version 1
 * @see ICDVersion
 */
public interface ICDCodeConverter {

    /**
     * Returns the version of ICD FROM which the service can convert.
     *
     * @return the version of ICD  FROM which the service can convert
     * @implSpec just return enum value without any checks.
     */
    @NotNull ICDVersion getFromVersion();

    /**
     * Returns the version of ICD TO which the service can convert.
     *
     * @return the version of ICD TO which the service can convert
     * @implSpec just return enum value without any checks.
     */
    @NotNull ICDVersion getToVersion();

    /**
     * Converts from ICD version specified in {@link #getFromVersion()} to
     * version specified in {@link #getToVersion()}.
     *
     * @param code code to convert.
     * @return converted code as String.
     */
    @NotNull String convert(@NotNull String code);

}
