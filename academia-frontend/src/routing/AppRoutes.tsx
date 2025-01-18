import {Navigate, Route, Routes} from "react-router";
import LoginPage from "../pages/LoginPage.tsx";
import {useContext} from "react";
import AuthContext from "../contexts/AuthContext.tsx";
import LecturePage from "../pages/LecturePage.tsx";
import LecturesPage from "../pages/LecturesPage.tsx";
import StudentsPage from "../pages/StudentsPage.tsx";
import ProfessorPage from "../pages/ProfessorsPage.tsx";
import {StudentProfilePage} from "../pages/StudentProfilePage.tsx";
import {ProfessorProfilePage} from "../pages/ProfessorProfilePage.tsx";

export const AppRoutes = () => {
    const {loginResponse} = useContext(AuthContext);

    if (loginResponse.token === "") {
        return (
            <Routes>
                <Route path="/login" element={<LoginPage/>}/>
                <Route path="*" element={<Navigate to="/login" replace/>}/>
            </Routes>
        );
    }

    if (loginResponse.role === "PROFESSOR") {
        return (
            <Routes>
                <Route path="/profile/professor" element={<ProfessorProfilePage isCurrentUser={true}/>}/>
                <Route path="/profile/professor/:id" element={<ProfessorProfilePage isCurrentUser={false}/>}/>
                <Route path="/profile/student/:id" element={<StudentProfilePage isCurrentUser={false}/>}/>
                <Route path="/lecture/:id" element={<LecturePage/>}/>
                <Route path="/lectures" element={<LecturesPage/>}/>
                <Route path="*" element={<Navigate to="/profile/professor" replace/>}/>
            </Routes>
        );
    } else if (loginResponse.role === "STUDENT") {
        return (
            <Routes>
                <Route path="/profile/student" element={<StudentProfilePage isCurrentUser={true}/>}/>
                <Route path="/profile/student/:id" element={<StudentProfilePage isCurrentUser={false}/>}/>
                <Route path="/profile/professor/:id" element={<ProfessorProfilePage isCurrentUser={false}/>}/>
                <Route path="/lecture/:id" element={<LecturePage />} />
                <Route path="*" element={<Navigate to="/profile/student" replace/>}/>
            </Routes>
        );
    } else if (loginResponse.role === "ADMIN") {
        return (<Routes>
                <Route path="/students" element={<StudentsPage/>}/>
                <Route path="/profile/student/:id" element={<StudentProfilePage isCurrentUser={false}/>}/>
                <Route path="/professors" element={<ProfessorPage/>}/>
                <Route path="/profile/professor/:id" element={<ProfessorProfilePage isCurrentUser={false}/>}/>
                <Route path="*" element={<Navigate to="/students" replace/>}/>
            </Routes>
        );
    }
};
