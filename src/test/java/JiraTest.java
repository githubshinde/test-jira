import io.restassured.RestAssured;
import io.restassured.filter.session.SessionFilter;

import java.io.File;

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
                        "    \"body\": \"New Comment.\",\n" +
                        "    \"visibility\": {\n" +
                        "        \"type\": \"role\",\n" +
                        "        \"value\": \"Administrators\"\n" +
                        "    }\n" +
                        "}").
                log().all().when().
                post("/rest/api/2/issue/{key}/comment").then().assertThat().statusCode(201);

        // Add attachment
        given().header("X-Atlassian-Token", "no-check").filter(sessionFilter).
                header("Content-Type","multipart/form-data").
                multiPart("file", new File("Jira.txt")).when().
                post("rest/api/2/issue/10001/attachments").then().log().all().
                assertThat().statusCode(200);


    }
}
