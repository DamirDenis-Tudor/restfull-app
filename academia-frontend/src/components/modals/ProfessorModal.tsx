import React, { useState, useEffect } from 'react';
import { Modal, Button, Form } from 'react-bootstrap';
import { fetchLink, Link } from "../../api/hateoas.ts";
import { toast } from 'react-toastify';

export interface ProfessorData {
    professorId?: number;
    firstName: string;
    lastName: string;
    email: string;
    affiliation: string;
    associationType: string;
    graderType: string;
}

interface ProfessorModalProps {
    professor?: ProfessorData;
    link: Link;
    onClose: () => void;
}

const ProfessorModal: React.FC<ProfessorModalProps> = ({ professor, link, onClose }) => {
    const [formData, setFormData] = useState<ProfessorData>({
        firstName: '',
        lastName: '',
        email: '',
        affiliation: '',
        associationType: '',
        graderType: '',
    });

    useEffect(() => {
        if (professor) {
            setFormData(professor);
        }
    }, [professor]);


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
                console.error("Error submitting professor data:", error);
            });
    };

    return (
        <Modal show={true} onHide={onClose}>
            <Modal.Header closeButton>
                <Modal.Title>{professor ? 'Edit Professor' : 'Add New Professor'}</Modal.Title>
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

                    <Form.Group controlId="affiliation">
                        <Form.Label>Affiliation</Form.Label>
                        <Form.Control
                            type="text"
                            name="affiliation"
                            value={formData.affiliation}
                            onChange={handleChange}
                            placeholder="Enter affiliation"
                        />
                    </Form.Group>

                    <Form.Group controlId="associationType">
                        <Form.Label>Association Type</Form.Label>
                        <Form.Control
                            type="text"
                            name="associationType"
                            value={formData.associationType}
                            onChange={handleChange}
                            placeholder="Enter association type"
                        />
                    </Form.Group>

                    <Form.Group controlId="graderType">
                        <Form.Label>Grader Type</Form.Label>
                        <Form.Control
                            type="text"
                            name="graderType"
                            value={formData.graderType}
                            onChange={handleChange}
                            placeholder="Enter grader type"
                        />
                    </Form.Group>

                    <Modal.Footer>
                        <Button variant="secondary" onClick={onClose}>
                            Close
                        </Button>
                        <Button variant="primary" type="submit">
                            {professor ? 'Update Professor' : 'Add Professor'}
                        </Button>
                    </Modal.Footer>
                </Form>
            </Modal.Body>
        </Modal>
    );
};

export default ProfessorModal;
