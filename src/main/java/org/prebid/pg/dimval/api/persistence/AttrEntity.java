package org.prebid.pg.dimval.api.persistence;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import java.io.Serializable;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@Entity
@Table(name = "attr")
@IdClass(AttrEntity.IdClass.class)
public class AttrEntity {

    @Id
    String versionId;

    @Id
    String accountId;

    @Id
    String attrId;

    @Id
    String attrLinkValue;

    String attrType;

    String attrDisplayName;

    String valueSet;

    String supportsAvails;

    Timestamp updatedAt;

    @Data
    static class IdClass implements Serializable {

        String versionId;

        String accountId;

        String attrId;

        String attrLinkValue;
    }

}
