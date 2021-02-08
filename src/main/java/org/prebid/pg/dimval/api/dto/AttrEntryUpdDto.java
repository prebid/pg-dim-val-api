package org.prebid.pg.dimval.api.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AttrEntryUpdDto {

    String action;

    AttrEntryDto attribute;

}
