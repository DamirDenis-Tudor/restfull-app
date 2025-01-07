import React, { useContext, useEffect, useState } from 'react';
import { Card, ListGroup } from 'react-bootstrap';
import { fetchLink, Link, Student } from "../../api/hateoas.ts";
import { HomePageContext } from "../../contexts/HomePageContext.tsx";
import { ProfileCard } from "./ProfileCard.tsx";
import { FaEdit, FaTrashAlt } from 'react-icons/fa';
import StudentModal from "../modals/StudentModal.tsx";

interface StudentCardProps {
    link?: Link;
    stud?: Student;
    layout: 'vertical' | 'horizontal';
}

const StudentCard: React.FC<StudentCardProps> = ({ link, stud, layout = 'vertical' }) => {
    const [student, setStudent] = useState<Student | undefined>(stud);
    const [isClicked, setIsClicked] = useState(false);
    const [isHovered, setIsHovered] = useState(false);
    const [showModal, setShowModal] = useState(false);

    const { setSelectedComponent } = useContext(HomePageContext);

    useEffect(() => {
        if (link) {
            fetchLink<Student>(link)
                .then((data) => setStudent(data))
                .catch((error) => {
                    throw error;
                });
        }
    }, [link]);

    const handleClick = () => {
        setIsClicked(true);

        if (student && student._links["profile"]) {
            setSelectedComponent(
                <ProfileCard
                    card={<StudentCard layout="horizontal" link={student._links["profile"]}/>}
                    lectureLink={student._links["lectures"]} title={'Student Profile'} />
            );
        }

        setTimeout(() => setIsClicked(false), 300);
    };

    const handleDelete = () => {
        if(student && student._links["delete"]) {
            fetchLink(student._links["delete"], undefined).then();
        }
    };

    const handleUpdate = () => {
        if(student && student._links["update"]) {
            setShowModal(true);
        }
    };

    const renderVertical = () => (
        <Card.Body>
            <Card.Title>Student Information</Card.Title>
            {student ? (
                <ListGroup variant="flush">
                    <ListGroup.Item>
                        <strong>Name:</strong>
                        <span className="d-block">{student.firstName} {student.lastName}</span>
                    </ListGroup.Item>
                    <ListGroup.Item>
                        <strong>Email:</strong>
                        <span className="d-block">{student.email}</span>
                    </ListGroup.Item>
                    <ListGroup.Item>
                        <strong>Cycle Type:</strong>
                        <span className="d-block">{student.cycleType}</span>
                    </ListGroup.Item>
                    <ListGroup.Item>
                        <strong>Study Year:</strong>
                        <span className="d-block">{student.studyYear}</span>
                    </ListGroup.Item>
                    <ListGroup.Item>
                        <strong>Group:</strong>
                        <span className="d-block">{student.studentGroup}</span>
                    </ListGroup.Item>
                </ListGroup>
            ) : (
                <div>No student data available.</div>
            )}
            {layout === 'vertical' && (
                <div className="d-flex justify-content-between mt-3">
                    {student?._links["update"] && (
                        <div
                            className="icon-button"
                            onClick={handleUpdate}
                            title="Update"
                            style={{cursor: 'pointer', color: '#f39c12'}}
                        ><FaEdit size={20}/></div>
                    )}
                    {student?._links["delete"] && (
                        <div
                            className="icon-button"
                            onClick={handleDelete}
                            title="Delete"
                            style={{cursor: 'pointer', color: '#e74c3c'}}
                        ><FaTrashAlt size={20} /></div>
                    )}
                </div>
            )}
        </Card.Body>
    );

    const renderHorizontal = () => (
        <Card.Body className="d-flex flex-wrap justify-content-between p-3">
            <div className="d-flex flex-column align-items-center mb-3" style={{ flex: '1 1 calc(20% - 1rem)', minWidth: '250px' }}>
                <strong>Name:</strong>
                <span>{student?.firstName} {student?.lastName}</span>
            </div>
            <div className="d-flex flex-column align-items-center mb-3" style={{ flex: '1 1 calc(20% - 1rem)', minWidth: '250px' }}>
                <strong>Email:</strong>
                <span>{student?.email}</span>
            </div>
            <div className="d-flex flex-column align-items-center mb-3" style={{ flex: '1 1 calc(20% - 1rem)', minWidth: '250px' }}>
                <strong>Cycle Type:</strong>
                <span>{student?.cycleType}</span>
            </div>
            <div className="d-flex flex-column align-items-center mb-3" style={{ flex: '1 1 calc(20% - 1rem)', minWidth: '250px' }}>
                <strong>Study Year:</strong>
                <span>{student?.studyYear}</span>
            </div>
            <div className="d-flex flex-column align-items-center mb-3" style={{ flex: '1 1 calc(20% - 1rem)', minWidth: '250px' }}>
                <strong>Group:</strong>
                <span>{student?.studentGroup}</span>
            </div>
        </Card.Body>
    );

    const cardStyle = {
        width: layout === 'horizontal' ? '100%' : '20rem',
        cursor: 'pointer',
        transition: 'transform 0.3s ease, border-color 0.3s ease',
        borderColor: isClicked ? '#0056b3' : isHovered ? '#007bff' : '',
        transform: isHovered ? 'scale(1.05)' : 'scale(1)',
    };

    return (
        <>
            <Card
                className={`student-card ${isClicked ? 'clicked' : ''} shadow-sm mb-10`}
                style={cardStyle}
                onClick={handleClick}
                onMouseEnter={() => setIsHovered(true)}
                onMouseLeave={() => setIsHovered(false)}
            >
                {layout === 'vertical' ? renderVertical() : renderHorizontal()}
            </Card>

            {showModal && student && student._links["update"] && (
                <StudentModal
                    student={student}
                    link={student._links["update"]}
                    onClose={() => setShowModal(false)}
                />
            )}
        </>
    );
};

export default StudentCard;
