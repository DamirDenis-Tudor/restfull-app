import React, { useState } from 'react';
import { Card, ListGroup } from 'react-bootstrap';
import { AssessmentTest } from "../../api/hateoas.ts";

interface AssessmentsSectionProps {
    assessments?: AssessmentTest[];
}

const AssessmentsSection: React.FC<AssessmentsSectionProps> = ({ assessments }) => {
    const [isClicked, setIsClicked] = useState(false);
    const [isHovered, setIsHovered] = useState(false);

    const cardStyle = {
        width: '20rem',
        cursor: 'pointer',
        transition: 'transform 0.3s ease, border-color 0.3s ease',
        borderColor: isClicked ? '#0056b3' : isHovered ? '#007bff' : '',
        transform: isHovered ? 'scale(1.05)' : 'scale(1)',
    };

    const handleClick = () => {
        setIsClicked(true);

        setTimeout(() => setIsClicked(false), 300);
    };

    return (
        <Card
            className={`shadow-sm ${isClicked ? 'clicked' : ''}`}
            style={cardStyle}
            onClick={handleClick}
            onMouseEnter={() => setIsHovered(true)}
            onMouseLeave={() => setIsHovered(false)}
        >
            <Card.Body style={{ display: 'flex', flexDirection: 'column', maxHeight: '390px', overflowY: 'auto' }}>
                <Card.Title>Assessments</Card.Title>
                <ListGroup variant="flush">
                    {assessments && assessments.length > 0 ? (
                        assessments.map((assessment, index) => (
                            <ListGroup.Item key={index}>
                                <div><strong>{assessment.type}</strong></div> {/* Makes the type appear on a new line */}
                                <div>{assessment.weight}%</div> {/* Makes the weight appear on the next line */}
                            </ListGroup.Item>
                        ))
                    ) : (
                        <ListGroup.Item key={1000}>
                            <strong>No assessment registered</strong>
                        </ListGroup.Item>
                    )}
                </ListGroup>
            </Card.Body>
        </Card>
    );
};

export default AssessmentsSection;
