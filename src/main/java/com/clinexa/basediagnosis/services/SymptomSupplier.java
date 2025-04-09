package com.clinexa.basediagnosis.services;

import com.clinexa.basediagnosis.Diagnosis;
import com.clinexa.basediagnosis.Symptom;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Service that lists symptoms for a given diagnosis.
 * <br>
 *
 * This services will be supplied by other modules for
 * different specialties.
 *
 * @since 0.1-dev.2
 * @author Nikita S.
 * @version 1
 */
public interface SymptomSupplier {

    /**
     * Checks whether diagnosis may be processed, i.e. if there's
     * a list of symptoms for it.
     *
     * @param diagnosis diagnosis to check.
     * @return true if given diagnosis can be processed, false otherwise.
     */
    boolean canProcess(@NotNull Diagnosis diagnosis);

    /**
     * Gets symptoms for a given diagnosis. If {@link #canProcess(Diagnosis)} returns true for
     * this diagnosis, it should be processed with no thrown exceptions (unless Diagnosis is
     * damaged or there's connection with the database/internet).
     *
     * @param diagnosis diagnosis to list symptoms for.
     * @return list of symptoms for a given diagnosis.
     */
    @NotNull List<Symptom> process(@NotNull Diagnosis diagnosis);

}
