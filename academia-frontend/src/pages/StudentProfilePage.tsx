import React, {useContext} from "react";
import {useLocation} from "react-router";
import {HomePageProvider} from "../contexts/HomePageProvider.tsx";
import {ProfileCard} from "../components/cards/ProfileCard.tsx";
import StudentCard from "../components/cards/StudentCard.tsx";
import AuthContext from "../contexts/AuthContext.tsx";
interface StudentsProfilePageProps {
    isCurrentUser: boolean;
}
export const StudentProfilePage: React.FC<StudentsProfilePageProps> = ({isCurrentUser}) => {
    const location = useLocation();
    const {loginResponse} = useContext(AuthContext);

    return (
        <HomePageProvider>
            {(location.state && !isCurrentUser) ? (
                <ProfileCard
                    card={<StudentCard link={location.state.profile} layout={"horizontal"}/>}
                    lectureLink={location.state.lectures}
                    title={"Student Profile"}
                />
            ) : (
                <ProfileCard
                    card={<StudentCard link={loginResponse._links["me"]} layout={"horizontal"}/>}
                    lectureLink={loginResponse._links["my-lectures"]}
                    title={"Student Profile"}
                />
            )}
        </HomePageProvider>
    );
};
