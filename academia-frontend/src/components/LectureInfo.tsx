import React, {useState, useEffect} from 'react';
import {Spinner, Col} from 'react-bootstrap';
import {
    Lecture,
    Link,
    AssessmentTest,
    EmbeddedResponse,
    FileInfo,
    fetchComponentData
} from "../api/hateoas.ts";
import AssessmentsSection from "./cards/AssesmentsCard.tsx";
import FilesSection from "./cards/FileCard.tsx";
import LectureCard from "./cards/LectureCard.tsx";
import ProfessorCard from "./cards/ProfessorCard.tsx";
import {StudentList} from "./lists/StudentList.tsx";
import {ItemsList} from "./lists/ItemsList.tsx";

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


    useEffect(() => {
        fetchComponentData<Lecture>(lectureLink)
            .then((data) => {
                setLectureInfo(data);
            })
            .catch((error) => {
                console.error("Error fetching lectureLink data:", error);
            });

        fetchComponentData<EmbeddedResponse<AssessmentTest>>(assessmentLink)
            .then((data) => {
                setAssessments(data);
            })
            .catch((error) => {
                console.error("Error fetching assessmentLink data:", error);
            });

        fetchComponentData<EmbeddedResponse<FileInfo>>(filesLink)
            .then((data) => {
                setFiles(data);
            })
            .catch((error) => {
                console.error("Error fetching filesLink data:", error);
            });

    }, [lectureLink, assessmentLink, filesLink, professorLink]);

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
                        <LectureCard key={"3"} lecture={lectureInfo}/>,
                        <ProfessorCard layout={"vertical"} key={professorLink.href} link={professorLink}/>,
                        <AssessmentsSection assessments={assessments?._embedded.assessment_tests}/>,
                    ]} title={'Lecture Details'}
                />

                <FilesSection files={files?._embedded.files}/>
                <StudentList link={lectureInfo?._links.students}/>
            </Col>
        </>


    );

};
