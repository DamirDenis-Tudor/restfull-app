import React, {useContext, useState} from "react";
import {HomePageContext} from "./HomePageContext.tsx";
import {Container} from "react-bootstrap";
import {ProfileCard} from "../components/cards/ProfileCard.tsx";
import AuthContext from "./AuthContext.tsx";
import ProfessorCard from "../components/cards/ProfessorCard.tsx";
import StudentCard from "../components/cards/StudentCard.tsx";


export const HomePageProvider: React.FC<{ children: React.ReactNode }> = ({children}) => {
    const {loginResponse} = useContext(AuthContext);
    const [componentWithLink, setSelectedComponent] = useState<React.ReactNode>(
        loginResponse.role === "PROFESSOR" ? (
            <ProfileCard
                card={<ProfessorCard link={loginResponse._links["me"]} layout={"horizontal"}/>}
                lectureLink={loginResponse._links["my-lectures"]} title={"Professor Profile"}/>
        ) : (
            <ProfileCard
                card={<StudentCard link={loginResponse._links["me"]} layout={"horizontal"}/>}
                lectureLink={loginResponse._links["my-lectures"]} title={"Student Profile"}/>
        )
    );

    return (
        <HomePageContext.Provider value={{componentWithLink, setSelectedComponent}}>
            {children}
            <div className="h-screen flex items-center justify-center bg-white-200">
                <Container
                    fluid
                    className="w-75 min-h-[90%]  h-auto flex flex-col items-center justify-center
                    tw-bg-gray-500 bg-opacity-80 p-6 rounded-lg shadow-lg mx-auto mt-20 mb-auto"
                >{componentWithLink}
                </Container>
            </div>

        </HomePageContext.Provider>
    );
};
