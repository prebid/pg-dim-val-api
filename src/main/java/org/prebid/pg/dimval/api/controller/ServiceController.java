package org.prebid.pg.dimval.api.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;
import org.prebid.pg.dimval.api.AppExc;
import org.prebid.pg.dimval.api.Constants;
import org.prebid.pg.dimval.api.dto.AttrEntryDto;
import org.prebid.pg.dimval.api.dto.AttrEntryDtoV2;
import org.prebid.pg.dimval.api.dto.FinalAttrStatusDto;
import org.prebid.pg.dimval.api.dto.RubiPageDto;
import org.prebid.pg.dimval.api.services.AttrSvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("${services.base-url}")
@Api(tags = {"PG Dimension Value API"})
@SwaggerDefinition(tags = {
        @Tag(
                name = "PG Dimension Value API",
                description = "Operations in uploading and querying dimensional values for targeting selection"
        )
})
public class ServiceController {

    private AttrSvc attrService;

    public ServiceController(AttrSvc attrService) {
        this.attrService = attrService;
    }

    @GetMapping("/v1/attr/names")
    @ApiOperation(value = "View the full set of attributes for an account or 'common' account")
    public RubiPageDto<AttrEntryDto> getAttrNames(
            @ApiParam(value = "account id - use 'common' for base account")
            @RequestParam(required = false) String account,
            @ApiParam(value = "supports avails - if absent, all attribute names are included")
            @RequestParam(name = "supports-avails", required = false) Boolean supportsAvails,
            Pageable pageable
    ) throws AppExc {
        log.info("supportsAvails={}", supportsAvails);
        if (account == null || account.isEmpty()) {
            return returnRequestedPage(pageable, attrService.getAllAttributes(supportsAvails));
        } else {
            return returnRequestedPage(pageable, attrService.getAccountAttributes(account, supportsAvails));
        }
    }

    @GetMapping("/v2/attr/names")
    @ApiOperation(value = "View the full set of attributes for an account or 'common' account")
    public RubiPageDto<AttrEntryDtoV2> getAttrNamesV2(
            @ApiParam(value = "account id - use 'common' for base account")
            @RequestParam(required = false) String account,
            @ApiParam(value = "supports avails - if absent, all attribute names are included")
            @RequestParam(name = "supports-avails", required = false) Boolean supportsAvails,
            Pageable pageable
    ) throws AppExc {
        log.info("supportsAvails=" + supportsAvails);
        if (account == null || account.isEmpty()) {
            return returnRequestedPage(pageable, attrService.getAllAttributesV2(supportsAvails));
        } else {
            return returnRequestedPage(pageable, attrService.getAccountAttributesV2(account, supportsAvails));
        }
    }

    @GetMapping("/v1/attr/values")
    @ApiOperation(value = "View the value set for an exact attribute for an account or 'common' account")
    public RubiPageDto<AttrEntryDto> getAttrValues(
            @ApiParam(
                    value = "account id - use 'common' for base account",
                    required = true
            ) @RequestParam String account,
            @ApiParam(
                    value = "attribute id - supports regular expressions when not used with attrLinkValue",
                    required = true
            ) @RequestParam String attrId,
            @ApiParam(value = "attribute link value")
            @RequestParam(required = false) String attrLinkValue,
            @ApiParam(value = "supports avails - if absent, all attribute values are included")
            @RequestParam(name = "supports-avails", required = false) Boolean supportsAvails,
            @ApiParam Pageable pageable
    ) throws AppExc {
        List<AttrEntryDto> attrEntryDto;
        if (attrLinkValue == null) {
            attrEntryDto = attrService.getAttributeValues(attrId, account, supportsAvails);
        } else {
            attrEntryDto = attrService.getAttributeValuesByLink(attrId, account, attrLinkValue, supportsAvails);
        }
        return returnRequestedPage(pageable, attrEntryDto);
    }

    @GetMapping("/v2/attr/values")
    @ApiOperation(value = "View the value set for an exact attribute for an account or 'common' account")
    public RubiPageDto<AttrEntryDtoV2> getAttrValuesV2(
            @ApiParam(
                    value = "account id - use 'common' for base account",
                    required = true
            ) @RequestParam String account,
            @ApiParam(
                    value = "attribute id - supports regular expressions when not used with attrLinkValue",
                    required = true
            ) @RequestParam String attrId,
            @ApiParam(value = "attribute link value")
            @RequestParam(required = false) String attrLinkValue,
            @ApiParam(value = "supports avails - if absent, all attribute values are included")
            @RequestParam(name = "supports-avails", required = false) Boolean supportsAvails,
            @ApiParam Pageable pageable
    ) throws AppExc {
        List<AttrEntryDtoV2> attrEntryDtoV2List;
        if (attrLinkValue == null) {
            attrEntryDtoV2List = attrService.getAttributeValuesV2(attrId, account, supportsAvails);
        } else {
            attrEntryDtoV2List = attrService.getAttributeValuesByLinkV2(attrId, account, attrLinkValue, supportsAvails);
        }
        return returnRequestedPage(pageable, attrEntryDtoV2List);
    }

