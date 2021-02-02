import io.restassured.RestAssured;
import io.restassured.filter.session.SessionFilter;
import io.restassured.path.json.JsonPath;
import org.testng.Assert;

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

        String expectedMessage = "Expected API message" ;

        String addComment = given().pathParam("key","10001").
                header("Content-Type","application/json").
                filter(sessionFilter).
                body("{\n" +
                        "    \"body\": \""+expectedMessage+"\",\n" +
                        "    \"visibility\": {\n" +
                        "        \"type\": \"role\",\n" +
                        "        \"value\": \"Administrators\"\n" +
                        "    }\n" +
                        "}").
                log().all().when().
                post("/rest/api/2/issue/{key}/comment").then().assertThat().statusCode(201).
                extract().response().asString();

        //get comment id

        JsonPath js = new JsonPath(addComment);
        String id = js.getString("id");

        // Add attachment
        given().header("X-Atlassian-Token", "no-check").filter(sessionFilter).
                pathParam("key","10001").
                header("Content-Type","multipart/form-data").
                multiPart("file", new File("Jira.txt")).when().
                post("rest/api/2/issue/{key}/attachments").then().log().all().
                assertThat().statusCode(200);


        //Get Issue

        String getResponse = given().filter(sessionFilter).pathParam("key", "10001").log().all().
                when().get("rest/api/2/issue/{key}").then().assertThat().statusCode(200).
                extract().response().asString();
        System.out.println(getResponse);

        // verify the comment passed through request is posted correctly

        JsonPath js1 = new JsonPath(getResponse) ;
        int commentsNumber = js1.getInt("fields.comment.comments.size()") ;

        for (int i=0; i<commentsNumber; ++i) {

            if(js1.getString("fields.comment.comments["+i+"].id").toString().equalsIgnoreCase(id)) {
                String actualMessage = js1.getString("fields.comment.comments["+i+"].body").toString();
                Assert.assertEquals(actualMessage ,expectedMessage);

            }

        }
}

}
