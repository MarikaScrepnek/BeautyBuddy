import { useEffect, useState } from "react";
import { getDiscussions, createDiscussion, searchDiscussions } from "../api/discussionApi";
import DiscussionCard from "../components/discussion/DiscussionCard";
import CreateDiscussionModal from "../components/discussion/CreateDiscussionModal";

import { FaSearch } from "react-icons/fa";

import "./Discussions.css";

export default function Discussions() {
  const [discussions, setDiscussions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [searchQuery, setSearchQuery] = useState("");
  const [activeSearchTerm, setActiveSearchTerm] = useState("");

  const refreshDiscussions = () => {
    setLoading(true);
    getDiscussions().then((data) => {
      setDiscussions(Array.isArray(data) ? data : data.content || []);
      setLoading(false);
    });
  };
  
  useEffect(() => {
    refreshDiscussions();
  }, []);

  const handleCreateDiscussion = async (title, text) => {
    const success = await createDiscussion(title, text);
      if (success) {
        refreshDiscussions();
        return true;
      }
      return false;
  };

  const handleSearchDiscussions = () => {
    if (!searchQuery.trim()) {
      setActiveSearchTerm("");
      refreshDiscussions();
      return;
    }
    setLoading(true);
    searchDiscussions(searchQuery).then((data) => {
      setDiscussions(Array.isArray(data) ? data : data.content || []);
      setActiveSearchTerm(searchQuery);
      setLoading(false);
    });
  };

  return (
    <div>
      <div className="discussions-header-container">
        <div className="discussions-button-row">
          <div className="create-discussion-action-icon" onClick={() => setShowModal(true)}>
            <span className="plus-sign">+</span>
            <span className="tooltip">Create Discussion</span>
          </div>
        </div>
        <div className="discussions-search-center">
          <div className="discussions-search-container">
            <div className="discussions-search">
              <input
                  type="text"
                  className="discussions-search-bar"
                  placeholder="Enter terms here..."
                  value={searchQuery}
                  onChange={e => setSearchQuery(e.target.value)}
                  onKeyDown={e => {
                      if (e.key === "Enter") handleSearchDiscussions();
                  }}
              />
              <button
                  type="button"
                  className="discussions-search-button"
                  aria-label="Search"
                  onClick={handleSearchDiscussions}
              >
                  <FaSearch />
              </button>
            </div>
          </div>
        </div>
      </div>
      {loading ? (
        <div>Loading...</div>
      ) : discussions.length === 0 ? (
        <div>No discussions yet.</div>
      ) : (
        discussions.map((d) => (
          <DiscussionCard key={d.id} {...d} searchTerm={activeSearchTerm} />
        ))
      )}
      <CreateDiscussionModal
        open={showModal}
        onClose={() => {
          setShowModal(false);
          refreshDiscussions();
        }}
        onCreate={handleCreateDiscussion}
      />
    </div>
  );
}