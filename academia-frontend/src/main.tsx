import './index.css';
import 'bootstrap/dist/css/bootstrap.min.css';
import {createRoot} from 'react-dom/client';
import {AppRoutes} from "./routing/AppRoutes.tsx";
import AuthProvider from "./contexts/AuthProvider.tsx";
import {BrowserRouter} from "react-router";


createRoot(document.getElementById('root')!).render(
    //<StrictMode>
        <AuthProvider>
            <BrowserRouter>
                <AppRoutes/>
            </BrowserRouter>
        </AuthProvider>
    //</StrictMode>
);
