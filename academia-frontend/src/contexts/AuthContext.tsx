import { createContext } from "react";

export interface IAuthContext {
    isAuthenticated: boolean | null;
    login: () => void;
    validate: () => void;
    logout: () => void;
}

const AuthContext = createContext<IAuthContext>({
    isAuthenticated: null,
    login: () => {},
    validate: () => {},
    logout: () => {},
});

export default AuthContext;
