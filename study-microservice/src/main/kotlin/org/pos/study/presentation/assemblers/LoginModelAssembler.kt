package org.pos.study.presentation.assemblers

import api.academia.Auth
import org.pos.study.business.dto.login.LoginResponse
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
class LoginModelAssembler {
    @Value(value = "\${spring.study.host.address}")
    lateinit var studyAddress: String

    fun toModel(entity: LoginResponse, userId: String?): EntityModel<LoginResponse> {
        val links = when (entity.role) {
            Auth.Role.ADMIN -> {
                listOf(
                    Link.of("${studyAddress}/api/academia/login")
                        .withSelfRel(),
                    Link.of("${studyAddress}/api/academia/professors")
                        .withRel("professors"),
                    Link.of("${studyAddress}/api/academia/students")
                        .withRel("students")
                )
            }

            Auth.Role.STUDENT -> {
                listOf(
                    Link.of("${studyAddress}/api/academia/login")
                        .withSelfRel(),
                    Link.of("${studyAddress}/api/academia/students/{studentId}/lectures")
                        .withRel("lectures"),
                    Link.of("${studyAddress}/api/academia/students/me")
                        .withRel("me"),
                )
            }

            Auth.Role.PROFESSOR -> {
                listOf(
                    Link.of("${studyAddress}/api/academia/login")
                        .withSelfRel(),
                    Link.of("${studyAddress}/api/academia/professors/${userId}")
                        .withRel("Profile"),
                    Link.of("${studyAddress}/api/academia/lectures?page=1&size=5")
                        .withRel("All Lectures"),
                    Link.of("${studyAddress}/api/academia/professors/${userId}/lectures")
                        .withRel("My Lectures"),
                )
            }

            Auth.Role.UNKNOWN -> emptyList<Link>()
            Auth.Role.UNRECOGNIZED -> emptyList<Link>()
        }

        return EntityModel.of(entity).add(links)
    }
}
