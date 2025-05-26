import { Routes, Route } from 'react-router-dom';

// import ManagerHelloPage from './pages/ManagerHelloPage';
import HomePage from './pages/HomePage';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import CounterPage from './pages/CounterPage';
import MenuPage from './pages/MenuPage';
import KitchenDashboard from './pages/KitchenDashboard';
import ReadyToServePage from './pages/ReadyToServePage';
export default function App() {
  return (
    <Routes>
      <Route path="/" element={<HomePage />} />
      <Route path="/login" element={<LoginPage />} />
      <Route path="/register" element={<RegisterPage />} />
      <Route path="/counter" element={<CounterPage />} />
      <Route path="/menus" element={<MenuPage />} />
      <Route path="/kitchen" element={<KitchenDashboard />} />
      <Route path="/ready-to-serve" element={<ReadyToServePage />} />

    </Routes>
  );
}
