import React from 'react';
import { Row, Col, Container } from 'react-bootstrap';
import { v4 } from 'uuid';

interface PaginationProps {
    title: string;
    items: React.ReactNode[];
}

export const ItemsList = ({ title, items }: PaginationProps) => {
    return (
        <Container className="flex row w-100 h-100 mb-10 p-4 border-1 justify-content-evenly">
            <h4>{title}</h4>
            <Row
                xs={1} sm={2} md={3} lg={items.length} xl={items.length}
                className="g-4"
                style={{ maxWidth: '90%', margin: '0 auto' }}
            >
                {items.map((element) => (
                    <Col key={v4()} className="border p-3 rounded mb-3">
                        {element}
                    </Col>
                ))}
            </Row>
        </Container>
    );
};
