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

    @Column( nullable = false)
    var firstName: String,

    @Column( nullable = false)
    var lastName: String,

    @Column( nullable = false, unique = true)
    var email: String,

    @Column( nullable = true )
    var affiliation: String?,

    @Column( nullable = false)
    @Enumerated(EnumType.STRING)
    var graderType: GraderType,

    @Column( nullable = false)
    @Enumerated(EnumType.STRING)
    var associationType: AssociationType,

    @JsonIgnore
    @OneToMany(mappedBy = "professor", fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true)
    var lectures: MutableList<Lecture> = mutableListOf()
) {
    enum class GraderType { Asistent, Conferentiar, Profesor, TitularLaborator }

    enum class AssociationType { Titular, Asociat, Extern }
}