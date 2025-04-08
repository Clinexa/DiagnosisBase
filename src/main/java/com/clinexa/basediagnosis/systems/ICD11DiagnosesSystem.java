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
import java.io.IOException;
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
            initToken(client);
            initRelease();
        } catch (IOException | InterruptedException | URISyntaxException e) {
            throw new DiagnosesSystemException(e);
        }
    }

    private void initToken(HttpClient client) throws IOException, InterruptedException {
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
        builder.POST(HttpRequest.BodyPublishers.ofString(urlParameters, StandardCharsets.UTF_8));
        builder.setHeader("Content-Type", "application/x-www-form-urlencoded");

        HttpResponse<String> response = client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != HttpURLConnection.HTTP_OK)
            throw new DiagnosesSystemException("Error response from ICD API: " + response.body());

        var responseObj = new JSONObject(response.body());
        if (!responseObj.has("access_token"))
            throw new DiagnosesSystemException("Response doesn't contain access token: " + response.body());
        setParameter(CLIENT_TOKEN_KEY, responseObj.getString("access_token"));
    }

    private void initRelease() throws URISyntaxException {
        JSONObject releaseResponse = getAPIResponse(new URI("release/11/mms"), ICD11Language.ENGLISH);
        if (!releaseResponse.has("latestRelease"))
            throw new DiagnosesSystemException("Response doesn't contain latest release: " + releaseResponse);
        String releaseName = releaseResponse.getString("latestRelease").replace("http://id.who.int/icd/release/11/", "").replace("/mms", "");
        setParameter(LATEST_RELEASE_NAME_KEY, releaseName);
    }

    @Override
    public Object getByICD11Code(String icd11Code) {
        try {
            JSONObject response = getAPIResponse(new URI(formQuery("") + "/codeinfo/" + icd11Code), ICD11Language.ENGLISH);
            String entityID = response.getString("stemId");
            entityID = entityID.substring(entityID.indexOf("mms") + 4);
            JSONObject codeResponse = getAPIResponse(new URI(formQuery(entityID)), ICD11Language.ENGLISH);
            return createPairByResponse(codeResponse, entityID).getKey();
        } catch (URISyntaxException e) {
            throw new DiagnosesSystemException(e);
        }
    }

    @Override
    public List<Map.Entry<Object, String>> getParentCategoryListing() {
        return getCategoryListing("");
    }

    @Override
    public List<Map.Entry<Object, String>> getCategoryListing(String category) {
        JSONObject apiResponse = getAPIResponse(URI.create(formQuery(category)), ICD11Language.ENGLISH);
        if (!apiResponse.has("child"))
            throw new DiagnosesSystemException("Given entity is not a category: " + category);
        JSONArray children = apiResponse.getJSONArray("child");

        List<Map.Entry<Object, String>> subcategories = new ArrayList<>();
        for (Object childURI : children) {
            subcategories.add(processChild((String) childURI));
        }
        return subcategories;
    }

    @Override
    public List<Map.Entry<Object, String>> getSearchResult(String query) {
        try {
            String queryForURI = formQuery("search?q=" + URLEncoder.encode(query, StandardCharsets.UTF_8));
            JSONObject response = getAPIResponse(new URI(queryForURI), ICD11Language.ENGLISH);
            JSONArray responsesArray = response.getJSONArray("destinationEntities");

            List<Map.Entry<Object, String>> subcategories = new ArrayList<>();
            for (Object obj : responsesArray) {
                JSONObject destinationEntity = (JSONObject) obj;
                String entityID = destinationEntity.getString("stemId");
                entityID = entityID.substring(entityID.indexOf("mms") + 4);
                JSONObject destinationEntityResponse = getAPIResponse(new URI(formQuery(entityID)), ICD11Language.ENGLISH);
                subcategories.add(createPairByResponse(destinationEntityResponse, entityID));
            }
            return subcategories;
        } catch (URISyntaxException e) {
            throw new DiagnosesSystemException(e);
        }
    }

    private Map.Entry<Object, String> processChild(String childURI) {
        String childEntity = ((String) childURI).replace("http://id.who.int/icd/release/11/2025-01/mms/", "");
        String childQuery = "release/11/" + data.get(LATEST_RELEASE_NAME_KEY) + "/mms/" + childEntity;
        JSONObject childResponse = getAPIResponse(URI.create(childQuery), ICD11Language.ENGLISH);
        return createPairByResponse(childResponse, childEntity);
    }

    private Map.Entry<Object, String> createPairByResponse(JSONObject childResponse, String childEntity) {
        Object object = switch (getObjectType(childResponse)) {
            case CATEGORY -> new DiagnosisCategory(getTitle(childResponse), childEntity);
            case DIAGNOSIS -> new Diagnosis(childResponse.getString("code"), getTitle(childResponse));
            case SYMPTOM -> new Symptom(childResponse.getString("code"), getTitle(childResponse));
            default -> throw new UnsupportedOperationException("Unsupported category: " + childEntity);
        };
        return new AbstractMap.SimpleEntry<>(object, childEntity);
    }

    private String getTitle(JSONObject response) {
        return response.getJSONObject("title").getString("@value");
    }

    private String formQuery(String category) {
        return "release/11/" + data.get(LATEST_RELEASE_NAME_KEY) + "/mms" + (category.isEmpty() ? "" : "/" + category);
    }

    @Override
    public void setParameter(String key, String value) {
        data.put(key, value);
    }

    private EntityType getObjectType(JSONObject object) {
        if (object.has("child"))
            return EntityType.CATEGORY;

        String code = object.getString("code");
        if (code.startsWith("M"))
            return EntityType.SYMPTOM;

        return EntityType.DIAGNOSIS;
    }

    private JSONObject getAPIResponse(URI apiURI, ICD11Language language) {
        return getAPIResponse(apiURI, language, new HashMap<>());
    }

    private JSONObject getAPIResponse(URI apiURI, ICD11Language language, Map<String, String> headers) {
        assert !apiURI.toString().startsWith("/");
        try (var client = HttpClient.newHttpClient()) {
            HttpRequest.Builder builder = HttpRequest.newBuilder();
            builder.uri(API_URI.resolve(apiURI));
            builder.GET();
            builder.setHeader("Authorization", "Bearer " + data.get(CLIENT_TOKEN_KEY));
            builder.setHeader("Accept", "application/json");
            builder.setHeader("Accept-Language", language.getCode());
            builder.setHeader("API-Version", "v2");
            for (Map.Entry<String, String> header : headers.entrySet()) {
                builder.setHeader(header.getKey(), header.getValue());
            }

            HttpResponse<String> response = client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == HttpsURLConnection.HTTP_NOT_FOUND)
                throw new DiagnosesSystemException("ICD API Not found: " + apiURI.toString());
            if (response.statusCode() != HttpURLConnection.HTTP_OK)
                throw new DiagnosesSystemException("Error response from ICD API: " + response.body());

            return new JSONObject(response.body());
        } catch (Exception e) {
            throw new DiagnosesSystemException(e);
        }
    }
}
