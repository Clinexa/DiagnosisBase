/*
 * This file is part of Clinexa DiagnosisBase.
 *
 * Clinexa DiagnosisBase is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * Clinexa DiagnosisBase is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License and GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License and GNU General Public
 * License along with Clinexa DiagnosisBase. If not, see <https://www.gnu.org/licenses/>.
 */

package com.clinexa.basediagnosis.systems;

import com.clinexa.basediagnosis.DiagnosesSystem;
import com.clinexa.basediagnosis.Diagnosis;
import com.clinexa.basediagnosis.DiagnosisCategoryListing;
import com.clinexa.basediagnosis.DiagnosisEntity;
import com.clinexa.basediagnosis.exceptions.DiagnosesSystemException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class ICD11DiagnosesSystem implements DiagnosesSystem {

    private Map<String, String> data;

    public final String CLIENT_ID_KEY = "CLIEND_ID";
    public final String CLIENT_SECRET_KEY = "CLIENT_SECRET";

    private final String CLIENT_TOKEN_KEY = "CLIENT_TOKEN";

    private final String API_URL_STRING = "https://id.who.int/icd/";
    private final URI API_URI;

    public ICD11DiagnosesSystem() {
        data = new HashMap<String, String>();
        try {
            API_URI = new URI(API_URL_STRING);
        } catch (Exception e) {
            throw new DiagnosesSystemException(e);
        }
    }

    @Override
    public void init() {
        try (var client = HttpClient.newHttpClient();) {
            if (!data.containsKey(CLIENT_ID_KEY) && !data.containsKey(CLIENT_SECRET_KEY))
                throw new DiagnosesSystemException("Information for WHO authentication was not given. Set " +
                        "ICD11DiagnosesSystem.CLIENT_ID_KEY and .CLIENT_ID_KEY using setParameter() method!");
            String clientID = data.get(CLIENT_ID_KEY);
            String clientSecret = data.get(CLIENT_SECRET_KEY);

            HttpRequest.Builder builder = HttpRequest.newBuilder();
            final String TOKEN_ENPOINT = "https://icdaccessmanagement.who.int/connect/token";
            final String SCOPE = "icdapi_access";
            final String GRANT_TYPE = "client_credentials";
            builder.uri(URI.create(TOKEN_ENPOINT));
            String urlParameters =
                    "client_id=" + URLEncoder.encode(clientID, StandardCharsets.UTF_8) +
                            "&client_secret=" + URLEncoder.encode(clientSecret, StandardCharsets.UTF_8) +
                            "&scope=" + URLEncoder.encode(SCOPE, StandardCharsets.UTF_8) +
                            "&grant_type=" + URLEncoder.encode(GRANT_TYPE, StandardCharsets.UTF_8);
            builder.POST(HttpRequest.BodyPublishers.ofString(urlParameters));

            HttpResponse<String> response = client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != HttpURLConnection.HTTP_OK)
                throw new DiagnosesSystemException("Error response from ICD API: " + response.body());

            var responseObj = new JSONObject(response.body());
            if (!responseObj.has("access_token"))
                throw new DiagnosesSystemException("Response doesn't contain access token: " + response.body());
            setParameter(CLIENT_TOKEN_KEY, responseObj.getString("access_token"));
        } catch (Exception e) {
            throw new DiagnosesSystemException(e);
        }
    }

    @Override
    public DiagnosisEntity getByICD11Code(String icd11Code) {
        return null;
    }

    @Override
    public DiagnosisCategoryListing getParentCategoryListing() {
        return null;
    }

    @Override
    public DiagnosisCategoryListing getCategoryListing(String category) {
        return null;
    }

    @Override
    public void setParameter(String key, String value) {
        data.put(key, value);
    }

    private JSONObject getAPIResponse(URI apiURI, ICD11Language language) {
        try (var client = HttpClient.newHttpClient()) {
            HttpRequest.Builder builder = HttpRequest.newBuilder();
            builder.uri(API_URI.resolve(apiURI));
            builder.GET();
            builder.setHeader("Authorization", "Bearer " + data.get(CLIENT_ID_KEY));
            builder.setHeader("Accept", "application/json");
            builder.setHeader("Accept-Language", language.getCode());
            builder.setHeader("API-Version", "v2");

            HttpResponse<String> response = client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != HttpURLConnection.HTTP_OK)
                throw new DiagnosesSystemException("Error response from ICD API: " + response.body());

            return new JSONObject(response.body());
        } catch (Exception e) {
            throw new DiagnosesSystemException(e);
        }
    }
}
