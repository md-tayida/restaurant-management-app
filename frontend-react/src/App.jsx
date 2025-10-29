import { Routes, Route } from 'react-router-dom';
import HomePage from './pages/HomePage';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import TableManagementPage from './pages/TableManagementPage';
import MenuPage from './pages/MenuPage';
import KitchenDashboard from './pages/KitchenDashboard';
import ReadyToServePage from './pages/ReadyToServePage';
import ManagerDashboard from './pages/ManagerDashboard';
import PaymentPage from './pages/PaymentPage';
import Test from './pages/TestPage';

export default function App() {
  return (
    <Routes>
      <Route path="/" element={<HomePage />} />
      <Route path="/login" element={<LoginPage />} />
      <Route path="/payment" element={<PaymentPage />} />
      <Route path="/test" element={<Test />} />

      <Route path="/register" element={<RegisterPage />} />
      <Route path="/tableManagementPage" element={<TableManagementPage />} />
      <Route path="/menus" element={<MenuPage />} />
      <Route path="/kitchen" element={<KitchenDashboard />} />
      <Route path="/ready-to-serve" element={<ReadyToServePage />} />
      <Route path="/ManagerDashboard" element={<ManagerDashboard />} />
    </Routes>
  );
}
