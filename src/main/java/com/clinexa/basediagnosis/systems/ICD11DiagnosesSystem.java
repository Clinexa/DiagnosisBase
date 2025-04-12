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
import com.clinexa.basediagnosis.implementations.AnotherLanguageGetter;
import com.clinexa.basediagnosis.implementations.TitledImplementation;
import com.clinexa.basediagnosis.utils.ICDLanguage;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;

/**
 * ICD 11-based symptoms and diagnoses management system.
 * <br>
 *
 * Can be used to get subcategories of ICD 11, their subcategories, diagnoses
 * and symptoms in them, get information by ICD 11 code etc.
 *
 * @since 0.1-dev.1
 * @author Nikita S.
 * @see DiagnosesSystem
 */
public final class ICD11DiagnosesSystem implements DiagnosesSystem {

    private ICDLanguage language = ICDLanguage.ENGLISH;

    private final Map<String, String> data;

    private static final int REQUEST_TIMEOUT = 10;

    /**
     * Key that should be passed to {@link #setParameter(String, String)} to set ICD 11 API's client id
     */
    public static final String CLIENT_ID_KEY = "CLIEND_ID";
    /**
     * Key that should be passed to {@link #setParameter(String, String)} to set ICD 11 API's client secret
     */
    public static final String CLIENT_SECRET_KEY = "CLIENT_SECRET";

    private final String CLIENT_TOKEN_KEY = "CLIENT_TOKEN";

    @SuppressWarnings("FieldCanBeLocal")
    private final String API_URL_STRING = "https://id.who.int/icd/";
    private final URI API_URI;

    private final String LATEST_RELEASE_NAME_KEY = "LATEST_RELEASE_NAME";

    private enum EntityType {
        DIAGNOSIS,
        SYMPTOM,
        CATEGORY
    }

    private final static DiagnosesSystem instance = new ICD11DiagnosesSystem();

    /**
     * Returns the only instance of ICD 11 diagnoses system.
     * <br>
     *
     * Before working with system, ICD 11 API keys should be set (see
     * {@link #CLIENT_ID_KEY} and {@link #CLIENT_SECRET_KEY}) and
     * {@link #init()} method should be called.
     *
     * @return instance of ICD11DiagnosesSystem.
     * @since 1.0-dev.2
     * @see <a href="https://en.wikipedia.org/wiki/Singleton_pattern">Singleton pattern</a>
     */
    public static @NotNull DiagnosesSystem getInstance() {
        return instance;
    }

    /**
     * Creates an empty instance of ICD 11 diagnoses system.
     *
     * @deprecated Use {@link #getInstance()}. Using default constructor brakes translations.
     *             Will be made private later.
     */
    @SuppressWarnings("DeprecatedIsStillUsed")
    @Deprecated(since = "0.1-dev.2", forRemoval = true)
    public ICD11DiagnosesSystem() {
        data = new HashMap<>();
        API_URI = formURI(API_URL_STRING);
    }

    /**
     * Initializes class before usage.
     * <br>
     *
     * ICD 11 API's client id and secret must be given using {@link #setParameter(String, String)}
     * before calling this method.
     *
     * @see #CLIENT_ID_KEY
     * @see #CLIENT_SECRET_KEY
     */
    @Override
    public void init() {
        try (var client = HttpClient.newHttpClient()) {
            initToken(client);
            initRelease(language);
        } catch (IOException | InterruptedException e) {
            throw new DiagnosesSystemException(e);
        }
    }

    /**
     * Gets token from ICD 11 API server and saves is under {@link #CLIENT_TOKEN_KEY}
     * in data.
     *
     * @param client initialized {@link HttpClient}.
     * @throws IOException if there's a problem with internet connection.
     * @throws InterruptedException if connection was interrupted.
     */
    private void initToken(@NotNull HttpClient client) throws IOException, InterruptedException {
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

        builder.timeout(Duration.ofSeconds(REQUEST_TIMEOUT));

        HttpResponse<String> response = client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != HttpURLConnection.HTTP_OK)
            throw new DiagnosesSystemException("Error response from ICD API: " + response.body());

