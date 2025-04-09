/**
 * General module for DiagnosisBase library.
 *
 * @since 0.1-dev.2
 * @author Nikita S.
 */
module DiagnosisBase {
    requires java.net.http;
    requires org.json;
    requires org.jetbrains.annotations;

    exports com.clinexa.basediagnosis;
    exports com.clinexa.basediagnosis.exceptions;
    exports com.clinexa.basediagnosis.systems;
    exports com.clinexa.basediagnosis.utils;
    exports com.clinexa.basediagnosis.services;

    uses com.clinexa.basediagnosis.services.ICDCodeConverter;
    uses com.clinexa.basediagnosis.services.SymptomSupplier;
}