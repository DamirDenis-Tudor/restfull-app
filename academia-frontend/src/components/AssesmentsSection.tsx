import React from 'react';
import { Card, ListGroup } from 'react-bootstrap';
import { AssessmentTest } from "../api/hateoas.ts";

interface AssessmentsSectionProps {
    assessments: AssessmentTest[];
}

const AssessmentsSection: React.FC<AssessmentsSectionProps> = ({ assessments }) => {
    return (
        <Card className="mb-4">
            <Card.Body>
                <h4>Assessments</h4>
                <ListGroup variant="flush">
                    {assessments.map((assessment, index) => (
                        <ListGroup.Item key={index}>
                            <strong>{assessment.type}</strong> - {assessment.weight}%
                        </ListGroup.Item>
                    ))}
                </ListGroup>
            </Card.Body>
        </Card>
    );
};

export default AssessmentsSection;
