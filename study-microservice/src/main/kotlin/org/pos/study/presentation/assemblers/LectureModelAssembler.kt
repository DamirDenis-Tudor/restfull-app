package org.pos.study.presentation.assemblers

import org.pos.study.persistence.entities.Lecture
import org.pos.study.presentation.assemblers.utils.LinkUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Page
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.Link
import org.springframework.hateoas.server.RepresentationModelAssembler
import org.springframework.stereotype.Component

@Component
class LectureModelAssembler : RepresentationModelAssembler<Lecture, EntityModel<Lecture>> {
    @Value(value = "\${spring.lectures.host.address}")
    lateinit var lecturesAddress: String

    @Value(value = "\${spring.study.host.address}")
    lateinit var studyAddress: String

    override fun toModel(entity: Lecture): EntityModel<Lecture> =
        EntityModel.of(entity).apply {
            this.add(
                Link.of("${studyAddress}/api/academia/lectures")
                    .withRel("parent")
                    .withType("GET"),

                Link.of("${studyAddress}/api/academia/lectures/${entity.id}")
                    .withSelfRel()
                    .withType("GET"),

                Link.of("${studyAddress}/api/academia/lectures/${entity.id}/professors")
                    .withRel("professor")
                    .withType("GET"),

                Link.of("${studyAddress}/api/academia/lectures/${entity.id}/students")
                    .withRel("students")
                    .withType("GET"),

                Link.of("${lecturesAddress}/api/academia/lectures/${entity.id}/assessments")
                    .withRel("assessments")
                    .withType("GET"),

                Link.of("${lecturesAddress}/api/academia/lectures/${entity.id}/files")
                    .withRel("files")
                    .withType("GET"),
            )
        }

    fun toCollectionModel(
        page: Page<Lecture>,
        professorId: Long? = null,
        studentId: Long? = null,
    ): CollectionModel<EntityModel<Lecture>> {
        val lectureModels = page.content.map { this.toModel(it) }
        val baseUri = when {
            professorId != null -> "${studyAddress}/api/academia/professors/$professorId/lectures"
            studentId != null -> "${studyAddress}/api/academia/students/$studentId/lectures"
            else -> "${studyAddress}/api/academia/lectures"
        }

        return CollectionModel.of(lectureModels).apply {
            LinkUtils.addPaginationLinks(this, baseUri, page)

            this.add(
                Link.of("${studyAddress}/api/academia/lectures/search")
                    .withRel("search")
                    .withType("GET"),
            )
        }
    }
}