    @GetMapping("/v1/attr/search")
    @ApiOperation(value = "View the value set display for an attribute based on a search string")
    public RubiPageDto<AttrEntryDto> searchByAttrValueString(
            @ApiParam(
                    value = "account id - use 'common' for base account",
                    required = true
            ) @RequestParam String account,
            @ApiParam(
                    value = "attribute id",
                    required = true
            ) @RequestParam String attrId,
            @ApiParam(
                    value = "search string - no wildcards",
                    required = true
            ) @RequestParam String valueSetDisplayNameSearchString,
            @ApiParam(
                    value = "search type - valid values are 'contains' and 'startsWith'",
                    required = true
            ) @RequestParam String searchType,
            @ApiParam(value = "supports avails - if absent, all attribute values are included")
            @RequestParam(name = "supports-avails", required = false) Boolean supportsAvails,
            @ApiParam Pageable pageable
    ) throws AppExc {
        return this.returnRequestedPage(
                pageable,
                attrService.searchByAttrValueString(
                        attrId, account, valueSetDisplayNameSearchString, searchType, supportsAvails
                )
        );
    }

    @GetMapping("/v2/attr/search")
    @ApiOperation(value = "View the value set display for an attribute based on a search string")
    public RubiPageDto<AttrEntryDtoV2> searchByAttrValueStringV2(
            @ApiParam(
                    value = "account id - use 'common' for base account",
                    required = true
            ) @RequestParam String account,
            @ApiParam(
                    value = "attribute id",
                    required = true
            ) @RequestParam String attrId,
            @ApiParam(
                    value = "search string - no wildcards",
                    required = true
            ) @RequestParam String valueSetDisplayNameSearchString,
            @ApiParam(
                    value = "search type - valid values are 'contains' and 'startsWith'",
                    required = true
            ) @RequestParam String searchType,
            @ApiParam(value = "supports avails - if absent, all attribute values are included")
            @RequestParam(name = "supports-avails", required = false) Boolean supportsAvails,
            @ApiParam Pageable pageable
    ) throws AppExc {
        return this.returnRequestedPage(
                pageable,
                attrService.searchByAttrValueStringV2(
                        attrId, account, valueSetDisplayNameSearchString, searchType, supportsAvails
                )
        );
    }

    @PostMapping("/v1/attr/upload")
    @ApiOperation(value = "Upload a csv sheet for an attribute")
    public ResponseEntity<FinalAttrStatusDto> uploadFileV1(
            @RequestParam MultipartFile file
    ) throws AppExc {
        return this.uploadFile(file, 7);
    }

    @PostMapping("/v2/attr/upload")
    @ApiOperation(value = "Upload a csv sheet for an attribute")
    public ResponseEntity<FinalAttrStatusDto> uploadFileV2(
            @RequestParam MultipartFile file
    ) throws AppExc {
        return this.uploadFile(file, 8);
    }

    private ResponseEntity<FinalAttrStatusDto> uploadFile(
            @RequestParam MultipartFile file, int numFields
    ) throws AppExc {
        final FinalAttrStatusDto finalAttrStatusDto = FinalAttrStatusDto.builder().timestamp(Instant.now()).build();

        if (file.isEmpty() || file.getSize() == 0) {
            finalAttrStatusDto.setMessage("Missing file attachment, or attached file size is zero");
            final HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
            finalAttrStatusDto.setStatus(httpStatus.value());
            finalAttrStatusDto.setError(httpStatus.getReasonPhrase());
            return new ResponseEntity<>(finalAttrStatusDto, httpStatus);
        }

        finalAttrStatusDto.setAttrStatusDtoList(attrService.uploadFile(file, numFields));
        finalAttrStatusDto.setMessage("Success");

        return new ResponseEntity<>(finalAttrStatusDto, HttpStatus.OK);
    }

    private <T> RubiPageDto<T> returnRequestedPage(Pageable pageable, List<T> dtoList) throws AppExc {
        int startPageNumber = pageable.getPageNumber();

        int startIdx = startPageNumber * pageable.getPageSize();
        int endIdx = 0;
        if (dtoList.size() < (startPageNumber + 1) * pageable.getPageSize()) {
            endIdx = dtoList.size();
        } else {
            endIdx = startIdx + pageable.getPageSize();
        }

        if (startIdx > endIdx) {
            throw new AppExc(Constants.DATA_ERROR, "Can't start at page " + startPageNumber);
        }

        RubiPageDto rubiPageDto = new RubiPageDto<>(new PageImpl<>(
                dtoList.subList(startIdx, endIdx),
                PageRequest.of(startPageNumber, pageable.getPageSize()),
                dtoList.size())
        );

        if (rubiPageDto.getPage().getTotalElements() == 0) {
            log.debug("zero elements::pre setEmptyContent()::content=" + rubiPageDto.getContent());
            rubiPageDto.setEmptyContent();
            log.debug("zero elements::post setEmptyContent()::zero elements::content=" + rubiPageDto.getContent());
        }
        return rubiPageDto;
    }
}

