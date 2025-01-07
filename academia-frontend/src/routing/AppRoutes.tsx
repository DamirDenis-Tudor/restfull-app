import {Navigate, Route, Routes} from "react-router";
import HomePage from "../pages/HomePage.tsx";
import LoginPage from "../pages/LoginPage.tsx";
import {useContext} from "react";
import AuthContext from "../contexts/AuthContext.tsx";

export const AppRoutes = () => {
    const {loginResponse} = useContext(AuthContext);

    if (loginResponse.token === "") {
        return <Routes>
            <Route path="/login" element={<LoginPage/>}/>
            <Route path="*" element={<Navigate to="/login" replace/>}/>
        </Routes>
    }

    return (
        <Routes>
            <Route path="/home" element={<HomePage/>}/>
            <Route path="*" element={<Navigate to="/home" replace/>}/>
        </Routes>
    );
}