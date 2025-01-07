import React, { useState, useEffect } from 'react';
import { Spinner, Card, Container, Row, Col } from 'react-bootstrap';
import {Lecture, Link, AssessmentTest, EmbeddedResponse, FileInfo, fetchComponentData} from "../api/hateoas.ts";
import AssessmentsSection from "./AssesmentsSection.tsx";
import FilesSection from "./FileSection.tsx";

interface FullLectureCardProps {
    lectureLink: Link;
    assessmentLink: Link;
    filesLink: Link;
}

export const FullLectureCard: React.FC<FullLectureCardProps> = ({ lectureLink, assessmentLink, filesLink }) => {
    const [lectureInfo, setLectureInfo] = useState<Lecture | null>(null);
    const [assessments, setAssessments] = useState<EmbeddedResponse<AssessmentTest> | null>(null);
    const [files, setFiles] = useState<EmbeddedResponse<FileInfo> | null>(null);
    const [loading, setLoading] = useState<boolean>(true);

    useEffect(() => {
        const fetchData = async () => {
            try {
                setLoading(true);
                return await Promise.all([
                    fetchComponentData<Lecture>(lectureLink),
                    fetchComponentData<EmbeddedResponse<AssessmentTest>>(assessmentLink),
                    fetchComponentData<EmbeddedResponse<FileInfo>>(filesLink),
                ]);
            } catch (error) {
                console.error("Error fetching data:", error);
                return [];
            } finally {
                setLoading(false);
            }
        };

        fetchData().then(([lectureData, assessmentData, filesData]) => {
            setLectureInfo(lectureData);
            setAssessments(assessmentData);
            setFiles(filesData);
        });

    }, [lectureLink, assessmentLink, filesLink]);

    if (loading) {
        return (
            <div className="d-flex justify-content-center align-items-center" style={{ height: '100vh' }}>
                <Spinner animation="border" />
            </div>
        );
    }

    return (
        <Container className="mt-4">
            {/* Render lecture info if available */}
            {lectureInfo && (
                <Card className="mb-4">
                    <Card.Body>
                        <h3>{lectureInfo.lectureName}</h3>
                        <p><strong>Study Year:</strong> {lectureInfo.studyYear}</p>
                        <p><strong>Lecture Type:</strong> {lectureInfo.lectureType}</p>
                        <p><strong>Category:</strong> {lectureInfo.categoryType}</p>
                        <p><strong>Examination Type:</strong> {lectureInfo.examinationType}</p>
                    </Card.Body>
                </Card>
            )}

            <Row>
                {/* Render Assessments Section */}
                <Col md={6}>
                    {assessments && (
                        <AssessmentsSection assessments={assessments._embedded.assessment_tests} />
                    )}
                </Col>

                {/* Render Files Section */}
                <Col md={6}>
                    {files && (
                        <FilesSection files={files?._embedded.files} />
                    )}
                </Col>
            </Row>
        </Container>
    );
};
