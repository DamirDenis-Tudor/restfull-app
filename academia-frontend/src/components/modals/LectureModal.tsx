import React, { useEffect, useState } from 'react';
import { Modal, Button, Form } from 'react-bootstrap';
import { fetchLink, Link } from '../../api/hateoas.ts';
import {toast} from "react-toastify";

export interface LectureData {
    id: string;
    lectureName: string;
    studyYear: number;
    lectureType: string;
    categoryType: string;
    examinationType: string;
}

interface LectureModalProps {
    lecture?: LectureData;
    link: Link;
    onClose: () => void;
}

const LectureModal: React.FC<LectureModalProps> = ({ lecture, link, onClose }) => {
    const [formData, setFormData] = useState<LectureData>({
        id: '',
        lectureName: '',
        studyYear: 0,
        lectureType: '',
        categoryType: '',
        examinationType: '',
    });

    useEffect(() => {
        if (lecture) {
            setFormData(lecture);
        }
    }, [lecture]);

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
                if (lecture){
                    toast.success("Lecture updated successfully.");
                }else {
                    toast.success("Lecture created successfully.");
                }
                onClose();
            })
            .catch((error) => {
                toast.error(error.message);
                console.error("Error updating lecture data:", error);
            });
    };

    return (
        <Modal show={true} onHide={onClose}>
            <Modal.Header closeButton>
                <Modal.Title>{lecture ? 'Update Lecture' : 'Add New Lecture'}</Modal.Title>
            </Modal.Header>
            <Modal.Body>
                <Form onSubmit={handleSubmit}>
                    <Form.Group controlId="lectureName">
                        <Form.Label>Lecture Name</Form.Label>
                        <Form.Control
                            type="text"
                            name="lectureName"
                            value={formData.lectureName}
                            onChange={handleChange}
                            placeholder="Enter lecture name"
                        />
                    </Form.Group>

                    <Form.Group controlId="studyYear">
                        <Form.Label>Study Year</Form.Label>
                        <Form.Control
                            type="number"
                            name="studyYear"
                            value={formData.studyYear}
                            onChange={handleChange}
                            placeholder="Enter study year"
                        />
                    </Form.Group>

                    <Form.Group controlId="lectureType">
                        <Form.Label>Lecture Type</Form.Label>
                        <Form.Control
                            type="text"
                            name="lectureType"
                            value={formData.lectureType}
                            onChange={handleChange}
                            placeholder="Enter lecture type"
                        />
                    </Form.Group>

                    <Form.Group controlId="categoryType">
                        <Form.Label>Category Type</Form.Label>
                        <Form.Control
                            type="text"
                            name="categoryType"
                            value={formData.categoryType}
                            onChange={handleChange}
                            placeholder="Enter category type"
                        />
                    </Form.Group>

                    <Form.Group controlId="examinationType">
                        <Form.Label>Examination Type</Form.Label>
                        <Form.Control
                            type="text"
                            name="examinationType"
                            value={formData.examinationType}
                            onChange={handleChange}
                            placeholder="Enter examination type"
                        />
                    </Form.Group>

                    <Modal.Footer>
                        <Button variant="secondary" onClick={onClose}>
                            Close
                        </Button>
                        <Button variant="primary" type="submit">
                            {lecture ? 'Update Lecture' : 'Add Lecture'}
                        </Button>
                    </Modal.Footer>
                </Form>
            </Modal.Body>
        </Modal>
    );
};

export default LectureModal;
