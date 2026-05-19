import Searchbar from '../../components/ui/Searchbar';
import UserSearch from '../user/components/UserSearch';
import './Feed.css';

export default function Feed() {

    return (

            <div className="feed-container">
                    <UserSearch placeholder="Search for users..." />
                Follow users to view their activity here!
            </div>

    );
}