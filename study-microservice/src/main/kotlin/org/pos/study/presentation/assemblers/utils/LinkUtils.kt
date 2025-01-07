package org.pos.study.presentation.assemblers.utils

import org.springframework.data.domain.Page
import org.springframework.hateoas.Link
import org.springframework.hateoas.CollectionModel

object LinkUtils {

    fun <T> addPaginationLinks(
        collectionModel: CollectionModel<T>,
        baseUri: String,
        page: Page<*>
    ) {
        collectionModel.add(Link.of(baseUri).withSelfRel())

        if (page.hasNext()) {
            collectionModel.add(Link.of("$baseUri?page=${page.number + 1}&size=${page.size}")
                .withType("GET")
                .withRel("next"))
        }

        if (page.hasPrevious()) {
            collectionModel.add(Link.of("$baseUri?page=${page.number - 1}&size=${page.size}")
                .withType("GET")
                .withRel("prev"))
        }

        if (!page.isFirst) {
            collectionModel.add(Link.of("$baseUri?page=0&size=${page.size}")
                .withType("GET")
                .withRel("first"))
        }

        if (!page.isLast) {
            collectionModel.add(Link.of("$baseUri?page=${page.totalPages - 1}&size=${page.size}")
                .withType("GET")
                .withRel("last"))
        }
    }
}
