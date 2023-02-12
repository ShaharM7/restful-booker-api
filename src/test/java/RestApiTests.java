import io.restassured.RestAssured;
import io.restassured.response.Response;
import net.datafaker.Faker;
import org.hamcrest.Matchers;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import payload.*;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;


public class RestApiTests {
    private String token;
    private String authRoute;
    private String bookingRoute;
    private String pingRoute;
    private final Faker faker = new Faker();

    BookingRequestPayload createBookingRequestPayload() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = sdfDate.format(new Date());

        return BookingRequestPayload.builder()
                .firstName("Shahar")
                .lastName("Moshe Refaeli")
                .totalPrice(faker.number().numberBetween(100, 500))
                .depositPaid(true)
                .bookingDates(BookingDates.builder().checkin(currentDate).checkout(currentDate).build())
                .additionalNeeds("Contract From GE :)")
                .build();
    }

    @BeforeMethod
    void whenCreateTokenReturns200() throws IOException {

        Properties properties = new Properties();
        InputStream inputStream = RestApiTests.class.getClassLoader().getResourceAsStream("application.properties");
        properties.load(inputStream);

        RestAssured.baseURI = properties.getProperty("base.uri");
        authRoute = properties.getProperty("base.auth");
        bookingRoute = properties.getProperty("base.booking");
        pingRoute = properties.getProperty("base.ping");

        AuthRequestPayload authRequestPayload = AuthRequestPayload.builder()
                .username("admin").password("password123").build();
        Response response = AuthApi.createToken(authRequestPayload, authRoute);
        token = response.as(AuthResponsePayload.class).getToken();

        assertThat(response.statusCode(), equalTo(SC_OK));
    }

    @Test
    public void createBookingWithValidParams_andGetTheBookingId_thenReturnStatusCode200() {

        BookingRequestPayload bookingRequestPayload = createBookingRequestPayload();
        int id =
                BookingApi.createBooking(bookingRequestPayload, bookingRoute)
                        .as(BookingResponsePayload.class)
                        .getBookingId();
        Response response = BookingApi.getBookingById(id, bookingRoute);

        System.out.println(response.statusCode());
        assertThat(response.statusCode(), equalTo(SC_OK));
    }

    @Test
    public void createBookingWithValidParams_andGetTheBookingId_thenReturnTheSameObject() {
        BookingRequestPayload bookingRequestPayload = createBookingRequestPayload();
        int id =
                BookingApi.createBooking(bookingRequestPayload, bookingRoute)
                        .as(BookingResponsePayload.class)
                        .getBookingId();

        BookingRequestPayload bookingResponsePayload = BookingApi.getBookingById(id, bookingRoute).as(BookingRequestPayload.class);

        System.out.println(bookingResponsePayload);
        assertThat(bookingRequestPayload.equals(bookingResponsePayload), Matchers.is(true));
    }

    @Test
    public void whenUpdateBooking_thenBookingAsUpdatedWithTheProvidedData() {
        BookingRequestPayload bookingRequestPayload = createBookingRequestPayload();
        int id =
                BookingApi.createBooking(bookingRequestPayload, bookingRoute)
                        .as(BookingResponsePayload.class)
                        .getBookingId();

        bookingRequestPayload.setFirstName(faker.name().firstName());
        bookingRequestPayload.setLastName(faker.name().lastName());
        bookingRequestPayload.setTotalPrice(faker.number().numberBetween(100, 500));

        BookingRequestPayload bookingResponsePayload =
                BookingApi.updateBooking(bookingRequestPayload, id, token, bookingRoute)
                        .as(BookingRequestPayload.class);

        System.out.println(bookingRequestPayload);
        assertThat(bookingRequestPayload.equals(bookingResponsePayload), Matchers.is(true));
    }

    @Test
    public void whenDeleteBooking_thenReturnStatusCode201() {
        int id =
                BookingApi.createBooking(createBookingRequestPayload(), bookingRoute)
                        .as(BookingResponsePayload.class)
                        .getBookingId();

        Response response = BookingApi.deleteBooking(id, token, bookingRoute);

        System.out.println(response.statusCode());
        assertThat(response.statusCode(), equalTo(SC_CREATED));
    }
}
