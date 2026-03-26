import { useEffect, useRef } from "react";
import { useState } from "react";

import { getWishlist, removeFromWishlist } from "../api/wishlistApi";
import { FaSort, FaFilter } from "react-icons/fa";
import AddToRoutineModal from "../../routines/modals/AddToRoutineModal";

import Toast from "../../../components/ui/Toast";
import Searchbar from "../../../components/ui/Searchbar";

import "./Wishlist.css";
import ReviewStars from "../../../components/ui/ReviewStars";
import Tooltip from "../../../components/ui/Tooltip";
import SortFilterModal from "../../../components/SortFilterPopup";
import { useNavigate } from "react-router-dom";

export default function Wishlist({isLoggedIn}) {
    const [showToast, setShowToast] = useState(false);
    const toastTime = 1000;

    const [wishlist, setWishlist] = useState([]);
    const [currentSort, setCurrentSort] = useState(null);      // backend sort key, e.g. "price_asc"
    const [currentFilter, setCurrentFilter] = useState(null);  // popup label, e.g. "Skincare" or "Below $20"
    const [currentQuery, setCurrentQuery] = useState("");

    const [wishlistLoading, setWishlistLoading] = useState(false);
    const [wishlistError, setWishlistError] = useState("");

    const [selectedItem, setSelectedItem] = useState(null);

    const [sortOptionsOpen, setSortOptionsOpen] = useState(false);
    const sortOptionsRef = useRef(null);

    const [filterOptionsOpen, setFilterOptionsOpen] = useState(false);
    const filterOptionsRef = useRef(null);

    const navigate = useNavigate();

    function getFilterParams(filterOption) {
        let category = null;
        let priceRange = null;

        switch (filterOption) {
            case "Skincare":
            case "Makeup":
            case "Haircare":
                category = filterOption;
                break;
        }

        return { category, priceRange };
    }

    async function fetchWishlistWithParams({ sortKey = currentSort, filterOption = currentFilter, query = currentQuery } = {}) {
        setWishlistLoading(true);
        setWishlistError("");
        try {
            const { category, priceRange } = getFilterParams(filterOption);
            const items = await getWishlist({
                sort: sortKey,
                category,
                priceRange,
                query,
            });
            setWishlist(items);
        } catch (err) {
            setWishlistError("Failed to load wishlist.");
            setWishlist([]);
        } finally {
            setWishlistLoading(false);
        }
    }

    async function loadWishlist() {
        setCurrentSort(null);
        setCurrentFilter(null);
        setCurrentQuery("");
        await fetchWishlistWithParams({ sortKey: null, filterOption: null, query: "" });
    }

    async function handleSearch(query) {
        setCurrentQuery(query);
        await fetchWishlistWithParams({ query });
    }

    function handleSelect(type, option) {
        if (type === "sort") {
            handleSort(option);
        } else if (type === "filter") {
            handleFilter(option);
        }
    }

    function mapSortOptionToKey(option) {
        switch (option) {
            case "Date Added: Newest First":
                return "added_desc";
            case "Date Added: Oldest First":
                return "added_asc";
            case "Price: Low to High":
                return "price_asc";
            case "Price: High to Low":
                return "price_desc";
            case "Rating: High to Low":
                return "rating_desc";
            case "Rating: Low to High":
                return "rating_asc";
            default:
                return null;
        }
    }

    function mapSortKeyToOption(key) {
        switch (key) {
            case "added_desc":
                return "Date Added: Newest First";
            case "added_asc":
                return "Date Added: Oldest First";
            case "price_asc":
                return "Price: Low to High";
            case "price_desc":
                return "Price: High to Low";
            case "rating_desc":
                return "Rating: High to Low";
            case "rating_asc":
                return "Rating: Low to High";
            default:
                return null;
        }
    }

    async function handleSort(option) {
        setSortOptionsOpen(!sortOptionsOpen);
        if (!option) return;

        const sortKey = mapSortOptionToKey(option);
        const nextSortKey = sortKey === currentSort ? null : sortKey;
        setCurrentSort(nextSortKey);
        await fetchWishlistWithParams({ sortKey: nextSortKey });
    }

    async function handleFilter(option) {
        setFilterOptionsOpen(!filterOptionsOpen);
        if (!option) return;

        const nextFilter = option === currentFilter ? null : option;
        setCurrentFilter(nextFilter);
        await fetchWishlistWithParams({ filterOption: nextFilter });
    }

    useEffect(() => {
        loadWishlist();
    }, []);

    return (
        <>
        {showToast && 
        <Toast message="Item removed from wishlist" type="success" duration={toastTime} onClose={() => setShowToast(false)} />
        }

        <div className="wishlist-header">
            <h1>Wishlist♥</h1>
            {isLoggedIn && (
                <>
                <div className="wishlist-header-actions">
                    <div className="sort-filter-wrapper" ref={sortOptionsRef}>
                        <Tooltip message="Sort" position="top">
                            <button style={{ fontSize: "18px" }} className="sort-button" onClick={() => handleSort(null)}>
                                <FaSort />
                            </button>
                        </Tooltip>
                        <SortFilterModal
                            isOpen={sortOptionsOpen}
                            onClose={() => setSortOptionsOpen(false)}
                            type="sort"
                            page ="wishlist"
                            onSelect={handleSelect}
                            selectedOption={mapSortKeyToOption(currentSort)}
                        />
                    </div>

                    <div className="sort-filter-wrapper" ref={filterOptionsRef}>
                        <Tooltip message="Filter" position="top">
                            <button className="filter-button" onClick={() => handleFilter(null)}>
                                <FaFilter />
                            </button>
                        </Tooltip>
                        <SortFilterModal
                            isOpen={filterOptionsOpen}
                            onClose={() => setFilterOptionsOpen(false)}
                            type="filter"
                            page ="wishlist"
                            onSelect={handleSelect}
                            selectedOption={currentFilter}
                        />
                    </div>
                </div>

                <span className="wishlist-search">
                    <Searchbar placeholder="Search wishlist..." onSearch={handleSearch} />
                </span>
                </>
            )}
        </div>

        {!isLoggedIn ? (
            <p>Please log in to see your wishlist.</p>
        ) : (
            <>
            {wishlist.length === 0 && !wishlistLoading && (
                <div style={{ display: "flex", justifyContent: "center", alignItems: "center"}}>
                    <p style={{ fontStyle: "italic", color: "#888" }}>Your wishlist is empty.</p>
                </div>
            )}
            <div
                className="wishlist-container"
                style={{scrollBehavior: 'smooth'}}
            >
                {wishlist.map((item) => (
                    <div className="wishlist-item-card" key={item.id} style={{ cursor: "pointer" }} onClick={() => navigate(`/products/${item.productId}`)}>

                    <div className="wishlist-item-header">
                        <h2 style={{ fontSize: "1.25rem", textAlign: "center" }}>
                            {item.productName}
                        </h2>
                        <div style={{display: "flex", flexDirection: "row", justifyContent: "center", alignItems: "center", gap: "4px"}}>
                            <p style={{textAlign: "center"}}>
                                {item.brandName} 
                            </p>
                            {item.shadeName && (
                                <>
                                <p style={{textAlign: "center"}}>•</p>
                                <p style={{textAlign: "center"}}>
                                    {item.shadeName}
                                </p>
                                </>
                            )}
                        </div>
                    </div>

                    <img src={item.imageLink} alt="Product" style={{height: "250px"}} />

                    <div className="wishlist-item-footer">

                        <div className="wishlist-item-meta">
                            <span style={{marginBottom: "6px"}}>${item.price}</span>
                            {item.rating ? (
                                <ReviewStars productId={item.productId} shadeName={item.shadeName} rating={item.rating} disabled={true} />
                            ) : (
                                <p style={{fontStyle: "italic", color: "#888"}}>No ratings yet</p>
                            )}
                        </div>

                        <div className="wishlist-actions">

                            <div
                                className="action-icon"
                                onClick={(e) => {
                                    e.stopPropagation();
                                    setSelectedItem(item);
                                }}
                            >
                                <span style={{ color: "#1a8ec4" }} className="icon">+</span>
                                <span className="tooltip">Add to routine</span>
                            </div>

                            <div
                                className="action-icon"
                                onClick={(e) => {
                                    e.stopPropagation();
                                    removeFromWishlist(item.productId, item.shadeName).then(() => {
                                        setWishlist(prev =>
                                            prev.filter(
                                                w =>
                                                    !(w.productId === item.productId &&
                                                    w.shadeName === item.shadeName)
                                            )
                                        );
                                    });
                                }}
                            >
                                <span style={{ color: "#ff4d4f" }} className="icon">−</span>
                                <span className="tooltip">Remove from wishlist</span>
                            </div>

                        </div>

                    </div>
                    
                </div>
            ))}
            {selectedItem && (
                <AddToRoutineModal
                    productId={selectedItem.productId}
                    shadeName={selectedItem.shadeName}
                    productName={selectedItem.productName}
                    baseCategory={selectedItem.baseCategoryName}
                    onClose={() => setSelectedItem(null)}
                />
            )}
            </div>
            </>
        )
        }
        </>
    );
}