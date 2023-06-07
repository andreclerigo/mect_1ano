import { Navigate, useRoutes } from 'react-router-dom';
// layouts
import DashboardLayout from './layouts/dashboard';
import SimpleLayout from './layouts/simple';
//
import RegisterPage from './pages/RegisterPage';
import ManagePage from './pages/ManagePage';
import LoginPage from './pages/LoginPage';
import Page404 from './pages/Page404';
import AdsPage from './pages/AdsPage';
import NewAdPage from './pages/NewAdPage';
import DashboardAppPage from './pages/DashboardAppPage';
import ConsumerPage from './pages/ConsumerPage';

import { useState, useEffect } from 'react';

// ----------------------------------------------------------------------

export default function Router() {

    const routes = useRoutes([
        {
        path: '/dashboard',
        element: <DashboardLayout />,
        children: [
            { element: <Navigate to="/dashboard/app" />, index: true },
            { path: 'app', element: <DashboardAppPage /> },
            { path: 'manage', element: <ManagePage/> },
            { path: 'ads', element: <AdsPage /> },
        ],
        },
        {
            path: 'consumer',
            element: <ConsumerPage />,

        },
        {
            path: 'login',
            element: <LoginPage />,
        },
        {
            path: 'register',
            element: <RegisterPage />,
        },
        {
            path: 'new-ad',
            element: <NewAdPage />,
        },
        {
        element: <SimpleLayout />,
        children: [
            { element: <Navigate to="/dashboard/app" />, index: true },
            { path: '404', element: <Page404 /> },
            { path: '*', element: <Navigate to="/404" /> },
        ],
        },
        {
        path: '*',
        element: <Navigate to="/404" replace />,
        },
    ]);

    return routes;
    }
