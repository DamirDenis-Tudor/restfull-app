import React from "react";
import {HomePageProvider} from "../contexts/HomePageProvider.tsx";
import {LectureInfo} from "../components/LectureInfo.tsx";
import {useLocation} from "react-router";


const LecturePage: React.FC = () => {
    const location = useLocation();
    return (
        <HomePageProvider>
            {location.state ? (
                <LectureInfo
                    lectureLink={location.state.lectureLink}
                    assessmentLink={location.state.assessmentLink}
                    professorLink={location.state.professorLink}
                    filesLink={location.state.filesLink}
                />
            ) : (<></>)
            }
        </HomePageProvider>
    );
};

export default LecturePage;