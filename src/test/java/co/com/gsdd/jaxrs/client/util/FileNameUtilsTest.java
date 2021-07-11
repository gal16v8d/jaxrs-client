package co.com.gsdd.jaxrs.client.util;

import java.net.URI;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import co.com.gsdd.jaxrs.client.exception.HTTPClientException;

class FileNameUtilsTest {

    @Test
    void replaceIllegalCharsTest() {
        URI uri = FileNameUtils.removeUnsupportedCharactersFromPath(
                "http://localhost//downloads///videos/MediaFolder[This is a sample(720p)]");
        Assertions.assertEquals("http://localhost/downloads/videos/MediaFolder%5BThis%20is%20a%20sample(720p)%5D",
                uri.toString());
    }

    @ParameterizedTest
    @NullSource
    @EmptySource
    @ValueSource(strings = { "   " })
    void invalidUriShouldLaunchHttpClientExcTest(String uri) {
        HTTPClientException e = Assertions.assertThrows(HTTPClientException.class,
                () -> FileNameUtils.removeUnsupportedCharactersFromPath(uri));
        Assertions.assertEquals("The requested uri -> '" + uri + "' is not valid.", e.getMessage());
    }
}
