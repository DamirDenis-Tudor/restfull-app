import React, { useEffect, useState } from 'react';
import { EmbeddedResponse, fetchLink, Lecture, Link } from "../../api/hateoas.ts";
import LectureCard from "../cards/LectureCard.tsx";
import { v4 } from "uuid";
import { PaginationList } from "./PaginationList.tsx";
import LectureModal from "../modals/LectureModal.tsx";

interface LectureListProps {
    link?: Link;
}

export const LectureList: React.FC<LectureListProps> = ({ link }) => {
    const [lecturesData, setLecturesData] = useState<EmbeddedResponse<Lecture>>();
    const [currentLink, setCurrentLink] = useState<Link | undefined>(link);
    const [showModal, setShowModal] = useState<boolean>(false);
    const [modalLink, setModalLink] = useState<Link | undefined>(undefined);

    useEffect(() => {
        if (currentLink) {
            fetchLink<EmbeddedResponse<Lecture>>(currentLink)
                .then((data) => {
                    setLecturesData(data);
                })
                .catch((error) => {
                    console.error("Error fetching lectures data:", error);
                });
        }
    }, [currentLink, link]);

    const openModal = () => {
        if (lecturesData?._links["create"]) {
            setModalLink(lecturesData._links["create"]);
            setShowModal(true);
        }
    };

    return (
        <>
            {currentLink && lecturesData && (
                <PaginationList
                    title="List of lectures"
                    data={lecturesData}
                    setCurrentLink={setCurrentLink}
                    renderItem={(lecture: Lecture | undefined) => (
                        <LectureCard key={v4()} lecture={lecture} />
                    )}
                    onAddElement={openModal}
                />
            )}

            {showModal && modalLink && (
                <LectureModal
                    link={modalLink}
                    lecture={undefined}
                    onClose={() => setShowModal(false)}
                />
            )}
        </>
    );
};
