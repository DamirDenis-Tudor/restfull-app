db = db.getSiblingDB("lectures-db");

const courses = [
    {
        _id: "100",
        assessment_tests: [
            { type: "string", weight: 100 }
        ],
        "course-files": [
            {
                file_name: "4TeamOnlineChat_URD_Final-1.pdf",
                uploaded_at: ISODate("2024-11-13T16:08:29.910Z"),
                size: 301964
            }
        ],
        created_at: ISODate("2024-11-13T16:07:45.375Z"),
        "lab-files": [
            {
                file_name: "4TeamOnlineChat_URD_Final-1.pdf",
                uploaded_at: ISODate("2024-11-13T16:08:23.502Z"),
                size: 301964
            }
        ]
    },
    {
        _id: "101",
        assessment_tests: [
            { type: "multiple-choice", weight: 40 },
            { type: "essay", weight: 60 }
        ],
        "course-files": [
            {
                file_name: "CourseOutline.pdf",
                uploaded_at: ISODate("2024-11-14T10:05:00.000Z"),
                size: 102400
            }
        ],
        created_at: ISODate("2024-11-13T18:00:00.000Z"),
        "lab-files": [
            {
                file_name: "LabGuide.pdf",
                uploaded_at: ISODate("2024-11-14T10:10:00.000Z"),
                size: 204800
            }
        ]
    },
    {
        _id: "102",
        assessment_tests: [
            { type: "quiz", weight: 50 },
            { type: "project", weight: 50 }
        ],
        "course-files": [
            {
                file_name: "ProjectInstructions.pdf",
                uploaded_at: ISODate("2024-11-15T12:00:00.000Z"),
                size: 150000
            }
        ],
        created_at: ISODate("2024-11-13T20:00:00.000Z"),
        "lab-files": [
            {
                file_name: "LabSolutions.pdf",
                uploaded_at: ISODate("2024-11-15T12:30:00.000Z"),
                size: 250000
            }
        ]
    }
];

function isValidCourse(course) {
    const totalWeight = course.assessment_tests.reduce((sum, test) => sum + test.weight, 0);
    return totalWeight === 100;
}

const validCourses = courses.filter(isValidCourse);
if (validCourses.length > 0) {
    db.lectures.insertMany(validCourses);
    print("Inserted courses with valid assessment test weights.");
} else {
    print("No valid courses to insert: all courses failed the assessment test weight check.");
}
