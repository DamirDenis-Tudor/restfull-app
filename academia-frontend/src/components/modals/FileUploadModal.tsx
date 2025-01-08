import React, { useState } from 'react';
import { Modal, Button, Form } from 'react-bootstrap';
import { toast } from "react-toastify";
import {Link} from "../../api/hateoas.ts";
import {uploadFile} from "../../api/calls.ts";

interface FileUploadModalProps {
    show: boolean;
    onHide: () => void;
    uploadFileLink?: Link;
    onFileUploaded: () => void;
}

export const FileUploadModal: React.FC<FileUploadModalProps> = ({ show, onHide, uploadFileLink, onFileUploaded }) => {
    const [newFile, setNewFile] = useState<File | null>(null);
    const [category, setCategory] = useState<string>('course');

    const handleFileUpload = () => {
        if (newFile && uploadFileLink) {
            uploadFileLink.href = uploadFileLink.href.replace("{}", category.toLowerCase())
            uploadFile(uploadFileLink, newFile)
                .then(() => {
                    onFileUploaded();
                    onHide();
                })
                .catch(() => {
                    toast.error("Error uploading file");
                    onHide();
                });
        } else {
            toast.error("Please select a file to upload.");
        }
    };

    return (
        <Modal show={show} onHide={onHide}>
            <Modal.Header closeButton>
                <Modal.Title>Upload File</Modal.Title>
            </Modal.Header>
            <Modal.Body>
                <Form>
                    <Form.Group controlId="formFile" className="mb-3">
                        <Form.Label>Choose a file</Form.Label>
                        <Form.Control
                            type="file"
                            onChange={(e) => {
                                const inputElement = e.target as HTMLInputElement;
                                if (inputElement.files) {
                                    setNewFile(inputElement.files[0]);
                                }
                            }}
                        />
                    </Form.Group>
                    <Form.Group controlId="formCategory" className="mb-3">
                        <Form.Label>File Category</Form.Label>
                        <Form.Control
                            as="select"
                            value={category}
                            onChange={(e) => setCategory(e.target.value)}
                        >
                            <option value="course">Course</option>
                            <option value="lab">Lab</option>
                        </Form.Control>
                    </Form.Group>
                </Form>
            </Modal.Body>
            <Modal.Footer>
                <Button variant="secondary" onClick={onHide}>
                    Close
                </Button>
                <Button variant="primary" onClick={handleFileUpload}>
                    Upload
                </Button>
            </Modal.Footer>
        </Modal>
    );
};
