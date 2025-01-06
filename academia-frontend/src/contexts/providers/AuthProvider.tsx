import React, { useState } from "react";
import AuthContext from "../AuthContext.tsx";

interface IAuthProviderProps {
    children: React.ReactNode;
}

const AuthProvider: React.FC<IAuthProviderProps> = ({ children }) => {
    const [authState] = useState<boolean|null>(true);

    const login = () => {

    };

    const validate = async () => {

    };

    const logout = async () => {

    };

    return (
        <AuthContext.Provider
            value={{
                isAuthenticated: authState,
                login: login,
                validate: validate,
                logout: logout,
            }}
        >
            {children}
        </AuthContext.Provider>
    );
};

export default AuthProvider;
