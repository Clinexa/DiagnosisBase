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

import com.clinexa.basediagnosis.*;
import com.clinexa.basediagnosis.exceptions.DiagnosesSystemException;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ICD11DiagnosesSystem implements DiagnosesSystem {

    private Map<String, String> data;

    public static final String CLIENT_ID_KEY = "CLIEND_ID";
    public static final String CLIENT_SECRET_KEY = "CLIENT_SECRET";

    private final String CLIENT_TOKEN_KEY = "CLIENT_TOKEN";

    private final String API_URL_STRING = "https://id.who.int/icd/";
    private final URI API_URI;

    private final String LATEST_RELEASE_NAME_KEY = "LATEST_RELEASE_NAME";

    private enum EntityType {
        DIAGNOSIS,
        SYMPTOM,
        CATEGORY
    }

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

            JSONObject releaseResponse = getAPIResponse(new URI("release/11/mms"), ICD11Language.ENGLISH);
            String releaseName = releaseResponse.getString("latestRelease").replace("http://id.who.int/icd/release/11/", "").replace("/mms", "");
            setParameter(LATEST_RELEASE_NAME_KEY, releaseName);
        } catch (Exception e) {
            throw new DiagnosesSystemException(e);
        }
    }

    @Override
    public DiagnosisEntity getByICD11Code(String icd11Code) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public List<Map.Entry<Object, String>> getParentCategoryListing() {
        return getCategoryListing("");
    }

    @Override
    public List<Map.Entry<Object, String>> getCategoryListing(String category) {
        String query = "release/11/" + data.get(LATEST_RELEASE_NAME_KEY) + "/mms" + (category.isEmpty() ? "" : "/" + category);
        JSONObject apiResponse = getAPIResponse(URI.create(query), ICD11Language.ENGLISH);
        JSONArray children = apiResponse.getJSONArray("child");
        List<Map.Entry<Object, String>> subcategories = new ArrayList<>();
        for (Object childURI : children) {
            try {
                String childEntity = ((String) childURI).replace("http://id.who.int/icd/release/11/2025-01/mms/", "");
                String childQuery = "release/11/" + data.get(LATEST_RELEASE_NAME_KEY) + "/mms/" + childEntity;
                JSONObject childResponse = getAPIResponse(URI.create(childQuery), ICD11Language.ENGLISH);
                getObjectType(childResponse, childEntity);
                switch (getObjectType(childResponse, childEntity)) {
                    case CATEGORY:
                        String name = childResponse.getJSONObject("title").getString("@value");
                        subcategories.add(new AbstractMap.SimpleEntry<>(new DiagnosisCategory(name, childEntity), childEntity));
                        break;
                    case DIAGNOSIS:
                        subcategories.add(new AbstractMap.SimpleEntry<>(new Diagnosis(childResponse.getString("code")), childEntity));
                        break;
                    case SYMPTOM:
                        subcategories.add(new AbstractMap.SimpleEntry<>(new Symptom(childResponse.getString("code")), childEntity));
                        break;
                    default:
                        throw new UnsupportedOperationException("Unsupported category: " + childEntity);
                }
            } catch (Exception e) {
                throw new DiagnosesSystemException(e);
            }
        }
        return subcategories;
    }

    @Override
    public List<Map.Entry<String, String>> getSearchResult(String query) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void setParameter(String key, String value) {
        data.put(key, value);
    }

    private EntityType getObjectType(JSONObject object, String entityID) throws URISyntaxException {
        if (object.has("child"))
            return EntityType.CATEGORY;

        String code = object.getString("code");
        if (code.startsWith("M"))
            return EntityType.SYMPTOM;

        return EntityType.DIAGNOSIS;
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
