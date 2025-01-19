import React, { useEffect, useState } from 'react';
import { EmbeddedResponse, fetchLink, Link, Student } from "../../api/hateoas.ts";
import { v4 } from "uuid";
import StudentCard from "../cards/StudentCard.tsx";
import {PaginationListVerticalClickable} from "./PaginationListVerticalClickable.tsx";


interface StudentListProps {
    link?: Link;
}

export const StudentListSelectable: React.FC<StudentListProps> = ({ link }) => {
    const [studentData, setStudentData] = useState<EmbeddedResponse<Student>>();
    const [currentLink, setCurrentLink] = useState<Link | undefined>(link);

    useEffect(() => {
        if (currentLink) {
            fetchLink<EmbeddedResponse<Student>, undefined>(currentLink, undefined)
                .then((data) => {
                    setStudentData(data);
                })
                .catch((error) => {
                    console.error("Error fetching student data:", error);
                });
        }
    }, [currentLink, link]);

    return (
        <>
            {currentLink && studentData && (
                <PaginationListVerticalClickable
                    key={"students"}
                    title="List of students"
                    data={studentData}
                    setCurrentLink={setCurrentLink}
                    renderItem={(student: Student | undefined) => {
                        return <StudentCard layout={'horizontal'} key={v4()} stud={student} onClickOverride = {() =>  console.log("Tessstttt") } />;
                    }}
                />
            )}
        </>
    );
};
