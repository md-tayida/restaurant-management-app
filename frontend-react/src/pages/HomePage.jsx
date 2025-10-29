import { Link, useNavigate } from 'react-router-dom';
import { useEffect, useState } from 'react';

export default function HomePage() {
    const [isLoggedIn, setIsLoggedIn] = useState(false);
    const navigate = useNavigate();

    useEffect(() => {
        // ตรวจสอบว่ามี token ใน localStorage ไหม
        const token = localStorage.getItem('token');
        setIsLoggedIn(!!token);
    }, []);

    const handleLogout = () => {
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        setIsLoggedIn(false);
        navigate('/login');
    };

    return (
        <div className="min-h-screen flex flex-col items-center justify-center bg-gradient-to-br from-blue-900 via-blue-800 to-gray-100 p-6 antialiased">
            <div className="w-full max-w-md bg-white/95 backdrop-blur-md rounded-2xl shadow-2xl p-8 space-y-6">

                {/* Header */}
                <div className="text-center">
                    <h1 className="text-4xl font-bold text-blue-900">
                        ระบบจัดการร้านอาหาร
                    </h1>
                </div>

                {/* Main Action Buttons */}
                <div className="space-y-4 pt-4">
                    <Link to="/menus" className="block">
                        <button className="w-full flex items-center justify-center gap-3 bg-emerald-500 text-white py-3 px-6 rounded-xl hover:bg-emerald-600 text-lg shadow-md transition-transform transform hover:scale-105">
                            <span>สั่งอาหาร</span>
                        </button>
                    </Link>

                    <Link to="/ManagerDashboard" className="block">
                        <button className="w-full flex items-center justify-center gap-3 bg-blue-700 text-white py-3 px-6 rounded-xl hover:bg-blue-800 text-lg shadow-md transition-transform transform hover:scale-105">
                            <span>ระบบหลังร้าน</span>
                        </button>
                    </Link>
                </div>

                {/* Footer Links */}
                <div className="border-t border-gray-200 pt-6">
                    <div className="flex justify-center items-center text-sm gap-6">
                        {isLoggedIn ? (
                            <button
                                onClick={handleLogout}
                                className="font-medium text-red-600 hover:underline"
                            >
                                ออกจากระบบ
                            </button>
                        ) : (
                            <>
                                <Link to="/login">
                                    <span className="font-medium text-blue-700 hover:underline">
                                        เข้าสู่ระบบ
                                    </span>
                                </Link>
                                <span className="text-gray-300">|</span>
                                <Link to="/register">
                                    <span className="font-medium text-blue-700 hover:underline">
                                        สมัครสมาชิก
                                    </span>
                                </Link>
                            </>
                        )}
                    </div>
                </div>

            </div>
        </div>
    );
}
