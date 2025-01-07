import React, {useEffect, useState} from 'react';
import {EmbeddedResponse, fetchLink, Link, Professor} from "../../api/hateoas.ts";
import {v4} from "uuid";
import {PaginationList} from "./PaginationList.tsx";
import ProfessorCard from "../cards/ProfessorCard.tsx";
import {Button} from "react-bootstrap";
import {FaPlusCircle} from "react-icons/fa";

interface ProfessorListProps {
    link?: Link;
}

export const ProfessorList: React.FC<ProfessorListProps> = ({link}) => {
    const [professorData, setProfessorData] = useState<EmbeddedResponse<Professor>>();
    const [currentLink, setCurrentLink] = useState<Link|undefined>(link);

    useEffect(() => {
        if (currentLink) {
            fetchLink<EmbeddedResponse<Professor>>(currentLink)
                .then((data) => {
                    setProfessorData(data);
                })
                .catch((error) => {
                    console.error("Error fetching lectures data:", error);
                });
        }
    }, [currentLink, link]);

    const handleAddProfessor = () => {
        console.log("Add new professor clicked");
    };

    return (
        <>
            {currentLink && (
                <PaginationList
                    title="List of Professors"
                    data={professorData}
                    setCurrentLink={setCurrentLink}
                    renderItem={(Professor: Professor | undefined) => {
                        return <ProfessorCard layout={'vertical'} key={v4()} prof={Professor}/>
                    }}
                />
            )}

        </>
    );
};
