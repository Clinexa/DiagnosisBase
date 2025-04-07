package com.clinexa.basediagnosis.systems;


import com.clinexa.basediagnosis.systems.misc.DataForToken;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;


import static org.junit.jupiter.api.Assertions.*;

class ICD11DiagnosesSystemTest {

    private ICD11DiagnosesSystem system;

    @BeforeEach
    void setUp() throws Exception{
        system = new ICD11DiagnosesSystem();
        DataForToken dataForToken = TestTokenManager.getDataForToken();
        system.setParameter(ICD11DiagnosesSystem.CLIENT_ID_KEY, dataForToken.getClientID());
        system.setParameter(ICD11DiagnosesSystem.CLIENT_SECRET_KEY, dataForToken.getClientSecret());
        system.init();
    }

    @Test
    @Disabled("Not implemented jet")
    void getByICD11Code() {
        throw new UnsupportedOperationException("Not implemented jet");
    }

    @Test
    void getParentCategoryListing() {
        throw new UnsupportedOperationException("Not implemented jet");
    }

    @Test
    @Disabled("Not implemented jet")
    void getCategoryListing() {
        throw new UnsupportedOperationException("Not implemented jet");
    }
}