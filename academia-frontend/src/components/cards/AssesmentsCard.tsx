import React, {useEffect, useState} from 'react';
import { Card, ListGroup, Button, Modal, Form, Alert } from 'react-bootstrap';
import {AssessmentTest, EmbeddedResponse, fetchLink} from "../../api/hateoas.ts";
import {toast} from "react-toastify";

interface AssessmentsSectionProps {
    assessments?: EmbeddedResponse<AssessmentTest>;
}

const AssessmentsSection: React.FC<AssessmentsSectionProps> = ({ assessments }) => {
    const [isClicked, setIsClicked] = useState(false);
    const [isHovered, setIsHovered] = useState(false);
    const [showModal, setShowModal] = useState(false);
    const [editJson, setEditJson] = useState<string>();
    const [errorMessage, setErrorMessage] = useState<string | null>(null);

    const handleClick = () => {
        setIsClicked(true);
        setTimeout(() => setIsClicked(false), 300);
    };

    const handleModalClose = () => {
        setShowModal(false);
        setErrorMessage(null);
    };

    useEffect(() => {
        setEditJson(JSON.stringify(assessments?._embedded?.assessment_tests, null, 2))
    }, [assessments]);

    const handleModalShow = () => setShowModal(true);

    const handleSave = async () => {
        try {
            const updatedAssessments: AssessmentTest[] = JSON.parse(editJson || '[]');

            if (Array.isArray(updatedAssessments) && updatedAssessments) {
                if (assessments?._links?.update) {
                    fetchLink(assessments?._links?.update, updatedAssessments)
                        .then(() => {
                                toast.success("Successfully updated assessments");
                                setShowModal(false)
                            }
                        )
                        .catch((error) => {
                            toast.error(error.message);
                        })
                }
            } else {
                setErrorMessage('Invalid format. Ensure each assessment has a type (string) and weight (number).');
            }
        } catch {
            setErrorMessage('Invalid JSON format. Please check the syntax.');
        }
    };

    return (
        <>
            <Card
                className={`shadow-sm ${isClicked ? 'clicked' : ''}`}
                style={ {
                    width: '20rem',
                    cursor: 'pointer',
                    transition: 'transform 0.3s ease, border-color 0.3s ease',
                    borderColor: isClicked ? '#0056b3' : isHovered ? '#007bff' : '',
                    transform: isHovered ? 'scale(1.05)' : 'scale(1)',
                }}
                onClick={handleClick}
                onMouseEnter={() => setIsHovered(true)}
                onMouseLeave={() => setIsHovered(false)}
            >
                <Card.Body style={{ display: 'flex', flexDirection: 'column', maxHeight: '390px', overflowY: 'auto' }}>
                    <Card.Title>Assessments</Card.Title>
                    <ListGroup variant="flush">
                        {assessments && assessments._embedded.assessment_tests.length > 0 ? (
                            assessments._embedded.assessment_tests.map((assessment, index) => (
                                <ListGroup.Item key={index}>
                                    <div><strong>{assessment.type}</strong></div>
                                    <div>{assessment.weight}%</div>
                                </ListGroup.Item>
                            ))
                        ) : (
                            <ListGroup.Item key={1000}>
                                <strong>No assessment registered</strong>
                            </ListGroup.Item>
                        )}
                    </ListGroup>

                    {assessments?._links?.update && (
                        <Button variant="primary" className="mt-3" onClick={handleModalShow}>
                            Edit Assessments
                        </Button>
                    )}
                </Card.Body>
            </Card>

            <Modal show={showModal} onHide={handleModalClose} centered>
                <Modal.Header closeButton>
                    <Modal.Title>Edit Assessments</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    {errorMessage && <Alert variant="danger">{errorMessage}</Alert>}
                    <Form.Control
                        as="textarea"
                        rows={10}
                        value={editJson || ''}
                        onChange={(e) => setEditJson(e.target.value)}
                    />
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={handleModalClose}>
                        Close
                    </Button>
                    <Button variant="primary" onClick={handleSave}>
                        Save Changes
                    </Button>
                </Modal.Footer>
            </Modal>
        </>
    );
};

export default AssessmentsSection;
