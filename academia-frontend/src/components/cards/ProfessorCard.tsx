import React, { useContext, useEffect, useState } from 'react';
import { Card, ListGroup } from 'react-bootstrap';
import { fetchComponentData, Link, Professor } from "../../api/hateoas.ts";
import { HomePageContext } from "../../contexts/HomePageContext.tsx";
import { ProfileCard } from "./ProfileCard.tsx";

interface ProfessorCardProps {
    link?: Link;
    prof?: Professor;
    layout: 'vertical' | 'horizontal';
}

const ProfessorCard: React.FC<ProfessorCardProps> = ({ link, prof, layout = 'vertical' }) => {
    const [professor, setProfessor] = useState<Professor | undefined>(prof);
    const [isClicked, setIsClicked] = useState(false);
    const [isHovered, setIsHovered] = useState(false);

    const { setSelectedComponent } = useContext(HomePageContext);

    useEffect(() => {
        if (link) {
            fetchComponentData<Professor>(link)
                .then((data) => setProfessor(data))
                .catch((error) => {
                    throw error;
                });
        }
    }, [link]);

    const handleClick = () => {
        setIsClicked(true);

        if (professor) {
            setSelectedComponent(
                <ProfileCard
                    card={<ProfessorCard layout="horizontal" link={professor._links["self"]}/>}
                    lectureLink={professor._links["my-lectures"]} title={'Professor Profile'}                />
            );
        }

        setIsClicked(false);
    };

    const cardStyle = {
        width: layout === 'horizontal' ? '100%' : '20rem',
        cursor: 'pointer',
        transition: 'transform 0.3s ease, border-color 0.3s ease',
        borderColor: isClicked ? '#0056b3' : isHovered ? '#007bff' : '',
        transform: isHovered ? 'scale(1.05)' : 'scale(1)',
    };

    const renderVertical = () => (
        <Card.Body>
            <Card.Title>Professor Information</Card.Title>
            {professor ? (
                <ListGroup variant="flush">
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
            ) : (
                <div>No professor data available.</div>
            )}
        </Card.Body>
    );


    const renderHorizontal = () => (
        <Card.Body className="d-flex flex-wrap justify-content-between p-3">
            <div className="d-flex flex-column align-items-center mb-3" style={{ flex: '1 1 calc(20% - 1rem)', minWidth: '250px' }}>
                <strong>Name:</strong>
                <span>{professor?.firstName} {professor?.lastName}</span>
            </div>
            <div className="d-flex flex-column align-items-center mb-3" style={{ flex: '1 1 calc(20% - 1rem)', minWidth: '250px' }}>
                <strong>Email:</strong>
                <span>{professor?.email}</span>
            </div>
            <div className="d-flex flex-column align-items-center mb-3" style={{ flex: '1 1 calc(20% - 1rem)', minWidth: '250px' }}>
                <strong>Department:</strong>
                <span>{professor?.affiliation}</span>
            </div>
            <div className="d-flex flex-column align-items-center mb-3" style={{ flex: '1 1 calc(20% - 1rem)', minWidth: '250px' }}>
                <strong>Association Type:</strong>
                <span>{professor?.associationType}</span>
            </div>
            <div className="d-flex flex-column align-items-center mb-3" style={{ flex: '1 1 calc(20% - 1rem)', minWidth: '250px' }}>
                <strong>Grader Type:</strong>
                <span>{professor?.graderType}</span>
            </div>
        </Card.Body>
    );


    return (
        <Card
            className={`lecture-card ${isClicked ? 'clicked' : ''} shadow-sm mb-10`}
            style={cardStyle}
            onClick={handleClick}
            onMouseEnter={() => setIsHovered(true)}
            onMouseLeave={() => setIsHovered(false)}
        >
            {layout === 'vertical' ? renderVertical() : renderHorizontal()}
        </Card>
    );
};

export default ProfessorCard;
