package co.com.gsdd.jaxrs.client.util;

import java.net.URI;

import javax.ws.rs.core.UriBuilder;

import org.apache.commons.lang3.StringUtils;

import co.com.gsdd.jaxrs.client.exception.HTTPClientException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FileNameUtils {

    public static final String REGEX_URI_DOUBLE_SLASH = "(?<!(http:|https:))[//]+";
    public static final String SLASH = "/";

    /**
     * It allows to replace all not compliant uri characters, for build a correct uri.
     * 
     * @param path
     *            the path to morph to uri.
     * @return space cleared uri.
     */
    public static URI removeUnsupportedCharactersFromPath(String path) {
        if (StringUtils.isBlank(path)) {
            throw new HTTPClientException("The requested uri -> '" + path + "' is not valid.");
        }
        String normalizedPath = path.replaceAll(REGEX_URI_DOUBLE_SLASH, SLASH);
        return UriBuilder.fromPath(normalizedPath).build();
    }
}
