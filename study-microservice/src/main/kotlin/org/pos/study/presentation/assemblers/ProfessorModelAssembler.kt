package org.pos.study.presentation.assemblers

import api.academia.Auth
import org.pos.study.persistence.entities.Professor
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
class ProfessorModelAssembler : RepresentationModelAssembler<Professor, EntityModel<Professor>> {
    @Value(value = "\${spring.study.host.address}")
    lateinit var studyAddress: String

    override fun toModel(entity: Professor): EntityModel<Professor> {
        val links = when (CurrentUserContext.getRole()) {
            Auth.Role.STUDENT -> {
                listOf(
                    Link.of("${studyAddress}/api/academia/professors&size=3")
                        .withType("GET")
                        .withRel("parent"),

                    Link.of("${studyAddress}/api/academia/professors/${entity.id}")
                        .withType("GET")
                        .withSelfRel(),
                    Link.of("${studyAddress}/api/academia/professors/${entity.id}/lectures")
                        .withType("GET")
                        .withRel("my-lectures")
                )
            }

            Auth.Role.PROFESSOR -> {
                listOf(
                    Link.of("${studyAddress}/api/academia/professors&size=3")
                        .withType("GET")
                        .withRel("parent"),
                    Link.of("${studyAddress}/api/academia/professors/${entity.id}")
                        .withSelfRel(),
                    Link.of("${studyAddress}/api/academia/professors/${entity.id}/lectures")
                        .withType("GET")
                        .withRel("my-lectures")
                ).let {
                    if (CurrentUserContext.getId().toInt() == entity.id)
                        it + listOf(
                            Link.of("${studyAddress}/api/academia/professors/${entity.id}")
                                .withType("GET")
                                .withRel("profile")
                        )
                    else it
                }
            }

            Auth.Role.ADMIN -> {
                listOf(
                    Link.of("${studyAddress}/api/academia/professors/${entity.id}")
                        .withType("GET")
                        .withSelfRel(),
                    Link.of("${studyAddress}/api/academia/professors/${entity.id}")
                        .withRel("update")
                        .withType("PUT"),
                    Link.of("${studyAddress}/api/academia/professors/${entity.id}")
                        .withRel("delete")
                        .withType("DELETE"),
                )
            }

            else -> emptyList()
        }

        return EntityModel.of(entity).add(links)
    }

    fun toCollectionModel(page: Page<Professor>): CollectionModel<EntityModel<Professor>> {
        val professorModels = page.content.map { this.toModel(it) }
        val baseUri = "${studyAddress}/api/academia/professors"

        return CollectionModel.of(professorModels).apply {
            LinkUtils.addPaginationLinks(this, baseUri, page)

            this.add(
                Link.of("${studyAddress}/api/academia/professors/search")
                    .withRel("search-professors")
                    .withType("GET"),
            )

            if (CurrentUserContext.getRole() == Auth.Role.ADMIN) {
                this.add(
                    Link.of("${studyAddress}/api/academia/professors")
                        .withRel("create")
                        .withType("POST"),
                )
            }
        }
    }
}