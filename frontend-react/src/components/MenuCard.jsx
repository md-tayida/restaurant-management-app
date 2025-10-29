const MenuCard = ({ item, onAdd }) => {
    if (!item) return null;
    const imageUrl =
        item.imgUrl ||
        `https://placehold.co/600x400/EFEFEF/333333?text=${encodeURIComponent(item.name)}`;

    return (
        <div className="bg-white rounded-2xl shadow-lg overflow-hidden hover:shadow-xl transition-shadow duration-300 flex flex-col">
            {/* รูปภาพ */}
            <div className="relative overflow-hidden">
                <img
                    src={imageUrl}
                    alt={item.name}
                    className="w-full h-44 object-cover transition-transform duration-300 hover:scale-110"
                    onError={(e) => {
                        e.target.onerror = null;
                        e.target.src =
                            "https://placehold.co/600x400/EFEFEF/333333?text=Error";
                    }}
                />
            </div>

            {/* ข้อมูลเมนู */}
            <div className="p-4 flex-1 flex flex-col justify-between">
                <div>
                    <h4
                        className="font-semibold text-[#212529] text-lg truncate"
                        title={item.name}
                    >
                        {item.name}
                    </h4>
                    <p className="text-gray-500 text-sm my-1 h-10 overflow-hidden">
                        {item.description || ""}
                    </p>
                </div>

                <div className="mt-2 flex items-center justify-between">
                    <p className="font-bold text-[#007BFF] text-lg">
                        {item.price} บาท
                    </p>

                    {item.status === 'AVAILABLE' ? (
                        <button
                            type="button"
                            onClick={(e) => {
                                e.stopPropagation();
                                onAdd(item);
                            }}
                            className="bg-blue-600 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded-lg text-sm transition-colors active:scale-95"
                        >
                            ➕ เพิ่ม
                        </button>
                    ) : (
                        <div className="relative group">
                            <button
                                type="button"
                                disabled
                                className="bg-gray-300 text-gray-500 font-bold py-2 px-4 rounded-lg text-sm cursor-not-allowed"
                            >
                                ❌ หมด
                            </button>
                            {/* Tooltip */}
                            <span className="absolute bottom-full mb-2 w-max hidden group-hover:block bg-black text-white text-xs rounded px-2 py-1">
                                เมนูหมดสต็อก
                            </span>
                        </div>
                    )}
                </div>

            </div>
        </div>
    );
};

export default MenuCard;
