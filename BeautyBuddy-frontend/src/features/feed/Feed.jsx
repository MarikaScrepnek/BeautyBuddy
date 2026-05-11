import Searchbar from '../../components/ui/Searchbar';
import './Feed.css';

export default function Feed() {

    return (

            <div className="feed-container">
                    <Searchbar placeholder="Search for users..." />
                Follow users to view their activity here!
            </div>

    );
}