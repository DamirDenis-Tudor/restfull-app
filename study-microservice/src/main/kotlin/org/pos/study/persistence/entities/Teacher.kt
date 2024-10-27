package org.pos.study.persistence.entities

import jakarta.persistence.*
import jakarta.validation.constraints.Size

@Entity
data class Teacher(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "teacher_id")
    var id: Int,

    @Size(min = 3, max = 20)
    var firstName: String,

    @Size(min = 3, max = 20)
    var lastName: String,

    var email: String,

    var affiliation: String,

    @Enumerated(EnumType.STRING)
    var graderType: GraderType,

    @Enumerated(EnumType.STRING)
    var associationType: AssociationType,

    @OneToMany(mappedBy = "teacher", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var disciplines: MutableList<Discipline> = mutableListOf()
) {
    enum class GraderType { Asistent, Conferentiar, Profesor, TitularLaborator }

    enum class AssociationType { Titular, Asociat, Extern }
}