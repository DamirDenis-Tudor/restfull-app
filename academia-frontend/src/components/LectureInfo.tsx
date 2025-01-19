import React, {useState, useEffect} from 'react';
import {Spinner, Col, Button} from 'react-bootstrap';
import {
    Lecture,
    Link,
    AssessmentTest,
    EmbeddedResponse,
    FileInfo,
    fetchLink
} from "../api/hateoas.ts";
import AssessmentsSection from "./cards/AssesmentsCard.tsx";
import FilesSection from "./lists/FileSection.tsx";
import LectureCard from "./cards/LectureCard.tsx";
import ProfessorCard from "./cards/ProfessorCard.tsx";
import {StudentList} from "./lists/StudentList.tsx";
import {ItemsList} from "./lists/ItemsList.tsx";
import {toast} from "react-toastify";
import {EnrollUnenrollModal} from "./modals/EnrollUnenrollModal.tsx";

interface FullLectureCardProps {
    lectureLink: Link;
    assessmentLink: Link;
    professorLink: Link;
    filesLink: Link;
}

export const LectureInfo: React.FC<FullLectureCardProps> = (
    {
        lectureLink,
        assessmentLink,
        professorLink,
        filesLink,
    }) => {
    const [lectureInfo, setLectureInfo] = useState<Lecture>();
    const [assessments, setAssessments] = useState<EmbeddedResponse<AssessmentTest>>();
    const [files, setFiles] = useState<EmbeddedResponse<FileInfo>>();
    const [showModal, setShowModal] = useState(false);
    const [studentIds, setStudentIds] = useState<string>('');
    const [isEnrolling, setIsEnrolling] = useState<boolean>(true);

    useEffect(() => {
        fetchLink<Lecture, undefined>(lectureLink, undefined)
            .then(setLectureInfo)
            .catch((error) => console.error("Error fetching lectureLink data:", error));

        fetchLink<EmbeddedResponse<AssessmentTest>, undefined>(assessmentLink, undefined)
            .then(setAssessments)
            .catch((error) => console.error("Error fetching assessmentLink data:", error));

        fetchLink<EmbeddedResponse<FileInfo>, undefined>(filesLink, undefined)
            .then(setFiles)
            .catch((error) => console.error("Error fetching filesLink data:", error));
    }, [lectureLink, assessmentLink, filesLink]);

    const handleEnrollUnenroll = () => {
        const studentIdsArray = studentIds.split(',')
            .map((id) => id.trim())
            .filter((id) => !isNaN(Number(id)))
            .map(Number);

        if (studentIdsArray.length === 0) {
            toast.error("Please provide valid student IDs.");
            return;
        }

        const link = isEnrolling ? lectureInfo?._links.enroll : lectureInfo?._links.unenroll;
        if (link) {
            fetchLink(link, studentIdsArray)
                .then(() => {
                    toast.success(`Successfully ${isEnrolling ? 'enrolled' : 'unenrolled'} students`);
                    setShowModal(false);
                })
                .catch((error) => {
                    toast.error(error.message || `An error occurred while ${isEnrolling ? 'enrolling' : 'unenrolling'} students.`);
                });
        }
    };

    const handleCloseModal = () => setShowModal(false);
    const handleShowModal = (isEnrollingAction: boolean) => {
        setIsEnrolling(isEnrollingAction);
        setShowModal(true);
    };

    if (!lectureInfo) {
        return (
            <div className="d-flex justify-content-center align-items-center" style={{height: '100vh'}}>
                <Spinner animation="border"/>
            </div>
        );
    }

    return (
        <div className="justify-content-center">
            <Col md={12} lg={11}>
                <ItemsList
                    items={[
                        <ProfessorCard layout={"vertical"} link={professorLink}/>,
                        <AssessmentsSection key={"3"} assessments={assessments}/>,
                        <LectureCard key={"3"} lecture={lectureInfo} clickable={false}/>,
                    ]}
                    title={'Lecture Details'}
                />

                <FilesSection fData={files}/>

                <div className="d-flex justify-content-center mt-3 mb-10">
                    {lectureInfo?._links.enroll && (
                        <Button variant="success" onClick={() => handleShowModal(true)} className="mr-2">
                            Enroll Students
                        </Button>
                    )}
                    {lectureInfo?._links.unenroll && (
                        <Button variant="danger" onClick={() => handleShowModal(false)} className="ml-2">
                            Unenroll Students
                        </Button>
                    )}
                </div>

                <StudentList link={lectureInfo?._links.students}/>
            </Col>

            <EnrollUnenrollModal
                show={showModal}
                isEnrolling={isEnrolling}
                studentIds={studentIds}
                studentLink={isEnrolling ? lectureInfo?._links.studentsNotEnrolled : lectureInfo?._links.students}
                onStudentIdsChange={setStudentIds}
                handleClose={handleCloseModal}
                handleAction={handleEnrollUnenroll}
            />
        </div>
    );
};
