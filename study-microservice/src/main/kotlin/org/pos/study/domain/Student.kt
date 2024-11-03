package org.pos.study.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*

@Entity
data class Student(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    var firstName: String,

    var lastName: String,

    @Column( nullable = true)
    @Enumerated(EnumType.STRING)
    var cycleType: CycleType?,

    @Column(unique = true, nullable = false)
    var email: String,

    @Column( nullable = false)
    var studyYear: Int,

    @Column( nullable = false)
    var studentGroup: Int,

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY, cascade = [CascadeType.DETACH])
    @JoinTable(
        name = "student_lecture",
        joinColumns = [JoinColumn(name = "student_id")],
        inverseJoinColumns = [JoinColumn(name = "lecture_id")]
    )
    var lectures: MutableList<Lecture> = mutableListOf()

) {
    enum class CycleType { Licenta, Master }
}
