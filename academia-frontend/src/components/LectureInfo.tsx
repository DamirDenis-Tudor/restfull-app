import React, {useState, useEffect} from 'react';
import {Spinner, Col, Button, Modal, Form} from 'react-bootstrap';
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
        filesLink
    }) => {

    const [lectureInfo, setLectureInfo] = useState<Lecture>();
    const [assessments, setAssessments] = useState<EmbeddedResponse<AssessmentTest>>();
    const [files, setFiles] = useState<EmbeddedResponse<FileInfo>>();
    const [showModal, setShowModal] = useState(false);
    const [studentIds, setStudentIds] = useState<string>('');
    const [isEnrolling, setIsEnrolling] = useState<boolean>(true);

    useEffect(() => {
        fetchLink<Lecture, undefined>(lectureLink, undefined)
            .then((data) => {
                setLectureInfo(data);
            })
            .catch((error) => {
                console.error("Error fetching lectureLink data:", error);
            });

        fetchLink<EmbeddedResponse<AssessmentTest>, undefined>(assessmentLink, undefined)
            .then((data) => {
                setAssessments(data);
            })
            .catch((error) => {
                console.error("Error fetching assessmentLink data:", error);
            });

        fetchLink<EmbeddedResponse<FileInfo>, undefined>(filesLink, undefined)
            .then((data) => {
                setFiles(data);
            })
            .catch((error) => {
                console.error("Error fetching filesLink data:", error);
            });
    }, [lectureLink, assessmentLink, filesLink]);

    const handleEnrollUnenroll = () => {
        const studentIdsArray = studentIds.split(',')
            .map(id => id.trim())
            .filter(id => !isNaN(Number(id)))
            .map(id => Number(id));

        if (studentIdsArray.length === 0) {
            toast.error("Please provide valid student IDs.");
            return;
        }

        if (isEnrolling) {
            if (lectureInfo?._links.enroll) {
                fetchLink(lectureInfo._links.enroll, studentIdsArray)
                    .then(() => {
                        toast.success("Successfully enrolled students");
                        setShowModal(false);
                    })
                    .catch((error) => {
                        toast.error(error.message || "An error occurred while enrolling students.");
                    });
            }
        } else {
            if (lectureInfo?._links.unenroll) {
                fetchLink(lectureInfo._links.unenroll, studentIdsArray)
                    .then(() => {
                        toast.success("Successfully unenrolled students");
                        setShowModal(false);
                    })
                    .catch((error) => {
                        toast.error(error.message || "An error occurred while unenrolling students.");
                    });
            }
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
        <>
            <Col md={12} lg={11}>
                <ItemsList
                    items={[
                        <ProfessorCard layout={"vertical"} link={professorLink}/>,
                        <AssessmentsSection assessments={assessments}/>,
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

            <Modal show={showModal} onHide={handleCloseModal}>
                <Modal.Header closeButton>
                    <Modal.Title>{isEnrolling ? 'Enroll Students' : 'Unenroll Students'}</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <Form>
                        <Form.Group controlId="studentIds">
                            <Form.Label>Enter Student IDs (comma separated)</Form.Label>
                            <Form.Control
                                type="text"
                                value={studentIds}
                                onChange={(e) => setStudentIds(e.target.value)}
                                placeholder="e.g., 1, 2, 3"
                            />
                        </Form.Group>
                    </Form>
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={handleCloseModal}>
                        Close
                    </Button>
                    <Button variant="primary" onClick={handleEnrollUnenroll}>
                        {isEnrolling ? 'Enroll' : 'Unenroll'}
                    </Button>
                </Modal.Footer>
            </Modal>
        </>
    );
};
