package org.pos.study.presentation.assemblers

import org.pos.study.persistence.entities.Student
import org.pos.study.presentation.assemblers.utils.LinkUtils
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
                .withRel("student-lecture"),

            Link.of("/api/academia/students/${entity.id}/lectures/{lectureId}")
                .withRel("enroll-student")
                .withType("POST"),

            Link.of("/api/academia/students/${entity.id}/lectures/{lectureId}")
                .withRel("unroll-student")
                .withType("DELETE")
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
                Link.of("/api/academia/students/search").withRel("search").withType("GET"),
                Link.of("/api/academia/students").withRel("create-student").withType("POST"),
                Link.of("/api/academia/students/{id}").withRel("delete-student").withType("DELETE"),
                Link.of("/api/academia/students/{id}").withRel("update-student").withType("PATCH")
            )
        }
    }
}
