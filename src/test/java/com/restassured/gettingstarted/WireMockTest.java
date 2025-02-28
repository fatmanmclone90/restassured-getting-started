package com.restassured.gettingstarted;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.restassured.gettingstarted.models.RequestBody;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.restassured.RestAssured.given;

public class WireMockTest {
    private WireMockServer wireMockServer;
    private final int port = 3001;

    @BeforeAll
    public static void beforeAll() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @BeforeEach
    void setup() {
        wireMockServer = new WireMockServer(WireMockConfiguration.options().port(port));
        wireMockServer.start();
        WireMock.configureFor("localhost", port);

        stubFor(get(urlEqualTo("/somewhere"))
                .withHeader("Content-Type", equalTo(String.valueOf(ContentType.JSON)))
                .willReturn(aResponse()
                        .withStatus(200)));

        stubFor(post(urlEqualTo("/somewhere"))
                .withHeader("Content-Type", equalTo(String.valueOf(ContentType.JSON)))
                .withRequestBody(matchingJsonPath("$.code", equalTo("123")))
                .willReturn(aResponse()
                        .withStatus(200)));
    }

    @Test
    public void get_example() {
        given()
                .when()
                .port(port)
                .baseUri("http://localhost")
                .contentType(ContentType.JSON)
                .get("/somewhere")
                .then()
                .statusCode(200);
    }

    @Test
    public void post_example() {
        var request = new RequestBody("123");

        given()
                .body(request)
                .port(port)
                .baseUri("http://localhost")
                .contentType(ContentType.JSON)
                .when()
                .post("/somewhere")
                .then()
                .statusCode(200);
    }

    @AfterEach
    void teardown() {
        if (wireMockServer != null) {
            wireMockServer.stop();
        }
    }
}
