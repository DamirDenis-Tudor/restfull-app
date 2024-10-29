package org.pos.study.controllers.assemblers

import org.pos.study.domain.Professor
import org.pos.study.domain.Student
import org.pos.study.utils.LinkUtils
import org.springframework.data.domain.Page
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.Link
import org.springframework.hateoas.server.RepresentationModelAssembler
import org.springframework.stereotype.Component

@Component
class StudentModelAssembler : RepresentationModelAssembler<Student, EntityModel<Student>> {
    override fun toModel(entity: Student): EntityModel<Student> =
        EntityModel.of(entity).add(
            Link.of("/api/academia/students")
                .withRel("parent"),
            Link.of("/api/academia/students/${entity.id}")
                .withSelfRel(),
            Link.of("/api/academia/students/${entity.id}/lectures")
                .withRel("student-lecture")
        )

    fun toCollectionModel(page: Page<Student>, lectureId: Long? = null): CollectionModel<EntityModel<Student>> {
        val studentModels = page.content.map { this.toModel(it) }
        val baseUri = when {
            lectureId != null -> "/api/academia/professors/$lectureId/lectures"
            else -> "/api/academia/lectures"
        }

        return CollectionModel.of(studentModels).apply {
            LinkUtils.addPaginationLinks(this, baseUri, page)

            this.add(
                Link.of("${baseUri}search").withRel("search").withType("GET"),
                Link.of(baseUri).withRel("create-student").withType("POST"),
                Link.of("${baseUri}/{id}").withRel("delete-student").withType("DELETE"),
                Link.of("${baseUri}/{id}").withRel("update-student").withType("PATCH")
            )
        }
    }
}
