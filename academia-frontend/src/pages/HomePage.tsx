import React from "react";
import {NavBar} from "../components/NavBar.tsx";
import {HomePageProvider} from "../contexts/HomePageProvider.tsx";
import { ToastContainer } from 'react-toastify';


const HomePage: React.FC = () => {
    return (
        <HomePageProvider>
            <NavBar/>
            <ToastContainer />
        </HomePageProvider>
    );
};

export default HomePage;