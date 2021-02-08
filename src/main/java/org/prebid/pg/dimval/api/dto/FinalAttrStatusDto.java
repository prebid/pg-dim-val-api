package org.prebid.pg.dimval.api.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@Builder
@ToString
public class FinalAttrStatusDto {

    Instant timestamp;

    Integer status;

    String error;

    String message;

    String path;

    List<AttrStatusDto> attrStatusDtoList;

}
