package org.pos.study.persistence.entities

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

@Entity
data class Student(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int = 0,

    @NotNull
    @Size(min = 3, max = 20)
    var firstName: String,

    @NotNull
    @Size(min = 3, max = 20)
    var lastName: String,

    @Enumerated(EnumType.STRING)
    @NotNull
    var cycleType: CycleType,

    @Column(unique = true)
    @NotNull
    var email: String,

    @NotNull
    var studyYear: Int,

    @NotNull
    var studentGroup: Int,

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "student_discipline",
        joinColumns = [JoinColumn(name = "student_id")],
        inverseJoinColumns = [JoinColumn(name = "discipline_id")]
    )
    var disciplines: MutableList<Discipline> = mutableListOf()

) {
    enum class CycleType { Licenta, Master }
}
