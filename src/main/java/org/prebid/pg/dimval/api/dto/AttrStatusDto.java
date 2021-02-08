package org.prebid.pg.dimval.api.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class AttrStatusDto {

    String versionId;

    String accountId;

    String attrId;
}
