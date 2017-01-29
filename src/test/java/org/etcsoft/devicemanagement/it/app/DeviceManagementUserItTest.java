package org.etcsoft.devicemanagement.it.app;

import org.eclipse.jetty.http.HttpStatus;
import org.junit.*;
import org.junit.rules.ExpectedException;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static java.lang.String.format;

public class DeviceManagementUserItTest extends DeviceManagementAbstract {
    @Rule
    public final ExpectedException thrownExpected = ExpectedException.none();

    @Test
    public void whenPostSimpleUser_thenOkUserStored() {
        given()
                .log().ifValidationFails()
                .log().all()
                .auth().preemptive().oauth2(DeviceManagementTestSuiteit.token)
                .header("Content-Type", JSON)
                .body("{\"email\":\"test@test.com\", \"username\":\"whenPostSimpleUser\", \"password\":\"123\"}")
            .when()
                .post(format("%s%s", DeviceManagementTestSuiteit.uriPath, "deviceManagement/users"))
            .then()
                .contentType(JSON)
                .statusCode(HttpStatus.OK_200);
    }

    @Test
    public void whenPostSimpleUserWithExistingUsername_thenExceptionUserAlreadyExists() {
        given()
                .log().ifValidationFails()
                .log().all()
                .auth().preemptive().oauth2(DeviceManagementTestSuiteit.token)
                .header("Content-Type", JSON)
                .body("{" +
                        "\"email\":\"test@test.com\", " +
                        "\"username\":\"whenPostSimpleUserWithExistingUsername\", " +
                        "\"password\":\"123\"}")
            .when()
                .post(format("%s%s", DeviceManagementTestSuiteit.uriPath, "deviceManagement/users"))
            .then()
                .contentType(JSON)
                .statusCode(HttpStatus.OK_200);
    }
}
