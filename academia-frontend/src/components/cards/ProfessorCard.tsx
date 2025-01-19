import React, {useEffect, useState} from 'react';
import {Card, ListGroup} from 'react-bootstrap';
import {fetchLink, Link, Professor} from "../../api/hateoas.ts";
import {FaEdit, FaTrashAlt} from "react-icons/fa";
import ProfessorModal from "../modals/ProfessorModal.tsx";
import ConfirmationModal from "../modals/ConfirmationModal.tsx";
import {toast} from "react-toastify";
import {useNavigate} from "react-router";

interface ProfessorCardProps {
    link?: Link;
    prof?: Professor;
    layout: 'vertical' | 'horizontal';
}

const ProfessorCard: React.FC<ProfessorCardProps> = ({link, prof, layout = 'vertical'}) => {
    const [professor, setProfessor] = useState<Professor | undefined>(prof);
    const [isClicked, setIsClicked] = useState(false);
    const [isHovered, setIsHovered] = useState(false);
    const [showModal, setShowModal] = useState(false);
    const [showConfirmDeleteModal, setShowConfirmDeleteModal] = useState(false);
    const navigate = useNavigate();

    useEffect(() => {
        if (link && professor === undefined) {
            fetchLink<Professor, undefined>(link, undefined)
                .then((data) => setProfessor(data))
                .catch((error) => {
                    toast.error(error.message);
                });
        }
    }, [link ,professor]);

    const handleClick = () => {
        setIsClicked(true);

        if (professor && professor._links["profile"]) {
            navigate("/profile/professor/" + professor.firstName, {
                    state: {
                        lectures: professor._links["lectures"],
                        profile: professor._links["profile"]
                    },
                }
            );
        }

        setIsClicked(false);
    };

    const handleDelete = () => {
        setShowConfirmDeleteModal(true);
    };

    const confirmDelete = () => {
        if (professor && professor._links["delete"]) {
            fetchLink(professor._links["delete"], undefined)
                .then(() => {
                    toast.success("Professor Deleted Successfully.");
                    setShowConfirmDeleteModal(false);
                })
                .catch(() => {
                    toast.error("Error deleting student");
                    setShowConfirmDeleteModal(false);
                });
        }
    };

    const handleUpdate = () => {
        if (professor && professor._links["update"]) {
            setShowModal(true);
        }
    };

    const renderVertical = () => (
        <Card.Body>
            <Card.Title>Professor Information</Card.Title>
            {professor ? (
                <ListGroup variant="flush">
                    <ListGroup.Item>
                        <strong>Id: {professor.id}</strong>
                    </ListGroup.Item>
                    <ListGroup.Item>
                        <strong>Name:</strong>
                        <span className="d-block">{professor.firstName} {professor.lastName}</span>
                    </ListGroup.Item>
                    <ListGroup.Item>
                        <strong>Email:</strong>
                        <span className="d-block">{professor.email}</span>
                    </ListGroup.Item>
                    <ListGroup.Item>
                        <strong>Department:</strong>
                        <span className="d-block">{professor.affiliation}</span>
                    </ListGroup.Item>
                    <ListGroup.Item>
                        <strong>Association Type:</strong>
                        <span className="d-block">{professor.associationType}</span>
                    </ListGroup.Item>
                    <ListGroup.Item>
                        <strong>Grader Type:</strong>
                        <span className="d-block">{professor.graderType}</span>
                    </ListGroup.Item>
                </ListGroup>
            ) : (<></>)}
            {layout === 'vertical' && (
                <div className="d-flex justify-content-between mt-3">
                    {professor?._links["update"] && (
                        <div
                            className="icon-button"
                            onClick={handleUpdate}
                            title="Update"
                            style={{cursor: 'pointer', color: '#f39c12'}}
                        ><FaEdit size={20}/></div>
                    )}
                    {professor?._links["delete"] && (
                        <div
                            className="icon-button"
                            onClick={handleDelete}
                            title="Delete"
                            style={{cursor: 'pointer', color: '#e74c3c'}}
                        ><FaTrashAlt size={20}/></div>
                    )}
                </div>
            )}
        </Card.Body>
    );

    const renderHorizontal = () => (
        <Card.Body className="d-flex flex-wrap justify-content-between p-3">
            <div className="d-flex flex-column align-items-center mb-3"
                 style={{flex: '1 1 calc(20% - 1rem)', minWidth: '250px'}}>
                <strong>Name:</strong>
                <span>{professor?.firstName} {professor?.lastName}</span>
            </div>
            <div className="d-flex flex-column align-items-center mb-3"
                 style={{flex: '1 1 calc(20% - 1rem)', minWidth: '250px'}}>
                <strong>Email:</strong>
                <span>{professor?.email}</span>
            </div>
            <div className="d-flex flex-column align-items-center mb-3"
                 style={{flex: '1 1 calc(20% - 1rem)', minWidth: '250px'}}>
                <strong>Department:</strong>
                <span>{professor?.affiliation}</span>
            </div>
            <div className="d-flex flex-column align-items-center mb-3"
                 style={{flex: '1 1 calc(20% - 1rem)', minWidth: '250px'}}>
                <strong>Association Type:</strong>
                <span>{professor?.associationType}</span>
            </div>
            <div className="d-flex flex-column align-items-center mb-3"
                 style={{flex: '1 1 calc(20% - 1rem)', minWidth: '250px'}}>
                <strong>Grader Type:</strong>
                <span>{professor?.graderType}</span>
            </div>
        </Card.Body>
    );

    const cardStyle = {
        width: '100%',
        cursor: 'pointer',
        transition: 'transform 0.3s ease, border-color 0.3s ease',
        borderColor: isClicked ? '#0056b3' : isHovered ? '#007bff' : '',
        transform: isHovered ? 'scale(1.00)' : 'scale(0.9)',
    };

    return (
        <>
            {professor ? (
                <Card
                    className={`lecture-card ${isClicked ? 'clicked' : ''} shadow-sm mb-10`}
                    style={cardStyle}
                    onClick={handleClick}
                    onMouseEnter={() => setIsHovered(true)}
                    onMouseLeave={() => setIsHovered(false)}
                >
                    {layout === 'vertical' ? renderVertical() : renderHorizontal()}
                </Card>
            ) : (<>Cannot load Professor Profile</>)}

            {showModal && professor && professor._links["update"] && (
                <ProfessorModal
                    professor={professor}
                    link={professor._links["update"]}
                    onClose={() => setShowModal(false)}
                />
            )}

            <ConfirmationModal
                show={showConfirmDeleteModal}
                message="Are you sure you want to delete this professor?"
                onConfirm={confirmDelete}
                onCancel={() => setShowConfirmDeleteModal(false)}
            />
        </>
    );
};

export default ProfessorCard;
