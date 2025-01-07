import React, { useContext, useState } from 'react';
import { Card, ListGroup } from 'react-bootstrap';
import { Lecture } from "../../api/hateoas.ts";
import { HomePageContext } from "../../contexts/HomePageContext.tsx";
import { LectureInfo } from "../LectureInfo.tsx";

interface LectureCardProps {
    lecture?: Lecture;
}

export const LectureCard: React.FC<LectureCardProps> = ({ lecture }) => {
    const [isClicked, setIsClicked] = useState(false);
    const [isHovered, setIsHovered] = useState(false);
    const { setSelectedComponent } = useContext(HomePageContext);

    const handleClick = () => {
        setIsClicked(true);

        if (lecture) {
            setSelectedComponent(
                <LectureInfo
                    lectureLink={lecture._links["self"]}
                    assessmentLink={lecture._links["assessments"]}
                    filesLink={lecture._links["files"]}
                    professorLink={lecture._links["professor"]}
                />
            );
        }
        setIsClicked(false)
    };

    const cardStyle = {
        width: '20rem',
        cursor: 'pointer',
        transition: 'transform 0.3s ease, border-color 0.3s ease',
        borderColor: isClicked ? '#0056b3' : isHovered ? '#007bff' : '',
        transform: isHovered ? 'scale(1.05)' : 'scale(1)',
    };

    return (
        <Card
            className={`lecture-card ${isClicked ? 'clicked' : ''} shadow-sm`}
            style={cardStyle}
            onClick={handleClick}
            onMouseEnter={() => setIsHovered(true)}
            onMouseLeave={() => setIsHovered(false)}
        >
            <Card.Body>
                <Card.Title>Details</Card.Title>
                {lecture ? (
                    <ListGroup variant="flush">
                        <ListGroup.Item>
                            <div><strong>Lecture Name:</strong></div>
                            <div>{lecture.lectureName}</div>
                        </ListGroup.Item>
                        <ListGroup.Item>
                            <div><strong>Study Year:</strong></div>
                            <div>{lecture.studyYear}</div>
                        </ListGroup.Item>
                        <ListGroup.Item>
                            <div><strong>Lecture Type:</strong></div>
                            <div>{lecture.lectureType}</div>
                        </ListGroup.Item>
                        <ListGroup.Item>
                            <div><strong>Category:</strong></div>
                            <div>{lecture.categoryType}</div>
                        </ListGroup.Item>
                        <ListGroup.Item>
                            <div><strong>Examination Type:</strong></div>
                            <div>{lecture.examinationType}</div>
                        </ListGroup.Item>
                    </ListGroup>
                ) : (
                    <div>No lecture data available.</div>
                )}
            </Card.Body>
        </Card>
    );
};

export default LectureCard;
