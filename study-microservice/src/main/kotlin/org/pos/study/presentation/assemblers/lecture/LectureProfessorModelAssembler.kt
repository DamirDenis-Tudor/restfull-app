package org.pos.study.presentation.assemblers.lecture

import api.academia.Auth
import org.pos.study.persistence.entities.Lecture
import org.pos.study.presentation.aspects.CurrentUserContext
import org.pos.study.presentation.assemblers.utils.LinkUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Page
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.Link
import org.springframework.hateoas.server.RepresentationModelAssembler
import org.springframework.stereotype.Component

@Component
class LectureProfessorModelAssembler(
    val lectureModelAssembler: LectureModelAssembler
) : RepresentationModelAssembler<Lecture, EntityModel<Lecture>> {
    @Value(value = "\${spring.lectures.host.hateoas}")
    lateinit var lecturesAddress: String

    @Value(value = "\${spring.study.host.hateoas}")
    lateinit var studyAddress: String

    override fun toModel(entity: Lecture): EntityModel<Lecture> {
       return lectureModelAssembler.toModel(entity)
    }

    fun toCollectionModel(
        page: Page<Lecture>
    ): CollectionModel<EntityModel<Lecture>> {
        val lectureModels = page.content.map { this.toModel(it) }

        val paginationUri = when(CurrentUserContext.getRole()){
            Auth.Role.STUDENT -> {
                "${studyAddress}/api/academia/students/${CurrentUserContext.getId()}/lectures"
            }
            Auth.Role.PROFESSOR -> {
                "${studyAddress}/api/academia/professors/${CurrentUserContext.getId()}/lectures"
            }
            else -> "${studyAddress}/api/academia/lectures"
        }

        return CollectionModel.of(lectureModels).apply {
            LinkUtils.addPaginationLinks(this, paginationUri, page)

            this.add(
                Link.of("${studyAddress}/api/academia/lectures/search")
                    .withRel("search")
                    .withType("GET"),
            )

            if (CurrentUserContext.getRole() == Auth.Role.PROFESSOR) {
                this.add(
                    Link.of("${studyAddress}/api/academia/lectures")
                        .withRel("create")
                        .withType("PUT"),
                )
            }
        }
    }
}