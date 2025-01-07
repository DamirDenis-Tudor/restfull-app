import React, { useEffect, useState } from 'react';
import { Card, ListGroup } from 'react-bootstrap';
import {fetchComponentData, Link, Professor} from "../api/hateoas.ts";

interface ProfessorCardProps {
    link: Link;
}

const ProfessorCard: React.FC<ProfessorCardProps> = ({ link }) => {
    const [professor, setProfessor] = useState<Professor>();

    useEffect(() => {
        fetchComponentData<Professor>(link)
            .then((data) => setProfessor(data))
            .catch((error) => {throw error});
    }, [link]);

    return (
        <Card className="professor-card" style={{ width: '32rem' }}>
            <Card.Body>
                <Card.Title>Professor Information</Card.Title>

                {professor && (
                    <ListGroup variant="flush">
                        <ListGroup.Item><strong>Name:</strong> {professor.firstName} {professor.lastName}</ListGroup.Item>
                        <ListGroup.Item><strong>Email:</strong> {professor.email}</ListGroup.Item>
                        <ListGroup.Item><strong>Department:</strong> {professor.affiliation}</ListGroup.Item>
                        <ListGroup.Item><strong>Association Type:</strong> {professor.associationType}</ListGroup.Item>
                        <ListGroup.Item><strong>Grader Type:</strong> {professor.graderType}</ListGroup.Item>
                    </ListGroup>
                )}
                {!professor && <div>No professor data available.</div>}
            </Card.Body>
        </Card>
    );
};

export default ProfessorCard;
