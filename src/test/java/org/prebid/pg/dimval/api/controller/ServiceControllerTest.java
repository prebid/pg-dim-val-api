package org.prebid.pg.dimval.api.controller;

import java.io.FileInputStream;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.prebid.pg.dimval.api.AppExc;
import org.prebid.pg.dimval.api.dto.AttrEntryDto;
import org.prebid.pg.dimval.api.dto.RubiPageDto;
import org.prebid.pg.dimval.api.services.AttrSvc;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockMultipartFile;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ServiceControllerTest {

    SoftAssertions softAssertions;

    @Mock
    AttrSvc attrService;

    @InjectMocks
    private ServiceController serviceController;

    private String attrId = "attrId";

    private String account = "account";

    private String searchType = "searchType";

    private String attrLinkValue = "attrLinkValue";

    private String valueSetDisplayNameSearchString = "valueSetDisplayNameSearchString";

    @BeforeEach
    public void setup() {
        softAssertions = new SoftAssertions();
    }

    @Test
    public void shouldCallGetAttrNames() {
        softAssertions
                .assertThatCode(() -> serviceController.getAttrNames(null, null, PageRequest.of(0, 20)))
                .doesNotThrowAnyException();
        softAssertions
                .assertThatCode(() -> serviceController.getAttrNames("", null, PageRequest.of(0, 20)))
                .doesNotThrowAnyException();
        softAssertions.assertAll();
        verify(attrService, times(2)).getAllAttributes(null);

        softAssertions
                .assertThatCode(
                        () -> serviceController.getAttrNames(account, null, PageRequest.of(0, 20))
                )
                .doesNotThrowAnyException();
        softAssertions.assertAll();
        verify(attrService, times(1)).getAccountAttributes(account, null);
    }

    @Test
    public void shouldCallGetAttrNamesV2() {
        softAssertions
                .assertThatCode(() -> serviceController.getAttrNamesV2(null, Boolean.TRUE, PageRequest.of(0, 20)))
                .doesNotThrowAnyException();
        softAssertions
                .assertThatCode(() -> serviceController.getAttrNamesV2("", Boolean.TRUE, PageRequest.of(0, 20)))
                .doesNotThrowAnyException();
        softAssertions.assertAll();
        verify(attrService, times(2)).getAllAttributesV2(Boolean.TRUE);

        softAssertions
                .assertThatCode(
                        () -> serviceController.getAttrNamesV2(account, Boolean.TRUE, PageRequest.of(0, 20))
                )
                .doesNotThrowAnyException();
        softAssertions.assertAll();
        verify(attrService, times(1)).getAccountAttributesV2(account, Boolean.TRUE);
    }

    @Test
    public void shouldThrowExceptionOnCallGetAttrNamesWithBadPageStart() {
        softAssertions
                .assertThatCode(() -> serviceController.getAttrNames(account, null, PageRequest.of(100, 20)))
                .isInstanceOf(AppExc.class)
                .hasMessage("Can't start at page 100");
        softAssertions.assertAll();

        verify(attrService, times(1)).getAccountAttributes(account, null);
    }

    @Test
    void shouldCallGetAttrValues() throws Exception {
        softAssertions
                .assertThatCode(
                        () -> serviceController.getAttrValues(
                                account, attrId, null, null, PageRequest.of(0, 20)
                        )
                )
                .doesNotThrowAnyException();
        softAssertions
                .assertThatCode(
                        () -> serviceController.getAttrValues(
                                account, attrId, attrLinkValue, null, PageRequest.of(0, 20)
                        )
                )
                .doesNotThrowAnyException();

        softAssertions.assertAll();

        verify(attrService, times(1)).getAttributeValues(attrId, account, null);
        verify(attrService, times(1)).getAttributeValuesByLink(attrId, account,  attrLinkValue, null);
    }

    @Test
    void shouldCallGetAttrValuesV2() throws Exception {
        softAssertions
                .assertThatCode(
                        () -> serviceController.getAttrValuesV2(
                                account, attrId, null, null, PageRequest.of(0, 20)
                        )
                )
                .doesNotThrowAnyException();
        softAssertions
                .assertThatCode(
                        () -> serviceController.getAttrValuesV2(
                                account, attrId, attrLinkValue, null, PageRequest.of(0, 20)
                        )
                )
                .doesNotThrowAnyException();

        softAssertions.assertAll();

        verify(attrService, times(1)).getAttributeValuesV2(attrId, account, null);
        verify(attrService, times(1)).getAttributeValuesByLinkV2(attrId, account,  attrLinkValue, null);
    }

    @Test
    void shouldCallGetAttrValues1() throws Exception {
        RubiPageDto<AttrEntryDto> attrEntryDtoRubiPageDto = serviceController.getAttrValues(account, attrId, attrLinkValue, null, PageRequest.of(0, 20));
        System.out.println(attrEntryDtoRubiPageDto.getContent());
        System.out.println(attrEntryDtoRubiPageDto.getPage());
    }

    @Test
    void shouldCallSearchByAttrValueString() {
        softAssertions.assertThatCode(
                () -> serviceController.searchByAttrValueString(
                        attrId, account, valueSetDisplayNameSearchString.toUpperCase(),
                        searchType, null, PageRequest.of(0, 20)
                )
        ).doesNotThrowAnyException();

        softAssertions.assertAll();

        verify(attrService, times(1))
                .searchByAttrValueString(account, attrId, valueSetDisplayNameSearchString.toUpperCase(), searchType, null);
    }

    @Test
    void shouldCallSearchByAttrValueStringV2() {
        softAssertions.assertThatCode(
                () -> serviceController.searchByAttrValueStringV2(
                        attrId, account, valueSetDisplayNameSearchString.toUpperCase(),
                        searchType, null, PageRequest.of(0, 20)
                )
        ).doesNotThrowAnyException();

        softAssertions.assertAll();

        verify(attrService, times(1))
                .searchByAttrValueStringV2(account, attrId, valueSetDisplayNameSearchString.toUpperCase(), searchType, null);
    }

    @Test
    void shouldCallUploadFile() throws Exception {
        FileInputStream inputFile = new FileInputStream( "src/test/resources/v1/upload.csv");
        MockMultipartFile file = new MockMultipartFile("file", "v1/upload.csv", "multipart/form-data", inputFile);
        softAssertions.assertThatCode(() -> serviceController.uploadFileV1(file)).doesNotThrowAnyException();
        softAssertions.assertAll();
        verify(attrService, times(1)).uploadFile(file, 7);
        inputFile.close();

        FileInputStream inputEmptyFile = new FileInputStream( "src/test/resources/upload_empty.csv");
        MockMultipartFile emptyFile = new MockMultipartFile("file", "upload_empty.csv", "multipart/form-data", inputEmptyFile);
        softAssertions.assertThatCode(() -> serviceController.uploadFileV1(emptyFile)).doesNotThrowAnyException();
        softAssertions.assertAll();
        verify(attrService, times(0)).uploadFile(emptyFile, 7);
    }

    @Test
    void shouldCallUploadFileV2() throws Exception {
        FileInputStream inputFile = new FileInputStream( "src/test/resources/v2/upload.csv");
        MockMultipartFile file = new MockMultipartFile("file", "v2/upload.csv", "multipart/form-data", inputFile);
        softAssertions.assertThatCode(() -> serviceController.uploadFileV2(file)).doesNotThrowAnyException();
        softAssertions.assertAll();
        verify(attrService, times(1)).uploadFile(file, 8);
        inputFile.close();

        FileInputStream inputEmptyFile = new FileInputStream( "src/test/resources/upload_empty.csv");
        MockMultipartFile emptyFile = new MockMultipartFile("file", "upload_empty.csv", "multipart/form-data", inputEmptyFile);
        softAssertions.assertThatCode(() -> serviceController.uploadFileV2(emptyFile)).doesNotThrowAnyException();
        softAssertions.assertAll();
        verify(attrService, times(0)).uploadFile(emptyFile, 8);
    }
}