import ProductList from './components/ProductList';
import { useSearchParams } from 'react-router-dom';
import { useEffect, useState } from 'react';

import './Products.css';

import RequestModal from './modals/RequestModal';
import { FaSearch } from 'react-icons/fa';
import { FaFilter } from 'react-icons/fa';
import { FaSort } from 'react-icons/fa';
import SortFilterModal from '../../components/SortFilterPopup';
import Tooltip from '../../components/ui/Tooltip';

export default function Products() {
  const [inputValue, setInputValue] = useState("");
  const [params] = useSearchParams();
  const [searchQuery, setSearchQuery] = useState(params.get("q") || "");
  const [currentSort, setCurrentSort] = useState(null);
  const [currentFilter, setCurrentFilter] = useState(null);
  const [sortOptionsOpen, setSortOptionsOpen] = useState(false);
  const [filterOptionsOpen, setFilterOptionsOpen] = useState(false);

  const [showRequest, setShowRequest] = useState(false);
  const [isLoading, setIsLoading] = useState(false);

  useEffect(() => {
    setInputValue(params.get("q") || "");
    setSearchQuery(params.get("q") || "");
  }, [params]);

  function mapSortOptionToKey(option) {
    switch (option) {
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

  function handleSelect(type, option) {
    if (type === "sort") {
      handleSort(option);
    } else if (type === "filter") {
      handleFilter(option);
    }
  }

  async function handleSort(option) {
    setSortOptionsOpen((open) => !open);
    if (!option) return;

    const nextSortKey = mapSortOptionToKey(option);
    setCurrentSort((current) => (current === nextSortKey ? null : nextSortKey));
  }

  async function handleFilter(option) {
    setFilterOptionsOpen((open) => !open);
    if (!option) return;

    setCurrentFilter((current) => (current === option ? null : option));
  }

  function onChange(e) {
    setInputValue(e.target.value);
  }

  const handleKeyDown = (e) => {
    if (e.key === "Enter") {
      handleSearch();
    }
  };

  const handleSearch = () => {
    setSearchQuery(inputValue);
  };

  return (
    <div>
      <div className="search-container">
        <div className="product-search-group">
          <input
            type="text"
            className="search-bar"
            placeholder="Search products..."
            value={inputValue}
            onChange={onChange}
            onKeyDown={handleKeyDown}
          />

          <button
            type="button"
            className="search-button"
            onClick={handleSearch}
            aria-label="Search"
          >
            <FaSearch />
          </button>
        </div>

        <div className="products-header-actions wishlist-header-actions">
          <div className="sort-filter-wrapper">
            <Tooltip message="Sort" position="bottom">
              <button style={{ fontSize: "18px" }} className="sort-button" onClick={() => handleSort(null)}>
                <FaSort />
              </button>
            </Tooltip>
            <SortFilterModal
              isOpen={sortOptionsOpen}
              onClose={() => setSortOptionsOpen(false)}
              type="sort"
              page="products"
              onSelect={handleSelect}
              selectedOption={mapSortKeyToOption(currentSort)}
            />
          </div>

          <div className="sort-filter-wrapper">
            <Tooltip message="Filter" position="bottom">
              <button className="filter-button" onClick={() => handleFilter(null)}>
                <FaFilter />
              </button>
            </Tooltip>
            <SortFilterModal
              isOpen={filterOptionsOpen}
              onClose={() => setFilterOptionsOpen(false)}
              type="filter"
              page="products"
              onSelect={handleSelect}
              selectedOption={currentFilter}
            />
          </div>
        </div>
      </div>

      <ProductList
        searchQuery={searchQuery}
        sortKey={currentSort}
        filterOption={currentFilter}
        onLoadingChange={setIsLoading}
      />

      {!isLoading && (
      <div className="product-not-found">
        <p>Don't see the product you're looking for? Send us a request!</p>
        <button
        className="request-product-button"
        onClick={() => setShowRequest(true)}
        >
          Request Product
        </button>
      </div>
      )}
      {showRequest && (
        <RequestModal onClose={() => setShowRequest(false)} />
      )}

    </div>
  );
}



/*attribution for user setting icon (put at bottom of page): <a href="https://www.flaticon.com/free-icons/setting" title="setting icons">Setting icons created by Tanah Basah - Flaticon</a>*/