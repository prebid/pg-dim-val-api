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
@Table(name = "attr_version")
@IdClass(AttrVersion.IdClass.class)
public class AttrVersion {

    @Id
    String accountId;

    @Id
    String attrId;

    String attrType;

    String versionId;

    String attrDisplayName;

    String supportsAvails;

    Timestamp updatedAt;

    @Data
    static class IdClass implements Serializable {

        String accountId;

        String attrId;

    }

}
