package org.pos.study.controllers.assemblers

import org.pos.study.domain.Professor
import org.pos.study.utils.LinkUtils
import org.springframework.data.domain.Page
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.Link
import org.springframework.hateoas.server.RepresentationModelAssembler
import org.springframework.stereotype.Component

@Component
class ProfessorModelAssembler : RepresentationModelAssembler<Professor, EntityModel<Professor>> {
    override fun toModel(entity: Professor): EntityModel<Professor> =
        EntityModel.of(entity).add(
                Link.of("/api/academia/professors")
                    .withRel("parent"),
                Link.of("/api/academia/professors/${entity.id}")
                    .withSelfRel(),
                Link.of("/api/academia/professors/${entity.id}/lectures")
                    .withRel("professor-lectures")
            )


    fun toCollectionModel(page: Page<Professor>): CollectionModel<EntityModel<Professor>> {
        val professorModels = page.content.map { this.toModel(it) }
        val baseUri = "/api/academia/professors"

        return CollectionModel.of(professorModels).apply {
            LinkUtils.addPaginationLinks(this, baseUri, page)

            this.add(
                Link.of("/api/academia/professors/search").withRel("search-professors").withType("GET"),
                Link.of("/api/academia/professors").withRel("create-professor").withType("POST"),
                Link.of("/api/academia/professors/{id}").withRel("update-professor").withType("PATCH"),
                Link.of("/api/academia/professors/{id}").withRel("delete-professor").withType("DELETE")
            )
        }
    }
}
