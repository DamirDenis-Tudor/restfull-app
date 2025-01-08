import React, {useState, useEffect} from 'react';
import {Spinner, Col} from 'react-bootstrap';
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
                        <ProfessorCard layout={"vertical"} key={professorLink.href} link={professorLink}/>,
                        <AssessmentsSection assessments={assessments}/>,
                        <LectureCard key={"3"} lecture={lectureInfo} clickable={false}/>,
                    ]} title={'Lecture Details'}
                />

                <FilesSection fData={files}/>
                <StudentList link={lectureInfo?._links.students}/>
            </Col>
        </>


    );

};