        var responseObj = new JSONObject(response.body());
        if (!responseObj.has("access_token"))
            throw new DiagnosesSystemException("Response doesn't contain access token: " + response.body());
        setParameter(CLIENT_TOKEN_KEY, responseObj.getString("access_token"));
    }

    /**
     * Gets actual release of ICD 11 from API and saves it in data
     * under {@link #LATEST_RELEASE_NAME_KEY}.
     *
     * @param language language to set during request. Shouldn't change anything.
     */
    private void initRelease(@NotNull ICDLanguage language) {
        JSONObject releaseResponse = getAPIResponse(formURI("release/11/mms"), language);
        if (!releaseResponse.has("latestRelease"))
            throw new DiagnosesSystemException("Response doesn't contain latest release: " + releaseResponse);
        String releaseName = releaseResponse.getString("latestRelease").replace("http://id.who.int/icd/release/11/", "").replace("/mms", "");
        setParameter(LATEST_RELEASE_NAME_KEY, releaseName);
    }

    /**
     * Returns {@link Diagnosis}, {@link Symptom}, or {@link DiagnosisCategory} object
     * for a given ICD 11 code.
     *
     * @param icd11Code ICD 11 code
     * @param language language to be used as default in returned object.
     * @return {@link Diagnosis}, {@link Symptom}, or {@link DiagnosisCategory} object for a given ICD 11 code.
     * @since 0.1-dev.2
     * @see DiagnosesSystem#getByICD11Code(String, ICDLanguage)
     */
    @Override
    public @NotNull Object getByICD11Code(@NotNull String icd11Code, @NotNull ICDLanguage language) {
        JSONObject response = getAPIResponse(formURI(formQuery("") + "/codeinfo/" + icd11Code), language);
        String entityID = response.getString("stemId");
        entityID = entityID.substring(entityID.indexOf("mms") + 4);
        JSONObject codeResponse = getAPIResponse(formURI(formQuery(entityID)), language);
        return createPairByResponse(codeResponse, entityID, language).getKey();
    }

    /**
     * Returns main categories of ICD 11.
     *
     * @param language default language of the results.
     * @return list of {@link Diagnosis}, {@link Symptom}, or {@link DiagnosisCategory} objects of main subcategories.
     * @since 0.1-dev.2
     * @see DiagnosesSystem#getParentCategoryListing(ICDLanguage)
     */
    @Override
    public @NotNull List<Map.Entry<Object, String>> getParentCategoryListing(@NotNull  ICDLanguage language) {
        return getCategoryListing("", language);
    }

    /**
     * Returns elements of the category in ICD 11.
     *
     * @param category ID of category to check.
     * @param language default language of the results.
     * @return list of {@link Diagnosis}, {@link Symptom}, or {@link DiagnosisCategory} objects form the category.
     * @since 0.1-dev.2
     * @see DiagnosesSystem#getCategoryListing(String, ICDLanguage)
     */
    @Override
    public @NotNull List<Map.Entry<Object, String>> getCategoryListing(@NotNull String category, @NotNull ICDLanguage language) {
        JSONObject apiResponse = getAPIResponse(URI.create(formQuery(category)), language);
        if (!apiResponse.has("child"))
            throw new DiagnosesSystemException("Given entity is not a category: " + category);
        JSONArray children = apiResponse.getJSONArray("child");

        List<Map.Entry<Object, String>> subcategories = new ArrayList<>();
        for (Object childURI : children) {
            subcategories.add(processChild((String) childURI, language));
        }
        return subcategories;
    }

    /**
     * Returns search results for the given input query.
     *
     * @param query query to search.
     * @param language default language of the results.
     * @return list of {@link Diagnosis}, {@link Symptom}, or {@link DiagnosisCategory} objects for the given query.
     * @since 0.1-dev.2
     * @see DiagnosesSystem#getSearchResult(String, ICDLanguage)
     */
    @Override
    public @NotNull List<Map.Entry<Object, String>> getSearchResult(@NotNull String query, @NotNull ICDLanguage language) {
        String queryForURI = formQuery("search?q=" + URLEncoder.encode(query, StandardCharsets.UTF_8));
        JSONObject response = getAPIResponse(formURI(queryForURI), language);
        JSONArray responsesArray = response.getJSONArray("destinationEntities");

        List<Map.Entry<Object, String>> subcategories = new ArrayList<>();
        for (Object obj : responsesArray) {
            JSONObject destinationEntity = (JSONObject) obj;
            String entityID = destinationEntity.getString("stemId");
            entityID = entityID.substring(entityID.indexOf("mms") + 4);
            JSONObject destinationEntityResponse = getAPIResponse(formURI(formQuery(entityID)), language);
            subcategories.add(createPairByResponse(destinationEntityResponse, entityID, language));
        }
        return subcategories;
    }

    /**
     * Returns untranslatable {@link Titled} object with title of
     * the ICD 11 entity by its ID.
     *
     * @param entity ID of the entity to search.
     * @param language the only language results will be available in.
     * @return {@link Titled} object with title of entity with given ID.
     * @since 0.1-dev.2
     * @see DiagnosesSystem#getTitleByEntityID(String, ICDLanguage)
     */
    @Override
    public @NotNull Titled getTitleByEntityID(@NotNull String entity, @NotNull ICDLanguage language) {
        JSONObject response = getAPIResponse(formURI(formQuery(entity)), language);
        String title = response.getJSONObject("title").getString("@value");
        return new TitledImplementation(title, language, AnotherLanguageGetter::throwException);
    }

    /**
     * Returns {@link Diagnosis}, {@link Symptom}, or {@link DiagnosisCategory} object
     * for a given ICD 11 code in set language.
     *
     * @param icd11Code ICD 11 code
     * @return {@link Diagnosis}, {@link Symptom}, or {@link DiagnosisCategory} object for a given ICD 11 code.
     * @see DiagnosesSystem#getByICD11Code(String)
     */
    @Override
    public @NotNull Object getByICD11Code(@NotNull String icd11Code) {
        return getByICD11Code(icd11Code, language);
    }

    /**
     * Returns main categories of ICD 11 in set language.
     *
     * @return list of {@link Diagnosis}, {@link Symptom}, or {@link DiagnosisCategory} objects of main subcategories.
     * @see DiagnosesSystem#getParentCategoryListing()
     */
    @Override
    public @NotNull List<Map.Entry<Object, String>> getParentCategoryListing() {
        return getParentCategoryListing(language);
    }

    /**
     * Returns elements of the category in ICD 11 for the set language.
     *
     * @param category ID of category to check.
     * @return list of {@link Diagnosis}, {@link Symptom}, or {@link DiagnosisCategory} objects form the category.
     * @see DiagnosesSystem#getCategoryListing(String)
     */
    @Override
    public @NotNull List<Map.Entry<Object, String>> getCategoryListing(@NotNull String category) {
        return getCategoryListing(category, language);
    }

    /**
     * Returns search results for the given input query for set language.
     *
     * @param query query to search.
     * @return list of {@link Diagnosis}, {@link Symptom}, or {@link DiagnosisCategory} objects for the given query.
     * @see DiagnosesSystem#getSearchResult(String)
     */
    @Override
    public @NotNull List<Map.Entry<Object, String>> getSearchResult(@NotNull String query) {
        return getSearchResult(query, language);
    }

    /**
     * Returns untranslatable {@link Titled} object with title of
     * the ICD 11 entity by its ID for set language.
     *
     * @param entity ID of the entity to search.
     * @return {@link Titled} object with title of entity with given ID.
     * @since 0.1-dev.2
     * @see DiagnosesSystem#getTitleByEntityID(String)
     */
    @Override
    public @NotNull Titled getTitleByEntityID(@NotNull String entity) {
        return getTitleByEntityID(entity, language);
    }

    /**
     * Sets default language.
     *
     * @param language language to be set as a one used by default.
     * @since 0.1-dev.2
     * @see DiagnosesSystem#setLanguage(ICDLanguage)
     */
    @Override
    public void setLanguage(@NotNull ICDLanguage language) {
        this.language = language;
    }

    /**
     * Process child subcategory during parsing a parent category.
     *
     * @param childURI API URI of the child.
     * @param language language that will be used as default.
     * @return Map.Entry pair with a corresponding object for a child and a String with its ID.
     */
    private @NotNull Map.Entry<Object, String> processChild(@NotNull String childURI, @NotNull ICDLanguage language) {
        String childEntity = childURI.substring(childURI.indexOf("mms/") + 4);
        String childQuery = "release/11/" + data.get(LATEST_RELEASE_NAME_KEY) + "/mms/" + childEntity;
        JSONObject childResponse = getAPIResponse(URI.create(childQuery), language);
        return createPairByResponse(childResponse, childEntity, language);
    }

    /**
     * Creates a pair for category child response.
     *
     * @param childResponse response from API for category's child
     * @param childEntity ID of the child.
     * @param language language to be used.
     * @return Map.Entry pair with a corresponding object for a child and a String with its ID.
     * @throws UnsupportedOperationException if type of the child isn't supported by the method.
     */
    private @NotNull Map.Entry<Object, String> createPairByResponse(@NotNull JSONObject childResponse,
                @NotNull String childEntity, @NotNull ICDLanguage language) {
        Object object = switch (getObjectType(childResponse)) {
            case CATEGORY -> new DiagnosisCategory(getTitle(childResponse), childEntity, language, this);
            case DIAGNOSIS -> new Diagnosis(this, language, childResponse.getString("code"), getTitle(childResponse));
            case SYMPTOM -> new Symptom(this, language, childResponse.getString("code"), getTitle(childResponse));
            //noinspection UnnecessaryDefault
            default -> throw new UnsupportedOperationException("Unsupported category: " + childEntity);
        };
        return new AbstractMap.SimpleEntry<>(object, childEntity);
    }

    /**
     * Returns title of the API response object.
     *
     * @param response response from the API.
     * @return title.@value from response's JSON.
     */
    private @NotNull String getTitle(@NotNull JSONObject response) {
        return response.getJSONObject("title").getString("@value");
    }

    /**
     * Forms API query for a given category ID.
     *
     * @param category category ID to be used.
     * @return API query for a given category ID.
     */
    private @NotNull String formQuery(@NotNull String category) {
        return "release/11/" + data.get(LATEST_RELEASE_NAME_KEY) + "/mms" + (category.isEmpty() ? "" : "/" + category);
    }

    /**
     * Sets parameter for system.
     *
     * @param key key (usually are available as public static final String constants ending in _KEY).
     * @param value value to be stored.
     * @see DiagnosesSystem#setParameter(String, String)
     */
    @Override
    public void setParameter(@NotNull String key, String value) {
        data.put(key, value);
    }

    /**
     * Creates new URI instance without checked exceptions.
     *
     * @param query query to be passed to URI constructor.
     * @return new URI object.
     * @throws DiagnosesSystemException if {@link URISyntaxException} is thrown.
     */
    private @NotNull URI formURI(@NotNull String query) {
        try {
            return new URI(query);
        } catch (URISyntaxException e) {
            throw new DiagnosesSystemException(e);
        }
    }

    /**
     * Returns object type from response for it.
     * <br>
     *
     * Category has "child" field. Diagnosis has an ICD 11 code. Symptom has an ICD 11 code starting
     * with 'M'.
     *
     * @param object response from API.
     * @return {@link EntityType} with a type that corresponds to a given response from API.
     */
    private @NotNull EntityType getObjectType(@NotNull JSONObject object) {
        if (object.has("child"))
            return EntityType.CATEGORY;

        String code = object.getString("code");
        if (code.startsWith("M"))
            return EntityType.SYMPTOM;

        return EntityType.DIAGNOSIS;
    }

    /**
     * Sends API request with no additional headers.
     *
     * @param apiURI URI to send a request to.
     * @param language language to be set in headers.
     * @return {@link JSONObject} with response from the API.
     */
    private @NotNull JSONObject getAPIResponse(@NotNull URI apiURI, @NotNull ICDLanguage language) {
        return getAPIResponse(apiURI, language, new HashMap<>());
    }

    /**
     * Sends API request with additional headers.
     *
     * @param apiURI URI to send a request to.
     * @param language language to be set in headers.
     * @param headers additional headers for the request.
     * @return {@link JSONObject} with response from the API.
     */
    private @NotNull JSONObject getAPIResponse(@NotNull URI apiURI, @NotNull ICDLanguage language, @NotNull Map<String, String> headers) {
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

            builder.timeout(Duration.ofSeconds(REQUEST_TIMEOUT));

            HttpResponse<String> response = client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == HttpsURLConnection.HTTP_NOT_FOUND)
                throw new DiagnosesSystemException("ICD API Not found: " + apiURI);
            if (response.statusCode() != HttpURLConnection.HTTP_OK)
                throw new DiagnosesSystemException("Error response from ICD API: " + response.body());

            return new JSONObject(response.body());
        } catch (Exception e) {
            throw new DiagnosesSystemException(e);
        }
    }
}
