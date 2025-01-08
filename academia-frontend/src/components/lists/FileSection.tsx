import React, {useState} from 'react';
import {Card, ListGroup} from 'react-bootstrap';
import {EmbeddedResponse, fetchLink, FileInfo} from "../../api/hateoas.ts";
import ConfirmationModal from "../modals/ConfirmationModal.tsx";
import {toast} from "react-toastify";
import FileCard from "../cards/FileCard.tsx";
import {FileUploadModal} from "../modals/FileUploadModal.tsx";
import {ItemsList} from "./ItemsList.tsx";

interface FilesSectionProps {
    fData?: EmbeddedResponse<FileInfo>;
}

const FilesSection: React.FC<FilesSectionProps> = ({fData}) => {
    const [showConfirmDeleteModal, setShowConfirmDeleteModal] = useState(false);
    const [fileToDelete, setFileToDelete] = useState<FileInfo | null>(null);
    const [showAddFileModal, setShowAddFileModal] = useState(false);

    const handleDelete = (file: FileInfo) => {
        setFileToDelete(file);
        setShowConfirmDeleteModal(true);
    };

    const confirmDelete = () => {
        if (fileToDelete) {
            fetchLink(fileToDelete._links["delete"], undefined)
                .then(() => {
                    toast.success("File deleted successfully");
                    setShowConfirmDeleteModal(false);
                })
                .catch(() => {
                    toast.error("Error deleting file");
                    setShowConfirmDeleteModal(false);
                });
        }
    };

    const courseFiles = fData?._embedded.files.filter(file => file.category === 'course');
    const labFiles = fData?._embedded.files.filter(file => file.category === 'lab');

    const renderFiles = (files: FileInfo[], title: string) => {
        return (
            <Card className="mb-3">
                <Card.Body>
                    <Card.Title>{title}</Card.Title>
                    <ListGroup variant="flush">
                        {files && files.length > 0 ? (
                            files.map((file, index) => (
                                <ListGroup.Item
                                    key={index}
                                    className="border p-3"
                                ><FileCard file={file} onDelete={handleDelete}/>
                                </ListGroup.Item>
                            ))
                        ) : (
                            <ListGroup.Item key={1000} className="border p-3">
                                <strong>No {title.toLowerCase()} files available</strong>
                            </ListGroup.Item>
                        )}
                    </ListGroup>
                </Card.Body>
            </Card>
        );
    };

    const handleFileUploaded = () => {
        toast.success("File uploaded successfully!");
    };

    return (
        <>
            <ItemsList
                title="Files"
                items={[
                    renderFiles(courseFiles || [], 'Course Files'),
                    renderFiles(labFiles || [], 'Lab Files')
                ]}
                onAddElement={fData?._links.upload ? () => setShowAddFileModal(true): undefined}
            />

            <ConfirmationModal
                show={showConfirmDeleteModal}
                message="Are you sure you want to delete this file?"
                onConfirm={confirmDelete}
                onCancel={() => setShowConfirmDeleteModal(false)}
            />

            <FileUploadModal
                show={showAddFileModal}
                onHide={() => setShowAddFileModal(false)}
                uploadFileLink={fData?._links.upload || undefined}
                onFileUploaded={handleFileUploaded}
            />
        </>
    );
};

export default FilesSection;
