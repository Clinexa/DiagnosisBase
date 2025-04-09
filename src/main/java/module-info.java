module DiagnosisBase {
    requires java.net.http;
    requires org.json;

    exports com.clinexa.basediagnosis;
    exports com.clinexa.basediagnosis.exceptions;
    exports com.clinexa.basediagnosis.systems;
    exports com.clinexa.basediagnosis.utils;
    exports com.clinexa.basediagnosis.services;

    uses com.clinexa.basediagnosis.services.ICDCodeConverter;
    uses com.clinexa.basediagnosis.services.SymptomSupplier;
}