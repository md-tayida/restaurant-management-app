import { useState } from "react"
import { Utensils, CreditCard, Table, Home, BarChart2, Users, Settings } from "lucide-react"
import MenuBar from "../components/MenuBar"
import MenuSide from "../components/MenuSide"
import CounterPage from "./TableManagementPage"
import KitchenDashboard from "./KitchenDashboard"
import PaymentPage from "./PaymentPage"
export default function ManagerDashboard() {
    const [activePage, setActivePage] = useState("tables")

    const topMenu = [
        { id: "tables", label: "Table", icon: <Table size={18} /> },
        { id: "kitchen", label: "Kitchen", icon: <Utensils size={18} /> },
        { id: "payments", label: "Payment", icon: <CreditCard size={18} /> },
    ]

    const sideMenu = [
        { id: "dashboard", label: "Dashboard", icon: <Home size={18} /> },
        { id: "reports", label: "Reports", icon: <BarChart2 size={18} /> },
        { id: "users", label: "User Management", icon: <Users size={18} /> },
        { id: "settings", label: "Settings", icon: <Settings size={18} /> },
    ]

    const renderContent = () => {
        switch (activePage) {
            case "tables":
                return <CounterPage />
            case "kitchen":
                return <KitchenDashboard />
            case "payments":
                return <PaymentPage />
            case "dashboard":
                return <h2 className="text-blue-900 font-semibold text-xl">📊 Manager Dashboard</h2>
            case "reports":
                return <h2 className="text-blue-900 font-semibold text-xl">📈 Reports and Analytics</h2>
            case "users":
                return <h2 className="text-blue-900 font-semibold text-xl">👥 User Management</h2>
            case "settings":
                return <h2 className="text-blue-900 font-semibold text-xl">⚙️ Settings</h2>
            default:
                return null
        }
    }

    return (
        <div className="min-h-screen bg-gray-100 text-gray-900 flex">
            <MenuSide activePage={activePage} setActivePage={setActivePage} sideMenu={sideMenu} />

            <div className="flex-1 flex flex-col">
                <MenuBar activePage={activePage} setActivePage={setActivePage} topMenu={topMenu} />

                <main className="flex-1 p-8 overflow-auto">
                    <header className="mb-6 border-b border-gray-300 pb-2">
                        <h1 className="text-3xl font-semibold text-blue-900 capitalize">
                            {topMenu.concat(sideMenu).find((m) => m.id === activePage)?.label}
                        </h1>
                    </header>
                    <div>{renderContent()}</div>
                </main>
            </div>
        </div>
    )
}
