import {BrowserRouter, Navigate, Route, Routes} from "react-router";
import WrapperRoute from "./WrapperRoute.tsx";
import HomePage from "../pages/HomePage.tsx";
import AuthProvider from "../contexts/providers/AuthProvider.tsx";
import LoginPage from "../pages/LoginPage.tsx";

export const AppRouter = () => {
    return (
        <AuthProvider>
            <BrowserRouter>
                <Routes>
                    <Route path="/login" element={<LoginPage/>}/>
                    <Route element={<WrapperRoute />}>

                    </Route>
                    <Route path="/home" element={<HomePage/>}/>
                    <Route path="*" element={<Navigate to="/Login" replace/>}/>
                </Routes>
            </BrowserRouter>
        </AuthProvider>
    );
}
