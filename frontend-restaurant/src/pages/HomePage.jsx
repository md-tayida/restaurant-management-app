import { Link } from 'react-router-dom';

export default function HomePage() {
    return (
        <div className="flex flex-col items-center justify-center min-h-screen bg-gray-100 p-6">
            <h1 className="text-4xl font-bold mb-6 text-center">🍴 ระบบจัดการร้านอาหาร</h1>

            <div className="grid gap-4 w-full max-w-md">
                <Link to="/menus">
                    <button className="w-full bg-green-500 text-white py-3 px-6 rounded-xl hover:bg-green-600 text-lg shadow">
                        สั่งอาหาร
                    </button>
                </Link>

                <Link to="/counter">
                    <button className="w-full bg-blue-500 text-white py-3 px-6 rounded-xl hover:bg-blue-600 text-lg shadow">
                        ระบบหลังร้าน
                    </button>
                </Link>

                <div className="flex justify-between mt-4">
                    <Link to="/login">
                        <span className="text-blue-700 hover:underline">เข้าสู่ระบบ</span>
                    </Link>
                    <Link to="/register">
                        <span className="text-blue-700 hover:underline">สมัครสมาชิก</span>
                    </Link>
                </div>
            </div>
        </div>
    );
}
