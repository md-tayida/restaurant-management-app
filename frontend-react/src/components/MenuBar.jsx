import React from "react";

export default function MenuBar({ topMenu, activePage, setActivePage, user }) {
    return (
        <nav className="flex justify-between items-center bg-gradient-to-r from-blue-950 via-blue-900 to-blue-800 text-white shadow-lg px-6 py-2">
            {/* เมนูด้านซ้าย */}
            <div className="flex space-x-4">
                {topMenu.map((item) => (
                    <button
                        key={item.id}
                        onClick={() => setActivePage(item.id)}
                        className={`flex items-center gap-2 px-6 py-3 rounded-xl font-medium tracking-wide transition-all duration-200
                            ${activePage === item.id
                                ? "bg-yellow-400 text-blue-950 shadow-md scale-105"
                                : "hover:bg-blue-700 hover:text-yellow-300"
                            }`}
                    >
                        <span className="text-lg">{item.icon}</span>
                        <span>{item.label}</span>
                    </button>
                ))}
            </div>

            {/* โปรไฟล์ผู้ใช้ด้านขวา */}
            <div className="flex items-center gap-3 bg-blue-800 px-4 py-2 rounded-xl shadow-md">
                <img
                    src={user?.avatar || "https://placehold.co/40x40/EFEFEF/333333?text=U"}
                    alt={user?.name || "User"}
                    className="w-10 h-10 rounded-full border-2 border-white"
                />
                <span className="font-medium">{user?.name || "Guest"}</span>
            </div>
        </nav>
    );
}
