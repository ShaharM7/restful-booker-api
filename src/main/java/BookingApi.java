import io.restassured.http.ContentType;
import io.restassured.response.Response;
import payload.BookingRequestPayload;

import javax.ws.rs.core.MediaType;

import static io.restassured.RestAssured.given;

public class BookingApi {

    public static Response getAllBookingIds(String bookingRoute) {
        return given().when().get(bookingRoute);
    }

    public static Response getBookingIdsByName(String firstName, String lastName, String bookingRoute) {
        return given().param("firstname", firstName)
                .param("lastname", lastName)
                .when()
                .get(bookingRoute);
    }

    public static Response getBookingIdsByDate(String checkin, String checkout, String bookingRoute) {
        return given().param("checkin", checkin).param("checkout", checkout).when().get(bookingRoute);
    }

    public static Response getBookingById(int id, String bookingRoute) {
        return given().accept(MediaType.APPLICATION_JSON).when().get(bookingRoute + id);
    }

    public static Response createBooking(BookingRequestPayload bookingRequestPayload, String bookingRoute) {
        return given().contentType(ContentType.JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(bookingRequestPayload)
                .when()
                .post(bookingRoute);
    }

    public static Response updateBooking(
            BookingRequestPayload bookingRequestPayload, int id, String authToken, String bookingRoute) {
        return given().contentType(ContentType.JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("Cookie", "token=" + authToken)
                .body(bookingRequestPayload)
                .when()
                .put(bookingRoute + id);
    }

    public static Response partialUpdateBooking(
            BookingRequestPayload bookingRequestPayload, int id, String authToken, String bookingRoute) {
        return given().contentType(ContentType.JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("Cookie", "token=" + authToken)
                .body(bookingRequestPayload)
                .when()
                .patch(bookingRoute + id);
    }

    public static Response deleteBooking(int id, String authToken, String bookingRoute) {
        return given().header("Cookie", "token=" + authToken).when().delete(bookingRoute + id);
    }
}
