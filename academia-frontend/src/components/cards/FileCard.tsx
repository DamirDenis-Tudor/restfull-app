import React from 'react';
import { Card, ListGroup } from 'react-bootstrap';
import { FileInfo } from "../../api/hateoas.ts";
import { ItemsList } from "../lists/ItemsList.tsx";
import { FaDownload } from 'react-icons/fa';
import { downloadFile } from "../../api/calls.ts";

interface FilesSectionProps {
    files?: FileInfo[];
}

const FilesSection: React.FC<FilesSectionProps> = ({ files }) => {
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
                                    className="border p-3 d-flex justify-content-between align-items-center"
                                >
                                    <div className="d-flex justify-content-between w-100">
                                        <div className="text-truncate" style={{ maxWidth: 'calc(100% - 30px)' }}>
                                            {file.file_metadata.file_name}
                                            <br />
                                            <small>Uploaded on: {new Date(file.file_metadata.uploaded_at).toLocaleString()}</small>
                                        </div>

                                        <FaDownload
                                            size={20}
                                            className="ml-2 text-primary"
                                            style={{ cursor: 'pointer' }}
                                            onClick={(e) => {
                                                e.stopPropagation();
                                                downloadFile(file._links.download.href).then();
                                            }}
                                        />
                                    </div>
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

    const courseFiles = files?.filter(file => file.file_metadata.category === 'course');
    const labFiles = files?.filter(file => file.file_metadata.category === 'lab');

    return (
        <ItemsList
            title="Files"
            items={[
                renderFiles(courseFiles || [], 'Course Files'),
                renderFiles(labFiles || [], 'Lab Files')
            ]}
        />
    );
};

export default FilesSection;
