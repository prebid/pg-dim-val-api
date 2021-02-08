package org.prebid.pg.dimval.api.dto;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class RubiPageDto<T> {

    private List<T> content;

    private RubiPage page;

    public RubiPageDto(Page page) {
        this.content = page.getContent();
        this.page = new RubiPage(page);
    }

    public List<T> getContent() {
        return content;
    }

    public RubiPage getPage() {
        return page;
    }

    public void setEmptyContent() {
        content = new ArrayList<>();
    }
}
