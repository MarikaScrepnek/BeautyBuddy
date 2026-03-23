import { useEffect, useState } from "react";
import { getCurrentUser } from "../auth/api/authApi";
import { getDiscussions, createDiscussion, searchDiscussions } from "./api/discussionApi";
import DiscussionCard from "./components/DiscussionCard";
import CreateDiscussionModal from "./modals/CreateDiscussionModal";
import AuthModal from "../auth/modals/AuthModal";

import { FaSearch } from "react-icons/fa";

import "./Discussions.css";
import Tooltip from "../../components/ui/Tooltip";

export default function Discussions() {
  const [discussions, setDiscussions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [searchQuery, setSearchQuery] = useState("");
  const [activeSearchTerm, setActiveSearchTerm] = useState("");
  const [showLoginModal, setShowLoginModal] = useState(false);
  const [isLoggedIn, setIsLoggedIn] = useState(false);

  useEffect(() => {
    getCurrentUser()
      .then(() => setIsLoggedIn(true))
      .catch(() => setIsLoggedIn(false));
    const handleAuthLogin = () => setIsLoggedIn(true);
    const handleAuthLogout = () => setIsLoggedIn(false);
    window.addEventListener("auth:login", handleAuthLogin);
    window.addEventListener("auth:logout", handleAuthLogout);
    return () => {
      window.removeEventListener("auth:login", handleAuthLogin);
      window.removeEventListener("auth:logout", handleAuthLogout);
    };
  }, []);

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
    if (!isLoggedIn) {
      setShowLoginModal(true);
      return false;
    }
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
            <Tooltip message="Create a Discussion" position="left">
            <button onClick={() => {
              if (!isLoggedIn) {
                setShowLoginModal(true);
                return;
              }
              setShowModal(true);
            }} className="plus-sign">+</button>
            </Tooltip>
        </div>
        <div className="discussions-search-center">
          <div className="discussions-search-container">
            <div className="discussions-search">
              <input
                  type="text"
                  className="discussions-search-bar"
                  placeholder="Search discussions..."
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
      {showLoginModal && (
        <AuthModal
          onClose={() => setShowLoginModal(false)}
          onLoginSuccess={() => {
            setShowLoginModal(false);
            setIsLoggedIn(true);
          }}
        />
      )}
    </div>
  );
}