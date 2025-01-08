import { Navigate, Route, Routes, useLocation } from "react-router";
import HomePage from "../pages/HomePage.tsx";
import LoginPage from "../pages/LoginPage.tsx";
import { useContext } from "react";
import AuthContext from "../contexts/AuthContext.tsx";
import LecturePage from "../pages/LecturePage.tsx";

export const AppRoutes = () => {
    const { loginResponse } = useContext(AuthContext);

    if (loginResponse.token === "") {
        return (
            <Routes>
                <Route path="/login" element={<LoginPage />} />
                <Route path="*" element={<Navigate to="/login" replace />} />
            </Routes>
        );
    }

    return (
        <Routes>
            <Route path="/home" element={<HomePage />} />
            <Route path="/lecture/*" element={<LecturePage/>}/>
            <Route path="*" element={<Navigate to="/home" replace />} />
        </Routes>
    );
};
