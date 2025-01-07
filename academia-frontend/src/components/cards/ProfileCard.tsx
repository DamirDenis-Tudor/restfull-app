import React, {ReactNode} from 'react';
import {Row} from 'react-bootstrap';
import {Link} from "../../api/hateoas.ts";
import {LectureList} from "../lists/LectureList.tsx";
import {v4} from "uuid";

interface ProfileCardProps {
    card: ReactNode,
    lectureLink: Link,
    title: string
}

export const ProfileCard: React.FC<ProfileCardProps> = ({card, lectureLink, title}) => {
    return (
        <>
            <h4>{title}</h4>
            <Row className="justify-content-center">
                {card}
            </Row>
            <LectureList key={v4()} link={lectureLink}/>
        </>
    );
};
