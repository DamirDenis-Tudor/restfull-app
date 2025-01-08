import React, { useContext } from "react";
import { useLocation } from "react-router";
import { HomePageProvider } from "../contexts/HomePageProvider.tsx";
import { ProfileCard } from "../components/cards/ProfileCard.tsx";
import ProfessorCard from "../components/cards/ProfessorCard.tsx";
import StudentCard from "../components/cards/StudentCard.tsx";
import { StudentList } from "../components/lists/StudentList.tsx";
import AuthContext from "../contexts/AuthContext.tsx";

export const ProfilePage: React.FC = () => {
    const location = useLocation();
    const { loginResponse } = useContext(AuthContext);

    return (
        <HomePageProvider>
            {location.state ? (
                loginResponse.role === "PROFESSOR" ? (
                    <ProfileCard
                        card={<ProfessorCard link={location.state.me} layout={"horizontal"} />}
                        lectureLink={location.state.my_lectures}
                        title={"Professor Profile"}
                    />
                ) : loginResponse.role === "STUDENT" ? (
                    <ProfileCard
                        card={<StudentCard link={location.state.me} layout={"horizontal"} />}
                        lectureLink={location.state.lectures}
                        title={"Student Profile"}
                    />
                ) : loginResponse.role === "ADMIN" ? (
                    <StudentList key="Students" link={location.state.students} />
                ) : null
            ) : (
                loginResponse.role === "PROFESSOR" ? (
                    <ProfileCard
                        card={<ProfessorCard link={loginResponse._links["me"]} layout={"horizontal"} />}
                        lectureLink={loginResponse._links["my-lectures"]}
                        title={"Professor Profile"}
                    />
                ) : loginResponse.role === "STUDENT" ? (
                    <ProfileCard
                        card={<StudentCard link={loginResponse._links["me"]} layout={"horizontal"} />}
                        lectureLink={loginResponse._links["lectures"]}
                        title={"Student Profile"}
                    />
                ) : loginResponse.role === "ADMIN" ? (
                    <StudentList key="Students" link={loginResponse._links["students"]} />
                ) : null
            )}
        </HomePageProvider>
    );
};
