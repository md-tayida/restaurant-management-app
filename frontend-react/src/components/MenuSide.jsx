import React from "react"
import { LogOut } from "lucide-react"

export default function MenuSide({ activePage, setActivePage, sideMenu }) {
    return (
        <aside className="w-64 bg-blue-900 text-white flex flex-col justify-between p-4 shadow-lg">
            <div>
                <h1 className="text-2xl font-bold mb-6 text-center">Manager Panel</h1>
                <nav className="space-y-2">
                    {sideMenu.map((item) => (
                        <button
                            key={item.id}
                            onClick={() => setActivePage(item.id)}
                            className={`w-full flex items-center gap-3 px-3 py-2 rounded-lg transition-all text-left text-sm font-medium ${activePage === item.id
                                    ? "bg-blue-600 text-white"
                                    : "hover:bg-blue-800 hover:text-white text-gray-200"
                                }`}
                        >
                            {item.icon}
                            {item.label}
                        </button>
                    ))}
                </nav>
            </div>

            <button className="mt-6 flex items-center justify-center gap-2 bg-red-500 hover:bg-red-600 text-white py-2 rounded-lg font-medium transition">
                <LogOut size={18} /> Logout
            </button>
        </aside>
    )
}
