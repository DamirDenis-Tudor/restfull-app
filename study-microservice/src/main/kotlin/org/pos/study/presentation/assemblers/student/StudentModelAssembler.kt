package org.pos.study.presentation.assemblers.student

import org.pos.study.persistence.entities.Student
import org.pos.study.presentation.assemblers.utils.LinkUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Page
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.Link
import org.springframework.hateoas.server.RepresentationModelAssembler
import org.springframework.stereotype.Component

@Component
class StudentModelAssembler : RepresentationModelAssembler<Student, EntityModel<Student>> {
    @Value(value = "\${spring.study.host.address}")
    lateinit var studyAddress: String

    override fun toModel(entity: Student): EntityModel<Student> {
        return EntityModel.of(entity).add(
            Link.of("${studyAddress}/api/academia/students")
                .withRel("parent"),
            Link.of("$studyAddress/api/academia/students/${entity.id}")
                .withSelfRel(),
            Link.of("${studyAddress}/api/academia/students/${entity.id}/lectures")
                .withRel("lectures"),
        )
    }

    fun toCollectionModel(page: Page<Student>, lectureId: Long? = null): CollectionModel<EntityModel<Student>> {
        val studentModels = page.content.map { this.toModel(it) }
        val baseUri = when {
            lectureId != null -> "$studyAddress/api/academia/lectures/$lectureId/lectures"
            else -> "$studyAddress/api/academia/students"
        }

        return CollectionModel.of(studentModels).apply {
            LinkUtils.addPaginationLinks(this, baseUri, page)

            this.add(
                Link.of("$studyAddress/api/academia/students/search").withRel("search").withType("GET"),
            )
        }
    }
}