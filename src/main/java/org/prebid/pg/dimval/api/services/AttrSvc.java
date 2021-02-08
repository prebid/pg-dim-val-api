package org.prebid.pg.dimval.api.services;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.prebid.pg.dimval.api.AppExc;
import org.prebid.pg.dimval.api.Constants;
import org.prebid.pg.dimval.api.Utils;
import org.prebid.pg.dimval.api.config.app.AttrDataConfig;
import org.prebid.pg.dimval.api.dto.AttrEntryDto;
import org.prebid.pg.dimval.api.dto.AttrEntryDtoV2;
import org.prebid.pg.dimval.api.dto.AttrStatusDto;
import org.prebid.pg.dimval.api.dto.ValueSet;
import org.prebid.pg.dimval.api.persistence.AttrEntity;
import org.prebid.pg.dimval.api.persistence.AttrVersion;
import org.prebid.pg.dimval.api.repo.AttrRepo;
import org.prebid.pg.dimval.api.repo.AttrVersionRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class AttrSvc {
    private ObjectMapper objectMapper;

    private AttrRepo attrRepo;

    private AttrVersionRepo attrVersionRepo;

    private Map<String, String> linkedAttrIdToParentMap = new HashMap<>();

    public AttrSvc(
            ObjectMapper objectMapper,
            AttrRepo attrRepo,
            AttrVersionRepo attrVersionRepo,
            AttrDataConfig attrDataConfig
    ) {
        this.objectMapper = Objects.requireNonNull(objectMapper);
        this.attrRepo = Objects.requireNonNull(attrRepo);
        this.attrVersionRepo = Objects.requireNonNull(attrVersionRepo);

        log.info("attrDataConfig={}", attrDataConfig.getAttrTreeLinks());

        Utils.getStream(attrDataConfig.getAttrTreeLinks())
                .map(link -> linkedAttrIdToParentMap.put(link.getLeaf(), link.getParent()))
                .count();
        log.info("linkedAttrIdToParentMap={}", linkedAttrIdToParentMap);
    }

    public List<AttrEntryDto> getAllAttributes(Boolean supportsAvails) {
        return this.addValuesCountV2(this.getAllAttributesV2(supportsAvails))
                .stream()
                .map(attrEntryDtoV2 ->
                        AttrEntryDto.builder()
                                .attrId(attrEntryDtoV2.getAttrId())
                                .attrType(attrEntryDtoV2.getAttrType())
                                .attrDisplayName(attrEntryDtoV2.getAttrDisplayName())
                                .accountId(attrEntryDtoV2.getAccountId())
                                .valueCount(attrEntryDtoV2.getValueCount())
                                .updatedAt(attrEntryDtoV2.getUpdatedAt()).build()
                ).collect(Collectors.toList());
    }

    public List<AttrEntryDtoV2> getAllAttributesV2(Boolean supportsAvails) {
        List<AttrVersion> objects = attrVersionRepo.getAllAttrList();
        final List<AttrEntryDtoV2> attrEntryDtoList = Utils.getStream(objects)
                .filter(attrVersion ->
                        !(Boolean.TRUE.equals(supportsAvails) && "N".equals(attrVersion.getSupportsAvails())))
                .map(attrVersion ->
                        AttrEntryDtoV2.builder()
                                .attrId(attrVersion.getAttrId())
                                .attrType(attrVersion.getAttrType())
                                .attrDisplayName(attrVersion.getAttrDisplayName())
                                .accountId(attrVersion.getAccountId())
                                .availsSupported(
                                        "Y".equals(attrVersion.getSupportsAvails()) ? Boolean.TRUE : Boolean.FALSE)
                                .updatedAt(attrVersion.getUpdatedAt()).build()
                ).collect(Collectors.toList());

        return addValuesCountV2(attrEntryDtoList);
    }

    public List<AttrEntryDto> getAccountAttributes(String accountId, Boolean supportsAvails) {
        return this.addValuesCountV2(this.getAccountAttributesV2(accountId, supportsAvails))
                .stream()
                .map(attrEntryDtoV2 ->
                        AttrEntryDto.builder()
                                .attrId(attrEntryDtoV2.getAttrId())
                                .attrType(attrEntryDtoV2.getAttrType())
                                .attrDisplayName(attrEntryDtoV2.getAttrDisplayName())
                                .accountId(attrEntryDtoV2.getAccountId())
                                .valueCount(attrEntryDtoV2.getValueCount())
                                .updatedAt(attrEntryDtoV2.getUpdatedAt()).build()
                ).collect(Collectors.toList());
    }

    public List<AttrEntryDtoV2> getAccountAttributesV2(String accountId, Boolean supportsAvails) {
        List<AttrVersion> commonAttrList;
        List<AttrVersion> accountAttrList;

        if (Boolean.TRUE.equals(supportsAvails)) {
            commonAttrList = attrVersionRepo.getSupportingPGAvailsAttrList(Constants.BASE_ACCOUNT_ID);
            accountAttrList = attrVersionRepo.getSupportingPGAvailsAttrList(accountId);
        } else {
            commonAttrList = attrVersionRepo.getAttrList(Constants.BASE_ACCOUNT_ID);
            accountAttrList = attrVersionRepo.getAttrList(accountId);
        }

        final List<AttrEntryDtoV2> attrEntryDtoList = Stream.of(accountAttrList, commonAttrList)
                .flatMap(Collection::stream)
                .collect(Collectors.toList())
                .stream().map(attrVersion ->
                        AttrEntryDtoV2.builder()
                                .attrId(attrVersion.getAttrId())
                                .attrType(attrVersion.getAttrType())
                                .attrDisplayName(attrVersion.getAttrDisplayName())
                                .accountId(attrVersion.getAccountId())
                                .attrDisplayName(attrVersion.getAttrDisplayName())
                                .availsSupported(
                                        "Y".equals(attrVersion.getSupportsAvails()) ? Boolean.TRUE : Boolean.FALSE)
                                .updatedAt(attrVersion.getUpdatedAt()).build()
                ).collect(Collectors.toList());

        return addValuesCountV2(attrEntryDtoList);
    }

    private List<AttrEntryDtoV2> addValuesCountV2(List<AttrEntryDtoV2> attrEntryDtoList) {
        Integer count;
        final List<AttrEntryDtoV2> finalAttrEntryDtoList = new ArrayList<>();
        for (AttrEntryDtoV2 attrEntryDto : attrEntryDtoList) {
            log.debug(attrEntryDto.toString());
            count = attrRepo.getValueCountsByAttrAndAccount(attrEntryDto.getAttrId(), attrEntryDto.getAccountId()
            );
            if (count != null) {
                attrEntryDto.setValueCount(count);
                finalAttrEntryDtoList.add(attrEntryDto);
            }
        }
        return finalAttrEntryDtoList;
    }

    public List<AttrEntryDto> getAttributeValues(
            String attrId, String accountId, Boolean supportsAvails) throws AppExc {
        return this.getAttributeValuesV2(attrId, accountId, supportsAvails)
                .stream()
                .map(attrEntryDtoV2 -> AttrEntryDto.builder()
                        .accountId(attrEntryDtoV2.getAccountId())
                        .attrId(attrEntryDtoV2.getAttrId())
                        .attrDisplayName(attrEntryDtoV2.getAttrDisplayName())
                        .value(attrEntryDtoV2.getValue())
                        .display(attrEntryDtoV2.getDisplay())
                        .pos(attrEntryDtoV2.getPos())
                        .build()
                ).collect(Collectors.toList());
    }

    public List<AttrEntryDtoV2> getAttributeValuesV2(
            String attrId, String accountId, Boolean supportsAvails) throws AppExc {
        if (linkedAttrIdToParentMap.containsKey(attrId)) {
            throw new AppExc(Constants.INVALID_REQUEST, String.format("attrId=%s requires attrLinkValue", attrId));
        }

        //https://dev.mysql.com/doc/refman/5.6/en/regexp.html
        if ("*".equals(attrId)) {
            attrId = ".*";
        } else if (attrId.startsWith("*")) {
            attrId = attrId.replaceFirst("\\*", "");
        } else if (attrId.startsWith("?")) {
            attrId = attrId.replaceFirst("\\?", "");
        }
        log.info("attrId={}", attrId);

        List<AttrVersion> attrVersionList = attrVersionRepo.getVersionForAttrInAccountRegexp(attrId, accountId);
        if (CollectionUtils.isEmpty(attrVersionList)) {
            attrVersionList = attrVersionRepo.getVersionForAttrInAccountRegexp(attrId, Constants.BASE_ACCOUNT_ID);
            accountId = Constants.BASE_ACCOUNT_ID;
        }

        if (CollectionUtils.isEmpty(attrVersionList)) {
            return new ArrayList<>();
        }

        AttrEntity attrEntity;
        final List<AttrEntity> attrEntityList = new ArrayList<>();
        for (AttrVersion attrVersion : attrVersionList) {
            log.info(
                    "Searching for {} with version {} in account {}",
                    attrVersion.getAttrId(), attrVersion.getVersionId(), accountId
            );
            attrEntity = attrRepo.getAttrValues(accountId, attrVersion.getVersionId(), attrVersion.getAttrId());

            if (attrEntity == null
                    || (Boolean.TRUE.equals(supportsAvails) && !"Y".equalsIgnoreCase(attrEntity.getSupportsAvails()))) {
                continue;
            }
            attrEntityList.add(attrEntity);
        }

        return Utils.getStream(attrEntityList)
                .map(entity -> toAttrEntryDtoV2List(entity, supportsAvails))
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    public List<AttrEntryDto> searchByAttrValueString(
            String attrId, String accountId, String searchStringIn, String searchType, Boolean supportsAvails
    ) {
        return this.searchByAttrValueStringV2(attrId, accountId, searchStringIn, searchType, supportsAvails)
                .stream()
                .map(attrEntryDtoV2 -> AttrEntryDto.builder()
                        .accountId(attrEntryDtoV2.getAccountId())
                        .attrId(attrEntryDtoV2.getAttrId())
                        .attrDisplayName(attrEntryDtoV2.getAttrDisplayName())
                        .value(attrEntryDtoV2.getValue())
                        .display(attrEntryDtoV2.getDisplay())
                        .pos(attrEntryDtoV2.getPos())
                        .build()
                ).collect(Collectors.toList());
    }

    public List<AttrEntryDtoV2> searchByAttrValueStringV2(
            String attrId, String accountId, String searchStringIn, String searchType, Boolean supportsAvails
    ) {
        final String searchString = "%" + searchStringIn.toUpperCase() + "%";

        AttrVersion attrVersion = attrVersionRepo.getVersionForAttrInAccountExact(attrId, accountId);
        if (attrVersion == null) {
            attrVersion = attrVersionRepo.getVersionForAttrInAccountExact(attrId, Constants.BASE_ACCOUNT_ID);
            accountId = Constants.BASE_ACCOUNT_ID;
        }

        if (attrVersion == null) {
            return new ArrayList<>();
        }

        final List<AttrEntity> attrEntityList
                = attrRepo.getAttrBySearchString(accountId, attrVersion.getVersionId(), attrId, searchString);

        if (CollectionUtils.isEmpty(attrEntityList)) {
            return new ArrayList<>();
        }

        //check this upper case part in m-r
        return Utils.getStream(attrEntityList)
                .map(attrEntity -> mapToMatchedOnly(attrEntity, searchStringIn.toUpperCase(), searchType))
                .filter(attrEntity -> !"[]".equals(attrEntity.getValueSet()))
                .map(attrEntity -> toAttrEntryDtoV2List(attrEntity, supportsAvails))
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    private List<AttrEntryDtoV2> toAttrEntryDtoV2List(AttrEntity matchedEntity, Boolean supportsAvails) {
        List<AttrEntryDtoV2> attrEntryDtoV2List = new ArrayList<>();
        try {
            attrEntryDtoV2List =
                    Arrays.asList(objectMapper.readValue(matchedEntity.getValueSet(), ValueSet[].class))
                            .stream()
                            .filter(entity ->
                                    !(Boolean.TRUE.equals(supportsAvails) && "N".equals(entity.getAvailsSupported()))
                            )
                            .map(valueSet ->
                                    AttrEntryDtoV2.builder()
                                            .accountId(matchedEntity.getAccountId())
                                            .attrId(matchedEntity.getAttrId())
                                            .attrDisplayName(matchedEntity.getAttrDisplayName())
                                            .value(valueSet.getValue())
                                            .display(valueSet.getDisplay())
                                            .pos(valueSet.getPos())
                                            .availsSupported(
                                                    "Y".equals(valueSet.getAvailsSupported())
                                                            ? Boolean.TRUE : Boolean.FALSE)
                                            .updatedAt(matchedEntity.getUpdatedAt())
                                            .build()
                            ).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error in mapping to AttrEntryDto from AttrEntity::{}", matchedEntity.getValueSet(), e);
        }
        return attrEntryDtoV2List;
    }

    private AttrEntity mapToMatchedOnly(AttrEntity attrEntity, String searchString, String searchType) {
        try {
            final List<ValueSet> valueSetList
                    = objectMapper.readerFor(
                            new TypeReference<List<ValueSet>>() { }).readValue(attrEntity.getValueSet());
            final List<ValueSet> matchedValueSetList = Utils.getStream(valueSetList)
                        .filter(
                                valueSet -> {
                                    if ("startsWith".equals(searchType)) {
                                        if (valueSet.getDisplay().toUpperCase().startsWith(searchString)) {
                                            return true;
                                        }
                                    } else {
                                        if (valueSet.getDisplay().toUpperCase().contains(searchString)) {
                                            return true;
                                        }
                                    }
                                    return false;
                                }
                        ).collect(Collectors.toList());
            attrEntity.setValueSet(objectMapper.writeValueAsString(matchedValueSetList));
        } catch (Exception e) {
            log.error("Error in finding matchedValueSet::{}::{}", attrEntity.getValueSet(), e);
            attrEntity.setValueSet(StringUtils.EMPTY);
        }

        return attrEntity;
    }

    public List<AttrEntryDto> getAttributeValuesByLink(
            String attrId, String accountId, String attrLinkValue, Boolean supportsAvails
    ) {
        return this.getAttributeValuesByLinkV2(attrId, accountId, attrLinkValue, supportsAvails)
                .stream()
                .map(attrEntryDtoV2 -> AttrEntryDto.builder()
                        .accountId(attrEntryDtoV2.getAccountId())
                        .attrId(attrEntryDtoV2.getAttrId())
                        .attrDisplayName(attrEntryDtoV2.getAttrDisplayName())
                        .value(attrEntryDtoV2.getValue())
                        .display(attrEntryDtoV2.getDisplay())
                        .pos(attrEntryDtoV2.getPos())
                        .build()
                ).collect(Collectors.toList());
    }

    public List<AttrEntryDtoV2> getAttributeValuesByLinkV2(
            String attrId, String accountId, String attrLinkValue, Boolean supportsAvails
    ) {
        AttrVersion attrVersion = attrVersionRepo.getVersionForAttrInAccountExact(attrId, accountId);

        if (attrVersion == null) {
            attrVersion = attrVersionRepo.getVersionForAttrInAccountExact(attrId, Constants.BASE_ACCOUNT_ID);
            accountId = Constants.BASE_ACCOUNT_ID;
        }

        if (attrVersion == null) {
            return new ArrayList<>();
        }

        final AttrEntity attrEntity = attrRepo.getAttrValuesByLink(
                accountId, attrVersion.getVersionId(), attrVersion.getAttrId(), attrLinkValue
        );

        if (attrEntity == null) {
            return new ArrayList<>();
        }

        if (Boolean.TRUE.equals(supportsAvails) && !"Y".equalsIgnoreCase(attrEntity.getSupportsAvails())) {
            return new ArrayList<>();
        }

        final List<AttrEntity> attrEntityList = new ArrayList<>();
        attrEntityList.add(attrEntity);

        return Utils.getStream(attrEntityList)
                .map(a -> toAttrEntryDtoV2List(a, supportsAvails))
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    @Transactional(rollbackFor = Exception.class)
    public List<AttrStatusDto> uploadFile(MultipartFile file, int numFields) throws AppExc {
        String attrDisplayName;
        final Set linkedAttrIdSet = linkedAttrIdToParentMap.keySet();

        int lines = 0;
        String attrId;
        String attrType = null;
        String attrLinkValue = "none";
        String supportsAvails = "N";
        final Map<String, Map<String, Map<String, List<Map<String, String>>>>>
                accountIdToAttrIdToAttrLinkValueToValueSetListMap = new HashMap<>();

        String[] values = null;
        try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            while ((values = reader.readNext()) != null) {
                if (values.length != numFields) {
                    String msg = String.format(
                            "All rows in file %s must contain %s columns, found %s columns in row %s",
                            file.getName(), numFields, values.length, lines + 1
                    );
                    log.error(msg);
                    throw new AppExc(Constants.INVALID_REQUEST, msg);
                }

                Map<String, String> valueSetMap = new HashMap<>();
                attrId = values[0];
                attrType = values[1];
                attrDisplayName = values[2];
                log.info("AttrId=" + attrId + ", AttrType = " + attrType + ", AttrDisplayName=" + attrDisplayName);

                if (linkedAttrIdSet.contains(attrId)) {
                    String[] parts = values[4].split("\\s*,\\s*");
                    if (parts.length > 1) {
                        attrLinkValue = parts[parts.length - 1];
                    }
                }

                valueSetMap.put("display", values[4]);
                valueSetMap.put("value", values[3]);
                valueSetMap.put("pos", values[6]);
                if (numFields > 7) {
                    if (values[7].isEmpty()) {
                        valueSetMap.put("availsSupported", "N");
                    } else if (!values[7].matches("^[YyNn]{1}$")) {
                        String msg = "Allowed values for (last) supports-avails field are Y,y,N,n or blank";
                        log.error(msg);
                        throw new AppExc(Constants.INVALID_REQUEST, msg);
                    } else {
                        valueSetMap.put("availsSupported", values[7]);
                        supportsAvails = "Y";
                    }
                }

                String accountIdKey
                        = ("*".equals(values[5]) || StringUtils.isEmpty(values[5]))
                        ? Constants.BASE_ACCOUNT_ID : values[5];

                accountIdToAttrIdToAttrLinkValueToValueSetListMap
                        .computeIfAbsent(accountIdKey, key -> new HashMap<>())
                        .computeIfAbsent(attrId + "<>" + attrDisplayName, key -> new HashMap<>())
                        .computeIfAbsent(attrLinkValue, key -> new ArrayList<>()).add(valueSetMap);

                lines++;
            }
        } catch (Exception e) {
            log.error("Values={}::exception::{}", values, e);
            throw new AppExc(Constants.INVALID_REQUEST, String.format(e.getMessage()));
        }

        if (lines == 0) {
            throw new AppExc(Constants.INVALID_REQUEST, "Zero lines processed from " + file.getName());
        }

        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

        final String versionId = UUID.randomUUID().toString();
        for (Entry<String, Map<String, Map<String, List<Map<String, String>>>>> entry
                : accountIdToAttrIdToAttrLinkValueToValueSetListMap.entrySet()) {
            final String accountIdKey = entry.getKey();
            for (Entry<String, Map<String, List<Map<String, String>>>> attrEntry : entry.getValue().entrySet()) {
                final String attrIdKey = attrEntry.getKey();
                for (Entry<String, List<Map<String, String>>> attrLinkValueEntry : attrEntry.getValue().entrySet()) {
                    List<Map<String, String>> valueSetListIn = attrLinkValueEntry.getValue();
                    final String valueSetJsonStr = objectMapper.convertValue(valueSetListIn, JsonNode.class).toString();
                    if ("none".equals(accountIdKey)) {
                        log.info("account_id = 'none', skipping");
                        continue;
                    }
                    final String attrIdPart = attrIdKey.split("<>")[0];
                    final String attrDisplayNamePart = attrIdKey.split("<>")[1];
                    log.debug(
                            "attrIdKey={}, attrDisplayName={}, attrType={}, ValueSetJsonStr={}, supportsAvails={}",
                            attrIdPart, attrDisplayNamePart, attrType, valueSetJsonStr, supportsAvails
                    );
                    if (numFields > 7) {
                        attrRepo.uploadAttrRow(
                                versionId, accountIdKey, attrIdPart, attrLinkValueEntry.getKey(),
                                attrType, attrDisplayNamePart, valueSetJsonStr, supportsAvails
                        );
                    } else {
                        attrRepo.uploadAttrRow(
                                versionId, accountIdKey, attrIdPart, attrLinkValueEntry.getKey(),
                                attrType, attrDisplayNamePart, valueSetJsonStr
                        );
                    }
                }
            }
        }

        final List<AttrStatusDto> dtoArrayList = new ArrayList<>();
        for (Entry<String, Map<String, Map<String, List<Map<String, String>>>>> entry
                : accountIdToAttrIdToAttrLinkValueToValueSetListMap.entrySet()) {
            final String accountIdKey = entry.getKey();

            for (String attrIdKey : entry.getValue().keySet()) {
                final String attrIdPart = attrIdKey.split("<>")[0];
                final String attrDisplayNamePart = attrIdKey.split("<>")[1];
                if ("none".equals(accountIdKey)) {
                    log.info("account_id = 'none', skipping version table update");
                    continue;
                }
                dtoArrayList.add(
                        AttrStatusDto.builder()
                                .versionId(versionId)
                                .accountId(accountIdKey)
                                .attrId(attrIdPart)
                                .build()
                );
                attrRepo.deleteExpiredVersion(versionId, accountIdKey, attrIdPart);
                String pgAvailsSupportedFlagInVersionTable = "Y";
                if (numFields > 7
                        && attrRepo.getValueCountsBySupportingPGAvailsAttrAndAccount(attrIdPart, accountIdKey) == 0) {
                    pgAvailsSupportedFlagInVersionTable = "N";
                }
                attrVersionRepo.updateVersion(
                        accountIdKey, attrIdPart, attrType, attrDisplayNamePart,
                        versionId, pgAvailsSupportedFlagInVersionTable
                );
            }
        }
        return dtoArrayList;
    }
}
