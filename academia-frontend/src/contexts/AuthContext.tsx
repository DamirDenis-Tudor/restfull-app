import { createContext } from "react";
import {Link} from "../api/hateoas.ts";

export interface LoginRequest {
    email: string;
    password: string;
}

export interface LoginResponse {
    token: string;
    role: string;
    message: string;
    _links: Record<string, Link>;
}

export interface IAuthContext {
    loginResponse: LoginResponse
    login: (email: string, password: string) => void;
    validate: () => boolean;
    logout: () => void;
}

const AuthContext = createContext<IAuthContext>({
    loginResponse: {
        role: sessionStorage.getItem("role") ?? "",
        token: sessionStorage.getItem("token") ?? "",
        message: "",
        _links: {}
    },
    login: async () => {},
    validate: () => false,
    logout: () => {},
});

export default AuthContext;
