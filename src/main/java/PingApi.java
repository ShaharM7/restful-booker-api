import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class PingApi {
    public static Response healthCheck(String pingRoute) {
        return given().when().get(pingRoute);
    }
}
