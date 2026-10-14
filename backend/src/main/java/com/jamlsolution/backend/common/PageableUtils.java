package com.jamlsolution.backend.common;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public final class PageableUtils {

    private PageableUtils() {}

    /**
     * Strips any sort property not in the allowed set and falls back to
     * {@code defaultProperty} if nothing valid remains. Prevents JPA from blowing
     * up when Swagger sends the placeholder value {@code ["string"]}.
     */
    public static Pageable sanitize(Pageable pageable, Set<String> allowed, String defaultProperty) {
        if (!pageable.getSort().isSorted()) {
            return pageable;
        }

        List<Sort.Order> validOrders = pageable.getSort().stream()
                .filter(order -> allowed.contains(order.getProperty()))
                .collect(Collectors.toList());

        Sort sort = validOrders.isEmpty()
                ? Sort.by(defaultProperty).ascending()
                : Sort.by(validOrders);

        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
    }
}
