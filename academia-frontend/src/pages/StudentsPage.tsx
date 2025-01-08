import React, {useContext} from "react";
import {useLocation} from "react-router";
import {HomePageProvider} from "../contexts/HomePageProvider.tsx";
import AuthContext from "../contexts/AuthContext.tsx";
import {StudentList} from "../components/lists/StudentList.tsx";

const StudentPage: React.FC = () => {
    const location = useLocation();
    const {loginResponse} = useContext(AuthContext);

    return (
        <HomePageProvider>
            {location.state ? (
                <StudentList key="All Students" link={location.state.students}/>
            ) : (
                <StudentList key="All Students" link={loginResponse._links["students"]}/>
            )}
        </HomePageProvider>
    );
};

export default StudentPage;
