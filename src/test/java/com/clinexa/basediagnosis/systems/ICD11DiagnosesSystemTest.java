package com.clinexa.basediagnosis.systems;


import com.clinexa.basediagnosis.*;
import com.clinexa.basediagnosis.systems.misc.DataForToken;
import com.clinexa.basediagnosis.utils.ICDLanguage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.opentest4j.TestAbortedException;


import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ICD11DiagnosesSystemTest {

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
    void getByICD11CodeSymptom() {
        String CODE1 = "MG24.01";
        Object breastCancerFearObj = system.getByICD11Code(CODE1);
        assertInstanceOf(Symptom.class, breastCancerFearObj);
        Symptom breastCancerFear = (Symptom) breastCancerFearObj;
        assertEquals(CODE1, breastCancerFear.getICD11Code());
        assertEquals("Fear of breast cancer female", breastCancerFear.getTitle());
    }

    @Test
    void getByICD11CodeDiagnosis() {
        String CODE1 = "1A40.0";
        Object diagnosisObj = system.getByICD11Code(CODE1);
        assertInstanceOf(Diagnosis.class, diagnosisObj);
        Diagnosis diagnosis = (Diagnosis) diagnosisObj;
        assertEquals(CODE1, diagnosis.getICD11Code());
        assertEquals("Gastroenteritis or colitis without specification of origin", diagnosis.getTitle());
    }

    @Test
    void getByICD11CodeDiagnosisRussian() {
        String CODE1 = "1A40.0";
        Object diagnosisObj = system.getByICD11Code(CODE1, ICDLanguage.RUSSIAN);
        assertInstanceOf(Diagnosis.class, diagnosisObj);
        Diagnosis diagnosis = (Diagnosis) diagnosisObj;
        assertEquals(CODE1, diagnosis.getICD11Code());
        assertEquals("Гастроэнтерит или колит неуточненного происхождения", diagnosis.getTitle());
    }

    @Test
    void getByICD11CodeCategory() {
        String CODE1 = "1A40";
        Object categoryObj = system.getByICD11Code(CODE1);
        assertInstanceOf(DiagnosisCategory.class, categoryObj);
        DiagnosisCategory category = (DiagnosisCategory) categoryObj;
        assertEquals("Gastroenteritis or colitis without specification of infectious agent", category.getName());
    }

    @Test
    void getParentCategoryListing() {
        List<Map.Entry<Object, String>> list = system.getParentCategoryListing();

        testAllCorrectClasses(list);
        assertFalse(list.isEmpty());
    }

    @Test
    void getCategoryListingAllCategories() {
        testAllTheSame("588616678", DiagnosisCategory.class);  // Gastroenteritis or colitis of infectious origin
    }

    @Test
    void getCategoryListingAllDiagnoses() {
        testAllTheSame("344162786", Diagnosis.class);  // 1A03 Intestinal infections due to Escherichia coli
    }

    @Test
    void getCategoryListingAllSymptoms() {
        testAllTheSame("1907420475", Symptom.class);  // Fear of cancer
    }

    @Test
    void testSearch() {
        List<Map.Entry<Object, String>> result = system.getSearchResult("Bipolar type I disorder manic");

        assertFalse(result.isEmpty());
        testAllCorrectClasses(result);
    }

    @Test
    void testGetTitleByEntityId() {
        Titled test = system.getTitleByEntityID("30738976");

        assertEquals("Viral intestinal infections", test.getTitle(ICDLanguage.ENGLISH));
    }

    @Test
    void testGetDiagnosisInAnotherLanguage() {
        Diagnosis diagnosis = (Diagnosis) system.getByICD11Code("1A40.0");

        assertEquals("Гастроэнтерит или колит неуточненного происхождения", diagnosis.getTitle(ICDLanguage.RUSSIAN));
    }

    void testAllTheSame(String category, Class<?> classType) {
        List<Map.Entry<Object, String>> list = system.getCategoryListing(category);
        for (Map.Entry<Object, String> entry : list) {
            assertInstanceOf(classType, entry.getKey());
        }
        assertFalse(list.isEmpty());
    }

    void testAllCorrectClasses(List<Map.Entry<Object, String>> list) {
        for (Map.Entry<Object, String> entry : list) {
            Object key = entry.getKey();
            assertTrue((key instanceof DiagnosisCategory) || (key instanceof Diagnosis) || (key instanceof System));
        }
    }
}