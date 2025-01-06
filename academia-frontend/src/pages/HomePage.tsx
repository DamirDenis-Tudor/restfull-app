import { Container } from "react-bootstrap";
import React, { useContext } from "react";
import AuthContext from "../contexts/AuthContext.tsx";
import { NavBarComponent } from "../components/navbar/NavBarComponent.tsx";

const HomePage: React.FC = () => {
    useContext(AuthContext);

    return (
        <>
            <NavBarComponent role="Professor" username="Marius" />
            <Container fluid className="home-container">
                {/* Your content goes here */}
            </Container>
        </>
    );
};

export default HomePage;
