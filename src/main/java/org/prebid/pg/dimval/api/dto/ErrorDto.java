package org.prebid.pg.dimval.api.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ErrorDto {

    String code;

    String message;

}
