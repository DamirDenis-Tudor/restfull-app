import React from 'react';
import { Card, ListGroup } from 'react-bootstrap';
import { FileInfo } from "../api/hateoas.ts";

interface FilesSectionProps {
    files: FileInfo[];
}

const FilesSection: React.FC<FilesSectionProps> = ({ files }) => {
    return (
        <Card className="mb-4">
            <Card.Body>
                <h4>Files</h4>
                <ListGroup variant="flush">
                    {files.map((file, index) => (
                        <ListGroup.Item key={index}>
                            <a href={file._links.find(link => link.type === 'GET')?.href} target="_blank" rel="noopener noreferrer">
                                {file.fileMetadata.file_name}
                            </a>
                            <br />
                            <small>Uploaded on: {new Date(file.fileMetadata.uploaded_at).toLocaleString()}</small>
                        </ListGroup.Item>
                    ))}
                </ListGroup>
            </Card.Body>
        </Card>
    );
};

export default FilesSection;
