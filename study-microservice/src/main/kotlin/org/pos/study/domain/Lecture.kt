package org.pos.study.domain

import com.fasterxml.jackson.annotation.JsonBackReference
import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

@Entity
data class Discipline(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int = 0,

    @NotNull
    @Size(min = 3, max = 20)
    var disciplineName: String,

    @NotNull
    var studyYear: Int,

    @NotNull
    @Enumerated(EnumType.STRING)
    var disciplineType: DisciplineType,

    @NotNull
    @Enumerated(EnumType.STRING)
    var categoryType: CategoryType,

    @NotNull
    @Enumerated(EnumType.STRING)
    var examinationType: ExaminationType,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id")
    @JsonBackReference
    var teacher: Teacher?,

    @ManyToMany(mappedBy = "disciplines", fetch = FetchType.LAZY)
    @JsonBackReference
    var students: MutableList<Student> = mutableListOf()

) {
    enum class DisciplineType { Impusa, Optionala, LiberAleasa }

    enum class CategoryType { Domeniu, Specialitate, Adiacenta }

    enum class ExaminationType { Examen, Cologviu }
}