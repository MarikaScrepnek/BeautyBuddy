import { useEffect, useState } from "react";
import { getCurrentUser } from "../auth/api/authApi";
import { getDiscussions, createDiscussion, searchDiscussions } from "./api/discussionApi";
import DiscussionCard from "./components/DiscussionCard";
import CreateDiscussionModal from "./modals/CreateDiscussionModal";
import AuthModal from "../auth/modals/AuthModal";

import { FaSearch } from "react-icons/fa";
import { FaSort } from "react-icons/fa";
import { FaCommentDots } from "react-icons/fa6";

import "./Discussions.css";
import Tooltip from "../../components/ui/Tooltip";
import SortFilterPopup from "../../components/SortFilterPopup";

export default function Discussions() {
  const [discussions, setDiscussions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [searchQuery, setSearchQuery] = useState("");
  const [activeSearchTerm, setActiveSearchTerm] = useState("");
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [showLoginModal, setShowLoginModal] = useState(false);
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [currentSort, setCurrentSort] = useState("created_desc");
  const [sortOptionsOpen, setSortOptionsOpen] = useState(false);

  const pageSize = 10;

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

  const loadDiscussions = async ({ page = currentPage, sortKey = currentSort, query = activeSearchTerm } = {}) => {
    setLoading(true);
    try {
      const data = query.trim()
        ? await searchDiscussions(query, sortKey, page, pageSize)
        : await getDiscussions(page, pageSize, sortKey);
      const items = Array.isArray(data) ? data : data?.content || [];
      const resolvedTotalPages = typeof data?.totalPages === "number"
        ? data.totalPages
        : typeof data?.totalElements === "number"
          ? Math.ceil(data.totalElements / pageSize)
          : (items.length === pageSize ? page + 2 : page + 1);

      setDiscussions(items);
      setCurrentPage(typeof data?.number === "number" ? data.number : page);
      setTotalPages(resolvedTotalPages > 0 ? resolvedTotalPages : (items.length ? 1 : 0));
    } finally {
      setLoading(false);
    }
  };
  
  useEffect(() => {
    loadDiscussions({ page: 0 });
  }, []);

  const handleCreateDiscussion = async (title, text) => {
    if (!isLoggedIn) {
      setShowLoginModal(true);
      return false;
    }
    const success = await createDiscussion(title, text);
    if (success) {
        setShowModal(false);
        await loadDiscussions({ page: currentPage, sortKey: currentSort, query: activeSearchTerm });
      return true;
    }
    return false;
  };

  const handleSearchDiscussions = () => {
    if (!searchQuery.trim()) {
      setActiveSearchTerm("");
      setCurrentPage(0);
      loadDiscussions({ page: 0, query: "" });
      return;
    }
    setActiveSearchTerm(searchQuery);
    setCurrentPage(0);
    loadDiscussions({ page: 0, query: searchQuery });
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

    setCurrentPage(0);
    setCurrentSort(nextSortKey);
    await loadDiscussions({ page: 0, sortKey: nextSortKey, query: activeSearchTerm });
  };

  return (
    <div>
      {discussions.length > 0 && (
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
      )}
      {loading ? (
        <div className="loading">Loading discussions...</div>
      ) : discussions.length === 0 ? (
        <div className="empty-state-container">
          <FaCommentDots className="empty-state-icon" />
          <h2 className="empty-state-title">No Discussions Yet</h2>
          <p className="empty-state-text">Be the first to start a conversation!</p>
          <button className="empty-state-button" onClick={() => {
            if (!isLoggedIn) {
              setShowLoginModal(true);
              return;
            }
            setShowModal(true);
          }}>Create the First Discussion</button>
        </div>
      ) : (
        <>
          <div className="discussions-list">
            {discussions.map((d) => (
              <DiscussionCard key={d.id} {...d} searchTerm={activeSearchTerm} sortKey={currentSort} />
            ))}
          </div>
          {(totalPages > 1 || currentPage > 0) && (
            <div className="discussions-pagination">
              <button
                type="button"
                className="discussions-page-btn"
                onClick={() => loadDiscussions({ page: Math.max(0, currentPage - 1) })}
                disabled={currentPage <= 0}
              >
                Previous
              </button>
              <span className="discussions-page-label">
                Page {currentPage + 1} of {totalPages}
              </span>
              <button
                type="button"
                className="discussions-page-btn"
                onClick={() => loadDiscussions({ page: currentPage + 1 })}
                disabled={totalPages > 0 ? currentPage >= totalPages - 1 : discussions.length < pageSize}
              >
                Next
              </button>
            </div>
          )}
        </>
      )}
      <CreateDiscussionModal
        open={showModal}
        onClose={() => {
          setShowModal(false);
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