import { useNavigate } from 'react-router-dom';
import { useState } from 'react';
import { FaUtensils } from 'react-icons/fa';
import { login } from '../api/authApi';

export default function LoginPage() {
    const [form, setForm] = useState({ username: '', password: '' });
    const [loading, setLoading] = useState(false);
    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        try {
            const res = await login(form);

            localStorage.setItem('token', res.token);

            localStorage.setItem('user', JSON.stringify(res.user));

            navigate('/');
        } catch (err) {
            alert(err.response?.data?.message || 'เกิดข้อผิดพลาดบางอย่าง');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-blue-900 via-blue-800 to-gray-800">
            <div className="bg-white/95 backdrop-blur-md shadow-2xl rounded-2xl w-full max-w-md p-10">
                <div className="flex flex-col items-center mb-8">
                    <FaUtensils className="text-blue-700 text-4xl mb-3" />
                    <h1 className="text-3xl font-bold text-blue-900">Restaurant Staff Login</h1>
                    <p className="text-gray-500 text-sm mt-1">ระบบจัดการร้านอาหาร</p>
                </div>

                <form onSubmit={handleSubmit} className="space-y-5">
                    <div>
                        <label className="block text-gray-700 font-medium mb-2">Username</label>
                        <input
                            type="text"
                            value={form.username}
                            onChange={(e) => setForm({ ...form, username: e.target.value })}
                            className="w-full p-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-600"
                            placeholder="กรอกชื่อผู้ใช้"
                            required
                        />
                    </div>

                    <div>
                        <label className="block text-gray-700 font-medium mb-2">Password</label>
                        <input
                            type="password"
                            value={form.password}
                            onChange={(e) => setForm({ ...form, password: e.target.value })}
                            className="w-full p-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-600"
                            placeholder="กรอกรหัสผ่าน"
                            required
                        />
                    </div>

                    <div className="flex items-center justify-between text-sm">
                        <label className="flex items-center gap-2 text-gray-600">
                            <input type="checkbox" className="accent-blue-600" /> Remember me
                        </label>
                        <a href="#" className="text-blue-600 hover:underline">Forgot password?</a>
                    </div>

                    <button
                        type="submit"
                        disabled={loading}
                        className={`w-full py-3 rounded-lg text-white font-semibold transition-all duration-200 shadow-md ${loading
                            ? 'bg-blue-300 cursor-not-allowed'
                            : 'bg-gradient-to-r from-blue-700 to-blue-500 hover:scale-[1.02]'
                            }`}
                    >
                        {loading ? 'กำลังเข้าสู่ระบบ...' : 'เข้าสู่ระบบ'}
                    </button>
                </form>
            </div>
        </div>
    );
}
