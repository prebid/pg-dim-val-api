package org.prebid.pg.dimval.api.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.sql.Timestamp;

@Getter
@Setter
@Builder
@ToString
public class AttrEntryDto {

    String accountId;

    String attrId;

    String attrType;

    String attrDisplayName;

    Integer valueCount;

    String value;

    String display;

    String pos;

    Timestamp updatedAt;

}
