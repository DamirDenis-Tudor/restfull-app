import React, {useContext} from "react";
import {useLocation} from "react-router";
import {HomePageProvider} from "../contexts/HomePageProvider.tsx";
import AuthContext from "../contexts/AuthContext.tsx";
import {ProfessorList} from "../components/lists/ProfessorsList.tsx";

const ProfessorPage: React.FC = () => {
    const location = useLocation();
    const {loginResponse} = useContext(AuthContext);

    return (
        <HomePageProvider>
            {location.state ? (
                <ProfessorList key="All Students" link={location.state.professors}/>
            ) : (
                <ProfessorList key="All Students" link={loginResponse._links["professors"]}/>
            )}
        </HomePageProvider>
    );
};

export default ProfessorPage;
