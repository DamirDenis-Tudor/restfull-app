
import {createRoot} from 'react-dom/client';
import {AppRouter} from "./routing/AppRouter.tsx";
import './index.css';
import 'bootstrap/dist/css/bootstrap.min.css';
import {StrictMode} from "react";


createRoot(document.getElementById('root')!).render(
    <StrictMode>
        <AppRouter/>
    </StrictMode>,
);
