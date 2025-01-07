import React, {useState} from "react";
import { HomePageContext } from "./HomePageContext.tsx";
import {Container} from "react-bootstrap";


export const HomePageProvider: React.FC<{ children: React.ReactNode }> = ({children}) => {
    const [componentWithLink, setSelectedComponent] = useState<React.ReactNode>(<></>);

    return (
        <HomePageContext.Provider value={{componentWithLink, setSelectedComponent}}>
            {children}
            <div className="h-screen flex items-center justify-center bg-white-200">
                <Container fluid
                           className="w-75 h-75 flex flex-col items-center justify-center tw-bg-gray-500 bg-opacity-80 p-6 rounded-lg shadow-lg mx-auto">
                    {componentWithLink}
                </Container>
            </div>
        </HomePageContext.Provider>
    );
};
