package org.pos.study.presentation.assemblers.student

import api.academia.Auth
import org.pos.study.persistence.entities.Student
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
class StudentLectureModelAssembler(
    val studentModelAssembler: StudentModelAssembler
) : RepresentationModelAssembler<Student, EntityModel<Student>> {
    @Value(value = "\${spring.study.host.hateoas}")
    lateinit var studyAddress: String

    override fun toModel(entity: Student): EntityModel<Student> {
        return studentModelAssembler.toModel(entity)
    }

    fun toCollectionModel(page: Page<Student>, lectureId: Long, subPath: String = ""): CollectionModel<EntityModel<Student>> {
        val studentModels = page.content.map { this.toModel(it) }
        val baseUri = when {
            subPath.isNotBlank()-> "$studyAddress/api/academia/lectures/$lectureId/students/$subPath"
            else ->  "$studyAddress/api/academia/lectures/$lectureId/students"
        }

        return CollectionModel.of(studentModels).apply {
            LinkUtils.addPaginationLinks(this, baseUri, page)

            this.add(
                Link.of("$studyAddress/api/academia/students/search")
                    .withRel("search")
                    .withType("GET"),
            )

            if (CurrentUserContext.getRole() == Auth.Role.ADMIN) {
                this.add(
                    Link.of("$studyAddress/api/academia/students")
                        .withRel("create")
                        .withType("POST"),
                )
            }
        }
    }
}