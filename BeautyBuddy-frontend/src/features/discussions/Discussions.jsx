import { useEffect, useState } from "react";
import { getCurrentUser } from "../auth/api/authApi";
import { getDiscussions, createDiscussion, searchDiscussions } from "./api/discussionApi";
import DiscussionCard from "./components/DiscussionCard";
import CreateDiscussionModal from "./modals/CreateDiscussionModal";
import AuthModal from "../auth/modals/AuthModal";

import { FaSearch } from "react-icons/fa";
import { FaSort } from "react-icons/fa";

import "./Discussions.css";
import Tooltip from "../../components/ui/Tooltip";
import SortFilterPopup from "../../components/SortFilterPopup";

export default function Discussions() {
  const [discussions, setDiscussions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [searchQuery, setSearchQuery] = useState("");
  const [activeSearchTerm, setActiveSearchTerm] = useState("");
  const [showLoginModal, setShowLoginModal] = useState(false);
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [currentSort, setCurrentSort] = useState("created_desc");
  const [sortOptionsOpen, setSortOptionsOpen] = useState(false);

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

  function mapSortOptionToKey(option) {
    switch (option) {
      case "Most Helpful":
        return "helpful_desc";
      case "Newest":
        return "created_desc";
      case "Oldest":
        return "created_asc";
      default:
        return null;
    }
  }

  function mapSortKeyToOption(key) {
    switch (key) {
      case "helpful_desc":
        return "Most Helpful";
      case "created_asc":
        return "Oldest";
      case "created_desc":
      default:
        return "Newest";
    }
  }

  const loadDiscussions = async ({ sortKey = currentSort, query = activeSearchTerm } = {}) => {
    setLoading(true);
    try {
      const data = query.trim()
        ? await searchDiscussions(query, sortKey)
        : await getDiscussions(0, 10, sortKey);
      setDiscussions(Array.isArray(data) ? data : data.content || []);
    } finally {
      setLoading(false);
    }
  };
  
  useEffect(() => {
    loadDiscussions();
  }, []);

  const handleCreateDiscussion = async (title, text) => {
    if (!isLoggedIn) {
      setShowLoginModal(true);
      return false;
    }
    const success = await createDiscussion(title, text);
    if (success) {
      await loadDiscussions({ query: activeSearchTerm });
      return true;
    }
    return false;
  };

  const handleSearchDiscussions = () => {
    if (!searchQuery.trim()) {
      setActiveSearchTerm("");
      loadDiscussions({ query: "" });
      return;
    }
    setActiveSearchTerm(searchQuery);
    loadDiscussions({ query: searchQuery });
  };

  function handleSelect(type, option) {
    if (type === "sort") {
      handleSort(option);
    }
  }

  async function handleSort(option) {
    setSortOptionsOpen((open) => !open);
    if (!option) return;

    const nextSortKey = mapSortOptionToKey(option);
    if (!nextSortKey || nextSortKey === currentSort) {
      return;
    }

    setCurrentSort(nextSortKey);
    await loadDiscussions({ sortKey: nextSortKey, query: activeSearchTerm });
  };

  return (
    <div>
      <div className="discussions-header-container">
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

          <div className="sort-filter-wrapper">
            <Tooltip message="Sort" position="bottom">
              <button style={{ fontSize: "20px" }} className="filter-sort-button" onClick={() => handleSort(null)}>
                  <FaSort />
              </button>
            </Tooltip>
            <SortFilterPopup
              isOpen={sortOptionsOpen}
              onClose={() => setSortOptionsOpen(false)}
              type="sort"
              page="discussions"
              onSelect={handleSelect}
              selectedOption={mapSortKeyToOption(currentSort)}
            />
          </div>

          <Tooltip message="Create a Discussion" position="bottom">
            <button onClick={() => {
              if (!isLoggedIn) {
                setShowLoginModal(true);
                return;
              }
              setShowModal(true);
            }} className="plus-sign">+</button>
          </Tooltip>
        </div>
      </div>
      {loading ? (
        <div>Loading...</div>
      ) : discussions.length === 0 ? (
        <div>No discussions yet.</div>
      ) : (
        discussions.map((d) => (
          <DiscussionCard key={d.id} {...d} searchTerm={activeSearchTerm} sortKey={currentSort} />
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