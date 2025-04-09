package com.clinexa.basediagnosis.systems;

import com.clinexa.basediagnosis.DiagnosesSystem;
import com.clinexa.basediagnosis.Diagnosis;
import com.clinexa.basediagnosis.Symptom;
import com.clinexa.basediagnosis.systems.misc.DataForToken;
import com.clinexa.basediagnosis.utils.ICDLanguage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opentest4j.TestAbortedException;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

public class TestDiagnosisEntitiesICD11Implementation {

    private DiagnosesSystem system;

    @BeforeEach
    void setUp() throws Exception{
        system = ICD11DiagnosesSystem.getInstance();
        DataForToken dataForToken;
        try {
            dataForToken = TestTokenManager.getDataForToken();
        } catch (RuntimeException e) {
            throw new TestAbortedException("Skipped due to no token been found.");
        }
        system.setParameter(ICD11DiagnosesSystem.CLIENT_ID_KEY, dataForToken.getClientID());
        system.setParameter(ICD11DiagnosesSystem.CLIENT_SECRET_KEY, dataForToken.getClientSecret());
        system.init();
    }

    @Test
    void testDiagnosisSerialization() throws IOException, ClassNotFoundException {
        Diagnosis diagnosis = (Diagnosis) system.getByICD11Code("1A40.0");
        var byteArrayOut = new ByteArrayOutputStream();
        try (var out = new ObjectOutputStream(byteArrayOut)) {
            out.writeObject(diagnosis);
        }

        var in = new ObjectInputStream(new ByteArrayInputStream(byteArrayOut.toByteArray()));
        Diagnosis diagnosisFromStream = (Diagnosis) in.readObject();

        assertEquals(diagnosis.getICD11Code(), diagnosisFromStream.getICD11Code());
        assertEquals(diagnosis, diagnosisFromStream);
        assertEquals(diagnosis.hashCode(), diagnosisFromStream.hashCode());
        assertEquals(diagnosis.getTitle(ICDLanguage.RUSSIAN), diagnosisFromStream.getTitle(ICDLanguage.RUSSIAN));
    }

    @Test
    void testSymptomSerialization() throws IOException, ClassNotFoundException {
        Symptom diagnosis = (Symptom) system.getByICD11Code("MG43.0");
        var byteArrayOut = new ByteArrayOutputStream();
        try (var out = new ObjectOutputStream(byteArrayOut)) {
            out.writeObject(diagnosis);
        }

        var in = new ObjectInputStream(new ByteArrayInputStream(byteArrayOut.toByteArray()));
        Symptom diagnosisFromStream = (Symptom) in.readObject();

        assertEquals(diagnosis.getICD11Code(), diagnosisFromStream.getICD11Code());
        assertEquals(diagnosis, diagnosisFromStream);
        assertEquals(diagnosis.hashCode(), diagnosisFromStream.hashCode());
        assertEquals(diagnosis.getTitle(ICDLanguage.RUSSIAN), diagnosisFromStream.getTitle(ICDLanguage.RUSSIAN));
    }

    @Test
    void testDiagnosisInequality() {
        Diagnosis diagnosis1 = (Diagnosis) system.getByICD11Code("1A40.0");
        Diagnosis diagnosis2 = (Diagnosis) system.getByICD11Code("1A40.Z");

        assertNotEquals(diagnosis1, diagnosis2);
        assertNotEquals(diagnosis1.hashCode(), diagnosis2.hashCode());  // I hope they aren't equal
    }

}
