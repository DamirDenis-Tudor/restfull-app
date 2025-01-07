import React, {useContext, useState} from 'react';
import { Card, ListGroup } from 'react-bootstrap';
import {Lecture} from "../api/hateoas.ts";
import {HomePageContext} from "../contexts/HomePageContext.tsx";
import {FullLectureCard} from "./FullLectureCard.tsx";



interface LectureCardProps {
    lecture: Lecture;
}

export const LectureCard: React.FC<LectureCardProps> = ({ lecture }) => {
    const [lec] = useState<Lecture>(lecture);
    const [isClicked, setIsClicked] = useState(false);
    const {setSelectedComponent} = useContext(HomePageContext);

    const handleClick = () => {
        setIsClicked(true);

        setSelectedComponent(
            <FullLectureCard
                lectureLink={lec._links["self"]}
                assessmentLink={lec._links["Assessments"]}
                filesLink={lec._links["Files"]}
            />
        );

        setTimeout(() => setIsClicked(false), 300);
    };

    return (
        <Card
            className={`lecture-card ${isClicked ? 'clicked' : ''} shadow-sm`}
            style={{
                width: '22rem',
                cursor: 'pointer',
                transition: 'transform 0.3s ease, border-color 0.3s ease',
                borderColor: isClicked ? '#0056b3' : '',
            }}
            onClick={handleClick}
            onMouseEnter={(e) => {
                e.currentTarget.style.borderColor = '#007bff';
                e.currentTarget.style.transform = 'scale(1.05)';
            }}
            onMouseLeave={(e) => {
                e.currentTarget.style.borderColor = '';
                e.currentTarget.style.transform = '';
            }}
        >
            <Card.Body>
                {lec ? (
                    <ListGroup variant="flush">
                        <ListGroup.Item><strong>Lecture Name:</strong> {lec.lectureName}</ListGroup.Item>
                        <ListGroup.Item><strong>Study Year:</strong> {lec.studyYear}</ListGroup.Item>
                        <ListGroup.Item><strong>Lecture Type:</strong> {lec.lectureType}</ListGroup.Item>
                        <ListGroup.Item><strong>Category:</strong> {lec.categoryType}</ListGroup.Item>
                        <ListGroup.Item><strong>Examination Type:</strong> {lec.examinationType}</ListGroup.Item>
                    </ListGroup>
                ) : (
                    <div>No lecture data available.</div>
                )}
            </Card.Body>
        </Card>
    );
};

export default LectureCard;
