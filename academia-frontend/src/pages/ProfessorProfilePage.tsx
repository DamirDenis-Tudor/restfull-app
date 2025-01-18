import React, {useContext} from "react";
import {useLocation} from "react-router";
import {HomePageProvider} from "../contexts/HomePageProvider.tsx";
import {ProfileCard} from "../components/cards/ProfileCard.tsx";
import AuthContext from "../contexts/AuthContext.tsx";
import ProfessorCard from "../components/cards/ProfessorCard.tsx";
interface ProfessorProfilePageProps {
    isCurrentUser: boolean;
}
export const ProfessorProfilePage: React.FC<ProfessorProfilePageProps> = ({isCurrentUser}) => {
    const location = useLocation();
    const {loginResponse} = useContext(AuthContext);

    return (
        <HomePageProvider>
            {(location.state && !isCurrentUser) ? (
                <ProfileCard
                    card={<ProfessorCard link={location.state.profile} layout={"horizontal"}/>}
                    lectureLink={location.state.lectures}
                    title={"Professor Profile"}
                />
            ) : (
                <ProfileCard
                    card={<ProfessorCard link={loginResponse._links["me"]} layout={"horizontal"}/>}
                    lectureLink={loginResponse._links["my-lectures"]}
                    title={"My Profile"}
                />
            )}
        </HomePageProvider>
    );
};
