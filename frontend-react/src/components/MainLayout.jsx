//src / layouts / MainLayout.jsx
import React from 'react';
import Sidebar from '../components/Sidebar';
import Navbar from '../components/Navbar';

const MainLayout = ({ children }) => {
    return (
        <div className="flex h-screen">
            <Sidebar />
            <div className="flex flex-col flex-1">
                <Navbar />
                <main className="p-4 overflow-auto">{children}</main>
            </div>
        </div>
    );
};

export default MainLayout;