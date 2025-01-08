import React from 'react';
import { Row, Col, Container, Button } from 'react-bootstrap';
import { v4 } from 'uuid';
import { FaPlusCircle } from 'react-icons/fa';

interface ItemsListProps {
    title: string;
    items: React.ReactNode[];
    onAddElement?: () => void;
}

export const ItemsList: React.FC<ItemsListProps> = ({ title, items, onAddElement = undefined }) => {
    return (
        <Container className="flex row w-100 h-100 mb-10 p-4 border-1 justify-content-evenly">
            <div className="d-flex justify-content-between align-items-center mb-3">
                <h4>{title}</h4>
                {onAddElement && (
                    <Button
                        variant="primary"
                        className="d-flex align-items-center"
                        onClick={onAddElement}
                        style={{
                            padding: '0.5rem 1rem',
                            borderRadius: '50%',
                            display: 'flex',
                            justifyContent: 'center',
                            alignItems: 'center',
                        }}
                    >
                        <FaPlusCircle style={{ fontSize: '1.5rem' }} />
                    </Button>
                )}
            </div>
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
