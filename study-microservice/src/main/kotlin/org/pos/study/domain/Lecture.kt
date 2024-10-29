package org.pos.study.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

@Entity
data class Lecture(
    @JsonIgnore
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int = 0,

    @NotNull
    @Size(min = 3, max = 20)
    var lectureName: String,

    @NotNull
    var studyYear: Int,

    @NotNull
    @Enumerated(EnumType.STRING)
    var lectureType: LectureType,

    @NotNull
    @Enumerated(EnumType.STRING)
    var categoryType: CategoryType,

    @NotNull
    @Enumerated(EnumType.STRING)
    var examinationType: ExaminationType,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "professor_id")
    @NotNull
    @JsonIgnore
    var professor: Professor,

    @ManyToMany(mappedBy = "lectures", fetch = FetchType.LAZY)
    @JsonIgnore
    var students: MutableList<Student> = mutableListOf(),
) {
    enum class LectureType { Impusa, Optionala, LiberAleasa }

    enum class CategoryType { Domeniu, Specialitate, Adiacenta }

    enum class ExaminationType { Examen, Cologviu }
}
