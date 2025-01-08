package org.pos.study.persistence.entities

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import org.pos.study.persistence.entities.Professor.GraderType

@Entity
data class Lecture(
    @Id
    var id: String,

    @Column(unique = true, length = 20, nullable = false)
    var lectureName: String,

    @Column(nullable = false)
    var studyYear: Int,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var lectureType: LectureType,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var categoryType: CategoryType,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var examinationType: ExaminationType,

    @ManyToOne(fetch = FetchType.EAGER, optional = true)
    @JoinColumn(name = "professor_id", nullable = true)
    @JsonIgnore
    var professor: Professor? = null,

    @ManyToMany(mappedBy = "lectures", fetch = FetchType.LAZY)
    @JsonIgnore
    var students: MutableList<Student> = mutableListOf(),
) {
    enum class LectureType {
        Impusa, Optionala, LiberAleasa;

        companion object {
            @JsonCreator
            @JvmStatic
            fun fromString(value: String): LectureType {
                return LectureType.entries.find { it.name.equals(value, ignoreCase = true) }
                    ?: throw IllegalArgumentException("Invalid lecture type: $value accepted values: ${LectureType.entries.map { it.name }}")
            }
        }
    }

    enum class CategoryType {
        Domeniu, Specialitate, Adiacenta;

        companion object {
            @JsonCreator
            @JvmStatic
            fun fromString(value: String): CategoryType {
                return CategoryType.entries.find { it.name.equals(value, ignoreCase = true) }
                    ?: throw IllegalArgumentException("Invalid category type: $value accepted values: ${CategoryType.entries.map { it.name }}")
            }
        }
    }

    enum class ExaminationType {
        Examen, Cologviu;

        companion object {
            @JsonCreator
            @JvmStatic
            fun fromString(value: String): ExaminationType {
                return ExaminationType.entries.find { it.name.equals(value, ignoreCase = true) }
                    ?: throw IllegalArgumentException("Invalid examination type: $value accepted values: ${ExaminationType.entries.map { it.name }}")
            }
        }
    }

    override fun toString(): String = this.let { it.students = mutableListOf(); }.toString()
}