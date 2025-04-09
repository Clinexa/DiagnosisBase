module DiagnosisBase {
    requires java.net.http;
    requires org.json;

    exports com.clinexa.basediagnosis;
    exports com.clinexa.basediagnosis.exceptions;
    exports com.clinexa.basediagnosis.systems;
    exports com.clinexa.basediagnosis.utils;
}