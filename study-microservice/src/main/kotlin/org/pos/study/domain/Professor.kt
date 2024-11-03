package org.pos.study.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import jakarta.persistence.*
import jakarta.validation.constraints.Size

@Entity
data class Professor(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "professor_id")
    var id: Int = 0,

    var firstName: String,

    var lastName: String,

    var email: String,

    var affiliation: String,

    @Enumerated(EnumType.STRING)
    var graderType: GraderType,

    @Enumerated(EnumType.STRING)
    var associationType: AssociationType,

    @JsonIgnore
    @OneToMany(mappedBy = "professor", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var lectures: MutableList<Lecture> = mutableListOf()
) {
    enum class GraderType { Asistent, Conferentiar, Profesor, TitularLaborator }

    enum class AssociationType { Titular, Asociat, Extern }
}