package org.prebid.pg.dimval.api.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class AttrUpdDto {

    List<AttrEntryUpdDto> attributeUpdates;
}
