package org.pos.study.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import jakarta.validation.constraints.NotNull

@Entity
data class Lecture(
    @JsonIgnore
    @Id
    var id: String,

    @Column(unique = true, length = 20, nullable = false)
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