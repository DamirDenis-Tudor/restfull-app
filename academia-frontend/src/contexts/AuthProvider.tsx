import React, {useState} from "react";
import AuthContext, {LoginRequest, LoginResponse} from "./AuthContext.tsx";
import {fetchLink} from "../api/hateoas.ts";
import {toast} from "react-toastify";

const authUrl: string = "http://localhost:8080/api/academia/login";

function defaultLoginResponse(): LoginResponse {
    return {
        role: sessionStorage.getItem("role") ?? "",
        token: sessionStorage.getItem("token") ?? "",
        message: "",
        _links: JSON.parse(sessionStorage.getItem("_links") ?? "{}")
    };
}

const AuthProvider: React.FC<{ children: React.ReactNode }> = ({children}) => {
    const [loginResponse, setLoginResponse] = useState<LoginResponse>(defaultLoginResponse());

    const login = async (email: string, password: string): Promise<void> => {
        const loginRequest: LoginRequest = {email, password};

        try {
            const response = await fetch(authUrl, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify(loginRequest),
            });

            if (response.ok) {
                const data: LoginResponse = await response.json();

                setLoginResponse(data)

                sessionStorage.setItem("token", data.token);
                sessionStorage.setItem("role", data.role);
                sessionStorage.setItem("_links", JSON.stringify(data._links));
            }else {
                const data = await response.json();
                toast.error(data.message);
            }
        } catch (error) {
            console.error("Login error:", error);
        }
    };

    const validate = (): boolean => {
        return loginResponse.token !== "";
    };

    const logout = (): void => {
        fetchLink(loginResponse._links.logout, undefined).then(r =>
            console.log("logout", r)
        ).catch(e => console.error(e));

        sessionStorage.removeItem("token");
        sessionStorage.removeItem("role");
        setLoginResponse(defaultLoginResponse());
    };

    return (
        <AuthContext.Provider
            value={{
                loginResponse,
                login,
                validate,
                logout,
            }}
        >
            {children}
        </AuthContext.Provider>
    );
};

export default AuthProvider;
