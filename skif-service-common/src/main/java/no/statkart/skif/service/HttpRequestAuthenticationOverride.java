package no.statkart.skif.service;

import java.util.List;
import java.util.Map;

public interface HttpRequestAuthenticationOverride {
    Map<String, List<String>> getHttpHeaders();
}
