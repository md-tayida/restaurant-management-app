import { useParams } from 'react-router-dom';
import KitchenPage from './KitchenPage';

const KitchenPageWrapper = () => {
    const { category } = useParams();
    return <KitchenPage category={category} />;
};

export default KitchenPageWrapper;