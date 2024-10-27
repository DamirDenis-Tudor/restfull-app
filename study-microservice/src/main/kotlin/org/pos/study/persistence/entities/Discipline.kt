package org.pos.study.persistence.entities

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
    var firstName: String,

    @NotNull
    @Size(min = 3, max = 20)
    var lastName: String,

    @Column(unique = true)
    @NotNull
    var email: String,

    @NotNull
    var studyYear: Int,

    @NotNull
    var studentGroup: Int,

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
    var teacher: Teacher,

    @ManyToMany(mappedBy = "disciplines", fetch = FetchType.LAZY)
    var students: MutableList<Student> = mutableListOf()

) {
    enum class DisciplineType { Impusa, Optionala, LiberAleasa }

    enum class CategoryType { Domeniu, Specialitate, Adiacenta }

    enum class ExaminationType { Examen, Cologviu }
}