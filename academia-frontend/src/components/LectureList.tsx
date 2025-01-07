import React, {useEffect, useState} from 'react';
import {Row, Col, Alert, Container, Pagination} from 'react-bootstrap';
import {EmbeddedResponse, fetchComponentData, Lecture, Link} from "../api/hateoas.ts";
import LectureCard from "./LectureCard.tsx";
import {v4} from "uuid";

interface LectureListProps {
    link: Link;
}

export const LectureList: React.FC<LectureListProps> = ({link}) => {
    const [lecturesData, setLecturesData] = useState<EmbeddedResponse<Lecture> | undefined>(undefined);
    const [currentLink, setCurrentLink] = useState<Link>(link);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {

        fetchComponentData<EmbeddedResponse<Lecture>>(currentLink)
            .then((data) => {
                setLecturesData(data);
                setError(null);
            })
            .catch((error) => {
                setError("Failed to load lectures data " + error, );
                console.error("Error fetching lectures data:", error);
            });

    }, [currentLink, link]);

    useEffect(() => {
        console.log("Fetching lectures");
    }, [lecturesData]);

    return (
        <>
            {lecturesData ? (
                <>
                    <Container className="d-flex justify-content-center align-items-center w-100 h-100">
                        {error && <Alert variant="danger">{error}</Alert>}

                        <Row
                            xs={1} sm={2} md={3} lg={3} xl={3}
                            className="g-4"
                            style={{maxWidth: '90%'}}
                        >{lecturesData._embedded.lectureList.slice(0, 6).map((lecture) => (
                            <Col key={v4()}>
                                <LectureCard lecture={lecture}/>
                            </Col>
                        ))}
                        </Row>
                    </Container>
                    <Pagination>
                        <Pagination.First
                            onClick={() => {
                                if (lecturesData._links.first) {
                                    setCurrentLink(lecturesData._links.first);
                                }
                            }}
                            disabled={!lecturesData._links.first}/>
                        <Pagination.Prev
                            onClick={() => {
                                if (lecturesData._links["prev"]) {
                                    setCurrentLink(lecturesData._links["prev"]);
                                }
                            }}
                            disabled={!lecturesData._links.prev}/>
                        <Pagination.Next
                            onClick={() => {
                                if (lecturesData._links.next) {
                                    setCurrentLink(lecturesData._links.next);
                                }
                            }}
                            disabled={!lecturesData._links.next}/>
                        <Pagination.Last
                            onClick={() => {
                                if (lecturesData._links.last) {
                                    setCurrentLink(lecturesData._links.last);
                                }
                            }}
                            disabled={!lecturesData._links.last}/>
                    </Pagination>
                </>
            ) : (
                <div>Loading...</div>
            )}
        </>
    );
};
