package org.pos.study.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*

@Entity
data class Lecture(
    @Id
    var id: String,

    @Column(unique = true, length = 20, nullable = false)
    var lectureName: String,

    @Column( nullable = false)
    var studyYear: Int,

    @Column( nullable = false)
    @Enumerated(EnumType.STRING)
    var lectureType: LectureType,

    @Column( nullable = false)
    @Enumerated(EnumType.STRING)
    var categoryType: CategoryType,

    @Column( nullable = false)
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
    enum class LectureType { Impusa, Optionala, LiberAleasa }

    enum class CategoryType { Domeniu, Specialitate, Adiacenta }

    enum class ExaminationType { Examen, Cologviu }
}