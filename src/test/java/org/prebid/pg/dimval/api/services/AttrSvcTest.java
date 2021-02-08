package org.prebid.pg.dimval.api.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.FileInputStream;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.prebid.pg.dimval.api.AppExc;
import org.prebid.pg.dimval.api.Constants;
import org.prebid.pg.dimval.api.config.app.AttrDataConfig;
import org.prebid.pg.dimval.api.dto.AttrEntryDto;
import org.prebid.pg.dimval.api.dto.AttrEntryDtoV2;
import org.prebid.pg.dimval.api.persistence.AttrEntity;
import org.prebid.pg.dimval.api.persistence.AttrVersion;
import org.prebid.pg.dimval.api.repo.AttrRepo;
import org.prebid.pg.dimval.api.repo.AttrVersionRepo;
import org.springframework.mock.web.MockMultipartFile;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AttrSvcTest {

    SoftAssertions softAssertions;

    private ObjectMapper objectMapper;

    @Mock
    private AttrRepo attrRepo;

    @Mock
    private AttrVersionRepo attrVersionRepo;

    private AttrDataConfig attrDataConfig;

    private AttrSvc attrSvc;

    private String attrId = "attrId";
    private String accountId = "accountId";
    private String attrType = "attrType";
    private String attrLinkValue = "attrLinkValue";
    private String versionId = "versionId";
    private String attrDisplayName = "attrDisplayName";
    private String valueSet = "[{\"value\": \"7287\", \"display\": \"Gibraltar,Gibraltar\"}]";
    private Instant now = Instant.now();

    private Map<String, String> linkedAttrIdToParentMap = new HashMap<>();


    @BeforeEach
    public void setup() {
        softAssertions = new SoftAssertions();
        objectMapper = new ObjectMapper();

        attrDataConfig = new AttrDataConfig();
        AttrDataConfig.AttrTreeLink attrTreeLink = new AttrDataConfig.AttrTreeLink();
        attrTreeLink.setLeaf("device.geo.ext.netacuity.metro");
        attrTreeLink.setParent("device.geo.ext.netacuity.country");
        List<AttrDataConfig.AttrTreeLink> links = new ArrayList<>();
        links.add(attrTreeLink);
        attrDataConfig.setAttrTreeLinks(links);

        attrSvc = new AttrSvc(objectMapper, attrRepo, attrVersionRepo, attrDataConfig);
    }

    @Test
    void shouldReturnAllAvailableAttributes() {
        int count = 3;
        List<AttrVersion> attrVersions = new ArrayList<>();
        Instant now = Instant.now();
        for (int i=0; i<count; i++) {
            AttrVersion attrVersion = new AttrVersion();
            attrVersion.setAccountId(accountId + i);
            attrVersion.setAttrDisplayName(attrDisplayName + i);
            attrVersion.setAttrId(attrId + i);
            attrVersion.setAttrType(attrType + i);
            attrVersion.setVersionId(versionId + i);
            attrVersion.setUpdatedAt(Timestamp.from(now));
            attrVersions.add(attrVersion);
        }

        when(attrVersionRepo.getAllAttrList()).thenReturn(attrVersions);

        List<AttrEntryDto> attrEntryDtos = attrSvc.getAllAttributes(null);

        softAssertions.assertThat(attrEntryDtos).hasSize(count);

        for (int i=0; i<count; i++) {
            softAssertions.assertThat(attrEntryDtos.get(i))
                    .hasFieldOrPropertyWithValue("accountId", accountId + i)
                    .hasFieldOrPropertyWithValue("attrId", attrId + i)
                    .hasFieldOrPropertyWithValue("attrType", attrType + i)
                    .hasFieldOrPropertyWithValue("attrDisplayName", attrDisplayName + i)
                    .hasFieldOrPropertyWithValue("valueCount", 0)
                    .hasFieldOrPropertyWithValue("value", null)
                    .hasFieldOrPropertyWithValue("display", null)
                    .hasFieldOrPropertyWithValue("pos", null)
                    .hasFieldOrPropertyWithValue("updatedAt", Timestamp.from(now));
        }

        softAssertions.assertAll();
    }

    @Test
    void shouldReturnAllAvailableSupportingPGAvailsAttributes() {
        int count = 3;
        List<AttrVersion> attrVersions = new ArrayList<>();
        Instant now = Instant.now();
        for (int i=0; i<count; i++) {
            AttrVersion attrVersion = new AttrVersion();
            attrVersion.setAccountId(accountId + i);
            attrVersion.setAttrDisplayName(attrDisplayName + i);
            attrVersion.setAttrId(attrId + i);
            attrVersion.setAttrType(attrType + i);
            attrVersion.setVersionId(versionId + i);
            attrVersion.setUpdatedAt(Timestamp.from(now));
            attrVersion.setSupportsAvails("Y");
            attrVersions.add(attrVersion);
        }

        for (int i=count; i<count+2; i++) {
            AttrVersion attrVersion = new AttrVersion();
            attrVersion.setAccountId(accountId + i);
            attrVersion.setAttrDisplayName(attrDisplayName + i);
            attrVersion.setAttrId(attrId + i);
            attrVersion.setAttrType(attrType + i);
            attrVersion.setVersionId(versionId + i);
            attrVersion.setUpdatedAt(Timestamp.from(now));
            attrVersion.setSupportsAvails("N");
            attrVersions.add(attrVersion);
        }

        when(attrVersionRepo.getAllAttrList()).thenReturn(attrVersions);

        List<AttrEntryDto> attrEntryDtos = attrSvc.getAllAttributes(Boolean.TRUE);

        softAssertions.assertThat(attrEntryDtos).hasSize(count);

        for (int i=0; i<count; i++) {
            softAssertions.assertThat(attrEntryDtos.get(i))
                    .hasFieldOrPropertyWithValue("accountId", accountId + i)
                    .hasFieldOrPropertyWithValue("attrId", attrId + i)
                    .hasFieldOrPropertyWithValue("attrType", attrType + i)
                    .hasFieldOrPropertyWithValue("attrDisplayName", attrDisplayName + i)
                    .hasFieldOrPropertyWithValue("valueCount", 0)
                    .hasFieldOrPropertyWithValue("value", null)
                    .hasFieldOrPropertyWithValue("display", null)
                    .hasFieldOrPropertyWithValue("pos", null)
                    .hasFieldOrPropertyWithValue("updatedAt", Timestamp.from(now));
        }

        softAssertions.assertAll();
    }

    @Test
    void shouldReturnAllAvailableSupportingPGAvailsAttributesV2() {
        int count = 3;
        List<AttrVersion> attrVersions = new ArrayList<>();
        Instant now = Instant.now();
        for (int i=0; i<count; i++) {
            AttrVersion attrVersion = new AttrVersion();
            attrVersion.setAccountId(accountId + i);
            attrVersion.setAttrDisplayName(attrDisplayName + i);
            attrVersion.setAttrId(attrId + i);
            attrVersion.setAttrType(attrType + i);
            attrVersion.setVersionId(versionId + i);
            attrVersion.setUpdatedAt(Timestamp.from(now));
            attrVersion.setSupportsAvails("Y");
            attrVersions.add(attrVersion);
        }

        for (int i=count; i<count+2; i++) {
            AttrVersion attrVersion = new AttrVersion();
            attrVersion.setAccountId(accountId + i);
            attrVersion.setAttrDisplayName(attrDisplayName + i);
            attrVersion.setAttrId(attrId + i);
            attrVersion.setAttrType(attrType + i);
            attrVersion.setVersionId(versionId + i);
            attrVersion.setUpdatedAt(Timestamp.from(now));
            attrVersion.setSupportsAvails("N");
            attrVersions.add(attrVersion);
        }

        when(attrVersionRepo.getAllAttrList()).thenReturn(attrVersions);

        List<AttrEntryDtoV2> attrEntryDtos = attrSvc.getAllAttributesV2(Boolean.TRUE);

        softAssertions.assertThat(attrEntryDtos).hasSize(count);

        for (int i=0; i<count; i++) {
            softAssertions.assertThat(attrEntryDtos.get(i))
                    .hasFieldOrPropertyWithValue("accountId", accountId + i)
                    .hasFieldOrPropertyWithValue("attrId", attrId + i)
                    .hasFieldOrPropertyWithValue("attrType", attrType + i)
                    .hasFieldOrPropertyWithValue("attrDisplayName", attrDisplayName + i)
                    .hasFieldOrPropertyWithValue("valueCount", 0)
                    .hasFieldOrPropertyWithValue("value", null)
                    .hasFieldOrPropertyWithValue("display", null)
                    .hasFieldOrPropertyWithValue("pos", null)
                    .hasFieldOrPropertyWithValue("availsSupported", Boolean.TRUE)
                    .hasFieldOrPropertyWithValue("updatedAt", Timestamp.from(now));
        }

        softAssertions.assertAll();
    }

    @Test
    void shouldReturnAvailableAttributesForAnAccount() {
        List<AttrVersion> commonAttrVersions = new ArrayList<>();
        Instant now = Instant.now();
        for (int i=0; i<2; i++) {
            AttrVersion attrVersion = new AttrVersion();
            attrVersion.setAccountId(Constants.BASE_ACCOUNT_ID);
            attrVersion.setAttrDisplayName(attrDisplayName + i);
            attrVersion.setAttrId(attrId + i);
            attrVersion.setAttrType(attrType + i);
            attrVersion.setVersionId(versionId + i);
            attrVersion.setUpdatedAt(Timestamp.from(now));
            commonAttrVersions.add(attrVersion);
        }

        List<AttrVersion> attrVersions = new ArrayList<>();
        for (int i=2; i<5; i++) {
            AttrVersion attrVersion = new AttrVersion();
            attrVersion.setAccountId(accountId);
            attrVersion.setAttrDisplayName(attrDisplayName + i);
            attrVersion.setAttrId(attrId + i);
            attrVersion.setAttrType(attrType + i);
            attrVersion.setVersionId(versionId + i);
            attrVersion.setUpdatedAt(Timestamp.from(now));
            attrVersions.add(attrVersion);
        }

        lenient().when(attrVersionRepo.getAttrList(Constants.BASE_ACCOUNT_ID)).thenReturn(commonAttrVersions);
        lenient().when(attrVersionRepo.getAttrList(accountId)).thenReturn(attrVersions);

        List<AttrEntryDto> attrEntryDtos = attrSvc.getAccountAttributes(accountId, null);

        softAssertions.assertThat(attrEntryDtos).hasSize(5);

        softAssertions.assertAll();
    }

    @Test
    void shouldReturnSupportingPGAvailsAttributesForAnAccountV2() {
        List<AttrVersion> commonAttrVersions = new ArrayList<>();
        Instant now = Instant.now();
        for (int i=0; i<2; i++) {
            AttrVersion attrVersion = new AttrVersion();
            attrVersion.setAccountId(Constants.BASE_ACCOUNT_ID);
            attrVersion.setAttrDisplayName(attrDisplayName + i);
            attrVersion.setAttrId(attrId + i);
            attrVersion.setAttrType(attrType + i);
            attrVersion.setVersionId(versionId + i);
            attrVersion.setUpdatedAt(Timestamp.from(now));
            attrVersion.setSupportsAvails("N");
            commonAttrVersions.add(attrVersion);
        }

        List<AttrVersion> attrVersions = new ArrayList<>();
        for (int i=2; i<5; i++) {
            AttrVersion attrVersion = new AttrVersion();
            attrVersion.setAccountId(accountId);
            attrVersion.setAttrDisplayName(attrDisplayName + i);
            attrVersion.setAttrId(attrId + i);
            attrVersion.setAttrType(attrType + i);
            attrVersion.setVersionId(versionId + i);
            attrVersion.setUpdatedAt(Timestamp.from(now));
            attrVersion.setSupportsAvails("Y");
            attrVersions.add(attrVersion);
        }

        lenient().when(attrVersionRepo.getAttrList(Constants.BASE_ACCOUNT_ID)).thenReturn(commonAttrVersions);
        lenient().when(attrVersionRepo.getAttrList(accountId)).thenReturn(attrVersions);

        List<AttrEntryDto> attrEntryDtos = attrSvc.getAccountAttributes(accountId, Boolean.TRUE);

        softAssertions.assertThat(attrEntryDtos).hasSize(0);

        softAssertions.assertAll();
    }

    @Test
    void shouldGetAttributeValues() throws Exception {
        AttrVersion attrVersion = new AttrVersion();
        attrVersion.setAccountId(accountId);//Constants.BASE_ACCOUNT_ID);
        attrVersion.setAttrDisplayName(attrDisplayName);
        attrVersion.setAttrId(attrId);
        attrVersion.setAttrType(attrType);
        attrVersion.setVersionId(versionId);
        attrVersion.setUpdatedAt(Timestamp.from(now));
        List<AttrVersion> attrVersionList = new ArrayList<>();
        attrVersionList.add(attrVersion);

        AttrEntity attrEntity = new AttrEntity();
        attrEntity.setValueSet(valueSet);
        attrEntity.setAccountId(accountId);
        attrEntity.setAttrDisplayName(attrDisplayName);
        attrEntity.setAttrId(attrId);
        attrEntity.setVersionId(versionId);

        lenient().when(attrVersionRepo.getVersionForAttrInAccountRegexp(attrId, accountId)).thenReturn(null);
        lenient().when(attrVersionRepo.getVersionForAttrInAccountRegexp(attrId, Constants.BASE_ACCOUNT_ID)).thenReturn(attrVersionList);
        when(attrRepo.getAttrValues(Constants.BASE_ACCOUNT_ID, attrVersion.getVersionId(), attrVersion.getAttrId())).thenReturn(attrEntity);

        List<AttrEntryDto> getAttributeValues = attrSvc.getAttributeValues(attrId, accountId, null);

        softAssertions.assertThat(getAttributeValues).hasSize(1);
        softAssertions.assertAll();

        verify(attrVersionRepo, times(1)).getVersionForAttrInAccountRegexp(attrId, accountId);
        verify(attrVersionRepo, times(1)).getVersionForAttrInAccountRegexp(attrId, Constants.BASE_ACCOUNT_ID);
        verify(attrRepo, times(1)).getAttrValues(Constants.BASE_ACCOUNT_ID, attrVersion.getVersionId(), attrVersion.getAttrId());
    }

    @Test
    void shouldGetAttributeValuesV2() throws Exception {
        AttrVersion attrVersion = new AttrVersion();
        attrVersion.setAccountId(accountId);
        attrVersion.setAttrDisplayName(attrDisplayName);
        attrVersion.setAttrId(attrId);
        attrVersion.setAttrType(attrType);
        attrVersion.setVersionId(versionId);
        attrVersion.setUpdatedAt(Timestamp.from(now));
        List<AttrVersion> attrVersionList = new ArrayList<>();
        attrVersionList.add(attrVersion);

        AttrEntity attrEntity = new AttrEntity();
        attrEntity.setValueSet(valueSet);
        attrEntity.setAccountId(accountId);
        attrEntity.setAttrDisplayName(attrDisplayName);
        attrEntity.setAttrId(attrId);
        attrEntity.setVersionId(versionId);

        lenient().when(attrVersionRepo.getVersionForAttrInAccountRegexp(attrId, accountId)).thenReturn(null);
        lenient().when(attrVersionRepo.getVersionForAttrInAccountRegexp(attrId, Constants.BASE_ACCOUNT_ID)).thenReturn(attrVersionList);
        when(attrRepo.getAttrValues(Constants.BASE_ACCOUNT_ID, attrVersion.getVersionId(), attrVersion.getAttrId())).thenReturn(attrEntity);

        List<AttrEntryDtoV2> attrEntryDtoV2List = attrSvc.getAttributeValuesV2(attrId, accountId, Boolean.TRUE);

        softAssertions.assertThat(attrEntryDtoV2List).hasSize(0);
        softAssertions.assertAll();

        verify(attrVersionRepo, times(1)).getVersionForAttrInAccountRegexp(attrId, accountId);
        verify(attrVersionRepo, times(1)).getVersionForAttrInAccountRegexp(attrId, Constants.BASE_ACCOUNT_ID);
        verify(attrRepo, times(1)).getAttrValues(Constants.BASE_ACCOUNT_ID, attrVersion.getVersionId(), attrVersion.getAttrId());
    }

    @Test
    void shouldNotThrowExceptionOnAttrNotFoundInGetAttributeValues() throws Exception {
        List<AttrVersion> attrVersionList = new ArrayList<>();

        lenient().when(attrVersionRepo.getVersionForAttrInAccountRegexp(attrId, accountId)).thenReturn(null);
        lenient().when(attrVersionRepo.getVersionForAttrInAccountRegexp(attrId, Constants.BASE_ACCOUNT_ID)).thenReturn(null);

        softAssertions.assertThatCode(() -> attrSvc.getAttributeValues(attrId, accountId, null)).doesNotThrowAnyException();
        softAssertions.assertThat(attrSvc.getAttributeValues(attrId, accountId, null)).isInstanceOf(ArrayList.class);
        softAssertions.assertThat(attrSvc.getAttributeValues(attrId, accountId, null)).hasSize(0);

        softAssertions.assertAll();

        verify(attrRepo, times(0)).getAttrValues(any(), any(), any());
    }

    @Test
    void shouldHandleRegexOnGetAttributeValues1() throws Exception {
        String attrId = "*";
        String updAttrId = "." + attrId;

        List<AttrVersion> attrVersionList = new ArrayList<>();

        AttrVersion attrVersion1 = new AttrVersion();
        attrVersion1.setAttrId("attrId1");
        attrVersion1.setVersionId("1");
        attrVersionList.add(attrVersion1);

        AttrVersion attrVersion2 = new AttrVersion();
        attrVersion2.setAttrId("attrId2");
        attrVersion2.setVersionId("1");
        attrVersionList.add(attrVersion2);

        lenient().when(attrVersionRepo.getVersionForAttrInAccountRegexp(updAttrId, accountId)).thenReturn(attrVersionList);

        softAssertions
                .assertThatCode(() -> attrSvc.getAttributeValues(attrId, accountId, null))
                .doesNotThrowAnyException();
        softAssertions.assertAll();

        verify(attrVersionRepo, times(1)).getVersionForAttrInAccountRegexp(updAttrId, accountId);
        verify(attrRepo, times(1)).getAttrValues(
                accountId, attrVersionList.get(0).getVersionId(), attrVersionList.get(0).getAttrId()
        );
        verify(attrRepo, times(1)).getAttrValues(
                accountId, attrVersionList.get(1).getVersionId(), attrVersionList.get(1).getAttrId()
        );
    }

    @Test
    void shouldHandleRegexOnGetAttributeValues2() throws Exception {
        String attrId = "*SA";
        String updAttrId = "SA";

        List<AttrVersion> attrVersionList = new ArrayList<>();

        AttrVersion attrVersion1 = new AttrVersion();
        attrVersion1.setAttrId("SA1");
        attrVersion1.setVersionId("1");
        attrVersionList.add(attrVersion1);

        AttrVersion attrVersion2 = new AttrVersion();
        attrVersion2.setAttrId("SA2");
        attrVersion2.setVersionId("1");
        attrVersionList.add(attrVersion2);

        lenient().when(attrVersionRepo.getVersionForAttrInAccountRegexp(updAttrId, accountId)).thenReturn(attrVersionList);

        softAssertions
                .assertThatCode(() -> attrSvc.getAttributeValues(attrId, accountId, null))
                .doesNotThrowAnyException();
        softAssertions.assertAll();

        verify(attrVersionRepo, times(1)).getVersionForAttrInAccountRegexp(updAttrId, accountId);
        verify(attrRepo, times(1)).getAttrValues(
                accountId, attrVersionList.get(0).getVersionId(), attrVersionList.get(0).getAttrId()
        );
        verify(attrRepo, times(1)).getAttrValues(
                accountId, attrVersionList.get(1).getVersionId(), attrVersionList.get(1).getAttrId()
        );
    }

    @Test
    void shouldHandleRegexOnGetAttributeValues3() throws Exception {
        String attrId = "?SA";
        String updAttrId = "SA";

        List<AttrVersion> attrVersionList = new ArrayList<>();

        AttrVersion attrVersion1 = new AttrVersion();
        attrVersion1.setAttrId("SA1");
        attrVersion1.setVersionId("1");
        attrVersionList.add(attrVersion1);

        AttrVersion attrVersion2 = new AttrVersion();
        attrVersion2.setAttrId("SA2");
        attrVersion2.setVersionId("1");
        attrVersionList.add(attrVersion2);

        lenient().when(attrVersionRepo.getVersionForAttrInAccountRegexp(updAttrId, accountId)).thenReturn(attrVersionList);

        softAssertions
                .assertThatCode(() -> attrSvc.getAttributeValues(attrId, accountId, null))
                .doesNotThrowAnyException();
        softAssertions.assertAll();

        verify(attrVersionRepo, times(1)).getVersionForAttrInAccountRegexp(updAttrId, accountId);
        verify(attrRepo, times(1)).getAttrValues(
                accountId, attrVersionList.get(0).getVersionId(), attrVersionList.get(0).getAttrId()
        );
        verify(attrRepo, times(1)).getAttrValues(
                accountId, attrVersionList.get(1).getVersionId(), attrVersionList.get(1).getAttrId()
        );
    }

    @Test
    void shouldNotThrowGetAttributeValuesForInvalidJSON() throws Exception {
        String attrId = "?SA";
        String updAttrId = "SA";

        List<AttrVersion> attrVersionList = new ArrayList<>();

        AttrVersion attrVersion1 = new AttrVersion();
        attrVersion1.setAttrId("SA1");
        attrVersion1.setVersionId("1");
        attrVersionList.add(attrVersion1);

        AttrVersion attrVersion2 = new AttrVersion();
        attrVersion2.setAttrId("SA2");
        attrVersion2.setVersionId("1");
        attrVersionList.add(attrVersion2);

        AttrEntity attrEntity = new AttrEntity();
        attrEntity.setValueSet("valueSet");
        attrEntity.setAccountId(accountId);
        attrEntity.setAttrDisplayName(attrDisplayName);
        attrEntity.setAttrId(attrId);
        attrEntity.setVersionId(versionId);

        lenient().when(attrVersionRepo.getVersionForAttrInAccountRegexp(updAttrId, accountId)).thenReturn(attrVersionList);
        lenient().when(attrRepo.getAttrValues(accountId, attrVersionList.get(0).getVersionId(), attrVersionList.get(0).getAttrId())).thenReturn(attrEntity);

        softAssertions.assertThatCode(() -> attrSvc.getAttributeValues(attrId, accountId, null)).doesNotThrowAnyException();

        softAssertions.assertAll();

        verify(attrVersionRepo, times(1)).getVersionForAttrInAccountRegexp(updAttrId, accountId);
        verify(attrRepo, times(1)).getAttrValues(
                accountId, attrVersionList.get(0).getVersionId(), attrVersionList.get(0).getAttrId()
        );
        verify(attrRepo, times(1)).getAttrValues(
                accountId, attrVersionList.get(1).getVersionId(), attrVersionList.get(1).getAttrId()
        );
    }

    @Test
    void shouldNotThrowGetAttributeValuesForNullJSON() throws Exception {
        String attrId = "?SA";
        String updAttrId = "SA";

        List<AttrVersion> attrVersionList = new ArrayList<>();

        AttrVersion attrVersion1 = new AttrVersion();
        attrVersion1.setAttrId("SA1");
        attrVersion1.setVersionId("1");
        attrVersionList.add(attrVersion1);

        AttrVersion attrVersion2 = new AttrVersion();
        attrVersion2.setAttrId("SA2");
        attrVersion2.setVersionId("1");
        attrVersionList.add(attrVersion2);

        AttrEntity attrEntity = new AttrEntity();
        attrEntity.setValueSet(null);
        attrEntity.setAccountId(accountId);
        attrEntity.setAttrDisplayName(attrDisplayName);
        attrEntity.setAttrId(attrId);
        attrEntity.setVersionId(versionId);

        lenient().when(attrVersionRepo.getVersionForAttrInAccountRegexp(updAttrId, accountId)).thenReturn(attrVersionList);
        lenient().when(attrRepo.getAttrValues(accountId, attrVersionList.get(0).getVersionId(), attrVersionList.get(0).getAttrId())).thenReturn(attrEntity);

        softAssertions.assertThatCode(() -> attrSvc.getAttributeValues(attrId, accountId, null)).doesNotThrowAnyException();

        softAssertions.assertAll();

        verify(attrVersionRepo, times(1)).getVersionForAttrInAccountRegexp(updAttrId, accountId);
        verify(attrRepo, times(1)).getAttrValues(
                accountId, attrVersionList.get(0).getVersionId(), attrVersionList.get(0).getAttrId()
        );
        verify(attrRepo, times(1)).getAttrValues(
                accountId, attrVersionList.get(1).getVersionId(), attrVersionList.get(1).getAttrId()
        );
    }

    @Test
    void shouldThrowExceptionOnGetAttributeValuesForLinkedAttrId() throws Exception {
        String attrId = "device.geo.ext.netacuity.metro";
        softAssertions
                .assertThatCode(() -> attrSvc.getAttributeValues(attrId, accountId, null))
                .isInstanceOf(AppExc.class)
                .hasFieldOrPropertyWithValue("code", Constants.INVALID_REQUEST)
                .hasMessage(String.format("attrId=%s requires attrLinkValue", attrId));

        softAssertions.assertAll();

        verify(attrVersionRepo, times(0)).getVersionForAttrInAccountRegexp(any(), any());
        verify(attrRepo, times(0)).getAttrValues(any(), any(), any());
    }


    @Test
    void handleWhenSearchedByAttrValueStringForNonExistingAttrId() {
        String searchStringIn = "Gibral";

        AttrVersion attrVersion = new AttrVersion();
        attrVersion.setAccountId(Constants.BASE_ACCOUNT_ID);
        attrVersion.setAttrDisplayName(attrDisplayName);
        attrVersion.setAttrId(attrId);
        attrVersion.setAttrType(attrType);
        attrVersion.setVersionId(versionId);
        attrVersion.setUpdatedAt(Timestamp.from(now));

        AttrEntity attrEntity = new AttrEntity();
        attrEntity.setValueSet(valueSet);
        attrEntity.setAccountId(accountId);
        attrEntity.setAttrDisplayName(attrDisplayName);
        attrEntity.setAttrId(attrId);
        attrEntity.setVersionId(versionId);
        List<AttrEntity> attrEntityList = new ArrayList<>();
        attrEntityList.add(attrEntity);

        lenient().when(attrVersionRepo.getVersionForAttrInAccountExact(attrId, accountId)).thenReturn(null);
        lenient().when(attrVersionRepo.getVersionForAttrInAccountExact(attrId, Constants.BASE_ACCOUNT_ID)).thenReturn(null);

        verify(attrRepo, times(0)).getAttrBySearchString(Constants.BASE_ACCOUNT_ID, attrVersion.getVersionId(), attrId, "%" + searchStringIn.toUpperCase() + "%");
        verify(attrVersionRepo, times(0)).getVersionForAttrInAccountExact(attrId, accountId);
        verify(attrVersionRepo, times(0)).getVersionForAttrInAccountExact(attrId, Constants.BASE_ACCOUNT_ID);

        softAssertions.assertAll();
    }

    @Test
    void shouldReturnValueWhenSearchedByAttrValueStringAndStartsWith() {
        String searchStringIn = "Gibral";
        String searchType = "startsWith";
        String accountId = Constants.BASE_ACCOUNT_ID;

        AttrVersion attrVersion = new AttrVersion();
        attrVersion.setAccountId(Constants.BASE_ACCOUNT_ID);
        attrVersion.setAttrDisplayName(attrDisplayName);
        attrVersion.setAttrId(attrId);
        attrVersion.setAttrType(attrType);
        attrVersion.setVersionId(versionId);
        attrVersion.setUpdatedAt(Timestamp.from(now));

        AttrEntity attrEntity = new AttrEntity();
        attrEntity.setValueSet(valueSet);
        attrEntity.setAccountId(accountId);
        attrEntity.setAttrDisplayName(attrDisplayName);
        attrEntity.setAttrId(attrId);
        attrEntity.setVersionId(versionId);
        List<AttrEntity> attrEntityList = new ArrayList<>();
        attrEntityList.add(attrEntity);

        lenient().when(attrVersionRepo.getVersionForAttrInAccountExact(attrId, accountId)).thenReturn(attrVersion);
        lenient().when(attrRepo.getAttrBySearchString(accountId, attrVersion.getVersionId(), attrId, "%" + searchStringIn.toUpperCase() + "%")).thenReturn(attrEntityList);

        softAssertions.assertThatCode(
                () -> attrSvc.searchByAttrValueString(attrId, accountId, searchStringIn.toUpperCase(), searchType, null)
        ).doesNotThrowAnyException();

        verify(attrRepo, times(1)).getAttrBySearchString(accountId, attrVersion.getVersionId(), attrId, "%" + searchStringIn.toUpperCase() + "%");
        verify(attrVersionRepo, times(1)).getVersionForAttrInAccountExact(attrId, accountId);

        softAssertions.assertAll();
    }

    @Test
    void shouldNotThrowExceptionWhenSearchedByAttrValueForAttrIdNotFound() {
        String searchStringIn = "Gibral";
        String searchType = "startsWith";

        lenient().when(attrVersionRepo.getVersionForAttrInAccountExact(attrId, accountId)).thenReturn(null);
        lenient().when(attrVersionRepo.getVersionForAttrInAccountExact(attrId, Constants.BASE_ACCOUNT_ID)).thenReturn(null);

        softAssertions.assertThat(attrSvc.searchByAttrValueString(attrId, accountId, searchStringIn.toUpperCase(), searchType, null)).hasSize(0);
        softAssertions.assertAll();

        verify(attrRepo, times(0)).getAttrBySearchString(any(), any(), any(), any());
        verify(attrVersionRepo, times(1)).getVersionForAttrInAccountExact(attrId, accountId);
    }

    @Test
    void shouldReturnValueWhenSearchedByAttrValueStringAndNotFoundInAccount() {
        String searchStringIn = "Gibral";
        String searchType = "startsWith";

        AttrVersion attrVersion = new AttrVersion();
        attrVersion.setAccountId(Constants.BASE_ACCOUNT_ID);
        attrVersion.setAttrDisplayName(attrDisplayName);
        attrVersion.setAttrId(attrId);
        attrVersion.setAttrType(attrType);
        attrVersion.setVersionId(versionId);
        attrVersion.setUpdatedAt(Timestamp.from(now));

        AttrEntity attrEntity = new AttrEntity();
        attrEntity.setValueSet(valueSet);
        attrEntity.setAccountId(accountId);
        attrEntity.setAttrDisplayName(attrDisplayName);
        attrEntity.setAttrId(attrId);
        attrEntity.setVersionId(versionId);
        List<AttrEntity> attrEntityList = new ArrayList<>();
        attrEntityList.add(attrEntity);

        lenient().when(attrVersionRepo.getVersionForAttrInAccountExact(attrId, accountId)).thenReturn(null);
        lenient().when(attrVersionRepo.getVersionForAttrInAccountExact(attrId, Constants.BASE_ACCOUNT_ID)).thenReturn(attrVersion);
        lenient().when(attrRepo.getAttrBySearchString(accountId, attrVersion.getVersionId(), attrId, "%" + searchStringIn.toUpperCase() + "%")).thenReturn(attrEntityList);

        softAssertions.assertThatCode(
                () -> attrSvc.searchByAttrValueString(attrId, accountId, searchStringIn.toUpperCase(), searchType, null)
        ).doesNotThrowAnyException();

        verify(attrRepo, times(1)).getAttrBySearchString(Constants.BASE_ACCOUNT_ID, attrVersion.getVersionId(), attrId, "%" + searchStringIn.toUpperCase() + "%");
        verify(attrVersionRepo, times(1)).getVersionForAttrInAccountExact(attrId, accountId);
        verify(attrVersionRepo, times(1)).getVersionForAttrInAccountExact(attrId, Constants.BASE_ACCOUNT_ID);

        softAssertions.assertAll();
    }

    @Test
    void shouldReturnValueWhenSearchedByAttrValueStringAndContains() {
        String searchStringIn = "bralt";
        String searchType = "contains";

        AttrVersion attrVersion = new AttrVersion();
        attrVersion.setAccountId(Constants.BASE_ACCOUNT_ID);
        attrVersion.setAttrDisplayName(attrDisplayName);
        attrVersion.setAttrId(attrId);
        attrVersion.setAttrType(attrType);
        attrVersion.setVersionId(versionId);
        attrVersion.setUpdatedAt(Timestamp.from(now));

        AttrEntity attrEntity = new AttrEntity();
        attrEntity.setValueSet(valueSet);
        attrEntity.setAccountId(accountId);
        attrEntity.setAttrDisplayName(attrDisplayName);
        attrEntity.setAttrId(attrId);
        attrEntity.setVersionId(versionId);
        List<AttrEntity> attrEntityList = new ArrayList<>();
        attrEntityList.add(attrEntity);

        lenient().when(attrVersionRepo.getVersionForAttrInAccountExact(attrId, accountId)).thenReturn(attrVersion);
        lenient().when(attrRepo.getAttrBySearchString(accountId, attrVersion.getVersionId(), attrId, "%" + searchStringIn.toUpperCase() + "%")).thenReturn(attrEntityList);

        softAssertions.assertThatCode(
                () -> attrSvc.searchByAttrValueString(attrId, accountId, searchStringIn.toUpperCase(), searchType, null)
        ).doesNotThrowAnyException();

        verify(attrRepo, times(1)).getAttrBySearchString(accountId, attrVersion.getVersionId(), attrId, "%" + searchStringIn.toUpperCase() + "%");
        verify(attrVersionRepo, times(1)).getVersionForAttrInAccountExact(attrId, accountId);

        softAssertions.assertAll();
    }

    @Test
    void shouldGetZeroResultsWhenAttributeValuesByLinkIsNotFound1() {
        lenient().when(attrVersionRepo.getVersionForAttrInAccountExact(attrId, accountId)).thenReturn(null);
        lenient().when(attrVersionRepo.getVersionForAttrInAccountExact(attrId, Constants.BASE_ACCOUNT_ID)).thenReturn(null);

        List<AttrEntryDto> attrEntryDtoList = attrSvc.getAttributeValuesByLink(attrId, accountId, attrLinkValue, null);

        softAssertions.assertThat(attrEntryDtoList).hasSize(0);

        verify(attrRepo, times(0)).getAttrValuesByLink(any(), any(), any(), any());

        softAssertions.assertAll();
    }


    @Test
    void shouldGetZeroResultsWhenAttributeValuesByLinkIsNotFound2() {
        AttrVersion attrVersion = new AttrVersion();
        attrVersion.setAccountId(Constants.BASE_ACCOUNT_ID);
        attrVersion.setAttrDisplayName(attrDisplayName);
        attrVersion.setAttrId(attrId);
        attrVersion.setAttrType(attrType);
        attrVersion.setVersionId(versionId);
        attrVersion.setUpdatedAt(Timestamp.from(now));

        lenient().when(attrVersionRepo.getVersionForAttrInAccountExact(attrId, accountId)).thenReturn(null);
        lenient().when(attrVersionRepo.getVersionForAttrInAccountExact(attrId, Constants.BASE_ACCOUNT_ID)).thenReturn(attrVersion);
        lenient().when(attrRepo.getAttrValuesByLink(accountId, attrVersion.getVersionId(), attrVersion.getAttrId(), attrLinkValue)).thenReturn(null);

        List<AttrEntryDto> attrEntryDtoList = attrSvc.getAttributeValuesByLink(attrId, accountId, attrLinkValue, null);

        softAssertions.assertThat(attrEntryDtoList).hasSize(0);

        verify(attrRepo, times(1)).getAttrValuesByLink(any(), any(), any(), any());

        softAssertions.assertAll();
    }

    @Test
    void shouldGetResultsWhenAttributeValuesByLinkIsFoundForBaseAccount() {
        String accountId = Constants.BASE_ACCOUNT_ID;

        AttrVersion attrVersion = new AttrVersion();
        attrVersion.setAccountId(accountId);
        attrVersion.setAttrDisplayName(attrDisplayName);
        attrVersion.setAttrId(attrId);
        attrVersion.setAttrType(attrType);
        attrVersion.setVersionId(versionId);
        attrVersion.setUpdatedAt(Timestamp.from(now));

        AttrEntity attrEntity = new AttrEntity();
        attrEntity.setValueSet(valueSet);
        attrEntity.setAccountId(accountId);
        attrEntity.setAttrDisplayName(attrDisplayName);
        attrEntity.setAttrId(attrId);
        attrEntity.setVersionId(versionId);

        lenient().when(attrVersionRepo.getVersionForAttrInAccountExact(attrId, accountId)).thenReturn(null);
        lenient().when(attrVersionRepo.getVersionForAttrInAccountExact(attrId, Constants.BASE_ACCOUNT_ID)).thenReturn(attrVersion);
        lenient().when(attrRepo.getAttrValuesByLink(accountId, attrVersion.getVersionId(), attrVersion.getAttrId(), attrLinkValue)).thenReturn(attrEntity);

        List<AttrEntryDto> attrEntryDtoList = attrSvc.getAttributeValuesByLink(attrId, accountId, attrLinkValue, null);

        softAssertions.assertThat(attrEntryDtoList).hasSize(1);

        verify(attrVersionRepo, times(1)).getVersionForAttrInAccountExact(attrId, Constants.BASE_ACCOUNT_ID);
        verify(attrRepo, times(1)).getAttrValuesByLink(accountId, attrVersion.getVersionId(), attrVersion.getAttrId(), attrLinkValue);

        softAssertions.assertAll();
    }

    @Test
    void shouldGetResultsWhenAttributeValuesByLinkIsFoundForAccount() {
        AttrVersion attrVersion = new AttrVersion();
        attrVersion.setAccountId(accountId);
        attrVersion.setAttrDisplayName(attrDisplayName);
        attrVersion.setAttrId(attrId);
        attrVersion.setAttrType(attrType);
        attrVersion.setVersionId(versionId);
        attrVersion.setUpdatedAt(Timestamp.from(now));

        AttrEntity attrEntity = new AttrEntity();
        attrEntity.setValueSet(valueSet);
        attrEntity.setAccountId(accountId);
        attrEntity.setAttrDisplayName(attrDisplayName);
        attrEntity.setAttrId(attrId);
        attrEntity.setVersionId(versionId);

        lenient().when(attrVersionRepo.getVersionForAttrInAccountExact(attrId, accountId)).thenReturn(attrVersion);
        lenient().when(attrRepo.getAttrValuesByLink(accountId, attrVersion.getVersionId(), attrVersion.getAttrId(), attrLinkValue)).thenReturn(attrEntity);

        List<AttrEntryDto> attrEntryDtoList = attrSvc.getAttributeValuesByLink(attrId, accountId, attrLinkValue, null);

        softAssertions.assertThat(attrEntryDtoList).hasSize(1);

        verify(attrVersionRepo, times(1)).getVersionForAttrInAccountExact(attrId, accountId);
        verify(attrVersionRepo, times(0)).getVersionForAttrInAccountExact(attrId, Constants.BASE_ACCOUNT_ID);
        verify(attrRepo, times(1)).getAttrValuesByLink(accountId, attrVersion.getVersionId(), attrVersion.getAttrId(), attrLinkValue);

        softAssertions.assertAll();
    }

    @Test
    void shouldNotGetResultsWhenAttributeValuesByLinkIsNotPGAvailsSupportedByAccountV2() {
        AttrVersion attrVersion = new AttrVersion();
        attrVersion.setAccountId(accountId);
        attrVersion.setAttrDisplayName(attrDisplayName);
        attrVersion.setAttrId(attrId);
        attrVersion.setAttrType(attrType);
        attrVersion.setVersionId(versionId);
        attrVersion.setUpdatedAt(Timestamp.from(now));

        AttrEntity attrEntity = new AttrEntity();
        attrEntity.setValueSet(valueSet);
        attrEntity.setAccountId(accountId);
        attrEntity.setAttrDisplayName(attrDisplayName);
        attrEntity.setAttrId(attrId);
        attrEntity.setVersionId(versionId);

        lenient().when(attrVersionRepo.getVersionForAttrInAccountExact(attrId, accountId)).thenReturn(attrVersion);
        lenient().when(attrRepo.getAttrValuesByLink(accountId, attrVersion.getVersionId(), attrVersion.getAttrId(), attrLinkValue)).thenReturn(attrEntity);

        List<AttrEntryDtoV2> attrEntryDtoList = attrSvc.getAttributeValuesByLinkV2(attrId, accountId, attrLinkValue, Boolean.TRUE);

        softAssertions.assertThat(attrEntryDtoList).hasSize(0);

        verify(attrVersionRepo, times(1)).getVersionForAttrInAccountExact(attrId, accountId);
        verify(attrVersionRepo, times(0)).getVersionForAttrInAccountExact(attrId, Constants.BASE_ACCOUNT_ID);
        verify(attrRepo, times(1)).getAttrValuesByLink(accountId, attrVersion.getVersionId(), attrVersion.getAttrId(), attrLinkValue);

        softAssertions.assertAll();
    }


    @Test
    void shouldUploadValidFileBaseAccount() throws Exception{
        String fileName = "v1/upload.csv";
        FileInputStream inputFile = new FileInputStream( "src/test/resources/" + fileName);
        MockMultipartFile file = new MockMultipartFile(fileName, fileName, "multipart/form-data", inputFile);

        lenient().doNothing().when(attrVersionRepo).updateVersion(any(), any(), any(), any(), any(), any());
        lenient().doNothing().when(attrRepo).uploadAttrRow(any(), any(), any(), any(), any(), any(), any());
        lenient().doNothing().when(attrRepo).deleteExpiredVersion(any(), any(), any());

        softAssertions.assertThatCode(() -> attrSvc.uploadFile(file, 7)).doesNotThrowAnyException();

        verify(attrVersionRepo, times(1)).updateVersion(any(), any(), any(), any(), any(), any());
        verify(attrRepo, times(1)).uploadAttrRow(any(), any(), any(), any(), any(), any(), any());
        verify(attrRepo, times(1)).deleteExpiredVersion(any(), any(), any());

        softAssertions.assertAll();
    }

    @Test
    void shouldUploadValidFileSpecificAccount() throws Exception{
        String fileName = "v1/upload_account.csv";
        FileInputStream inputFile = new FileInputStream( "src/test/resources/" + fileName);
        MockMultipartFile file = new MockMultipartFile(fileName, fileName, "multipart/form-data", inputFile);

        lenient().doNothing().when(attrVersionRepo).updateVersion(any(), any(), any(), any(), any(), any());
        lenient().doNothing().when(attrRepo).uploadAttrRow(any(), any(), any(), any(), any(), any(), any());
        lenient().doNothing().when(attrRepo).deleteExpiredVersion(any(), any(), any());

        softAssertions.assertThatCode(() -> attrSvc.uploadFile(file, 7)).doesNotThrowAnyException();

        verify(attrVersionRepo, times(3)).updateVersion(any(), any(), any(), any(), any(), any());
        verify(attrRepo, times(3)).uploadAttrRow(any(), any(), any(), any(), any(), any(), any());
        verify(attrRepo, times(3)).deleteExpiredVersion(any(), any(), any());

        softAssertions.assertAll();
    }

    @Test
    void shouldUploadV2ValidFileBaseAccount() throws Exception{
        String fileName = "v2/upload.csv";
        FileInputStream inputFile = new FileInputStream( "src/test/resources/" + fileName);
        MockMultipartFile file = new MockMultipartFile(fileName, fileName, "multipart/form-data", inputFile);

        lenient().doNothing().when(attrVersionRepo).updateVersion(any(), any(), any(), any(), any(), any());
        lenient().doNothing().when(attrRepo).uploadAttrRow(any(), any(), any(), any(), any(), any(), any());
        lenient().doNothing().when(attrRepo).deleteExpiredVersion(any(), any(), any());

        softAssertions.assertThatCode(() -> attrSvc.uploadFile(file, 8)).doesNotThrowAnyException();

        verify(attrVersionRepo, times(1)).updateVersion(any(), any(), any(), any(), any(), any());
        verify(attrRepo, times(1)).uploadAttrRow(any(), any(), any(), any(), any(), any(), any(), any());
        verify(attrRepo, times(1)).deleteExpiredVersion(any(), any(), any());

        softAssertions.assertAll();
    }

    @Test
    void shouldUploadV2ValidFileSpecificAccount() throws Exception{
        String fileName = "v2/upload_account.csv";
        FileInputStream inputFile = new FileInputStream( "src/test/resources/" + fileName);
        MockMultipartFile file = new MockMultipartFile(fileName, fileName, "multipart/form-data", inputFile);

        lenient().doNothing().when(attrVersionRepo).updateVersion(any(), any(), any(), any(), any(), any());
        lenient().doNothing().when(attrRepo).uploadAttrRow(any(), any(), any(), any(), any(), any(), any(), any());
        lenient().doNothing().when(attrRepo).deleteExpiredVersion(any(), any(), any());

        softAssertions.assertThatCode(() -> attrSvc.uploadFile(file, 8)).doesNotThrowAnyException();

        verify(attrVersionRepo, times(3)).updateVersion(any(), any(), any(), any(), any(), any());
        verify(attrRepo, times(3)).uploadAttrRow(any(), any(), any(), any(), any(), any(), any(), any());
        verify(attrRepo, times(3)).deleteExpiredVersion(any(), any(), any());

        softAssertions.assertAll();
    }

    @Test
    void shouldRejectZeroLineFile() throws Exception {
        String fileName = "upload_empty.csv";
        FileInputStream inputFile = new FileInputStream( "src/test/resources/" + fileName);
        MockMultipartFile file = new MockMultipartFile(fileName, fileName, "multipart/form-data", inputFile);

        lenient().doNothing().when(attrVersionRepo).updateVersion(any(), any(), any(), any(), any(), any());
        lenient().doNothing().when(attrRepo).uploadAttrRow(any(), any(), any(), any(), any(), any(), any());
        lenient().doNothing().when(attrRepo).deleteExpiredVersion(any(), any(), any());

        softAssertions.assertThatCode(() -> attrSvc.uploadFile(file, 7)).isInstanceOf(AppExc.class)
                .hasFieldOrPropertyWithValue("code", Constants.INVALID_REQUEST)
                .hasMessage("Zero lines processed from " + fileName);

        verify(attrVersionRepo, times(0)).updateVersion(any(), any(), any(), any(), any(), any());
        verify(attrRepo, times(0)).uploadAttrRow(any(), any(), any(), any(), any(), any(), any());
        verify(attrRepo, times(0)).deleteExpiredVersion(any(), any(), any());

        softAssertions.assertAll();

        softAssertions.assertThatCode(() -> attrSvc.uploadFile(file, 8)).isInstanceOf(AppExc.class)
                .hasFieldOrPropertyWithValue("code", Constants.INVALID_REQUEST)
                .hasMessage("Zero lines processed from " + fileName);

        verify(attrVersionRepo, times(0)).updateVersion(any(), any(), any(), any(), any(), any());
        verify(attrRepo, times(0)).uploadAttrRow(any(), any(), any(), any(), any(), any(), any());
        verify(attrRepo, times(0)).deleteExpiredVersion(any(), any(), any());

        softAssertions.assertAll();

    }

    @Test
    void shouldNotUploadFileWithLessNumberOfColumns() throws Exception {
        String fileName = "v1/upload_less_cols.csv";
        FileInputStream inputFile = new FileInputStream( "src/test/resources/" + fileName);
        MockMultipartFile file = new MockMultipartFile(fileName, fileName, "multipart/form-data", inputFile);

        lenient().doNothing().when(attrVersionRepo).updateVersion(any(), any(), any(), any(), any(), any());
        lenient().doNothing().when(attrRepo).uploadAttrRow(any(), any(), any(), any(), any(), any(), any());
        lenient().doNothing().when(attrRepo).deleteExpiredVersion(any(), any(), any());

        softAssertions.assertThatCode(() -> attrSvc.uploadFile(file, 7))
                .isInstanceOf(AppExc.class)
                .hasFieldOrPropertyWithValue("code", Constants.INVALID_REQUEST)
                .hasMessage("All rows in file " + fileName + " must contain 7 columns, found 6 columns in row 3");

        verify(attrVersionRepo, times(0)).updateVersion(any(), any(), any(), any(), any(), any());
        verify(attrRepo, times(0)).uploadAttrRow(any(), any(), any(), any(), any(), any(), any());
        verify(attrRepo, times(0)).deleteExpiredVersion(any(), any(), any());

        softAssertions.assertAll();
    }

    @Test
    void shouldNotUploadFileV2WithLessNumberOfColumns() throws Exception {
        String fileName = "v2/upload_less_cols.csv";
        FileInputStream inputFile = new FileInputStream( "src/test/resources/" + fileName);
        MockMultipartFile file = new MockMultipartFile(fileName, fileName, "multipart/form-data", inputFile);

        lenient().doNothing().when(attrVersionRepo).updateVersion(any(), any(), any(), any(), any(), any());
        lenient().doNothing().when(attrRepo).uploadAttrRow(any(), any(), any(), any(), any(), any(), any());
        lenient().doNothing().when(attrRepo).deleteExpiredVersion(any(), any(), any());

        int numFields = 8;
        softAssertions.assertThatCode(() -> attrSvc.uploadFile(file, numFields))
                .isInstanceOf(AppExc.class)
                .hasFieldOrPropertyWithValue("code", Constants.INVALID_REQUEST)
                .hasMessage("All rows in file " + fileName + " must contain " + numFields + " columns, found 7 columns in row 3");

        verify(attrVersionRepo, times(0)).updateVersion(any(), any(), any(), any(), any(), any());
        verify(attrRepo, times(0)).uploadAttrRow(any(), any(), any(), any(), any(), any(), any());
        verify(attrRepo, times(0)).deleteExpiredVersion(any(), any(), any());

        softAssertions.assertAll();
    }

    @Test
    void shouldNotUploadFileWithInvalidAvailsSupportedField() throws Exception {
        String fileName = "v2/upload_invalid_pg_avails.csv";
        FileInputStream inputFile = new FileInputStream( "src/test/resources/" + fileName);
        MockMultipartFile file = new MockMultipartFile(fileName, fileName, "multipart/form-data", inputFile);

        lenient().doNothing().when(attrVersionRepo).updateVersion(any(), any(), any(), any(), any(), any());
        lenient().doNothing().when(attrRepo).uploadAttrRow(any(), any(), any(), any(), any(), any(), any());
        lenient().doNothing().when(attrRepo).deleteExpiredVersion(any(), any(), any());

        softAssertions.assertThatCode(() -> attrSvc.uploadFile(file, 8))
                .isInstanceOf(AppExc.class)
                .hasFieldOrPropertyWithValue("code", Constants.INVALID_REQUEST)
                .hasMessage("Allowed values for (last) supports-avails field are Y,y,N,n or blank");

        verify(attrVersionRepo, times(0)).updateVersion(any(), any(), any(), any(), any(), any());
        verify(attrRepo, times(0)).uploadAttrRow(any(), any(), any(), any(), any(), any(), any());
        verify(attrRepo, times(0)).deleteExpiredVersion(any(), any(), any());

        softAssertions.assertAll();
    }

    @Test
    void shouldNotUploadFileWithMoreNumberOfColumns() throws Exception{
        String fileName = "v1/upload_more_cols.csv";
        FileInputStream inputFile = new FileInputStream( "src/test/resources/" + fileName);
        MockMultipartFile file = new MockMultipartFile(fileName, fileName, "multipart/form-data", inputFile);
        lenient().doNothing().when(attrVersionRepo).updateVersion(any(), any(), any(), any(), any(), any());
        lenient().doNothing().when(attrRepo).uploadAttrRow(any(), any(), any(), any(), any(), any(), any());
        lenient().doNothing().when(attrRepo).deleteExpiredVersion(any(), any(), any());

        int numFields = 7;
        softAssertions.assertThatCode(() -> attrSvc.uploadFile(file, numFields))
                .isInstanceOf(AppExc.class)
                .hasFieldOrPropertyWithValue("code", Constants.INVALID_REQUEST)
                .hasMessage("All rows in file " + fileName + " must contain " + numFields + " columns, found 8 columns in row 2");

        verify(attrVersionRepo, times(0)).updateVersion(any(), any(), any(), any(), any(), any());
        verify(attrRepo, times(0)).uploadAttrRow(any(), any(), any(), any(), any(), any(), any());
        verify(attrRepo, times(0)).deleteExpiredVersion(any(), any(), any());

        softAssertions.assertAll();
    }

    @Test
    void shouldNotUploadFileV2WithMoreNumberOfColumns() throws Exception{
        String fileName = "v2/upload_more_cols.csv";
        FileInputStream inputFile = new FileInputStream( "src/test/resources/" + fileName);
        MockMultipartFile file = new MockMultipartFile(fileName, fileName, "multipart/form-data", inputFile);
        lenient().doNothing().when(attrVersionRepo).updateVersion(any(), any(), any(), any(), any(), any());
        lenient().doNothing().when(attrRepo).uploadAttrRow(any(), any(), any(), any(), any(), any(), any());
        lenient().doNothing().when(attrRepo).deleteExpiredVersion(any(), any(), any());

        int numFields = 8;
        softAssertions.assertThatCode(() -> attrSvc.uploadFile(file, numFields))
                .isInstanceOf(AppExc.class)
                .hasFieldOrPropertyWithValue("code", Constants.INVALID_REQUEST)
                .hasMessage("All rows in file " + fileName + " must contain " + numFields + " columns, found 9 columns in row 2");

        verify(attrVersionRepo, times(0)).updateVersion(any(), any(), any(), any(), any(), any());
        verify(attrRepo, times(0)).uploadAttrRow(any(), any(), any(), any(), any(), any(), any());
        verify(attrRepo, times(0)).deleteExpiredVersion(any(), any(), any());

        softAssertions.assertAll();
    }
}
