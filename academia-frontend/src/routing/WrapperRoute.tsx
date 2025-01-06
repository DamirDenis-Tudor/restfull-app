import React, { useContext, useEffect } from "react";
import { Outlet, Navigate } from "react-router";
import AuthContext from "../contexts/AuthContext";

const WrapperRoute : React.FC = () => {
    const { isAuthenticated, validate } = useContext(AuthContext);

    useEffect(() => {
        validate();
    }, [validate]);

    if (isAuthenticated === null) {
        return <div>Loading...</div>;
    }

    return isAuthenticated ? <Outlet /> : <Navigate to="/login" replace />;
};

export default WrapperRoute;
