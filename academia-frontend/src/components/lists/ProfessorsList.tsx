import React, {useEffect, useState} from 'react';
import {EmbeddedResponse, fetchLink, Link, Professor} from "../../api/hateoas.ts";
import {v4} from "uuid";
import {PaginationList} from "./PaginationList.tsx";
import ProfessorCard from "../cards/ProfessorCard.tsx"; // Assuming you have a ProfessorCard component
import ProfessorModal from "../modals/ProfessorModal.tsx";

interface ProfessorListProps {
    link?: Link;
}

export const ProfessorList: React.FC<ProfessorListProps> = ({link}) => {
    const [professorData, setProfessorData] = useState<EmbeddedResponse<Professor>>();
    const [currentLink, setCurrentLink] = useState<Link | undefined>(link);
    const [showModal, setShowModal] = useState<boolean>(false);
    const [modalLink, setModalLink] = useState<Link | undefined>(undefined);

    useEffect(() => {
        if (currentLink) {
            fetchLink<EmbeddedResponse<Professor>>(currentLink)
                .then((data) => {
                    setProfessorData(data);
                })
                .catch((error) => {
                    console.error("Error fetching professor data:", error);
                });
        }
    }, [currentLink, link]);

    const openModal = () => {
        if (professorData?._links["create"]) {
            setModalLink(professorData._links["create"]);
            setShowModal(true);
        }
    };

    return (
        <>
            {currentLink && professorData && (
                <PaginationList
                    title="List of Professors"
                    data={professorData}
                    setCurrentLink={setCurrentLink}
                    renderItem={(professor: Professor) => {
                        return <ProfessorCard key={v4()} prof={professor} layout={'vertical'}/>;
                    }}
                    onAddElement={openModal}
                />
            )}

                {showModal && modalLink && (
                    <ProfessorModal
                        link={modalLink}
                        professor={undefined}
                        onClose={() => setShowModal(false)}
                    />
                )}
        </>
    );
};
