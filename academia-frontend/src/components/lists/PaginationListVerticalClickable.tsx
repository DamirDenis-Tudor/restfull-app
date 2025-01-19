import React from 'react';
import { Row, Col, Pagination, Container } from 'react-bootstrap';
import { EmbeddedResponse, Link } from "../../api/hateoas.ts";

interface PaginationProps<T> {
    title: string;
    data?: EmbeddedResponse<T>;
    setCurrentLink: React.Dispatch<React.SetStateAction<Link | undefined>>;
    renderItem: (item: T) => React.ReactNode;
}

export const PaginationListVerticalClickable = <T, >(
    {
        title,
        data,
        setCurrentLink,
        renderItem,
    }: PaginationProps<T>) => {

    if (!data) {
        return <div>Loading...</div>;
    }

    const itemsKey = Object.keys(data._embedded)[0];
    const items = data._embedded[itemsKey];

    if (!items || items.length === 0) {
        return <div>No data available</div>;
    }

    return (
        <Container className="flex row w-100 h-100 mb-10 p-4 border-1 justify-content-evenly">
            <div className="d-flex justify-content-between align-items-center mb-3">
                <h4>{title}</h4>
            </div>
            <Col
                xs={1}
                sm={2}
                md={3}
                lg={Math.min(Math.max(items.length, 3), 12)}
                xl={Math.min(Math.max(items.length, 3), 12)}
                className="g-4"
                style={{ maxWidth: '1000', margin: '0 auto' }}
            >
                {items.slice(0, 5).map((item, index) => (
                    <Row
                        key={index}
                        className="border p-3 rounded mb-3"
                    >{renderItem(item)}
                    </Row>
                ))}
            </Col>


            <Pagination
                className="d-flex justify-content-center align-items-center mt-4 mb-4"
                style={{
                    border: '1px solid #ddd',
                    borderRadius: '8px',
                    padding: '10px',
                }}
            >
                <Pagination.First
                    onClick={() => {
                        if (data._links.first) {
                            setCurrentLink(data._links.first);
                        }
                    }}
                    disabled={!data._links.first}
                />
                <Pagination.Prev
                    onClick={() => {
                        if (data._links.prev) {
                            setCurrentLink(data._links.prev);
                        }
                    }}
                    disabled={!data._links.prev}
                />
                <Pagination.Next
                    onClick={() => {
                        if (data._links.next) {
                            setCurrentLink(data._links.next);
                        }
                    }}
                    disabled={!data._links.next}
                />
                <Pagination.Last
                    onClick={() => {
                        if (data._links.last) {
                            setCurrentLink(data._links.last);
                        }
                    }}
                    disabled={!data._links.last}
                />
            </Pagination>
        </Container>
    );
};
