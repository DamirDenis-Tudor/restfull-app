import React from "react";
import {NavBar} from "../components/NavBar.tsx";
import {HomePageProvider} from "../contexts/HomePageProvider.tsx";


const HomePage: React.FC = () => {
    return (
        <HomePageProvider>
            <NavBar/>
        </HomePageProvider>
    );
};

export default HomePage;