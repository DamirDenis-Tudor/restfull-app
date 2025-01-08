package org.pos.study.persistence.entities

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import org.pos.study.persistence.entities.Student.CycleType

@Entity
data class Professor(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "professor_id")
    var id: Int = 0,

    @Column(nullable = false)
    var firstName: String,

    @Column(nullable = false)
    var lastName: String,

    @Column(nullable = false, unique = true)
    var email: String,

    @Column(nullable = true)
    var affiliation: String?,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var graderType: GraderType,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var associationType: AssociationType,

    @JsonIgnore
    @OneToMany(mappedBy = "professor", fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true)
    var lectures: MutableList<Lecture> = mutableListOf()
) {
    enum class GraderType {
        Asistent, Conferentiar, Profesor, TitularLaborator;

        companion object {
            @JsonCreator
            @JvmStatic
            fun fromString(value: String): GraderType {
                return GraderType.entries.find { it.name.equals(value, ignoreCase = true) }
                    ?: throw IllegalArgumentException("Invalid grade type: $value accepted values: ${GraderType.entries.map { it.name }}")
            }
        }
    }

    enum class AssociationType {
        Titular, Asociat, Extern;

        companion object {
            @JsonCreator
            @JvmStatic
            fun fromString(value: String): AssociationType {
                return AssociationType.entries.find { it.name.equals(value, ignoreCase = true) }
                    ?: throw IllegalArgumentException("Invalid association type: $value accepted values: ${AssociationType.entries.map { it.name }}")
            }
        }
    }

    override fun toString(): String = this.let { it.lectures = mutableListOf() }.toString()

}