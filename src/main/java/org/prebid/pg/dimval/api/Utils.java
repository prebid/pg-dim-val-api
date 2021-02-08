package org.prebid.pg.dimval.api;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public interface Utils {

    static <T> Stream<T> getStream(List<T> list) {
        return Optional.ofNullable(list).map(List::stream).orElseGet(Stream::empty);
    }

}
