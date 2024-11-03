package org.pos.study.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

@Entity
data class Student(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long,

    @NotNull
    @Size(min = 3, max = 20)
    var firstName: String,

    @NotNull
    @Size(min = 3, max = 20)
    var lastName: String,

    @Enumerated(EnumType.STRING)
    @NotNull
    var cycleType: CycleType?,

    @Column(unique = true)
    @NotNull
    var email: String,

    @NotNull
    var studyYear: Int,

    @NotNull
    var studentGroup: Int,

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "student_lecture",
        joinColumns = [JoinColumn(name = "student_id")],
        inverseJoinColumns = [JoinColumn(name = "lecture_id")]
    )
    var lectures: MutableList<Lecture> = mutableListOf()

) {
    enum class CycleType { Licenta, Master }
}
