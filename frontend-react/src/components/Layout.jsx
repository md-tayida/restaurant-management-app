// src/layout/Layout.jsx
import Navbar from "../components/Navbar";
import Footer from "../components/Footer";

const Layout = ({ children }) => {
    return (
        <div className="min-h-screen flex flex-col">
            <Navbar />
            <main className="flex-grow p-4 bg-gray-50">{children}</main>
            <Footer />
        </div>
    );
};

export default Layout;
