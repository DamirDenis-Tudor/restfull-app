import Container from 'react-bootstrap/Container';
import Nav from 'react-bootstrap/Nav';
import Navbar from 'react-bootstrap/Navbar';
import AuthContext from "../contexts/AuthContext.tsx";
import React, {useContext} from "react";
import ProfessorCard from "./ProfessorCard.tsx";
import {HomePageContext} from "../contexts/HomePageContext.tsx";
import {LectureList} from "./LectureList.tsx";

enum ComponentType {

}


export const NavBar: React.FC = () => {
    const {loginResponse, logout} = useContext(AuthContext);
    const {setSelectedComponent} = useContext(HomePageContext);

    return (
        <Navbar expand="lg" bg="light" className="border-black">
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
                    <Nav.Link onClick={
                        () => {
                            setSelectedComponent(
                                <ProfessorCard link={loginResponse._links["Profile"]}></ProfessorCard>
                            )
                        }
                    } className="tw-text-zblack fw-bold">Profile</Nav.Link>
                    <Nav.Link onClick={
                        () => {
                            setSelectedComponent(
                                <LectureList key = "All Lectures" link={loginResponse._links["All Lectures"]}></LectureList>
                            )
                        }
                    } className="tw-text-black fw-bold">All Lectures</Nav.Link>
                    <Nav.Link onClick={
                        () => {
                            setSelectedComponent(
                                <LectureList key = "My Lectures" link={loginResponse._links["My Lectures"]}></LectureList>
                            )
                        }
                    } className="tw-text-black fw-bold">My Lectures</Nav.Link>
                    <Nav.Link onClick={logout} className="tw-text-black fw-bold">Logout</Nav.Link>
                </Nav>
            </Container>
        </Navbar>
    );
}
