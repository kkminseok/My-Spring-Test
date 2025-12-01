import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.text.RandomStringGenerator;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class RestAPITest {

    @Test
    public void givenUserDoesNotExists_whenUserInfoRetrieved_then404IsReceived() throws IOException {
        // Given
        String name = RandomStringUtils.randomAlphabetic(10);
        HttpGet request = new HttpGet("https://api.github.com/users/" + name);

        // When
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        // Then
        Assertions.assertEquals(httpResponse.getCode(), HttpStatus.SC_NOT_FOUND);
    }

    @Test
    public void givenRequestWithNoAcceptHeader_whenRequestIsExecuted_thenDefaultResponseContentTypeIsJson() throws IOException {
        // Given
        String jsonMimType = "application/json";
        HttpGet request = new HttpGet("https://api.github.com/users/kkminseok");

        // When
        HttpResponse response = HttpClientBuilder.create().build().execute(request);

        // Then
        Assertions.assertEquals(response.getFirstHeader("Content-Type").getValue().split(";")[0], jsonMimType);
    }

    @Test
    public void givenUserExists_whenUserInformationIsRetrieved_thenRetrievedResourceIsCorrect() throws IOException, ParseException {
        // Given
        HttpGet request = new HttpGet("https://api.github.com/users/kkminseok");

        // When
        CloseableHttpResponse response = HttpClientBuilder.create().build().execute(request);

        // Then
        GitHubUser gitHubUser = retrieveResourceFromResponse(response, GitHubUser.class);
        Assertions.assertEquals("kkminseok", gitHubUser.login());

    }

    @Test
    @DisplayName("okhttp 테스트")
    public void okhttpTest() throws IOException {
        // Given


        OkHttpClient client = new OkHttpClient();

        // When
        RequestBody body = RequestBody.Companion.create(null, MediaType.parse("application/json; charset=utf-8"));

        Request request = new Request.Builder().url("http://tech.dev-oc.alpha-nhncloud.com:9003/cxsolution/api/v2/hub/setting/operation").put(body).build();

        Call call = client.newCall(request);

        Response response = call.execute();

        System.out.println(response.body().string());

        // Then
    }


    private <T> T retrieveResourceFromResponse(final CloseableHttpResponse response, final Class<T> clazz) throws IOException, ParseException {
        final String jsonFromResponse = EntityUtils.toString(response.getEntity());
        final ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper.readValue(jsonFromResponse, clazz);
    }
}
