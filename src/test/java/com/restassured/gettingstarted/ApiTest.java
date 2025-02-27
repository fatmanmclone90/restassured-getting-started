package com.restassured.gettingstarted;

import com.restassured.gettingstarted.filters.MyFilter;
import com.restassured.gettingstarted.models.RequestBody;
import com.restassured.gettingstarted.models.Response;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.SpecificationQuerier;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import java.util.UUID;
import java.util.function.Function;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.preemptive;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ApiTest {

    @BeforeAll
    public static void setup() {
        // sets globally
        RestAssured.requestSpecification = new RequestSpecBuilder()
                .setBaseUri("http://localhost")
                .setPort(3000)
                .addHeader("User-Agent", UUID.randomUUID().toString())
                .setAuth(preemptive().basic("user", "password"))
                .setContentType(ContentType.JSON)
                .build();
        // RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    public void get_jsonBodyAssert() {
        given()
                .when()
                .get("/somewhere")
                .then()
                .statusCode(200)
                .body("status", equalTo("ok"))
                .body("type", equalTo(2));
    }

    // fussy on formatting
    @Test
    public void get_stringBodyAssert() {
        var body = given()
                .when()
                .get("/somewhere")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .asString();

        assertEquals("""
                {
                  "status": "ok",
                  "type": 2
                }""", body);
    }

    @Test
    public void get_pojoAssert() {
        var response = given()
                .when()
                .get("/somewhere")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .as(Response.class);

        assertEquals(new Response("ok", 2), response);

    }

    @Test
    public void get_jsonAssert() throws JSONException {
        var body = given()
                .when()
                .get("/somewhere")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .asString();

        JSONAssert.assertEquals("{\"status\":\"ok\",\"type\":2}}", body, JSONCompareMode.STRICT);
    }

    @Test
    public void get_contentType() {
        given()
                .when()
                .get("/somewhere")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON.withCharset(UTF_8));
    }

    @Test
    public void post_bodyJObject() throws JSONException {
        JSONObject jsonObj = new JSONObject()
                .put("phoneNumber", "353837986524")
                .put("messageContent", "test");
        given()
                .body(jsonObj.toString())
                .when()
                .post("/something")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON.withCharset(UTF_8));
    }

    @Test
    public void post_bodyPojo() {
        var request = new RequestBody("123");
        given()
                .body(request)
                .when()
                .post("/something")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON.withCharset(UTF_8));
    }

    @Test
    public void get_hamcrestMatcher() {
        given()
                .when()
                .get("/somewhere")
                .then()
                .header("some-int", x -> Integer.parseInt(x), lessThan(500)) // https://hamcrest.org/JavaHamcrest/javadoc/1.3/org/hamcrest/Matchers.html
                .header("some-int", stringToInt, lessThan(500)) // https://hamcrest.org/JavaHamcrest/javadoc/1.3/org/hamcrest/Matchers.html
                .contentType(ContentType.JSON.withCharset(UTF_8));
    }

    @Test
    public void get_responseAwareMatcher() {
        var request = new RequestBody("123");
        given()
                .when()
                .get("/somewhere")
                .then()
                .header("type", response -> equalTo(response.body().jsonPath().get("type").toString())) // compare headers and body
                .contentType(ContentType.JSON.withCharset(UTF_8));
    }

    @Test
    public void get_jsonSchemaValidation() {
        given()
                .when()
                .get("/somewhere")
                .then()
                .assertThat().body(matchesJsonSchemaInClasspath("Response.json"));
    }

    @Test
    public void get_pathParam() {
        var id = 50;
        given()
                .when()
                .get("somewhere/{id}", id)
                .then()
                .assertThat()
                .statusCode(200)
                .body("id", equalTo(id));

    }

    @Test
    public void query_Spec() {
        var spec = new RequestSpecBuilder()
                .addHeader("foo", "bar")
                .build();

        var queryable = SpecificationQuerier.query(spec);
        var headerValue = queryable.getHeaders().getValue("foo");

        assertEquals("bar", headerValue);
    }

    @Test
    public void get_customFilter() {
        given()
                .when()
                .filter(new MyFilter())
                .get("somewhere")
                .then()
                .assertThat()
                .statusCode(200);
    }


    Function<String, Integer> stringToInt = s -> Integer.parseInt(s);
}
