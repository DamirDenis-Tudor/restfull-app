import React, {useEffect, useState} from 'react';
import {EmbeddedResponse, fetchComponentData, Lecture, Link} from "../../api/hateoas.ts";
import LectureCard from "../cards/LectureCard.tsx";
import {v4} from "uuid";
import {PaginationList} from "./PaginationList.tsx";

interface LectureListProps {
    link?: Link;
}

export const LectureList: React.FC<LectureListProps> = ({link}) => {
    const [lecturesData, setLecturesData] = useState<EmbeddedResponse<Lecture>>();
    const [currentLink, setCurrentLink] = useState<Link | undefined>(link);

    useEffect(() => {
        if (currentLink) {
            fetchComponentData<EmbeddedResponse<Lecture>>(currentLink)
                .then((data) => {
                    setLecturesData(data);
                })
                .catch((error) => {
                    console.error("Error fetching lectures data:", error);
                });
        }
    }, [currentLink, link]);

    return (
        <PaginationList
            title="List of lectures"
            data={lecturesData}
            setCurrentLink={setCurrentLink}
            renderItem={(lecture: Lecture | undefined) => {
                return <LectureCard key={v4()} lecture={lecture}/>
            }}
        />
    );
};
