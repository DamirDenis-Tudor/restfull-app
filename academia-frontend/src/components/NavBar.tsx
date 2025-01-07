import Container from 'react-bootstrap/Container';
import Nav from 'react-bootstrap/Nav';
import Navbar from 'react-bootstrap/Navbar';
import AuthContext from "../contexts/AuthContext.tsx";
import React, {useContext} from "react";
import {HomePageContext} from "../contexts/HomePageContext.tsx";
import {LectureList} from "./lists/LectureList.tsx";
import {ProfileCard} from "./cards/ProfileCard.tsx";
import ProfessorCard from "./cards/ProfessorCard.tsx";
import StudentCard from "./cards/StudentCard.tsx";
import {StudentList} from "./lists/StudentList.tsx";
import {ProfessorList} from "./lists/ProfessorsList.tsx";


export const NavBar: React.FC = () => {
    const {loginResponse, logout} = useContext(AuthContext);
    const {setSelectedComponent} = useContext(HomePageContext);

    const getNavLinks = () => {
        if (loginResponse.role === "PROFESSOR") {
            return (
                <>
                    <Nav.Link
                        onClick={() => {
                            setSelectedComponent(
                                <ProfileCard
                                    card={<ProfessorCard layout="horizontal" link={loginResponse._links["me"]}/>}
                                    lectureLink={loginResponse._links["my-lectures"]} title={''} />
                            );
                        }}
                        className="tw-text-zblack fw-bold"
                    >Profile</Nav.Link>
                    <Nav.Link
                        onClick={() => {
                            setSelectedComponent(
                                <LectureList key="All Lectures" link={loginResponse._links["all-lectures"]} />
                            );
                        }}
                        className="tw-text-black fw-bold"
                    >All Lectures</Nav.Link>
                </>
            );
        } else if (loginResponse.role === "STUDENT") {
            return (
                <>
                    <Nav.Link
                        onClick={() => {
                            setSelectedComponent(
                                <ProfileCard
                                    card={<StudentCard layout="horizontal" link={loginResponse._links["me"]}/>}
                                    lectureLink={loginResponse._links["lectures"]} title={'Student Profile'} />
                            );
                        }}
                        className="tw-text-zblack fw-bold"
                    >Profile</Nav.Link>
                </>
            );
        } else if (loginResponse.role === "ADMIN") {
            return (
                <>
                    <Nav.Link
                        onClick={() => {
                            setSelectedComponent(
                                <StudentList key="Students" link={loginResponse._links["students"]} />
                            );
                        }}
                        className="tw-text-black fw-bold"
                    >Students</Nav.Link>
                    <Nav.Link
                        onClick={() => {
                            setSelectedComponent(
                                <ProfessorList key="Professors" link={loginResponse._links["professors"]} />
                            );
                        }}
                        className="tw-text-black fw-bold"
                    >Professors</Nav.Link>
                </>
            );
        }

        return <></>;
    };


    return (
        <Navbar style={{zIndex: 1}} expand="lg" bg="light" className="border-black position-fixed w-100 top-0 start-0">
            <Container>
                <Navbar.Brand href="/" className="fw-bold flex items-center">
                    <img
                        src="https://sso.tuiasi.ro/auth/welcome-content/favicon.ico"
                        alt="Noodle Icon"
                        className="w-8 h-8 mr-2"
                    />Noodle@{loginResponse.role}
                </Navbar.Brand>
                <Nav.Item className="d-flex align-items-center me-3">
                    <span className="text-light">@{loginResponse.role}</span>
                </Nav.Item>

                <Nav className="ms-auto">
                    {getNavLinks()}
                    <Nav.Link onClick={logout} className="tw-text-black fw-bold">Logout</Nav.Link>
                </Nav>
            </Container>
        </Navbar>
    );
}
