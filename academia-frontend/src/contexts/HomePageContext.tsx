import React, {createContext} from "react";

export interface HomePageContextProps {
    componentWithLink:  React.ReactNode;
    setSelectedComponent: (component: React.ReactNode) => void;
}

export const HomePageContext = createContext<HomePageContextProps>({
    componentWithLink: <></>,
    setSelectedComponent: () => {}
});