package org.prebid.pg.dimval.api.dto;

import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;

@ToString
@Slf4j
public class RubiPage {

    private int size;
    private long totalElements;
    private int totalPages;
    private int number;

    RubiPage(Page page) {
        log.debug("RubiPage::ctor");
        this.number = page.getNumber();
        this.size = page.getSize();
        this.totalElements = page.getTotalElements();
        this.totalPages = page.getTotalPages();
    }

    public int getNumber() {
        return number;
    }

    public int getSize() {
        return size;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public long getTotalElements() {
        return totalElements;
    }

}
