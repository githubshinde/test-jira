import io.restassured.RestAssured;
import io.restassured.filter.session.SessionFilter;

import static io.restassured.RestAssured.given;

public class JiraTest {
    public static void main(String[] args) {

        //login scenario

        RestAssured.baseURI = "http://localhost:8888";

        SessionFilter sessionFilter = new SessionFilter();

        given().header("Content-Type","application/json").
                body("{\n" +
                        "    \"username\": \"TESTJIRA123\",\n" +
                        "    \"password\": \"Test123\"\n" +
                        "}").filter(sessionFilter).when().log().all().
                post("/rest/auth/1/session").then().assertThat().statusCode(200).
                log().all();


        //add comment
        given().pathParam("key","10001").
                header("Content-Type","application/json").
                filter(sessionFilter).
                body("{\n" +
                        "    \"body\": \"S3. Pellentesque eget venenatis elit. Duis eu justo eget augue iaculis fermentum. Sed semper quam laoreet nisi egestas at posuere augue semper.\",\n" +
                        "    \"visibility\": {\n" +
                        "        \"type\": \"role\",\n" +
                        "        \"value\": \"Administrators\"\n" +
                        "    }\n" +
                        "}").
                log().all().when().
                post("/rest/api/2/issue/{key}/comment").then().assertThat().statusCode(201);
    }
}
