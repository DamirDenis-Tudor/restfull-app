import React, { useState, useEffect } from 'react';
import { Modal, Button, Form } from 'react-bootstrap';
import { fetchLink, Link } from "../../api/hateoas.ts";
import { toast } from 'react-toastify';

export interface StudentData {
    id?: number;
    firstName: string;
    lastName: string;
    email: string;
    cycleType: string;
    studyYear: string;
    studentGroup: string;
}

interface StudentModalProps {
    student?: StudentData;
    link: Link;
    onClose: () => void;
}

const StudentModal: React.FC<StudentModalProps> = ({ student, link, onClose }) => {
    const [formData, setFormData] = useState<StudentData>({
        firstName: '',
        lastName: '',
        email: '',
        cycleType: '',
        studyYear: '',
        studentGroup: '',
    });

    useEffect(() => {
        if (student) {
            setFormData(student);
        }
    }, [student]);

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const { name, value } = e.target;
        setFormData((prev) => ({
            ...prev,
            [name]: value,
        }));
    };

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        fetchLink(link, formData)
            .then(() => {
                onClose();
            })
            .catch((error) => {
                toast.error(error.message);
                console.error("Error creating/updating student:", error);
            });
    };

    return (
        <Modal show={true} onHide={onClose}>
            <Modal.Header closeButton>
                <Modal.Title>{student ? 'Edit Student' : 'Add New Student'}</Modal.Title>
            </Modal.Header>
            <Modal.Body>
                <Form onSubmit={handleSubmit}>
                    <Form.Group controlId="firstName">
                        <Form.Label>First Name</Form.Label>
                        <Form.Control
                            type="text"
                            name="firstName"
                            value={formData.firstName}
                            onChange={handleChange}
                            placeholder="Enter first name"
                        />
                    </Form.Group>
                    <Form.Group controlId="lastName">
                        <Form.Label>Last Name</Form.Label>
                        <Form.Control
                            type="text"
                            name="lastName"
                            value={formData.lastName}
                            onChange={handleChange}
                            placeholder="Enter last name"
                        />
                    </Form.Group>
                    <Form.Group controlId="email">
                        <Form.Label>Email</Form.Label>
                        <Form.Control
                            type="email"
                            name="email"
                            value={formData.email}
                            onChange={handleChange}
                            placeholder="Enter email"
                        />
                    </Form.Group>
                    <Form.Group controlId="cycleType">
                        <Form.Label>Cycle Type</Form.Label>
                        <Form.Control
                            type="text"
                            name="cycleType"
                            value={formData.cycleType}
                            onChange={handleChange}
                            placeholder="Enter cycle type"
                        />
                    </Form.Group>
                    <Form.Group controlId="studyYear">
                        <Form.Label>Study Year</Form.Label>
                        <Form.Control
                            type="text"
                            name="studyYear"
                            value={formData.studyYear}
                            onChange={handleChange}
                            placeholder="Enter study year"
                        />
                    </Form.Group>
                    <Form.Group controlId="studentGroup">
                        <Form.Label>Student Group</Form.Label>
                        <Form.Control
                            type="text"
                            name="studentGroup"
                            value={formData.studentGroup}
                            onChange={handleChange}
                            placeholder="Enter student group"
                        />
                    </Form.Group>
                    <Modal.Footer>
                        <Button variant="secondary" onClick={onClose}>
                            Close
                        </Button>
                        <Button variant="primary" type="submit">
                            {student ? 'Update Student' : 'Add Student'}
                        </Button>
                    </Modal.Footer>
                </Form>
            </Modal.Body>
        </Modal>
    );
};

export default StudentModal;
