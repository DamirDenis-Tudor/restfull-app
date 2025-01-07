import React, { useEffect, useState } from 'react';
import { EmbeddedResponse, fetchLink, Link, Student } from "../../api/hateoas.ts";
import { v4 } from "uuid";
import { PaginationList } from "./PaginationList.tsx";
import StudentCard from "../cards/StudentCard.tsx";
import StudentModal from "../modals/StudentModal.tsx";

interface StudentListProps {
    link?: Link;
}

export const StudentList: React.FC<StudentListProps> = ({ link }) => {
    const [studentData, setStudentData] = useState<EmbeddedResponse<Student>>();
    const [currentLink, setCurrentLink] = useState<Link | undefined>(link);
    const [showModal, setShowModal] = useState<boolean>(false);
    const [modalLink, setModalLink] = useState<Link | undefined>(undefined);

    useEffect(() => {
        if (currentLink) {
            fetchLink<EmbeddedResponse<Student>>(currentLink)
                .then((data) => {
                    setStudentData(data);
                })
                .catch((error) => {
                    console.error("Error fetching student data:", error);
                });
        }
    }, [currentLink, link]);

    const openModal = () => {
        if (studentData?._links["create"]) {
            setModalLink(studentData._links["create"]);
            setShowModal(true);
        }
    };

    return (
        <>
            {currentLink && studentData && (
                <PaginationList
                    title="List of students"
                    data={studentData}
                    setCurrentLink={setCurrentLink}
                    renderItem={(student: Student | undefined) => {
                        return <StudentCard layout={'vertical'} key={v4()} stud={student} />;
                    }}
                    onAddElement={openModal}
                />
            )}

            {showModal && modalLink && (
                <StudentModal
                    link={modalLink}
                    student={undefined}
                    onClose={() => setShowModal(false)}
                />
            )}
        </>
    );
};
