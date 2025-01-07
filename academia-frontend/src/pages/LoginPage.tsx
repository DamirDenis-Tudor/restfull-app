import React, {useState, useContext} from 'react';
import { Container, Button } from 'react-bootstrap';
import { TextInput } from '../components/TextInput.tsx';
import AuthContext from "../contexts/AuthContext.tsx";

const LoginPage: React.FC = () => {
    const { login } = useContext(AuthContext);
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [errorMessage, setErrorMessage] = useState('');

    const handleLogin = async (event: React.FormEvent) => {
        event.preventDefault();
        try {
            login(email, password);
            setErrorMessage('');
        } catch {
            setErrorMessage('Login failed. Please check your credentials.');
        }
    };

    return (
        <div className="h-screen flex items-center justify-center bg-white-200">
            <Container className="w-25 flex flex-col items-center justify-center tw-bg-gray-500 bg-opacity-80 p-6 rounded-lg shadow-lg max-w-sm mx-auto">
                <p className="text-3xl font-bold mb-6 flex items-center text-black">
                    <img
                        src="https://sso.tuiasi.ro/auth/welcome-content/favicon.ico"
                        alt="Noodle Icon"
                        className="w-8 h-8 mr-2"
                    />
                    <span>Noodle</span>
                </p>

                <form onSubmit={handleLogin} className="w-full">
                    <div className="mb-xl-3" />
                    <TextInput
                        className="w-120"
                        id="username"
                        placeholder="Enter your username"
                        ariaDescribedby="usernameHelp"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                    />

                    <div className="mb-xl-3" />
                    <TextInput
                        className="w-120"
                        id="password"
                        type="password"
                        placeholder="Enter your password"
                        ariaDescribedby="passwordHelp"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                    />

                    <div className="mb-xl-3" />
                    {errorMessage && <p className="text-red-500">{errorMessage}</p>}
                    <Button type="submit" variant="light" className="mt-4 w-100 text-black border-dark-subtle">
                        Log In
                    </Button>
                </form>
            </Container>
        </div>
    );
};

export default LoginPage;
