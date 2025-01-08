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
class LectureModelAssembler : RepresentationModelAssembler<Lecture, EntityModel<Lecture>> {
    @Value(value = "\${spring.lectures.host.address}")
    lateinit var lecturesAddress: String

    @Value(value = "\${spring.study.host.address}")
    lateinit var studyAddress: String

    override fun toModel(entity: Lecture): EntityModel<Lecture> {
        val links = when (CurrentUserContext.getRole()) {
            Auth.Role.STUDENT -> {
                listOf(
                    Link.of("${studyAddress}/api/academia/lectures/${entity.id}")
                        .withSelfRel()
                        .withType("GET"),

                    Link.of("${studyAddress}/api/academia/lectures/${entity.id}/professors")
                        .withRel("professor")
                        .withType("GET"),

                    Link.of("${lecturesAddress}/api/academia/lectures/${entity.id}/assessments")
                        .withRel("assessments")
                        .withType("GET"),

                    Link.of("${lecturesAddress}/api/academia/lectures/${entity.id}/files")
                        .withRel("files")
                        .withType("GET"),
                )
            }

            Auth.Role.PROFESSOR -> {
                listOf(
                    Link.of("${studyAddress}/api/academia/lectures")
                        .withRel("parent")
                        .withType("GET"),

                    Link.of("${studyAddress}/api/academia/lectures/${entity.id}")
                        .withSelfRel()
                        .withType("GET"),

                    Link.of("${studyAddress}/api/academia/lectures/${entity.id}/professors")
                        .withRel("professor")
                        .withType("GET"),

                    Link.of("${studyAddress}/api/academia/lectures/${entity.id}/professors")
                        .withRel("profile")
                        .withType("GET"),

                    Link.of("${lecturesAddress}/api/academia/lectures/${entity.id}/assessments")
                        .withRel("assessments")
                        .withType("GET"),

                    Link.of("${lecturesAddress}/api/academia/lectures/${entity.id}/files")
                        .withRel("files")
                        .withType("GET"),
                ).let {
                    if (CurrentUserContext.getId().toInt() == (entity.professor?.id ?: -1))
                        it + listOf(
                            Link.of("${studyAddress}/api/academia/lectures/${entity.id}/students")
                                .withRel("students")
                                .withType("GET"),
                            Link.of("${studyAddress}/api/academia/lectures/${entity.id}")
                                .withRel("update")
                                .withType("PUT"),
                            Link.of("${studyAddress}/api/academia/lectures/${entity.id}")
                                .withRel("delete")
                                .withType("DELETE"),
                            )
                    else it
                }
            }

            else -> emptyList()
        }

        return EntityModel.of(entity).add(links)
    }

    fun toCollectionModel(
        page: Page<Lecture>
    ): CollectionModel<EntityModel<Lecture>> {
        val lectureModels = page.content.map { this.toModel(it) }

        val paginationUri = "${studyAddress}/api/academia/lectures"

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