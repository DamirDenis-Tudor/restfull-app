// .scripts/2-init-mongo.js

db = db.getSiblingDB("lectures-db");

const courses = [
    {
        _id: "100",
        assessment_tests: [
            { type: "string", weight: 100 }
        ],
        created_at: ISODate("2024-11-13T16:07:45.375Z"),
    },
    {
        _id: "101",
        assessment_tests: [
            { type: "multiple-choice", weight: 40 },
            { type: "essay", weight: 60 }
        ],
    },
    {
        _id: "102",
        assessment_tests: [
            { type: "quiz", weight: 50 },
            { type: "project", weight: 50 }
        ],
        created_at: ISODate("2024-11-13T20:00:00.000Z"),
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
