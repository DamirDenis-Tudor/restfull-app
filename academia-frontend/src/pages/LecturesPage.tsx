import React, {useContext} from "react";
import { useLocation } from "react-router";
import { HomePageProvider } from "../contexts/HomePageProvider.tsx";
import {LectureList} from "../components/lists/LectureList.tsx";
import AuthContext from "../contexts/AuthContext.tsx";

const LecturePage: React.FC = () => {
    const location = useLocation();
    const {loginResponse} = useContext(AuthContext);

    return (
        <HomePageProvider>
            {location.state ? (
                <LectureList key="All Lectures" link={location.state.lectures} />
            ) : (
                <LectureList key="All Lectures" link={loginResponse._links["all-lectures"]} />
            )}
        </HomePageProvider>
    );
};

export default LecturePage;
