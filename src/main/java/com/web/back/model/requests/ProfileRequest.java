package com.web.back.model.requests;

import java.util.List;

public record ProfileRequest(Integer profileId, String description, List<String> permissionKeys) {
}
