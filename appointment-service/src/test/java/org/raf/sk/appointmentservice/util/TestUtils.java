package org.raf.sk.appointmentservice.util;

import org.raf.sk.appointmentservice.service.Response;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public final class TestUtils {

    public static <U, V> void assertResponse(Response<U> response, int expectedStatusCode, String expectedMessage, V expectedData) {
        assertThat(response.getStatusCode()).isEqualTo(expectedStatusCode);
        assertThat(response.getMessage()).isEqualTo(expectedMessage);
        assertThat(response.getData()).isEqualTo(expectedData);
    }

}
