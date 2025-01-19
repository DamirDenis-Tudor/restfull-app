import React from 'react';
import { FaDownload, FaTrashAlt } from 'react-icons/fa';
import { Button } from 'react-bootstrap';
import { downloadFile } from "../../api/calls.ts";
import { FileInfo } from "../../api/hateoas.ts";

interface FileCardProps {
    file: FileInfo;
    onDelete: (file: FileInfo) => void;
}

const FileCard: React.FC<FileCardProps> = ({ file, onDelete }) => {
    return (
        <div className="d-flex justify-content-between w-100 border p-3" style={{transform: 'scale(0.90)'}}>
            <div className="text-truncate" style={{ maxWidth: 'calc(100% - 30px)'}}>
                {file.file_name}
                <br />
                <small>Uploaded on: {new Date(file.uploaded_at).toLocaleString()}</small>
            </div>

            <div className="d-flex">
                <Button
                    variant="success"
                    size="sm"
                    className="ml-2"
                    style={{
                        display: 'flex',
                        alignItems: 'center',
                        padding: '5px 10px',
                        backgroundColor: '#4CAF50',
                        borderColor: '#4CAF50',
                        color: 'white'
                    }}
                    onClick={(e) => {
                        e.stopPropagation();
                        downloadFile(file._links.download.href).then();
                    }}
                >
                    <FaDownload size={16} />
                </Button>

                {file._links["delete"] && (
                    <Button
                        variant="danger"
                        size="sm"
                        className="ml-2"
                        style={{
                            backgroundColor: '#FF7043',
                            borderColor: '#FF7043',
                            color: 'white'
                        }}
                        onClick={(e) => {
                            e.stopPropagation();
                            onDelete(file);
                        }}
                    >
                        <FaTrashAlt size={16} />
                    </Button>
                )}
            </div>
        </div>
    );
};

export default FileCard;
