import React, {useContext, useState} from 'react';
import {Card, ListGroup, Button} from 'react-bootstrap';
import {fetchLink, Lecture} from "../../api/hateoas.ts";
import {HomePageContext} from "../../contexts/HomePageContext.tsx";
import {FaEdit, FaTrashAlt} from 'react-icons/fa';
import LectureModal from "../modals/LectureModal.tsx";
import ConfirmationModal from "../modals/ConfirmationModal.tsx";
import {toast} from "react-toastify";
import {useNavigate} from "react-router"; // Import the confirmation modal

interface LectureCardProps {
    lecture?: Lecture;
    clickable?: boolean;
}

export const LectureCard: React.FC<LectureCardProps> = ({lecture, clickable = true}) => {
    const [isClicked, setIsClicked] = useState(false);
    const [isHovered, setIsHovered] = useState(false);
    const [showModal, setShowModal] = useState(false);
    const [showConfirmDeleteModal, setShowConfirmDeleteModal] = useState(false);
    useContext(HomePageContext);
    const navigate = useNavigate();

    const handleClick = () => {
        setIsClicked(true);

        if (lecture && clickable) {
            navigate("/lecture/"+lecture.id , {
                state: {
                    lectureLink: lecture._links["self"],
                    assessmentLink: lecture._links["assessments"],
                    filesLink: lecture._links["files"],
                    professorLink: lecture._links["professor"]
                }
            });
        }

        setIsClicked(false);
    };

    const handleEdit = () => {
        if (lecture) {
            setShowModal(true);
        }
    };

    const handleDelete = () => {
        setShowConfirmDeleteModal(true);
    };

    const confirmDelete = () => {
        if (lecture && lecture._links["delete"]) {
            fetchLink(lecture._links["delete"], undefined).then(() => {
                toast.error("Lecture deleted successfully.");
                setShowConfirmDeleteModal(false);
            }).catch(() => {
                toast.error("Error deleting lecture");
                setShowConfirmDeleteModal(false);
            });
        }
    };

    const cardStyle = {
        width: '20rem',
        cursor: 'pointer',
        transition: 'transform 0.3s ease, border-color 0.3s ease',
        borderColor: isClicked ? '#0056b3' : isHovered ? '#007bff' : '',
        transform: isHovered ? 'scale(1.05)' : 'scale(1)',
    };

    return (
        <div>
            <Card
                className={`lecture-card ${isClicked ? 'clicked' : ''} shadow-sm`}
                style={cardStyle}
                onClick={handleClick}
                onMouseEnter={() => setIsHovered(true)}
                onMouseLeave={() => setIsHovered(false)}
            >
                <Card.Body>
                    <Card.Title>Details</Card.Title>
                    {lecture ? (
                        <ListGroup variant="flush">
                            <ListGroup.Item>
                                <strong>Id: {lecture.id}</strong>
                            </ListGroup.Item>
                            <ListGroup.Item>
                                <div><strong>Lecture Name:</strong></div>
                                <div>{lecture.lectureName}</div>
                            </ListGroup.Item>
                            <ListGroup.Item>
                                <div><strong>Study Year:</strong></div>
                                <div>{lecture.studyYear}</div>
                            </ListGroup.Item>
                            <ListGroup.Item>
                                <div><strong>Lecture Type:</strong></div>
                                <div>{lecture.lectureType}</div>
                            </ListGroup.Item>
                            <ListGroup.Item>
                                <div><strong>Category:</strong></div>
                                <div>{lecture.categoryType}</div>
                            </ListGroup.Item>
                            <ListGroup.Item>
                                <div><strong>Examination Type:</strong></div>
                                <div>{lecture.examinationType}</div>
                            </ListGroup.Item>
                        </ListGroup>
                    ) : (
                        <div>No lecture data available.</div>
                    )}

                    <div className="d-flex justify-content-between mt-3">
                        {lecture?._links["update"] && (
                            <Button
                                variant="warning"
                                onClick={handleEdit}
                                title="Edit"
                            >
                                <FaEdit size={16}/>
                            </Button>
                        )}
                        {lecture?._links["delete"] && (
                            <Button
                                variant="danger"
                                onClick={handleDelete}
                                title="Delete"
                            >
                                <FaTrashAlt size={16}/>
                            </Button>
                        )}
                    </div>
                </Card.Body>
            </Card>

            {showModal && lecture && (
                <LectureModal
                    lecture={lecture}
                    link={lecture._links["update"]!}
                    onClose={() => setShowModal(false)}
                />
            )}

            <ConfirmationModal
                show={showConfirmDeleteModal}
                message="Are you sure you want to delete this lecture?"
                onConfirm={confirmDelete}
                onCancel={() => setShowConfirmDeleteModal(false)}
            />
        </div>
    );
};

export default LectureCard;
