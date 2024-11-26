package org.pos.study.presentation.assemblers

import org.pos.study.persistence.entities.Lecture
import org.pos.study.presentation.assemblers.utils.LinkUtils
import org.springframework.data.domain.Page
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.Link
import org.springframework.hateoas.server.RepresentationModelAssembler
import org.springframework.stereotype.Component

@Component
class LectureModelAssembler : RepresentationModelAssembler<Lecture, EntityModel<Lecture>> {
    override fun toModel(entity: Lecture): EntityModel<Lecture> =
        EntityModel.of(entity).apply {
            this.add(
                Link.of("/api/academia/lectures")
                    .withRel("parent")
                    .withType("GET"),

                Link.of("/api/academia/lectures/${entity.id}")
                    .withSelfRel()
                    .withType("GET"),

                Link.of("/api/academia/lectures/${entity.id}/professors")
                    .withRel("lecture-professor")
                    .withType("GET"),

                Link.of("/api/academia/lectures/${entity.id}/students")
                    .withRel("lecture-student")
                    .withType("GET"),

                Link.of("/api/academia/lectures/${entity.id}/students/enroll")
                    .withRel("enroll-students")
                    .withType("PATCH"),

                Link.of("/api/academia/lectures/${entity.id}/students/unenroll")
                    .withRel("unroll-students")
                    .withType("PATCH")
            )
        }

    fun toCollectionModel(
        page: Page<Lecture>,
        professorId: Long? = null,
        studentId: Long? = null
    ): CollectionModel<EntityModel<Lecture>> {
        val lectureModels = page.content.map { this.toModel(it) }
        val baseUri = when {
            professorId != null -> "/api/academia/professors/$professorId/lectures"
            studentId != null -> "/api/academia/students/$studentId/lectures"
            else -> "/api/academia/lectures"
        }

        return CollectionModel.of(lectureModels).apply {
            LinkUtils.addPaginationLinks(this, baseUri, page)

            this.add(
                Link.of("/api/academia/lectures/search")
                    .withRel("search")
                    .withType("GET"),
                Link.of("/api/academia/lectures")
                    .withRel("create-lecture")
                    .withType("POST"),
                Link.of("/api/academia/lectures/{id}")
                    .withRel("delete-lecture")
                    .withType("DELETE"),
                Link.of("/api/academia/lectures/{id}")
                    .withRel("update-lecture")
                    .withType("PATCH")
            )
        }
    }
}
