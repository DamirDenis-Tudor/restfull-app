package org.pos.study.presentation.assemblers

import org.pos.study.persistence.entities.Professor
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

    override fun toModel(entity: Professor): EntityModel<Professor> =
        EntityModel.of(entity).add(
            Link.of("${studyAddress}/api/academia/professors&size=3")
                .withRel("parent"),
            Link.of("${studyAddress}/api/academia/professors/${entity.id}")
                .withSelfRel(),
            Link.of("${studyAddress}/api/academia/professors/${entity.id}/lectures")
                .withRel("my-lectures")
        )


    fun toCollectionModel(page: Page<Professor>): CollectionModel<EntityModel<Professor>> {
        val professorModels = page.content.map { this.toModel(it) }
        val baseUri = "${studyAddress}/api/academia/professors"

        return CollectionModel.of(professorModels).apply {
            LinkUtils.addPaginationLinks(this, baseUri, page)

            this.add(
                Link.of("${studyAddress}/api/academia/professors/search").withRel("search-professors").withType("GET"),
            )
        }
    }
}