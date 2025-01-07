import React, {useEffect, useState} from 'react';
import {EmbeddedResponse, fetchComponentData, Link, Student} from "../../api/hateoas.ts";
import {v4} from "uuid";
import {PaginationList} from "./PaginationList.tsx";
import StudentCard from "../cards/StudentCard.tsx";

interface StudentListProps {
    link?: Link;
}

export const StudentList: React.FC<StudentListProps> = ({link}) => {
    const [studentData, setStudentData] = useState<EmbeddedResponse<Student>>();
    const [currentLink, setCurrentLink] = useState<Link|undefined>(link);

    useEffect(() => {
        if (currentLink) {
            fetchComponentData<EmbeddedResponse<Student>>(currentLink)
                .then((data) => {
                    setStudentData(data);
                })
                .catch((error) => {
                    console.error("Error fetching lectures data:", error);
                });
        }
    }, [currentLink, link]);

    return (
        <>
            {currentLink && (
                <PaginationList
                    title="List of students"
                    data={studentData}
                    setCurrentLink={setCurrentLink}
                    renderItem={(student: Student | undefined) => {
                        return <StudentCard layout={'vertical'} key={v4()} stud={student}/>
                    }}
                />
            )}

        </>
    );
};
