import React from 'react';
import { Container, Button } from 'react-bootstrap';
import { TextInputComponent } from '../components/textInput/TextInputComponent.tsx';

const LoginPage: React.FC = () => {
    return (
        <div className="h-screen flex items-center justify-center bg-white-200">
            <Container className="w-25  flex flex-col items-center justify-center
            bg-gray-200 bg-opacity-80 p-6 rounded-lg shadow-lg max-w-sm mx-auto">

                <p className="text-3xl font-bold mb-6 flex items-center">
                    <img
                        src="https://sso.tuiasi.ro/auth/welcome-content/favicon.ico"
                        alt="Noodle Icon"
                        className="w-8 h-8 mr-2"
                    />
                    <span>Noodle</span>
                </p>

                <div className="mb-xl-3"/>

                <TextInputComponent
                    className="w-75"
                    id="username"
                    placeholder="Enter your username"
                    ariaDescribedby="usernameHelp"
                />

                <div className="mb-xl-3"/>

                <TextInputComponent
                    className="w-75"
                    id="password"
                    type="password"
                    placeholder="Enter your password"
                    ariaDescribedby="passwordHelp"
                />

                <div className="mb-xl-3"/>

                <Button type="submit" variant="outline-light" className="mt-4 w-75 text-black border-black">
                    Log In
                </Button>

            </Container>
        </div>
    );
};

export default LoginPage;
